package com.tutormatch.ms_core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * EP-09: Petición de un Alumno pidiendo apoyo en una materia/tema que no
 * encontró en el catálogo. El resto de la comunidad la ve en un tablero.
 */
@Entity
@Table(name = "peticiones", schema = "schema_core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Peticion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // El alumno_id se extrae del JWT, no es FK física (mismo patrón que Sesion.tutorId)
    @Column(name = "alumno_id", nullable = false)
    private UUID alumnoId;

    /** Nombre del alumno solicitante, tomado del claim "nombre" del JWT al crear la petición. */
    @Column(name = "alumno_nombre", length = 255, nullable = false)
    private String alumnoNombre;

    @Column(name = "materia", nullable = false, length = 150)
    private String materia;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    /**
     * Estado lógico de la petición.
     * Valores posibles (en minúsculas, columna VARCHAR — no enum de Postgres):
     * "activa" | "atendida" | "eliminada"
     * HU-35: al eliminar se hace borrado lógico (estado = "eliminada"), no se borra la fila.
     */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "activa";

    // HU-36: datos del tutor que marcó la petición como atendida (null hasta entonces)
    @Column(name = "tutor_atendio_id")
    private UUID tutorAtendioId;

    @Column(name = "tutor_atendio_nombre", length = 255)
    private String tutorAtendioNombre;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "activa";
        }
    }
}