package com.optiroute.dto;

public class JwtResponse {
    private String token;

    // No-args constructor
    public JwtResponse() {
    }

    // All-args constructor (Fixes the UserService error)
    public JwtResponse(String token) {
        this.token = token;
    }

    // Getter
    public String getToken() {
        return token;
    }

    // Setter
    public void setToken(String token) {
        this.token = token;
    }
}