package com.tutormatch.ms_usuarios.dto;

import lombok.Data;

@Data
public class RegistroDto {
    private String nombre;
    private String email;
    private String password;
}