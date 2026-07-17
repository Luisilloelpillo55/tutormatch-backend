package com.tutormatch.ms_core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "asistencias", schema = "schema_core")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "sesion_id", nullable = false)
    private UUID sesionId;

    @Column(name = "alumno_id", nullable = false)
    private UUID alumnoId;

    /** Estado: PRESENTE | AUSENTE */
    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "marcado_en")
    private LocalDateTime marcadoEn;

    @Column(name = "creado_en", insertable = false, updatable = false)
    private LocalDateTime creadoEn;
}