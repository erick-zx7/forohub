package com.alura.foro_hub.infra.exception;

import java.util.List;

public record ValidationErrorResponse(
        int status,
        String error,
        String mensaje,
        String path,
        List<FieldErrorDetail> errores
) {
    public record FieldErrorDetail(
            String campo,
            String mensaje
    ) {}
}
