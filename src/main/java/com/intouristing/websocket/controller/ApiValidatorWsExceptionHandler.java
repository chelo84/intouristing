package com.intouristing.websocket.controller;

import com.intouristing.controller.handler.ApiValidatorExceptionHandler;
import com.intouristing.model.dto.ErrorWsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by Marcelo Lacroix on 17/08/2019.
 */
@Slf4j
@ControllerAdvice
@RestController
public class ApiValidatorWsExceptionHandler {

    private final String LOOKUP_DESTINATION = "lookupDestination";
    private final String STOMP_COMMAND = "stompCommand";

    private final MessageSource messageSource;

    public ApiValidatorWsExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/error")
    public final ErrorWsDTO handleWebSocketExceptions(Exception ex, Message message) {
        ErrorWsDTO errorDetails = buildErrorDetails(ex, message);

        return errorDetails;
    }

    private ErrorWsDTO buildErrorDetails(Exception ex, Message message) {
        log.error(ex.getMessage(), ex);

        return ErrorWsDTO
                .builder()
                .timestamp(LocalDateTime.now())
                .lookupDestination(
                        (String) message.getHeaders().get(LOOKUP_DESTINATION)
                )
                .message(
                        ApiValidatorExceptionHandler.getMessage(ex, messageSource)
                )
                .stompCommand(
                        Optional.ofNullable((StompCommand) message.getHeaders().get(STOMP_COMMAND))
                                .map(StompCommand::getMessageType)
                                .map(SimpMessageType::name)
                                .orElse(null)
                )
                .exception(ex.getClass().getSimpleName())
                .build();
    }

}
