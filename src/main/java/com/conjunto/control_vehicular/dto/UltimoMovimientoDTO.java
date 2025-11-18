package com.conjunto.control_vehicular.dto;


import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ========== Último movimiento ==========
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UltimoMovimientoDTO {
    private Long id;
    private String tipoMovimiento; // ENTRADA o SALIDA
    private LocalDateTime fechaHora;
    private String placaVehiculo;
    private String marcaVehiculo;
    private boolean autorizado;
}