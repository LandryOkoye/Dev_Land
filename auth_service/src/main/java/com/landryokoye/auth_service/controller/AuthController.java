package com.landryokoye.auth_service.controller;

import com.landryokoye.auth_service.dto.*;
import com.landryokoye.auth_service.exceptions.InvalidRequestException;
import com.landryokoye.auth_service.exceptions.ResourceNotFoundException;
import com.landryokoye.auth_service.feignclient.UserService;
import com.landryokoye.auth_service.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @GetMapping("/home")
    ResponseEntity<?> checking(@RequestParam String name){
        return ResponseEntity.ok(name + " This Api is working in good health, fucking idiot");
    }

    @PostMapping("/register")
    ResponseEntity<ApiResponse> RegisterUser(@RequestBody CreateUserRequest request){
        try {
            if(request != null){
                UserDto user = authService.createUser(request);
                return ResponseEntity.ok(new ApiResponse("Success", user));
            }
            log.debug("Invalid credentials");
            throw new InvalidRequestException("Invalid credentials");
        } catch (InvalidRequestException e) {
            log.debug("Not creating user cause: " + e.getStackTrace(), e.getCause());
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(BAD_REQUEST));
        }catch(RuntimeException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getCause()));
        }

    }

    @PostMapping("/login")
    ResponseEntity<ApiResponse> login(@RequestBody SignInRequest request){
        try{
            AuthResponse response = authService.SignIn(request);
            if(response != null){
                return ResponseEntity.status(OK).body(new ApiResponse("Success", response));
            }
        } catch (ResourceNotFoundException  | IllegalStateException e) {
            return ResponseEntity.status(EXPECTATION_FAILED).body(new ApiResponse(EXPECTATION_FAILED));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(INTERNAL_SERVER_ERROR));
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
