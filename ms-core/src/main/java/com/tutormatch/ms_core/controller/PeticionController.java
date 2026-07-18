package com.tutormatch.ms_core.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutormatch.ms_core.dto.PeticionRequestDto;
import com.tutormatch.ms_core.dto.PeticionResponseDto;
import com.tutormatch.ms_core.service.PeticionService;

/**
 * EP-09: Tablero de Peticiones (HU-33 a HU-36).
 *
 * NOTA: aquí usamos hasRole('ALUMNO') / hasRole('TUTOR') SIN el prefijo ROLE_.
 * Spring Security le agrega el prefijo "ROLE_" automáticamente dentro de
 * hasRole(...), y nuestro JwtAuthenticationConverter ya entrega las
 * autoridades con el prefijo incluido (ej. "ROLE_TUTOR"). Poner
 * hasRole('ROLE_TUTOR') terminaría buscando la autoridad "ROLE_ROLE_TUTOR",
 * que nunca existe, y el endpoint quedaría inaccesible para todos.
 */
@RestController
@RequestMapping("/api/core/peticiones")
public class PeticionController {

    private final PeticionService peticionService;

    public PeticionController(PeticionService peticionService) {
        this.peticionService = peticionService;
    }

    // -----------------------------------------------------------------------
    // HU-33: POST — Crear una petición (solo Alumno)
    // -----------------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<PeticionResponseDto> crearPeticion(
            @RequestBody PeticionRequestDto dto,
            @AuthenticationPrincipal Jwt jwt) {

        UUID alumnoId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        String alumnoNombre = jwt.getClaimAsString("nombre");

        PeticionResponseDto creada = peticionService.crearPeticion(dto, alumnoId, alumnoNombre);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // -----------------------------------------------------------------------
    // HU-34: GET — Tablero general (cualquier usuario logueado)
    // -----------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<PeticionResponseDto>> listarTablero(@AuthenticationPrincipal Jwt jwt) {
        UUID usuarioActualId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        return ResponseEntity.ok(peticionService.listarTablero(usuarioActualId));
    }

    // -----------------------------------------------------------------------
    // HU-35: DELETE — Eliminar (lógicamente) una petición propia
    // -----------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPeticion(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        UUID alumnoId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        peticionService.eliminarPeticion(id, alumnoId);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------------------
    // HU-36: PATCH — Marcar como atendida (solo Tutor)
    // -----------------------------------------------------------------------
    @PatchMapping("/{id}/atender")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<PeticionResponseDto> marcarComoAtendida(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        UUID tutorId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        String tutorNombre = jwt.getClaimAsString("nombre");

        return ResponseEntity.ok(peticionService.marcarComoAtendida(id, tutorId, tutorNombre));
    }

    // -----------------------------------------------------------------------
    // Manejo de errores
    // -----------------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationError(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityError(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}