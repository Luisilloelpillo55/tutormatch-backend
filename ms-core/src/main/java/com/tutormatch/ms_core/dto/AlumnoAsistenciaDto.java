package com.tutormatch.ms_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AlumnoAsistenciaDto {
    private UUID alumnoId;
    private UUID inscripcionId;
    private String nombreCompleto;
    private String correo;
    private String estado; // PRESENTE | AUSENTE | PENDIENTE | NO_MARCADA
    private boolean puedeMarcar;
    private LocalDateTime marcadoEn;
}