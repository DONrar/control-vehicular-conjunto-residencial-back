package com.conjunto.control_vehicular.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @Column(length = 500)
    private String foto; // URL o base64

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoPersona tipo;

    @Column(length = 50)
    private String torre;

    @Column(length = 20)
    private String apartamento;

    @Column(unique = true, nullable = false, length = 36)
    private String qrToken;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoPago estadoPago = EstadoPago.AL_DIA;

    @Column
    private LocalDateTime fechaUltimoPago;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    // Relación con vehículos
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PersonaVehiculo> vehiculos = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (qrToken == null) {
            qrToken = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public enum TipoPersona {
        PROPIETARIO,
        CONDUCTOR_AUTORIZADO,
        PROPIETARIO_Y_CONDUCTOR
    }

    public enum EstadoPago {
        AL_DIA,
        EN_MORA
    }

    // Método helper para agregar vehículo
    public void agregarVehiculo(Vehiculo vehiculo, PersonaVehiculo.RolVehiculo rol) {
        PersonaVehiculo personaVehiculo = PersonaVehiculo.builder()
                .persona(this)
                .vehiculo(vehiculo)
                .rol(rol)
                .build();
        vehiculos.add(personaVehiculo);
        vehiculo.getPersonas().add(personaVehiculo);
    }

    // Método helper para remover vehículo
    public void removerVehiculo(Vehiculo vehiculo) {
        vehiculos.removeIf(pv -> pv.getVehiculo().equals(vehiculo));
        vehiculo.getPersonas().removeIf(pv -> pv.getPersona().equals(this));
    }
}