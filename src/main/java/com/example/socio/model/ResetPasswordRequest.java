package com.example.socio.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordRequest {

    private String email;
    private String newPassword;

}