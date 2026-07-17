package com.tutormatch.ms_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SesionHistorialDto {
    private UUID id;
    private String titulo;
    private String descripcion;
    private String lugar;
    private LocalDateTime fechaHora;
    private Integer cupoMaximo;
    private Integer inscritos;
    private String estado;
    /** true si aún está dentro de la ventana de 24h para tomar/editar asistencia */
    private boolean ventanaAsistenciaAbierta;
}