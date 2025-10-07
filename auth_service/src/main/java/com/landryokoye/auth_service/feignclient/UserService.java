package com.landryokoye.auth_service.feignclient;

import com.landryokoye.auth_service.dto.CreateUserRequest;
import com.landryokoye.auth_service.dto.UserDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", url = "http://localhost:8081", path = "/user")
public interface UserService {

//    @Headers({
//        "Content-Type: application/json",
//        "X-Internal-Key: "
//    })
    @PostMapping("/register")
    ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request);

    @GetMapping("/email")
    ResponseEntity<UserDto> getUserByEmail(@RequestParam String email);

    @GetMapping("/username")
    ResponseEntity<UserDto> getUserByUsername(@RequestParam String username);

    @GetMapping("/google")
    ResponseEntity<UserDto> getUserByGoogle_Id(@RequestParam String google_id);
}
