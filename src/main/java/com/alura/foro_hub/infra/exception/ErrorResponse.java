package com.alura.foro_hub.infra.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        String mensaje,
        String path,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(
            int status,
            String error,
            String mensaje,
            String path
    ) {
        return new ErrorResponse(
                status,
                error,
                mensaje,
                path,
                LocalDateTime.now()
        );
    }
}
