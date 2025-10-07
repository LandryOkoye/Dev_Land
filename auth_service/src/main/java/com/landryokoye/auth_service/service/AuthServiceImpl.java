package com.landryokoye.auth_service.service;

import com.landryokoye.auth_service.dto.AuthResponse;
import com.landryokoye.auth_service.dto.CreateUserRequest;
import com.landryokoye.auth_service.dto.SignInRequest;
import com.landryokoye.auth_service.dto.UserDto;
import com.landryokoye.auth_service.enums.Roles;
import com.landryokoye.auth_service.exceptions.InvalidRequestException;
import com.landryokoye.auth_service.exceptions.ResourceNotFoundException;
import com.landryokoye.auth_service.feignclient.UserService;
import com.landryokoye.auth_service.model.User;
import com.landryokoye.auth_service.security.JwtService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService{

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;

    // Feign Clients
    @Autowired
    private UserService userService;

    @Override
    public UserDto createUser(CreateUserRequest request) {

        Roles roles = request.role() != null ? request.role() : Roles.AUTHOR;
        CreateUserRequest updatedRequest = new CreateUserRequest(
                request.firstName(),
                request.lastName(),
                request.username(),
                request.email(),
                request.password(),
                request.sex(),
                roles
        );

        ResponseEntity<UserDto> response = userService.createUser(updatedRequest);

        if(response.getStatusCode().is2xxSuccessful() && response.hasBody()){
            log.info("Response has body");
            UserDto user = new UserDto();
            var responseBody = response.getBody();
            user.setId(responseBody.getId());
            user.setFirstName(responseBody.getFirstName());
            user.setLastName(responseBody.getLastName());
            user.setUsername(responseBody.getUsername());
            user.setPassword(responseBody.getPassword());
            user.setSex(responseBody.getSex());
            user.setRoles(responseBody.getRoles());
            return user;
        }
        log.debug("Error creating user");
        throw new InvalidRequestException("Error creating user, maybe invalid credentials");

    }

    @Override
    public AuthResponse GoogleSignIn(String IdToken) {
        return null;
    }

    @Override
    public AuthResponse SignIn(SignInRequest request) {
        if(request == null){
            throw new ResourceNotFoundException("Invalid Credentials");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.usernameOrEmail(),
                            request.password()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            JwtService jwt = new JwtService();
            String  jwt_token = jwtService.generateToken(authentication);
            String jwt_refresh_token = jwtService.generateRefreshToken(authentication);

            return new AuthResponse(jwt_token, jwt_refresh_token);
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e.getCause());
        }
    }


    

}
