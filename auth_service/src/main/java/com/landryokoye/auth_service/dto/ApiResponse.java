package com.landryokoye.auth_service.dto;

public class ApiResponse {
    String status;
    Object value;

    public ApiResponse(){

    }

    public ApiResponse(String status, Object value) {
        this.status = status;
        this.value = value;
    }

    public ApiResponse(Object value){}
}
