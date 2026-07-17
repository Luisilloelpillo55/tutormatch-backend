package com.tutormatch.ms_core.repository;

import com.tutormatch.ms_core.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, UUID> {

    List<Asistencia> findBySesionId(UUID sesionId);

    Optional<Asistencia> findBySesionIdAndAlumnoId(UUID sesionId, UUID alumnoId);

    List<Asistencia> findByAlumnoId(UUID alumnoId);
}