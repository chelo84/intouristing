package com.intouristing.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Created by Marcelo Lacroix on 10/08/2019.
 */
@Getter
@Setter
@NoArgsConstructor
public class ErrorDTO {

    private LocalDateTime timestamp;
    private String path;
    private Integer status;
    private String error;
    private String message;

    public ErrorDTO(LocalDateTime now, int status, String error, String message, String path) {
        this.timestamp = now;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

}
