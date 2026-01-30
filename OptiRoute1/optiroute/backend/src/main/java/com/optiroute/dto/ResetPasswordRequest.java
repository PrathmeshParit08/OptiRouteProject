package com.optiroute.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String securityAnswer;
    private String newPassword;
}
