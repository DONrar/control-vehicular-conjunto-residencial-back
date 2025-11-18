package com.conjunto.control_vehicular.dto;

import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
// ========== DTO de Movimiento ==========
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    private Long id;
    private String nombrePersona;
    private String documentoPersona;
    private String placaVehiculo;
    private String marcaVehiculo;
    private String tipoMovimiento;
    private LocalDateTime fechaHora;
    private boolean autorizado;
    private String motivoDenegacion;
    private String observaciones;
    private String estadoPagoAlMomento;
    private String apartamentoAlMomento;
    private String usuarioRegistro;
}