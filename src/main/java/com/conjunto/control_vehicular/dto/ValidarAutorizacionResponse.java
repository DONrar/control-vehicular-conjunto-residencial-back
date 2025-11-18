package com.conjunto.control_vehicular.dto;

import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
// ========== Response de validación ==========
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidarAutorizacionResponse {
    private boolean autorizado;
    private String mensaje;
    private String razon;
    private PersonaInfoDTO persona;
    private VehiculoAsociadoDTO vehiculo;
}