package com.alura.foro_hub.domain.topico.dto;

import jakarta.validation.constraints.Size;

public record TopicoUpdateRequest(

        @Size(max = 300, message = "El título no puede superar los 300 caracteres")
        String titulo,

        String mensaje,

        @Size(max = 150, message = "El autor no puede superar los 150 caracteres")
        String autor,

        @Size(max = 100, message = "El curso no puede superar los 100 caracteres")
        String curso
) {
}
