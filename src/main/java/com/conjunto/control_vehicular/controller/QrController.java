package com.conjunto.control_vehicular.controller;

import com.conjunto.control_vehicular.dto.*;
import com.conjunto.control_vehicular.service.QrScanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/qr")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "QR Scan", description = "Endpoints para escaneo de QR y control de acceso vehicular")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class QrController {

    private final QrScanService qrScanService;

    /**
     * Escanear QR - Endpoint principal que usa el guarda
     * POST /api/v1/qr/scan
     */
    @PostMapping("/scan")
    @PreAuthorize("hasAnyRole('GUARDA', 'SUPERVISOR', 'ADMIN')")
    @Operation(summary = "Escanear código QR",
            description = "Obtiene toda la información de una persona al escanear su QR: datos personales, vehículos autorizados, estado de pago, último movimiento")
    public ResponseEntity<ApiResponse<QrScanResponse>> escanearQr(
            @Valid @RequestBody QrScanRequest request) {

        log.info("Solicitud de escaneo QR recibida");

        try {
            QrScanResponse response = qrScanService.escanearQr(request.getQrToken());
            return ResponseEntity.ok(
                    ApiResponse.success(response, "QR escaneado exitosamente")
            );
        } catch (Exception e) {
            log.error("Error al escanear QR: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error al escanear QR: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Escanear QR por GET (alternativa más simple)
     * GET /api/v1/qr/scan/{qrToken}
     */
    @GetMapping("/scan/{qrToken}")
    @PreAuthorize("hasAnyRole('GUARDA', 'SUPERVISOR', 'ADMIN')")
    @Operation(summary = "Escanear QR por token",
            description = "Alternativa GET para escanear QR directamente con el token en la URL")
    public ResponseEntity<ApiResponse<QrScanResponse>> escanearQrPorToken(
            @PathVariable String qrToken) {

        try {
            QrScanResponse response = qrScanService.escanearQr(qrToken);
            return ResponseEntity.ok(
                    ApiResponse.success(response, "QR escaneado exitosamente")
            );
        } catch (Exception e) {
            log.error("Error al escanear QR: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error al escanear QR: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Registrar movimiento (entrada o salida)
     * POST /api/v1/qr/movimiento
     */
    @PostMapping("/movimiento")
    @PreAuthorize("hasAnyRole('GUARDA', 'SUPERVISOR', 'ADMIN')")
    @Operation(summary = "Registrar movimiento vehicular",
            description = "Registra una entrada o salida de vehículo. Valida autorización y estado de pago.")
    public ResponseEntity<ApiResponse<RegistrarMovimientoResponse>> registrarMovimiento(
            @Valid @RequestBody RegistrarMovimientoRequest request,
            Authentication authentication) {

        log.info("Registrando movimiento: {} - Placa: {}",
                request.getTipoMovimiento(), request.getPlaca());

        try {
            String username = authentication.getName();
            RegistrarMovimientoResponse response = qrScanService.registrarMovimiento(request, username);

            if (response.isExitoso()) {
                return ResponseEntity.ok(
                        ApiResponse.success(response, response.getMensaje())
                );
            } else {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error(response.getMensaje(), response.getAlertas())
                );
            }
        } catch (Exception e) {
            log.error("Error al registrar movimiento: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error al registrar movimiento: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Validar autorización persona-vehículo
     * POST /api/v1/qr/validar
     */
    @PostMapping("/validar")
    @PreAuthorize("hasAnyRole('GUARDA', 'SUPERVISOR', 'ADMIN')")
    @Operation(summary = "Validar autorización",
            description = "Verifica si una persona está autorizada para conducir un vehículo específico")
    public ResponseEntity<ApiResponse<ValidarAutorizacionResponse>> validarAutorizacion(
            @Valid @RequestBody ValidarAutorizacionRequest request) {

        try {
            ValidarAutorizacionResponse response = qrScanService.validarAutorizacion(
                    request.getQrToken(),
                    request.getPlaca()
            );

            return ResponseEntity.ok(
                    ApiResponse.success(response, response.getMensaje())
            );
        } catch (Exception e) {
            log.error("Error al validar autorización: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Error: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Health check del servicio QR
     * GET /api/v1/qr/health
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica que el servicio QR esté funcionando")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(
                ApiResponse.success("OK", "Servicio QR funcionando correctamente")
        );
    }
}