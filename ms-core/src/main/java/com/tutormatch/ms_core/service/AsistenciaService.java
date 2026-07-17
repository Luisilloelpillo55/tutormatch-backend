package com.tutormatch.ms_core.service;

import com.tutormatch.ms_core.client.UsuarioClient;
import com.tutormatch.ms_core.dto.AlumnoAsistenciaDto;
import com.tutormatch.ms_core.dto.HistorialAsistenciaDto;
import com.tutormatch.ms_core.dto.UsuarioResponseDto;
import com.tutormatch.ms_core.entity.Asistencia;
import com.tutormatch.ms_core.entity.Inscripcion;
import com.tutormatch.ms_core.entity.Sesion;
import com.tutormatch.ms_core.repository.AsistenciaRepository;
import com.tutormatch.ms_core.repository.InscripcionRepository;
import com.tutormatch.ms_core.repository.SesionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AsistenciaService {

    private static final String INSCRIPCION_CONFIRMADA = "CONFIRMADA";
    private static final String PRESENTE = "PRESENTE";
    private static final String AUSENTE = "AUSENTE";
    private static final String PENDIENTE = "PENDIENTE";     // dentro de la ventana, sin marcar
    private static final String NO_MARCADA = "NO_MARCADA";   // ventana cerrada, nunca se marcó (gris)

    private final AsistenciaRepository asistenciaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final SesionRepository sesionRepository;
    private final UsuarioClient usuarioClient;

    public AsistenciaService(AsistenciaRepository asistenciaRepository,
            InscripcionRepository inscripcionRepository,
            SesionRepository sesionRepository,
            UsuarioClient usuarioClient) {
        this.asistenciaRepository = asistenciaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.sesionRepository = sesionRepository;
        this.usuarioClient = usuarioClient;
    }

    /** Ventana de marcado: desde que inicia la sesión hasta 24 horas después. */
    private boolean dentroDeVentana(Sesion sesion) {
        LocalDateTime ahora = LocalDateTime.now();
        return !ahora.isBefore(sesion.getFechaHora()) && ahora.isBefore(sesion.getFechaHora().plusDays(1));
    }

    private boolean yaExpiro(Sesion sesion) {
        return LocalDateTime.now().isAfter(sesion.getFechaHora().plusDays(1));
    }

    // =========================================================================
    // TUTOR: lista de alumnos inscritos + su estado de asistencia
    // =========================================================================
    @Transactional(readOnly = true)
    public List<AlumnoAsistenciaDto> listarAsistenciaDeSesion(UUID sesionId, UUID tutorId) {
        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada."));

        if (!sesion.getTutorId().equals(tutorId)) {
            throw new SecurityException("Solo el tutor que creó la sesión puede ver/marcar la asistencia.");
        }

        List<Inscripcion> inscritos = inscripcionRepository.findBySesionId(sesionId).stream()
                .filter(i -> INSCRIPCION_CONFIRMADA.equals(i.getEstado()))
                .collect(Collectors.toList());

        boolean puedeMarcarAhora = dentroDeVentana(sesion);
        boolean expiro = yaExpiro(sesion);

        return inscritos.stream().map(insc -> {
            Asistencia asistencia = asistenciaRepository
                    .findBySesionIdAndAlumnoId(sesionId, insc.getAlumnoId())
                    .orElse(null);

            String estado;
            if (asistencia != null) {
                estado = asistencia.getEstado();
            } else if (expiro) {
                estado = NO_MARCADA;
            } else {
                estado = PENDIENTE;
            }

            UsuarioResponseDto alumno = usuarioClient.obtenerUsuarioPorId(insc.getAlumnoId());

            return new AlumnoAsistenciaDto(
                    insc.getAlumnoId(),
                    insc.getId(),
                    alumno.getNombre(),
                    alumno.getEmail(),
                    estado,
                    puedeMarcarAhora,
                    asistencia != null ? asistencia.getMarcadoEn() : null);
        }).collect(Collectors.toList());
    }

    // =========================================================================
    // TUTOR: marcar asistencia de un alumno
    // =========================================================================
    @Transactional
    public void marcarAsistencia(UUID sesionId, UUID alumnoId, UUID tutorId, String estadoSolicitado) {
        if (!PRESENTE.equals(estadoSolicitado) && !AUSENTE.equals(estadoSolicitado)) {
            throw new IllegalArgumentException("Estado inválido. Usa PRESENTE o AUSENTE.");
        }

        Sesion sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada."));

        if (!sesion.getTutorId().equals(tutorId)) {
            throw new SecurityException("Solo el tutor que creó la sesión puede marcar la asistencia.");
        }

        if (LocalDateTime.now().isBefore(sesion.getFechaHora())) {
            throw new IllegalArgumentException("Aún no puedes marcar asistencia: la sesión no ha comenzado.");
        }

        if (yaExpiro(sesion)) {
            throw new IllegalArgumentException(
                    "Ya no puedes marcar ni modificar la asistencia: el plazo de 24 horas expiró.");
        }

        inscripcionRepository.findBySesionIdAndAlumnoIdAndEstado(sesionId, alumnoId, INSCRIPCION_CONFIRMADA)
                .orElseThrow(() -> new IllegalArgumentException("Este alumno no está inscrito en esta sesión."));

        Asistencia asistencia = asistenciaRepository.findBySesionIdAndAlumnoId(sesionId, alumnoId)
                .orElse(new Asistencia());

        asistencia.setSesionId(sesionId);
        asistencia.setAlumnoId(alumnoId);
        asistencia.setEstado(estadoSolicitado);
        asistencia.setMarcadoEn(LocalDateTime.now());

        asistenciaRepository.save(asistencia);
    }

    // =========================================================================
    // ALUMNO: historial personal de asistencias (todas sus sesiones pasadas)
    // =========================================================================
    @Transactional(readOnly = true)
    public List<HistorialAsistenciaDto> obtenerHistorialAlumno(UUID alumnoId) {
        LocalDateTime ahora = LocalDateTime.now();

        List<Inscripcion> inscripciones = inscripcionRepository
                .findByAlumnoIdAndEstado(alumnoId, INSCRIPCION_CONFIRMADA);

        return inscripciones.stream()
                .map(insc -> sesionRepository.findById(insc.getSesionId()).orElse(null))
                .filter(sesion -> sesion != null && sesion.getFechaHora().isBefore(ahora))
                .sorted(Comparator.comparing(Sesion::getFechaHora).reversed())
                .map(sesion -> {
                    Asistencia asistencia = asistenciaRepository
                            .findBySesionIdAndAlumnoId(sesion.getId(), alumnoId)
                            .orElse(null);

                    String estado;
                    if (asistencia != null) {
                        estado = asistencia.getEstado();
                    } else if (yaExpiro(sesion)) {
                        estado = NO_MARCADA;
                    } else {
                        estado = PENDIENTE;
                    }

                    return new HistorialAsistenciaDto(
                            sesion.getId(),
                            sesion.getTitulo(),
                            sesion.getTutorNombre(),
                            sesion.getFechaHora(),
                            estado);
                })
                .collect(Collectors.toList());
    }
}