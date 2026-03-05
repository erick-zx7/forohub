package com.alura.foro_hub.domain.topico;

import com.alura.foro_hub.domain.topico.dto.TopicoRequest;
import com.alura.foro_hub.domain.topico.dto.TopicoResponse;
import com.alura.foro_hub.domain.topico.dto.TopicoUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicoService {

    private final TopicoRepository repository;

    //Crear
    @Transactional
    public TopicoResponse crear(TopicoRequest request) {
        boolean duplicado = repository.existsByTituloAndMensajeAndEstadoNot(
                request.titulo(),
                request.mensaje(),
                EstadoTopico.ELIMINADO
        );

        if (duplicado) {
            throw new RuntimeException(
                    "Ya existe un tópico activo con el mismo título y mensaje"
            );
        }

        Topico topico = Topico.builder()
                .titulo(request.titulo())
                .mensaje(request.mensaje())
                .autor(request.autor())
                .curso(request.curso())
                .estado(EstadoTopico.ABIERTO)
                .build();

        repository.save(topico);
        return TopicoResponse.fromEntity(topico);
    }

    //Listar todos
    @Transactional(readOnly = true)
    public Page<TopicoResponse> listarTodos(Pageable pageable) {
        return repository
                .findAllByEstadoNot(EstadoTopico.ELIMINADO, pageable)
                .map(TopicoResponse::fromEntity);
    }

    //Buscar por id
    @Transactional(readOnly = true)
    public TopicoResponse buscarPorId(Long id) {
        Topico topico = obtenerTopicoActivo(id);
        return TopicoResponse.fromEntity(topico);
    }

    //Actualizar
    @Transactional
    public TopicoResponse actualizar(Long id, TopicoUpdateRequest request) {
        Topico topico = obtenerTopicoActivo(id);
        topico.actualizarDatos(
                request.titulo(),
                request.mensaje(),
                request.autor(),
                request.curso()
        );
        return TopicoResponse.fromEntity(topico);
    }

    //Eliminar
    @Transactional
    public void eliminar(Long id) {
        Topico topico = obtenerTopicoActivo(id);
        topico.eliminar();
    }

    //Obtener topico activo por id
    private Topico obtenerTopicoActivo(Long id) {
        Topico topico = repository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Tópico no encontrado con id: " + id
                ));

        if (!topico.estaActivo()) {
            throw new RuntimeException(
                    "El tópico con id " + id + " fue eliminado"
            );
        }

        return topico;
    }
}
