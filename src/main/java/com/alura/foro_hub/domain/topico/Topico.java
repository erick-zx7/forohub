package com.alura.foro_hub.domain.topico;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "topicos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 300)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTopico estado;

    @Column(nullable = false, length = 150)
    private String autor;

    @Column(nullable = false, length = 100)
    private String curso;

    // --- Métodos de negocio ---

    public void actualizarDatos(String titulo, String mensaje, String autor, String curso) {
        if (titulo != null && !titulo.isBlank()) this.titulo = titulo;
        if (mensaje != null && !mensaje.isBlank()) this.mensaje = mensaje;
        if (autor != null && !autor.isBlank()) this.autor = autor;
        if (curso != null && !curso.isBlank()) this.curso = curso;
    }

    public void eliminar() {
        this.estado = EstadoTopico.ELIMINADO;
    }

    public boolean estaActivo() {
        return this.estado != EstadoTopico.ELIMINADO;
    }
}
