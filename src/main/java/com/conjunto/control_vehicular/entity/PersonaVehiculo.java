package com.conjunto.control_vehicular.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "persona_vehiculo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RolVehiculo rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column
    private LocalDateTime fechaAutorizacion;

    @Column
    private LocalDateTime fechaVencimiento; // Por si las autorizaciones tienen vencimiento

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (fechaAutorizacion == null) {
            fechaAutorizacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public enum RolVehiculo {
        PROPIETARIO,
        CONDUCTOR_AUTORIZADO
    }

    // Método para verificar si la autorización está vigente
    public boolean esVigente() {
        if (!activo) return false;
        if (fechaVencimiento == null) return true;
        return LocalDateTime.now().isBefore(fechaVencimiento);
    }
}