package com.tutormatch.ms_notificaciones.controller;

import com.tutormatch.ms_notificaciones.dto.NotificacionRequest;
import com.tutormatch.ms_notificaciones.entity.Notificacion;
import com.tutormatch.ms_notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    // Endpoint para disparar alertas/notificaciones
    @PostMapping("/enviar")
    public ResponseEntity<String> enviarNotificacion(@RequestBody NotificacionRequest request) {
        notificacionService.procesarNotificacion(request);
        return ResponseEntity.ok("Notificación procesada y enviada correctamente.");
    }

    // Endpoint utilizado para obtener el historial de notificaciones de un usuario
    @GetMapping("/historial/{usuarioId}")
    public ResponseEntity<List<Notificacion>> obtenerHistorial(@PathVariable UUID usuarioId) {
        List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesNoLeidas(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

    // Endpoint para marcar como leída
    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable UUID id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok().build();
    }
}