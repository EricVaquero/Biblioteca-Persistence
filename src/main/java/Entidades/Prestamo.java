package Entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ejemplar_id")
    private Ejemplar ejemplar;

    private LocalDate fechaInicio;
    private LocalDate fechaDevolucion;

    public int getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Ejemplar getEjemplar() { return ejemplar; }
    public void setEjemplar(Ejemplar ejemplar) { this.ejemplar = ejemplar; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
}

