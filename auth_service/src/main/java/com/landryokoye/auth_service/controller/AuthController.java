package com.landryokoye.auth_service.controller;

import com.landryokoye.auth_service.dto.*;
import com.landryokoye.auth_service.exceptions.InvalidRequestException;
import com.landryokoye.auth_service.exceptions.ResourceNotFoundException;
import com.landryokoye.auth_service.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@RestController
//@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    ResponseEntity<ApiResponse> RegisterUser(@RequestBody CreateUserRequest request){
        try {
            if(request != null){
                UserDto user = authService.createUser(request);
                ResponseEntity.status(OK).body(new ApiResponse("Success", user));
            }
            log.debug("Invalid credentials");
            throw new InvalidRequestException("Invalid credentials");
        } catch (InvalidRequestException e) {
            log.debug("Not creating user cause: " + e.getStackTrace(), e.getCause());
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(BAD_REQUEST));
        }finally {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/login")
    ResponseEntity<ApiResponse> login(@RequestBody SignInRequest request){
        try{
            AuthResponse response = authService.SignIn(request);
            if(response == null && !response.getToken().isEmpty()){
                return ResponseEntity.status(OK).body(new ApiResponse("Success", response));
            }
        } catch (ResourceNotFoundException  | IllegalStateException e) {
            return ResponseEntity.status(EXPECTATION_FAILED).body(new ApiResponse(EXPECTATION_FAILED));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/oauth2/google")
    ResponseEntity<ApiResponse> GoogleSignIn(String id_token){
        //
        return null;
    }

    @PostMapping("/logout") // this endpoint will ba called by the User-service to log out a user.
    ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token){
        // Use a in-memory cache or a cacher like redis to blacklist the user token with TTL
        return null;
    }
}
