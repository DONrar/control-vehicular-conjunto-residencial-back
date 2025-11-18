package com.conjunto.control_vehicular.dto;

import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ========== Request para escanear QR ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrScanRequest {
    private String qrToken;
}