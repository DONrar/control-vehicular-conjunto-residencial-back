package com.conjunto.control_vehicular.dto;

import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ========== Estado de pago ==========
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoPagoDTO {
    private String estado; // AL_DIA o EN_MORA
    private LocalDateTime fechaUltimoPago;
    private String mensaje;
    private boolean permiteIngreso;
}