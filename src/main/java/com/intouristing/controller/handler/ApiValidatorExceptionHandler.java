package com.intouristing.controller.handler;

import com.intouristing.exceptions.RootException;
import com.intouristing.model.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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

    private static final String UNEXPECTED_ERROR = "Exception.unexpected";

    private final MessageSource messageSource;

    public ApiValidatorExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static String getMessage(Exception ex, MessageSource messageSource) {
        String message;
        try {
            RootException rootEx = (RootException) ex;
            if (ArrayUtils.isNotEmpty(rootEx.getParams())) {
                message = messageSource.getMessage(ex.getMessage(), rootEx.getParams(), LocaleContextHolder.getLocale());
            } else {
                message = messageSource.getMessage(new DefaultMessageSourceResolvable(rootEx.getMessage()), LocaleContextHolder.getLocale());
            }
        } catch (NoSuchMessageException | ClassCastException e) {
            message = messageSource.getMessage(new DefaultMessageSourceResolvable(UNEXPECTED_ERROR), LocaleContextHolder.getLocale());
        }
        return message;
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorDTO errorDetails = buildErrorDetails(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDTO buildErrorDetails(Exception ex, WebRequest request, HttpStatus httpStatus) {
        log.error(ex.getMessage(), ex);
        String message = getMessage(ex, messageSource);
        return new ErrorDTO(LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                request.getContextPath());
    }
}