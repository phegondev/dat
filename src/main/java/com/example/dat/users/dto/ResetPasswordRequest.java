package com.example.dat.users.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {

    //will be used to request for forgot password
    private String email;

    //will be used to set new password
    private String code;
    private String newPassword;
}
