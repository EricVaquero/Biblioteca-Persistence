package Entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @Column(name = "isbn", length = 20)
    private String isbn;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 100)
    private String autor;

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
}
