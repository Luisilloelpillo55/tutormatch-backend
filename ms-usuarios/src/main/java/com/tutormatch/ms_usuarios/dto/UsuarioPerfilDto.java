package com.tutormatch.ms_usuarios.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UsuarioPerfilDto {
    private UUID id;
    private String nombre;
    private String email;
    private String estadoSolicitud;
    private List<String> roles;
}
