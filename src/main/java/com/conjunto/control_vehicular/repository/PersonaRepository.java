package com.conjunto.control_vehicular.repository;

import com.conjunto.control_vehicular.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ========== PersonaRepository ==========
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByQrToken(String qrToken);

    Optional<Persona> findByDocumento(String documento);

    List<Persona> findByActivoTrue();

    List<Persona> findByTorreAndApartamento(String torre, String apartamento);

    @Query("SELECT p FROM Persona p WHERE p.estadoPago = :estado AND p.activo = true")
    List<Persona> findByEstadoPago(@Param("estado") Persona.EstadoPago estado);

    boolean existsByDocumento(String documento);

    boolean existsByQrToken(String qrToken);
}