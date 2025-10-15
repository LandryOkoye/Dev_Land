package com.landryokoye.auth_service.dto;

public class ApiResponse {
    String message;
    Object body;

    public ApiResponse(){}

    public ApiResponse(String message, Object body) {
        this.message = message;
        this.body = body;
    }

    public ApiResponse(Object body){
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
