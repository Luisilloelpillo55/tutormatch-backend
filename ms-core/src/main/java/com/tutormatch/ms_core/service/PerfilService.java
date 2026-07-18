package com.tutormatch.ms_core.service;

import com.tutormatch.ms_core.dto.EstadisticasAlumnoDto;
import com.tutormatch.ms_core.dto.EstadisticasTutorDto;
import com.tutormatch.ms_core.dto.SesionHistorialDto;
import com.tutormatch.ms_core.entity.Inscripcion;
import com.tutormatch.ms_core.entity.Sesion;
import com.tutormatch.ms_core.repository.InscripcionRepository;
import com.tutormatch.ms_core.repository.SesionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PerfilService {

    private static final String SESION_ACTIVA = "ACTIVA";
    private static final String INSCRIPCION_CONFIRMADA = "CONFIRMADA";

    private final SesionRepository sesionRepository;
    private final InscripcionRepository inscripcionRepository;

    public PerfilService(SesionRepository sesionRepository, InscripcionRepository inscripcionRepository) {
        this.sesionRepository = sesionRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    // =========================================================================
    // TUTOR: historial de sesiones pasadas
    // =========================================================================
    @Transactional(readOnly = true)
    public List<SesionHistorialDto> obtenerHistorialTutor(UUID tutorId) {
        LocalDateTime ahora = LocalDateTime.now();

        List<Sesion> pasadas = sesionRepository
                .findByTutorIdAndFechaHoraBeforeOrderByFechaHoraDesc(tutorId, ahora);

        return pasadas.stream().map(sesion -> {
            long inscritos = inscripcionRepository
                    .countBySesionIdAndEstado(sesion.getId(), INSCRIPCION_CONFIRMADA);

            boolean ventanaAbierta = ahora.isBefore(sesion.getFechaHora().plusDays(1));

            return new SesionHistorialDto(
                    sesion.getId(),
                    sesion.getTitulo(),
                    sesion.getDescripcion(),
                    sesion.getLugar(),
                    sesion.getFechaHora(),
                    sesion.getCupoMaximo(),
                    (int) inscritos,
                    sesion.getEstado(),
                    ventanaAbierta);
        }).collect(Collectors.toList());
    }

    // =========================================================================
    // TUTOR: estadísticas (sesiones impartidas + alumnos atendidos)
    // =========================================================================
    @Transactional(readOnly = true)
    public EstadisticasTutorDto obtenerEstadisticasTutor(UUID tutorId) {
        LocalDateTime ahora = LocalDateTime.now();

        List<Sesion> pasadas = sesionRepository
                .findByTutorIdAndFechaHoraBeforeOrderByFechaHoraDesc(tutorId, ahora);

        List<Sesion> impartidas = pasadas.stream()
                .filter(s -> SESION_ACTIVA.equals(s.getEstado()))
                .collect(Collectors.toList());

        Set<UUID> alumnosUnicos = impartidas.stream()
                .flatMap(s -> inscripcionRepository.findBySesionId(s.getId()).stream())
                .filter(i -> INSCRIPCION_CONFIRMADA.equals(i.getEstado()))
                .map(Inscripcion::getAlumnoId)
                .collect(Collectors.toSet());

        return new EstadisticasTutorDto(impartidas.size(), alumnosUnicos.size());
    }

    // =========================================================================
    // ALUMNO: estadísticas (tutorías recibidas)
    // =========================================================================
    @Transactional(readOnly = true)
    public EstadisticasAlumnoDto obtenerEstadisticasAlumno(UUID alumnoId) {
        LocalDateTime ahora = LocalDateTime.now();

        List<Inscripcion> confirmadas = inscripcionRepository
                .findByAlumnoIdAndEstado(alumnoId, INSCRIPCION_CONFIRMADA);

        long tutoriasRecibidas = confirmadas.stream()
                .map(i -> sesionRepository.findById(i.getSesionId()).orElse(null))
                .filter(s -> s != null && s.getFechaHora().isBefore(ahora))
                .count();

        return new EstadisticasAlumnoDto(tutoriasRecibidas);
    }
}