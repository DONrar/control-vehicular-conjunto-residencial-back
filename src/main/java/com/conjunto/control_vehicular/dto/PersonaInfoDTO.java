package com.conjunto.control_vehicular.dto;

import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ========== Info de la persona ==========
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaInfoDTO {
    private Long id;
    private String nombre;
    private String documento;
    private String foto;
    private String tipo;
    private String torre;
    private String apartamento;
    private String email;
    private String telefono;
}