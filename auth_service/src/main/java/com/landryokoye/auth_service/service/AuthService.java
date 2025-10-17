package com.landryokoye.auth_service.service;

import com.landryokoye.auth_service.dto.AuthResponse;
import com.landryokoye.auth_service.dto.CreateUserRequest;
import com.landryokoye.auth_service.dto.SignInRequest;
import com.landryokoye.auth_service.dto.UserDto;

public interface AuthService {

    UserDto createUser(CreateUserRequest request);
    AuthResponse GoogleSignIn(String IdToken);
    AuthResponse SignIn(SignInRequest request);

    
}
