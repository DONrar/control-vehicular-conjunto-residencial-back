package com.conjunto.control_vehicular.repository;
import com.conjunto.control_vehicular.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface MovimientoVehiculoRepository extends JpaRepository<MovimientoVehiculo, Long> {

    // Buscar último movimiento de un vehículo
    Optional<MovimientoVehiculo> findFirstByVehiculoPlacaOrderByFechaHoraDesc(String placa);

    // Buscar último movimiento de una persona
    Optional<MovimientoVehiculo> findFirstByPersonaQrTokenOrderByFechaHoraDesc(String qrToken);

    // Movimientos por rango de fecha
    List<MovimientoVehiculo> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    // Movimientos por persona
    Page<MovimientoVehiculo> findByPersonaId(Long personaId, Pageable pageable);

    // Movimientos por vehículo
    Page<MovimientoVehiculo> findByVehiculoId(Long vehiculoId, Pageable pageable);

    // Movimientos por tipo
    Page<MovimientoVehiculo> findByTipoMovimiento(
            MovimientoVehiculo.TipoMovimiento tipo,
            Pageable pageable
    );

    // Movimientos autorizados/no autorizados
    Page<MovimientoVehiculo> findByAutorizado(Boolean autorizado, Pageable pageable);

    // Query compleja: Movimientos con filtros
    @Query("SELECT m FROM MovimientoVehiculo m WHERE " +
            "(:personaId IS NULL OR m.persona.id = :personaId) AND " +
            "(:vehiculoId IS NULL OR m.vehiculo.id = :vehiculoId) AND " +
            "(:tipo IS NULL OR m.tipoMovimiento = :tipo) AND " +
            "(:autorizado IS NULL OR m.autorizado = :autorizado) AND " +
            "(:fechaInicio IS NULL OR m.fechaHora >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR m.fechaHora <= :fechaFin)")
    Page<MovimientoVehiculo> findConFiltros(
            @Param("personaId") Long personaId,
            @Param("vehiculoId") Long vehiculoId,
            @Param("tipo") MovimientoVehiculo.TipoMovimiento tipo,
            @Param("autorizado") Boolean autorizado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    // Verificar si hay entrada sin salida para un vehículo
    @Query("SELECT COUNT(m) > 0 FROM MovimientoVehiculo m WHERE " +
            "m.vehiculo.placa = :placa AND m.tipoMovimiento = 'ENTRADA' AND " +
            "NOT EXISTS (SELECT m2 FROM MovimientoVehiculo m2 WHERE " +
            "m2.vehiculo.placa = :placa AND m2.tipoMovimiento = 'SALIDA' AND m2.fechaHora > m.fechaHora)")
    boolean tieneEntradaSinSalida(@Param("placa") String placa);

    // Estadísticas: Movimientos por día
    @Query("SELECT DATE(m.fechaHora) as fecha, COUNT(m) as cantidad FROM MovimientoVehiculo m " +
            "WHERE m.fechaHora BETWEEN :inicio AND :fin GROUP BY DATE(m.fechaHora)")
    List<Object[]> contarMovimientosPorDia(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );
}