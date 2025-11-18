package com.conjunto.control_vehicular.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_vehiculo", indexes = {
        @Index(name = "idx_fecha_hora", columnList = "fechaHora"),
        @Index(name = "idx_persona", columnList = "id_persona"),
        @Index(name = "idx_vehiculo", columnList = "id_vehiculo"),
        @Index(name = "idx_tipo", columnList = "tipoMovimiento")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_registro")
    private Usuario usuarioRegistro; // El guarda que registró

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private Boolean autorizado = true;

    @Column(length = 500)
    private String motivoDenegacion;

    @Column(length = 1000)
    private String observaciones;

    // Información del estado de pago al momento del registro
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Persona.EstadoPago estadoPagoAlMomento;

    @Column(length = 50)
    private String apartamentoAlMomento;

    @PrePersist
    protected void onCreate() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
        // Capturar estado de pago al momento del registro
        if (persona != null) {
            estadoPagoAlMomento = persona.getEstadoPago();
            apartamentoAlMomento = persona.getTorre() + " - " + persona.getApartamento();
        }
    }

    public enum TipoMovimiento {
        ENTRADA,
        SALIDA
    }

    // Método helper para crear movimiento denegado
    public static MovimientoVehiculo crearDenegado(Persona persona, Vehiculo vehiculo,
                                                   TipoMovimiento tipo, String motivo,
                                                   Usuario guarda) {
        return MovimientoVehiculo.builder()
                .persona(persona)
                .vehiculo(vehiculo)
                .tipoMovimiento(tipo)
                .autorizado(false)
                .motivoDenegacion(motivo)
                .usuarioRegistro(guarda)
                .fechaHora(LocalDateTime.now())
                .build();
    }

    // Método helper para crear movimiento autorizado
    public static MovimientoVehiculo crearAutorizado(Persona persona, Vehiculo vehiculo,
                                                     TipoMovimiento tipo, Usuario guarda) {
        return MovimientoVehiculo.builder()
                .persona(persona)
                .vehiculo(vehiculo)
                .tipoMovimiento(tipo)
                .autorizado(true)
                .usuarioRegistro(guarda)
                .fechaHora(LocalDateTime.now())
                .build();
    }
}