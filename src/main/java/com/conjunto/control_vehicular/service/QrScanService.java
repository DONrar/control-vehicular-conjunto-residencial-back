package com.conjunto.control_vehicular.service;

import com.conjunto.control_vehicular.dto.*;
import com.conjunto.control_vehicular.entity.*;
import com.conjunto.control_vehicular.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.conjunto.control_vehicular.exception.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrScanService {

    private final PersonaRepository personaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final PersonaVehiculoRepository personaVehiculoRepository;
    private final MovimientoVehiculoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Método principal: Escanear QR y obtener toda la información
     */
    @Transactional(readOnly = true)
    public QrScanResponse escanearQr(String qrToken) {
        log.info("Escaneando QR: {}", qrToken);

        // 1. Buscar persona por QR
        Persona persona = personaRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RecursoNoEncontradoException("QR no válido o persona no encontrada"));

        if (!persona.getActivo()) {
            throw new AutorizacionDenegadaException("Esta persona está inactiva en el sistema");
        }

        // 2. Obtener vehículos asociados
        List<VehiculoAsociadoDTO> vehiculos = obtenerVehiculosAsociados(persona);

        // 3. Obtener estado de pago
        EstadoPagoDTO estadoPago = construirEstadoPago(persona);

        // 4. Obtener último movimiento
        UltimoMovimientoDTO ultimoMovimiento = obtenerUltimoMovimiento(qrToken);

        // 5. Determinar si puede ingresar/salir
        boolean puedeIngresarSalir = determinarPermiso(persona, vehiculos, estadoPago);
        String mensaje = construirMensaje(persona, vehiculos, estadoPago);

        return QrScanResponse.builder()
                .persona(construirPersonaInfo(persona))
                .vehiculos(vehiculos)
                .estadoPago(estadoPago)
                .ultimoMovimiento(ultimoMovimiento)
                .puedeIngresarSalir(puedeIngresarSalir)
                .mensaje(mensaje)
                .build();
    }

    /**
     * Registrar movimiento (entrada o salida)
     */
    @Transactional
    public RegistrarMovimientoResponse registrarMovimiento(
            RegistrarMovimientoRequest request,
            String username) {

        log.info("Registrando movimiento: {} - Placa: {} - Tipo: {}",
                request.getQrToken(), request.getPlaca(), request.getTipoMovimiento());

        List<String> alertas = new ArrayList<>();

        // 1. Validar persona
        Persona persona = personaRepository.findByQrToken(request.getQrToken())
                .orElseThrow(() -> new RecursoNoEncontradoException("QR no válido"));

        // 2. Validar vehículo
        Vehiculo vehiculo = vehiculoRepository.findByPlaca(request.getPlaca().toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));

        // 3. Validar autorización
        PersonaVehiculo autorizacion = personaVehiculoRepository
                .findByQrTokenAndPlaca(request.getQrToken(), request.getPlaca())
                .orElse(null);

        if (autorizacion == null || !autorizacion.esVigente()) {
            // Movimiento NO autorizado
            Usuario guarda = usuarioRepository.findByUsername(username).orElse(null);
            MovimientoVehiculo movimientoDenegado = MovimientoVehiculo.crearDenegado(
                    persona, vehiculo,
                    MovimientoVehiculo.TipoMovimiento.valueOf(request.getTipoMovimiento()),
                    "Conductor no autorizado para este vehículo",
                    guarda
            );
            movimientoDenegado.setObservaciones(request.getObservaciones());
            movimientoRepository.save(movimientoDenegado);

            return RegistrarMovimientoResponse.builder()
                    .exitoso(false)
                    .mensaje("❌ Movimiento DENEGADO: Conductor no autorizado para este vehículo")
                    .movimiento(convertirAMovimientoDTO(movimientoDenegado))
                    .build();
        }

        // 4. Validar estado de pago
        if (persona.getEstadoPago() == Persona.EstadoPago.EN_MORA) {
            alertas.add("⚠️ ALERTA: Propietario en MORA de administración");
        }

        // 5. Validar consistencia entrada/salida
        if (request.getTipoMovimiento().equals("SALIDA")) {
            boolean tieneEntrada = movimientoRepository.tieneEntradaSinSalida(request.getPlaca());
            if (!tieneEntrada) {
                alertas.add("⚠️ ALERTA: No hay registro de entrada previo para este vehículo");
            }
        }

        // 6. Crear y guardar movimiento autorizado
        Usuario guarda = usuarioRepository.findByUsername(username).orElse(null);
        MovimientoVehiculo movimiento = MovimientoVehiculo.crearAutorizado(
                persona, vehiculo,
                MovimientoVehiculo.TipoMovimiento.valueOf(request.getTipoMovimiento()),
                guarda
        );
        movimiento.setObservaciones(request.getObservaciones());
        movimientoRepository.save(movimiento);

        String mensajeExito = String.format("✅ %s registrada exitosamente - %s %s %s",
                request.getTipoMovimiento(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getPlaca());

        return RegistrarMovimientoResponse.builder()
                .exitoso(true)
                .mensaje(mensajeExito)
                .movimiento(convertirAMovimientoDTO(movimiento))
                .alertas(alertas.isEmpty() ? null : alertas)
                .build();
    }

    /**
     * Validar si una persona puede conducir un vehículo
     */
    @Transactional(readOnly = true)
    public ValidarAutorizacionResponse validarAutorizacion(String qrToken, String placa) {
        Persona persona = personaRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RecursoNoEncontradoException("QR no válido"));

        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo no encontrado"));

        PersonaVehiculo autorizacion = personaVehiculoRepository
                .findByQrTokenAndPlaca(qrToken, placa)
                .orElse(null);

        if (autorizacion != null && autorizacion.esVigente()) {
            return ValidarAutorizacionResponse.builder()
                    .autorizado(true)
                    .mensaje("✅ Autorizado")
                    .razon(String.format("Persona autorizada como %s", autorizacion.getRol()))
                    .persona(construirPersonaInfo(persona))
                    .vehiculo(construirVehiculoAsociado(autorizacion))
                    .build();
        } else {
            return ValidarAutorizacionResponse.builder()
                    .autorizado(false)
                    .mensaje("❌ No autorizado")
                    .razon("Esta persona NO está autorizada para conducir este vehículo")
                    .persona(construirPersonaInfo(persona))
                    .build();
        }
    }

    // ========== Métodos auxiliares ==========

    private List<VehiculoAsociadoDTO> obtenerVehiculosAsociados(Persona persona) {
        return persona.getVehiculos().stream()
                .filter(pv -> pv.getActivo() && pv.getVehiculo().getActivo())
                .map(this::construirVehiculoAsociado)
                .collect(Collectors.toList());
    }

    private VehiculoAsociadoDTO construirVehiculoAsociado(PersonaVehiculo pv) {
        Vehiculo v = pv.getVehiculo();
        return VehiculoAsociadoDTO.builder()
                .idVehiculo(v.getId())
                .placa(v.getPlaca())
                .marca(v.getMarca())
                .modelo(v.getModelo())
                .color(v.getColor())
                .tipo(v.getTipo().name())
                .rol(pv.getRol().name())
                .activo(pv.getActivo())
                .fechaAutorizacion(pv.getFechaAutorizacion())
                .fechaVencimiento(pv.getFechaVencimiento())
                .vigente(pv.esVigente())
                .build();
    }

    private PersonaInfoDTO construirPersonaInfo(Persona p) {
        return PersonaInfoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .documento(p.getDocumento())
                .foto(p.getFoto())
                .tipo(p.getTipo().name())
                .torre(p.getTorre())
                .apartamento(p.getApartamento())
                .email(p.getEmail())
                .telefono(p.getTelefono())
                .build();
    }

    private EstadoPagoDTO construirEstadoPago(Persona persona) {
        boolean alDia = persona.getEstadoPago() == Persona.EstadoPago.AL_DIA;
        return EstadoPagoDTO.builder()
                .estado(persona.getEstadoPago().name())
                .fechaUltimoPago(persona.getFechaUltimoPago())
                .mensaje(alDia ? "✅ Al día" : "⚠️ En mora")
                .permiteIngreso(alDia)
                .build();
    }

    private UltimoMovimientoDTO obtenerUltimoMovimiento(String qrToken) {
        return movimientoRepository.findFirstByPersonaQrTokenOrderByFechaHoraDesc(qrToken)
                .map(m -> UltimoMovimientoDTO.builder()
                        .id(m.getId())
                        .tipoMovimiento(m.getTipoMovimiento().name())
                        .fechaHora(m.getFechaHora())
                        .placaVehiculo(m.getVehiculo().getPlaca())
                        .marcaVehiculo(m.getVehiculo().getMarca())
                        .autorizado(m.getAutorizado())
                        .build())
                .orElse(null);
    }

    private boolean determinarPermiso(Persona persona, List<VehiculoAsociadoDTO> vehiculos, EstadoPagoDTO estadoPago) {
        return persona.getActivo() && !vehiculos.isEmpty();
    }

    private String construirMensaje(Persona persona, List<VehiculoAsociadoDTO> vehiculos, EstadoPagoDTO estadoPago) {
        if (!persona.getActivo()) {
            return "❌ Persona inactiva";
        }
        if (vehiculos.isEmpty()) {
            return "⚠️ No tiene vehículos asociados";
        }
        if (!estadoPago.isPermiteIngreso()) {
            return "⚠️ Propietario en mora - Verificar con administración";
        }
        return "✅ Puede ingresar/salir con los vehículos autorizados";
    }

    private MovimientoDTO convertirAMovimientoDTO(MovimientoVehiculo m) {
        return MovimientoDTO.builder()
                .id(m.getId())
                .nombrePersona(m.getPersona().getNombre())
                .documentoPersona(m.getPersona().getDocumento())
                .placaVehiculo(m.getVehiculo().getPlaca())
                .marcaVehiculo(m.getVehiculo().getMarca())
                .tipoMovimiento(m.getTipoMovimiento().name())
                .fechaHora(m.getFechaHora())
                .autorizado(m.getAutorizado())
                .motivoDenegacion(m.getMotivoDenegacion())
                .observaciones(m.getObservaciones())
                .estadoPagoAlMomento(m.getEstadoPagoAlMomento() != null ? m.getEstadoPagoAlMomento().name() : null)
                .apartamentoAlMomento(m.getApartamentoAlMomento())
                .usuarioRegistro(m.getUsuarioRegistro() != null ? m.getUsuarioRegistro().getNombreCompleto() : null)
                .build();
    }
}