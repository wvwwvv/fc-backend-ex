package com.fc.fcseoularchive.error;

import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private String status;
    private String error;
    private String message;

    public ErrorResponse(String status, String error, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

}
