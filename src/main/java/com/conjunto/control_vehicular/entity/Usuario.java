package com.conjunto.control_vehicular.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombreCompleto;

    @Column(unique = true, length = 20)
    private String documento;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Boolean bloqueado = false;

    @Column
    private LocalDateTime ultimoAcceso;

    @Column
    private Integer intentosFallidos = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column
    private LocalDateTime fechaActualizacion;

    // Roles del usuario
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();

    // Movimientos registrados por este usuario
    @OneToMany(mappedBy = "usuarioRegistro")
    @Builder.Default
    private Set<MovimientoVehiculo> movimientosRegistrados = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public enum Rol {
        ROLE_ADMIN,
        ROLE_GUARDA,
        ROLE_SUPERVISOR
    }

    // Implementación de UserDetails para Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    // Métodos helper

    public void agregarRol(Rol rol) {
        roles.add(rol);
    }

    public void removerRol(Rol rol) {
        roles.remove(rol);
    }

    public boolean tieneRol(Rol rol) {
        return roles.contains(rol);
    }

    public void registrarAccesoExitoso() {
        this.ultimoAcceso = LocalDateTime.now();
        this.intentosFallidos = 0;
    }

    public void registrarAccesoFallido() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= 7) {
            this.bloqueado = true;
        }
    }
}