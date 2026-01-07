package Entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "isbn")
    private Libro libro;

    @Column(nullable = false)
    private String estado; // Disponible, Prestado, Dañado

    public int getId() { return id; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

