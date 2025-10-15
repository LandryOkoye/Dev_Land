package com.landryokoye.auth_service.service;

import com.landryokoye.auth_service.dto.*;

public interface AuthService {

    UserDto createUser(CreateUserRequest request);
    AuthResponse GoogleSignIn(String IdToken);
    AuthResponse SignIn(SignInRequest request);

    
}
