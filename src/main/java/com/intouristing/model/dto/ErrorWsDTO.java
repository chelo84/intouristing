package com.intouristing.model.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorWsDTO {

    private LocalDateTime timestamp;

    private String lookupDestination;

    private String message;

    private String stompCommand;

    private String exception;

}
