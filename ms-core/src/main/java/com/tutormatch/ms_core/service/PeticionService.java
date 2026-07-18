package com.tutormatch.ms_core.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tutormatch.ms_core.dto.PeticionRequestDto;
import com.tutormatch.ms_core.dto.PeticionResponseDto;
import com.tutormatch.ms_core.entity.Peticion;
import com.tutormatch.ms_core.repository.PeticionRepository;

@Service
public class PeticionService {

    private static final String ESTADO_ACTIVA = "activa";
    private static final String ESTADO_ATENDIDA = "atendida";
    private static final String ESTADO_ELIMINADA = "eliminada";

    private static final int MATERIA_MIN_LENGTH = 3;
    private static final int DESCRIPCION_MIN_LENGTH = 10;

    private final PeticionRepository peticionRepository;

    public PeticionService(PeticionRepository peticionRepository) {
        this.peticionRepository = peticionRepository;
    }

    // =========================================================================
    // HU-33: Crear una petición
    // =========================================================================

    @Transactional
    public PeticionResponseDto crearPeticion(PeticionRequestDto dto, UUID alumnoId, String alumnoNombre) {

        if (dto.getMateria() == null || dto.getMateria().trim().length() < MATERIA_MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "La materia debe tener al menos " + MATERIA_MIN_LENGTH + " caracteres.");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().trim().length() < DESCRIPCION_MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "La descripción del tema debe tener al menos " + DESCRIPCION_MIN_LENGTH + " caracteres.");
        }

        Peticion nueva = new Peticion();
        nueva.setAlumnoId(alumnoId);
        nueva.setAlumnoNombre(alumnoNombre != null ? alumnoNombre : "Alumno");
        nueva.setMateria(dto.getMateria().trim());
        nueva.setDescripcion(dto.getDescripcion().trim());
        nueva.setEstado(ESTADO_ACTIVA);

        return mapToResponseDto(peticionRepository.save(nueva), alumnoId);
    }

    // =========================================================================
    // HU-34: Tablero general de peticiones activas/atendidas
    // =========================================================================

    public List<PeticionResponseDto> listarTablero(UUID usuarioActualId) {
        return peticionRepository.findByEstadoNotOrderByCreadoEnDesc(ESTADO_ELIMINADA)
                .stream()
                .map(p -> mapToResponseDto(p, usuarioActualId))
                .collect(Collectors.toList());
    }

    // =========================================================================
    // HU-35: Eliminar (lógicamente) una petición propia
    // =========================================================================

    @Transactional
    public void eliminarPeticion(UUID peticionId, UUID alumnoId) {
        Peticion peticion = peticionRepository.findById(peticionId)
                .orElseThrow(() -> new IllegalArgumentException("Petición no encontrada."));

        if (!peticion.getAlumnoId().equals(alumnoId)) {
            throw new SecurityException("No tienes permiso para eliminar esta petición.");
        }
        if (ESTADO_ELIMINADA.equals(peticion.getEstado())) {
            throw new IllegalArgumentException("Esta petición ya fue eliminada.");
        }

        peticion.setEstado(ESTADO_ELIMINADA);
        peticionRepository.save(peticion);
    }

    // =========================================================================
    // HU-36: Marcar como atendida (solo Tutor)
    // =========================================================================

    @Transactional
    public PeticionResponseDto marcarComoAtendida(UUID peticionId, UUID tutorId, String tutorNombre) {
        Peticion peticion = peticionRepository.findById(peticionId)
                .orElseThrow(() -> new IllegalArgumentException("Petición no encontrada."));

        if (ESTADO_ELIMINADA.equals(peticion.getEstado())) {
            throw new IllegalArgumentException("No puedes atender una petición eliminada.");
        }
        if (ESTADO_ATENDIDA.equals(peticion.getEstado())) {
            throw new IllegalArgumentException("Esta petición ya fue atendida por " + peticion.getTutorAtendioNombre() + ".");
        }

        peticion.setEstado(ESTADO_ATENDIDA);
        peticion.setTutorAtendioId(tutorId);
        peticion.setTutorAtendioNombre(tutorNombre != null ? tutorNombre : "Tutor");

        return mapToResponseDto(peticionRepository.save(peticion), tutorId);
    }

    // =========================================================================
    // Entity -> DTO
    // =========================================================================

    private PeticionResponseDto mapToResponseDto(Peticion peticion, UUID usuarioActualId) {
        return new PeticionResponseDto(
                peticion.getId(),
                peticion.getAlumnoId(),
                peticion.getAlumnoNombre(),
                peticion.getMateria(),
                peticion.getDescripcion(),
                peticion.getEstado(),
                peticion.getTutorAtendioId(),
                peticion.getTutorAtendioNombre(),
                peticion.getCreadoEn(),
                peticion.getAlumnoId().equals(usuarioActualId)
        );
    }
}