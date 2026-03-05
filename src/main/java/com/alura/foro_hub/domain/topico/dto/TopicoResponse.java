package com.alura.foro_hub.domain.topico.dto;

import com.alura.foro_hub.domain.topico.EstadoTopico;
import com.alura.foro_hub.domain.topico.Topico;

import java.time.LocalDateTime;

public record TopicoResponse(

        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        EstadoTopico estado,
        String autor,
        String curso
) {
    public static TopicoResponse fromEntity(Topico topico) {
        return new TopicoResponse(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getEstado(),
                topico.getAutor(),
                topico.getCurso()
        );
        }
}
