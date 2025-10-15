package com.landryokoye.auth_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.landryokoye.auth_service.dto.*;
import com.landryokoye.auth_service.enums.Roles;
import com.landryokoye.auth_service.exceptions.InvalidRequestException;
import com.landryokoye.auth_service.exceptions.ResourceNotFoundException;
import com.landryokoye.auth_service.feignclient.UserService;
import com.landryokoye.auth_service.model.User;
import com.landryokoye.auth_service.security.JwtService;
import feign.FeignException;
import org.apache.http.HttpStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

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
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GoogleAuthService googleAuthService;

    // Feign Clients
    @Autowired
    private UserService userService;

    @Override
    public UserDto createUser(CreateUserRequest request) {
        try{
            ResponseEntity<ApiResponse> response = userService.createUser(request);
            if(response.getStatusCode().is2xxSuccessful()){
                ApiResponse apiResponse = response.getBody();
                assert apiResponse != null;
                log.info("User Account created!");

                /* Use ObjectMapper to convert the raw object to the specific type (UserDto).
                      Because the body received for the response is a linkedHashMap and cant be cast to a class like UserDto.
                 */
                UserDto userDto = objectMapper.convertValue(apiResponse.getBody(), UserDto.class);
                return userDto;
            } else if (response.getStatusCode().is4xxClientError()) {
                ApiResponse err = response.getBody();
                throw new IllegalCallerException("Registration failed: " + err);
            }else{
                throw new RuntimeException("Unknown error with status: " + response.getStatusCode());
            }

        }catch(FeignException e){
            if(e.status() == 400) {
                throw new RuntimeException("Feign Client 400 error : " + e.getMessage());
            }
            throw new RuntimeException("Feign Service Call Failed: ", e.getCause());
        }
    }

    @Override
    public AuthResponse GoogleSignIn(String id_Token) {
        Optional<GoogleIdToken.Payload> payloadOpt = googleAuthService.verify(id_Token);
        if(payloadOpt.isEmpty()){
            throw new IllegalStateException("Empty Payload");
        }
        GoogleIdToken.Payload payload = payloadOpt.get();
        String firstName = payload.get("givenName").toString();
        String lastName = payload.get("familyName").toString();
        String sex = payload.get("gender").toString();
        String email = payload.getEmail();
        String profileImg = payload.get("picture").toString();
        String uid = payload.getSubject();
        String username = payload.get("name").toString();

        ResponseEntity<ApiResponse> response = userService.getUserByEmail(email);
        ApiResponse body;
        User user = new User();

        if(response.getStatusCode().is2xxSuccessful() && response.hasBody()){
            assert response.getBody() != null;
            user = objectMapper.convertValue(response.getBody().getBody(), User.class );
        }else {

            CreateUserRequest request = new CreateUserRequest(
                    firstName,
                    lastName,
                    username,
                    email,
                    null,
                    sex,
                    Roles.AUTHOR,
                    uid
            );

            ResponseEntity<ApiResponse> apiResponse = userService.createUser(request);
            if(apiResponse.hasBody() && apiResponse.getStatusCode().is2xxSuccessful()){
                user = objectMapper.convertValue(apiResponse.getBody().getBody(), User.class);
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null
                )
        );
        log.debug("Authentication Done");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Context set");
        JwtService jwt = new JwtService();
        String  jwt_token = jwtService.generateToken(authentication);
        String jwt_refresh_token = jwtService.generateRefreshToken(authentication);
        log.debug("Jwt Token generated");
        return new AuthResponse(jwt_token, jwt_refresh_token);
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
            log.debug("Authentication Done");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Context set");
            JwtService jwt = new JwtService();
            String  jwt_token = jwtService.generateToken(authentication);
            String jwt_refresh_token = jwtService.generateRefreshToken(authentication);
            log.debug("Jwt Token generated");
            return new AuthResponse(jwt_token, jwt_refresh_token);
        } catch (AuthenticationException e) {
            throw new IllegalStateException(e.getCause());
        }
    }


    

}
