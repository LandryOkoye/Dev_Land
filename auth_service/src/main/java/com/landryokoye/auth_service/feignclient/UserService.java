package com.landryokoye.auth_service.feignclient;

import com.landryokoye.auth_service.dto.ApiResponse;
import com.landryokoye.auth_service.dto.CreateUserRequest;
import com.landryokoye.auth_service.dto.UserDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", url = "http://localhost:8081/user")
public interface UserService {

//    @Headers({
//        "Content-Type: application/json",
//        "X-Internal-Key: "
//    })
    @PostMapping("/register")
    ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request);

    @GetMapping("/email")
    ResponseEntity<ApiResponse> getUserByEmail(@RequestParam String email);

    @GetMapping("/username")
    ResponseEntity<ApiResponse> getUserByUsername(@RequestParam String username);

    @GetMapping("/google")
    ResponseEntity<ApiResponse> getUserByGoogle_Id(@RequestParam String google_id);
}
