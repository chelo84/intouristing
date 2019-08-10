package com.intouristing.intouristing.controller.handler;

import com.intouristing.intouristing.model.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Slf4j
@ControllerAdvice
@RestController
public class ApiValidatorExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorDTO errorDetails = buildErrorDetails(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDTO buildErrorDetails(Exception ex, WebRequest request, HttpStatus httpStatus) {
        log.error(ex.getMessage(), ex);
        return new ErrorDTO(LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                ex.getMessage(),
                request.getContextPath());
    }
}