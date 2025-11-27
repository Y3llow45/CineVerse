package com.example.CineVerse.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class TodoApiException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;
}
