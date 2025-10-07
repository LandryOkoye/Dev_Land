package com.landryokoye.auth_service.service;

import com.landryokoye.auth_service.dto.UserDto;
import com.landryokoye.auth_service.exceptions.ResourceNotFoundException;
import com.landryokoye.auth_service.feignclient.UserService;
import com.landryokoye.auth_service.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    @Autowired
    private UserService userService;
//    @Autowired
//    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = null;
        if(usernameOrEmail.contains("@gmail.com")){
            // make a request to the user-service to get the user object by email if found in the DB
            // add it to the created user object
            try {
                ResponseEntity<UserDto> userDto = userService.getUserByEmail(usernameOrEmail);
                assert userDto.getBody() != null;
                user = mapToUser(userDto.getBody());
            } catch (ResourceNotFoundException e) {
                throw new ResourceNotFoundException("User not found");
            }
        } else {
            // make a request to the user-service to get the user by username if found
            // add to user object
            try {
                ResponseEntity<UserDto> userDto = userService.getUserByUsername(usernameOrEmail);
                assert userDto.getBody() != null;
                user = mapToUser(userDto.getBody());
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
            ResponseEntity<UserDto> userDto = userService.getUserByGoogle_Id(google_id);
            assert userDto.getBody() != null;
            user = mapToUser(userDto.getBody());
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
