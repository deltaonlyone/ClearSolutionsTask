package org.example.clearsolutiontask.controller.advice;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.example.clearsolutiontask.dto.ErrorDto;
import org.example.clearsolutiontask.exception.ServiceException;
import org.example.clearsolutiontask.exception.UserNotFoundException;
import org.example.clearsolutiontask.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto handleValidationExceptions(MethodArgumentNotValidException ex) {
        return new ErrorDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorDto handleConstraintViolationExceptions(ConstraintViolationException ex) {
        return new ErrorDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorDto handleValidationExceptions(ValidationException ex) {
        log.warn("Handled ValidationException: {}", ex.getMessage());
        return new ErrorDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorDto handleUserExceptions(UserNotFoundException ex) {
        log.warn("Handled UserNotFoundException: {}", ex.getMessage());
        return new ErrorDto(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    public ErrorDto handleServiceExceptions(ServiceException ex) {
        log.error("Handled ServiceException: {}", ex.getMessage());
        return new ErrorDto(ex.getMessage());
    }
}
