package com.example.dat.users.controller;

import com.example.dat.res.Response;
import com.example.dat.users.dto.LoginRequest;
import com.example.dat.users.dto.LoginResponse;
import com.example.dat.users.dto.RegistrationRequest;
import com.example.dat.users.dto.ResetPasswordRequest;
import com.example.dat.users.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String >> register(@RequestBody @Valid RegistrationRequest registrationRequest){
        return ResponseEntity.ok(authService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response<?>> forgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(authService.forgetPassword(resetPasswordRequest.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        return ResponseEntity.ok(authService.updatePasswordViaResetCode(resetPasswordRequest));
    }

    @GetMapping("/login/google")
    public void startGoogleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }


}
