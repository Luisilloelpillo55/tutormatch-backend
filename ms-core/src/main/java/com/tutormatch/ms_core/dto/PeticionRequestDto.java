package com.tutormatch.ms_core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * HU-33: datos que manda el Alumno para crear una petición.
 */
@Getter
@Setter
public class PeticionRequestDto {
    private String materia;
    private String descripcion;
}