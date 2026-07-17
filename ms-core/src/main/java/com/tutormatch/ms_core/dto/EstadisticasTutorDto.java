package com.tutormatch.ms_core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstadisticasTutorDto {
    private long sesionesImpartidas;
    private long alumnosAtendidos;
}