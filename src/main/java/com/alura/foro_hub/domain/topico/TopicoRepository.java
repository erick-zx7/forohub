package com.alura.foro_hub.domain.topico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {

    boolean existsByTituloAndMensajeAndEstadoNot(
            String titulo,
            String mensaje,
            EstadoTopico estado
    );

    Page<Topico> findAllByEstadoNot(EstadoTopico estado, Pageable pageable);
}
