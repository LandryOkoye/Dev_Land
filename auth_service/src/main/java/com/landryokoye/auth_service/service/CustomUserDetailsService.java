package com.landryokoye.auth_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.landryokoye.auth_service.dto.ApiResponse;
import com.landryokoye.auth_service.dto.UserDto;
import com.landryokoye.auth_service.exceptions.ResourceNotFoundException;
import com.landryokoye.auth_service.feignclient.UserService;
import com.landryokoye.auth_service.model.User;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = null;
        if(usernameOrEmail.contains("@gmail.com")){
            // make a request to the user-service to get the user object by email if found in the DB
            // add it to the created user object
            try {
                ResponseEntity<ApiResponse> response = userService.getUserByEmail(usernameOrEmail);
                assert response.getBody() != null;
                user = objectMapper.convertValue(response.getBody().getBody(), User.class);
                log.debug("user found in the DB as: " + user);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("User not found");
            }
        } else {
            // make a request to the user-service to get the user by username if found
            // add to user object
            try {
                ResponseEntity<ApiResponse> response = userService.getUserByUsername(usernameOrEmail);
                assert response.getBody() != null;
                user = objectMapper.convertValue(response.getBody().getBody(), User.class);
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("User not found");
            }
        }
        return user;
    }


    public UserDetails loadByGoogleId(String google_id){
        User user = null;
        // make a service request to the user service to retrieve the user object if found, then assign itthe user object created.
        try {
            ResponseEntity<ApiResponse> response = userService.getUserByGoogle_Id(google_id);
            assert response.getBody() != null;
            user = objectMapper.convertValue(response.getBody().getBody(), User.class);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    public User mapToUser(UserDto userDto){
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setSex(userDto.getSex());
        user.setRoles(userDto.getRoles());
        user.setGoogleId(userDto.getGoogle_id());
        return user;
    }

}
