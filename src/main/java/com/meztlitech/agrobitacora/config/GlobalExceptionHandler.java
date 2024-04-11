package com.meztlitech.agrobitacora.config;

import com.meztlitech.agrobitacora.dto.ErrorMessage;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@ResponseBody
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler  {

    @ExceptionHandler(value = { HttpClientErrorException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ResponseEntity<ErrorMessage> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request) {
        log.error(ex.getMessage());
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(ex.getStatusCode().value());
        error.setTimestamp(LocalDateTime.now());
        error.setMessage(ex.getMessage());
        return ResponseEntity.ok(error);
    }

    @ExceptionHandler(value = { HttpServerErrorException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ResponseEntity<ErrorMessage> handleHttpServerErrorException(HttpServerErrorException ex, WebRequest request) {
        log.error(ex.getMessage());
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(ex.getStatusCode().value());
        error.setTimestamp(LocalDateTime.now());
        error.setMessage(ex.getMessage());
        return ResponseEntity.ok(error);
    }

    @ExceptionHandler(value = { ResponseStatusException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ResponseEntity<ErrorMessage> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        log.error(ex.getMessage());
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(ex.getStatusCode().value());
        error.setTimestamp(LocalDateTime.now());
        error.setMessage(ex.getMessage());
        return ResponseEntity.ok(error);
    }

    @ExceptionHandler(value = { RuntimeException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ResponseEntity<ErrorMessage> handleResponseStatusException(RuntimeException ex, WebRequest request) {
        log.error(ex.getMessage());
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(LocalDateTime.now());
        error.setMessage(ex.getMessage());
        return ResponseEntity.ok(error);
    }

    @ExceptionHandler(value = { ExpiredJwtException.class })
    protected ResponseEntity<ErrorMessage> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        log.error(ex.getMessage());
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        error.setTimestamp(LocalDateTime.now());
        error.setMessage(ex.getMessage());
        return ResponseEntity.ok(error);
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<ErrorMessage> handleSqlException(ConstraintViolationException ex, WebRequest request) {
        log.error(ex.getMessage());
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        error.setTimestamp(LocalDateTime.now());
        if (ex.getMessage().contains("duplicate key")) {
            error.setMessage("Llave duplicada");
        } else {
            error.setMessage(ex.getMessage());
        }
        return ResponseEntity.ok(error);
    }
}