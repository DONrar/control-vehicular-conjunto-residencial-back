package com.conjunto.control_vehicular.dto;

import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ========== Vehículo asociado con su rol ==========
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoAsociadoDTO {
    private Long idVehiculo;
    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private String tipo;
    private String rol; // PROPIETARIO o CONDUCTOR_AUTORIZADO
    private boolean activo;
    private LocalDateTime fechaAutorizacion;
    private LocalDateTime fechaVencimiento;
    private boolean vigente;
}