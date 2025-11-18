package com.conjunto.control_vehicular.dto;

import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;


// ========== Response del registro de movimiento ==========
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarMovimientoResponse {
    private boolean exitoso;
    private String mensaje;
    private MovimientoDTO movimiento;
    private List<String> alertas;
}