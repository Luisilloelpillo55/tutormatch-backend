package com.tutormatch.ms_usuarios.dto;

import lombok.Data;

import java.util.UUID;

// HU-06: fila de la tabla de solicitudes pendientes que ve el Administrador.
// Incluimos el id porque HU-07 (Aprobar/Rechazar) lo va a necesitar para
// saber sobre qué usuario actuar.
@Data
public class SolicitudPendienteDto {
    private UUID id;
    private String nombre;
    private String email;
    private String justificacion;
}