package com.alura.foro_hub.domain.topico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TopicoRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 300, message = "El título no puede superar los 300 caracteres")
        String titulo,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 150, message = "El autor no puede superar los 150 caracteres")
        String autor,

        @NotBlank(message = "El curso es obligatorio")
        @Size(max = 100, message = "El curso no puede superar los 100 caracteres")
        String curso
) {
}
