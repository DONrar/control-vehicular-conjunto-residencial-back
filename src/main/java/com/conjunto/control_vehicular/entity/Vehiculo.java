package com.conjunto.control_vehicular.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehiculo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(length = 30)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVehiculo tipo;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(length = 500)
    private String observaciones;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    // Relación con personas
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PersonaVehiculo> personas = new HashSet<>();

    // Relación con movimientos
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<MovimientoVehiculo> movimientos = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (placa != null) {
            placa = placa.toUpperCase().trim();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
        if (placa != null) {
            placa = placa.toUpperCase().trim();
        }
    }

    public enum TipoVehiculo {
        AUTOMOVIL,
        MOTOCICLETA,
        CAMIONETA,
        CAMPERO,
        OTRO
    }
}