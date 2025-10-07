package com.landryokoye.auth_service.dto;

public record SignInRequest(
    String usernameOrEmail,
    String password
) {
    
}
