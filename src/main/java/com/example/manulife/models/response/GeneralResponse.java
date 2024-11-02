package com.example.manulife.models.response;

import lombok.Data;

@Data
public class GeneralResponse {
    private boolean success;
    private String message;
    private Object data;

    public GeneralResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}

