package com.tutormatch.ms_core.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutormatch.ms_core.entity.Peticion;

@Repository
public interface PeticionRepository extends JpaRepository<Peticion, UUID> {

    /**
     * HU-34: tablero general — excluye las eliminadas lógicamente,
     * ordenadas de la más reciente a la más antigua.
     */
    List<Peticion> findByEstadoNotOrderByCreadoEnDesc(String estado);
}