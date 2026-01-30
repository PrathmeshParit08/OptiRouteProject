package com.optiroute.service;

import com.optiroute.dto.JwtResponse;
import com.optiroute.dto.LoginRequest;
import com.optiroute.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private com.optiroute.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        System.out.println("Login attempt for email: " + loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            System.out.println("Login successful for: " + loginRequest.getUsername());
            return new JwtResponse(jwt);
        } catch (Exception e) {
            System.err.println("Login failed for " + loginRequest.getUsername() + ": " + e.getMessage());
            throw e;
        }
    }

    public String registerUser(com.optiroute.dto.RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        com.optiroute.model.User user = new com.optiroute.model.User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setMobileNumber(registerRequest.getMobileNumber());
        user.setSecurityQuestion(registerRequest.getSecurityQuestion());
        user.setSecurityAnswer(registerRequest.getSecurityAnswer());

        userRepository.save(user);

        return "User registered successfully!";
    }

    public String getSecurityQuestion(String email) {
        com.optiroute.model.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Error: Email not found!"));
        return user.getSecurityQuestion();
    }

    public String resetPassword(com.optiroute.dto.ResetPasswordRequest request) {
        com.optiroute.model.User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: Email not found!"));

        if (!user.getSecurityAnswer().equalsIgnoreCase(request.getSecurityAnswer())) {
            throw new RuntimeException("Error: Incorrect security answer!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Password reset successfully!";
    }
}
