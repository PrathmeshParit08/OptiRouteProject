package com.optiroute.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String username;
    private String mobileNumber;
    private String password;
    // Email update is kept separate or disabled for now due to complexity
}
