package com.tutormatch.ms_core.controller;

import com.tutormatch.ms_core.dto.EstadisticasAlumnoDto;
import com.tutormatch.ms_core.dto.EstadisticasTutorDto;
import com.tutormatch.ms_core.dto.SesionHistorialDto;
import com.tutormatch.ms_core.service.PerfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/core/perfil")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping("/historial-tutor")
    @PreAuthorize("hasRole('ROLE_TUTOR')")
    public ResponseEntity<List<SesionHistorialDto>> historialTutor(@AuthenticationPrincipal Jwt jwt) {
        UUID tutorId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        return ResponseEntity.ok(perfilService.obtenerHistorialTutor(tutorId));
    }

    @GetMapping("/estadisticas-tutor")
    @PreAuthorize("hasRole('ROLE_TUTOR')")
    public ResponseEntity<EstadisticasTutorDto> estadisticasTutor(@AuthenticationPrincipal Jwt jwt) {
        UUID tutorId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        return ResponseEntity.ok(perfilService.obtenerEstadisticasTutor(tutorId));
    }

    @GetMapping("/estadisticas-alumno")
    @PreAuthorize("hasRole('ROLE_ALUMNO')")
    public ResponseEntity<EstadisticasAlumnoDto> estadisticasAlumno(@AuthenticationPrincipal Jwt jwt) {
        UUID alumnoId = UUID.fromString(jwt.getClaimAsString("usuario_id"));
        return ResponseEntity.ok(perfilService.obtenerEstadisticasAlumno(alumnoId));
    }
}