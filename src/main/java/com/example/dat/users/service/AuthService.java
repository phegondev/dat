package com.example.dat.users.service;

import com.example.dat.res.Response;
import com.example.dat.users.dto.LoginRequest;
import com.example.dat.users.dto.LoginResponse;
import com.example.dat.users.dto.RegistrationRequest;
import com.example.dat.users.dto.ResetPasswordRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface AuthService {

    Response<String> register(RegistrationRequest request);

    Response<LoginResponse> login(LoginRequest loginRequest);

    Response<?> forgetPassword(String email);

    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);


    Response<LoginResponse> loginRegisterByGoogleOAuth2(OAuth2AuthenticationToken authenticationToken);

}
