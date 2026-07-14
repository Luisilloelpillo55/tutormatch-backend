package com.tutormatch.ms_notificaciones.repository;

import com.tutormatch.ms_notificaciones.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    // Busca todas las notificaciones de un usuario y las ordena de la más nueva a
    // la más antigua y solo las no leídas
    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByCreadoEnDesc(UUID usuarioId);

}