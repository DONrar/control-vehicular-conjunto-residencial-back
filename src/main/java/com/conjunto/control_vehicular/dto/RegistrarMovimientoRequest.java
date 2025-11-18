package com.conjunto.control_vehicular.dto;

import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ========== Request para registrar movimiento ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarMovimientoRequest {
    private String qrToken;
    private String placa;
    private String tipoMovimiento; // ENTRADA o SALIDA
    private String observaciones;
}