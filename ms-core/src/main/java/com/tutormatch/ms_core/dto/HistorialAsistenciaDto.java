package com.tutormatch.ms_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class HistorialAsistenciaDto {
    private UUID sesionId;
    private String tituloSesion;
    private String tutorNombre;
    private LocalDateTime fechaHora;
    private String estado; // PRESENTE | AUSENTE | PENDIENTE | NO_MARCADA
}