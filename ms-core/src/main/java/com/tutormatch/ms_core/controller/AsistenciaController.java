package com.tutormatch.ms_core.controller;

import com.tutormatch.ms_core.dto.AlumnoAsistenciaDto;
import com.tutormatch.ms_core.dto.HistorialAsistenciaDto;
import com.tutormatch.ms_core.dto.MarcarAsistenciaRequestDto;
import com.tutormatch.ms_core.service.AsistenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/core")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/sesiones/{sesionId}/asistencia")
    @PreAuthorize("hasRole('ROLE_TUTOR')")
    public ResponseEntity<List<AlumnoAsistenciaDto>> listarAsistencia(
            @PathVariable UUID sesionId,
            @AuthenticationPrincipal Jwt jwt) {

        UUID tutorId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        return ResponseEntity.ok(asistenciaService.listarAsistenciaDeSesion(sesionId, tutorId));
    }

    @PutMapping("/sesiones/{sesionId}/asistencia/{alumnoId}")
    @PreAuthorize("hasRole('ROLE_TUTOR')")
    public ResponseEntity<Void> marcarAsistencia(
            @PathVariable UUID sesionId,
            @PathVariable UUID alumnoId,
            @RequestBody MarcarAsistenciaRequestDto dto,
            @AuthenticationPrincipal Jwt jwt) {

        UUID tutorId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        asistenciaService.marcarAsistencia(sesionId, alumnoId, tutorId, dto.getEstado());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/asistencia/mi-historial")
    @PreAuthorize("hasRole('ROLE_ALUMNO')")
    public ResponseEntity<List<HistorialAsistenciaDto>> miHistorial(@AuthenticationPrincipal Jwt jwt) {
        UUID alumnoId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        return ResponseEntity.ok(asistenciaService.obtenerHistorialAlumno(alumnoId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationError(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityError(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}