package com.example.dat.users.service;

import com.example.dat.doctor.entity.Doctor;
import com.example.dat.doctor.repo.DoctorRepo;
import com.example.dat.enums.AuthProvider;
import com.example.dat.exceptions.BadRequestException;
import com.example.dat.exceptions.NotFoundException;
import com.example.dat.notification.dto.NotificationDTO;
import com.example.dat.notification.service.NotificationService;
import com.example.dat.patient.entity.Patient;
import com.example.dat.patient.repo.PatientRepo;
import com.example.dat.res.Response;
import com.example.dat.role.entity.Role;
import com.example.dat.role.repo.RoleRepo;
import com.example.dat.security.JwtService;
import com.example.dat.users.dto.LoginRequest;
import com.example.dat.users.dto.LoginResponse;
import com.example.dat.users.dto.RegistrationRequest;
import com.example.dat.users.dto.ResetPasswordRequest;
import com.example.dat.users.entity.PasswordResetCode;
import com.example.dat.users.entity.User;
import com.example.dat.users.repo.PasswordResetRepo;
import com.example.dat.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;

    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;

    private final CodeGenerator codeGenerator;
    private final PasswordResetRepo passwordResetRepo;


    @Value("${password.reset.link}")
    private String resetLink;

    @Value("${login.link}")
    private String loginLink;


    @Override
    public Response<String> register(RegistrationRequest request) {
        /// 1. Check if user already exists
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User with email already exists");
        }

        // Determine the roles to assign. Default to PATIENT if none are provided.
        List<String> requestedRoleNames = (request.getRoles() != null && !request.getRoles().isEmpty())
                ? request.getRoles().stream().map(String::toUpperCase).toList()
                : List.of("PATIENT");

        boolean isDoctor = requestedRoleNames.contains("DOCTOR");

        if (isDoctor && (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank())) {
            throw new BadRequestException("License number required to register a doctor.");
        }

        /// 2. Load and validate roles from the database
        List<Role> roles = requestedRoleNames.stream()
                .map(roleRepo::findByName)
                .flatMap(Optional::stream)
                .toList();

        if (roles.isEmpty()) {
            throw new NotFoundException("Registration failed: Requested roles were not found in the database.");
        }

        /// 3. Create and save new user entity
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .roles(roles)
                .build();

        User savedUser = userRepo.save(newUser);

        log.info("New user registered: {} with {} roles.", savedUser.getEmail(), roles.size());

        /// 4. Process Profile Creation
        for (Role role : roles) {
            String roleName = role.getName();

            switch (roleName) {
                case "PATIENT":
                    createPatientProfile(savedUser);
                    log.info("Patient profile created: {}", savedUser.getEmail());
                    break;

                case "DOCTOR":
                    createDoctorProfile(request, savedUser);
                    log.info("Doctor profile created: {}", savedUser.getEmail());
                    break;

                case "ADMIN":
                    log.info("Admin role assigned to user: {}", savedUser.getEmail());
                    break;

                default:
                    log.warn("Assigned role '{}' has no corresponding profile creation logic.", roleName);
                    break;
            }
        }

        /// 5. Send welcome email out
        sendRegistrationEmail(request, savedUser);

        // 6. Return success response
        return Response.<String>builder()
                .statusCode(200)
                .message("Registration successful. A welcome email has been sent to you.")
                .data(savedUser.getEmail())
                .build();

    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Password doesn't match");
        }

        String token = jwtService.generateToken(user.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .token(token)
                .build();

        return Response.<LoginResponse>builder()
                .statusCode(200)
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }

    @Override
    public Response<?> forgetPassword(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        passwordResetRepo.deleteByUserId(user.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .user(user)
                .code(code)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();

        passwordResetRepo.save(resetCode);


        //send email reset link out
        NotificationDTO passwordResetEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset")
                .templateVariables(Map.of(
                        "name", user.getName(),
                        "resetLink", resetLink + code
                ))
                .build();

        notificationService.sendEmail(passwordResetEmail, user);


        return Response.builder()
                .statusCode(200)
                .message("Password reset code sent to your email")
                .build();
    }

    @Override
    public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {

        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();

        log.info("CODE IS: " + code);
        log.info("NEW PASSWORD IS: " + newPassword);

        // Find and validate code
        PasswordResetCode resetCode = passwordResetRepo.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid reset code"));


        // Check expiration first
        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetRepo.delete(resetCode); // Clean up expired code
            throw new BadRequestException("Reset code has expired");
        }

        //update the password
        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // Delete the code immediately after successful use
        passwordResetRepo.delete(resetCode);

        // Send password confirmation email
        NotificationDTO passwordResetEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Updated Successfully")
                .templateName("password-update-confirmation")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();

        notificationService.sendEmail(passwordResetEmail, user);

        return Response.builder()
                .statusCode(200)
                .message("Password updated successfully")
                .build();

    }


    @Override
    @Transactional
    public Response<LoginResponse> loginRegisterByGoogleOAuth2(OAuth2AuthenticationToken authenticationToken) {

        if(authenticationToken == null){
            log.error("OAuth2AuthenticationToken is null. Cannot process login/registration.");
            return Response.<LoginResponse>builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Authentication token is missing")
                    .data(null)
                    .build();
        }
        OAuth2User oAuth2User = authenticationToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        String firstName = oAuth2User.getAttribute("given_name");

        User user = userRepo.findByEmail(email).orElse(null);

        if(user == null){

            Role defaultRole = roleRepo.findByName("PATIENT")
                    .orElseThrow(() -> new NotFoundException("PATIENT role Not Found"));

            List<Role> userRoles = List.of(defaultRole);

            assert email != null;
            User userToSave = User.builder()
                    .name(firstName)
                    .email(email.toLowerCase())
                    .roles(userRoles)
                    .authProvider(AuthProvider.GOOGLE)
                    .build();

            user = userRepo.save(userToSave);

            createPatientProfile(user);

            /// 5. Send welcome email out
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setName(user.getName());

            sendRegistrationEmail(registrationRequest, user);
        }

        String token = jwtService.generateToken(user.getEmail());

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());


        LoginResponse loginData = LoginResponse.builder()
                .token(token)
                .roles(roleNames)
                .build();


        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(loginData)
                .build();
    }


    private void createPatientProfile(User user) {
        Patient patient = Patient.builder()
                .user(user)
                .build();

        patientRepo.save(patient);
        log.info("Patient profile created");
    }

    private void createDoctorProfile(RegistrationRequest request, User user) {
        Doctor doctor = Doctor.builder()
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber())
                .user(user)
                .build();

        doctorRepo.save(doctor);
    }

    private void sendRegistrationEmail(RegistrationRequest request, User user) {

        log.info("Trying to send Email Out");

        NotificationDTO welcomeEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Welcome to DAT Health!")
                .templateName("welcome")
                .message("Thank you for registering Your account is ready.")
                .templateVariables(Map.of(
                        "name", request.getName(),
                        "loginLink", loginLink
                ))
                .build();

        notificationService.sendEmail(welcomeEmail, user);
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusHours(5);
    }
}















