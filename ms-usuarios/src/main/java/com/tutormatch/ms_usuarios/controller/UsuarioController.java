package com.tutormatch.ms_usuarios.controller;

import com.tutormatch.ms_usuarios.dto.RegistroDto;
import com.tutormatch.ms_usuarios.entity.Usuario;
import com.tutormatch.ms_usuarios.service.UsuarioService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroDto dto) {
        try {
            service.registrar(dto);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> obtenerUsuario(@PathVariable UUID id) {
        Usuario usuario = service.obtenerPorId(id);

        Map<String, String> response = new HashMap<>();

        response.put("email", usuario.getEmail());
        response.put("nombre", usuario.getNombre());

        return ResponseEntity.ok(response);
    }

}