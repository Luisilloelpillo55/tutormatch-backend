package com.tutormatch.ms_notificaciones.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class NotificacionRequest {
    private UUID usuarioId;
    private String correoDestino;
    private String titulo;
    private String mensaje;
}