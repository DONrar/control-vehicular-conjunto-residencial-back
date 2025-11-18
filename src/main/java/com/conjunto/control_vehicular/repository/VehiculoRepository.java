package com.conjunto.control_vehicular.repository;
import com.conjunto.control_vehicular.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ========== VehiculoRepository ==========
@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByActivoTrue();

    @Query("SELECT v FROM Vehiculo v JOIN v.personas pv WHERE pv.persona.id = :personaId AND pv.activo = true")
    List<Vehiculo> findVehiculosByPersonaId(@Param("personaId") Long personaId);

    @Query("SELECT v FROM Vehiculo v JOIN v.personas pv WHERE pv.persona.qrToken = :qrToken AND pv.activo = true")
    List<Vehiculo> findVehiculosByQrToken(@Param("qrToken") String qrToken);

    boolean existsByPlaca(String placa);
}