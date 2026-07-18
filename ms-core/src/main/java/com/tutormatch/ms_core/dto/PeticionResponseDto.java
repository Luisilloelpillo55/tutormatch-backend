package com.tutormatch.ms_core.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * HU-34: tarjeta del tablero de peticiones.
 * Incluye "propia" para que el front decida sin adivinar si debe mostrar
 * el botón Eliminar (HU-35), comparando contra el usuario_id del JWT.
 */
@Getter
@Setter
@AllArgsConstructor
public class PeticionResponseDto {
    private UUID id;
    private UUID alumnoId;
    private String alumnoNombre;
    private String materia;
    private String descripcion;
    private String estado;
    private UUID tutorAtendioId;
    private String tutorAtendioNombre;
    private LocalDateTime creadoEn;
    private boolean propia;
}