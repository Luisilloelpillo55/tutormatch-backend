package com.tutormatch.ms_core.dto;

import lombok.Data;

@Data
public class MarcarAsistenciaRequestDto {
    private String estado; // "PRESENTE" | "AUSENTE"
}