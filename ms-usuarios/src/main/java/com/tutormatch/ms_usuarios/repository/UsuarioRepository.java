package com.tutormatch.ms_usuarios.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tutormatch.ms_usuarios.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);

    // HU-06: lista de usuarios con una solicitud de tutor en un estado dado (ej. "pendiente")
    List<Usuario> findByEstadoSolicitud(String estadoSolicitud);
}