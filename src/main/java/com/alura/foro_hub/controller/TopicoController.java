package com.alura.foro_hub.controller;

import com.alura.foro_hub.domain.topico.TopicoService;
import com.alura.foro_hub.domain.topico.dto.TopicoRequest;
import com.alura.foro_hub.domain.topico.dto.TopicoResponse;
import com.alura.foro_hub.domain.topico.dto.TopicoUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topicos")
@RequiredArgsConstructor
public class TopicoController {

    private final TopicoService service;

    //Crear
    @PostMapping
    public ResponseEntity<TopicoResponse> crear(
            @RequestBody @Valid TopicoRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        TopicoResponse response = service.crear(request);

        URI uri = uriBuilder
                .path("/topicos/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    //Listar
    @GetMapping
    public ResponseEntity<Page<TopicoResponse>> listarTodos(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    //Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    //Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<TopicoResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid TopicoUpdateRequest request
    ) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    //Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
