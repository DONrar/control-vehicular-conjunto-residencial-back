package com.conjunto.control_vehicular.repository;
import com.conjunto.control_vehicular.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ========== PersonaVehiculoRepository ==========
@Repository
public interface PersonaVehiculoRepository extends JpaRepository<PersonaVehiculo, Long> {

    @Query("SELECT pv FROM PersonaVehiculo pv WHERE pv.persona.id = :personaId AND pv.vehiculo.id = :vehiculoId")
    Optional<PersonaVehiculo> findByPersonaIdAndVehiculoId(
            @Param("personaId") Long personaId,
            @Param("vehiculoId") Long vehiculoId
    );

    @Query("SELECT pv FROM PersonaVehiculo pv WHERE pv.persona.qrToken = :qrToken AND pv.vehiculo.placa = :placa AND pv.activo = true")
    Optional<PersonaVehiculo> findByQrTokenAndPlaca(
            @Param("qrToken") String qrToken,
            @Param("placa") String placa
    );

    List<PersonaVehiculo> findByPersonaIdAndActivoTrue(Long personaId);

    List<PersonaVehiculo> findByVehiculoIdAndActivoTrue(Long vehiculoId);

    @Query("SELECT COUNT(pv) > 0 FROM PersonaVehiculo pv WHERE pv.persona.qrToken = :qrToken AND pv.vehiculo.placa = :placa AND pv.activo = true")
    boolean existsAutorizacion(@Param("qrToken") String qrToken, @Param("placa") String placa);
}