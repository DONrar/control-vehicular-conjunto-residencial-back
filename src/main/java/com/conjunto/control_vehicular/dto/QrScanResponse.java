package com.conjunto.control_vehicular.dto;
import com.conjunto.control_vehicular.entity.Persona;
import com.conjunto.control_vehicular.entity.PersonaVehiculo;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ========== Response del escaneo QR (lo que ve el guarda) ==========
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrScanResponse {
    private PersonaInfoDTO persona;
    private List<VehiculoAsociadoDTO> vehiculos;
    private EstadoPagoDTO estadoPago;
    private UltimoMovimientoDTO ultimoMovimiento;
    private boolean puedeIngresarSalir;
    private String mensaje;
}
