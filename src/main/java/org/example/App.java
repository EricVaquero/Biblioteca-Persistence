package org.example;

import Entidades.Usuario;
import Entidades.Libro;
import Entidades.Ejemplar;
import Entidades.Prestamo;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class App {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
    private static final EntityManager em = emf.createEntityManager();
    private static final Scanner teclado = new Scanner(System.in);
    private static Usuario usuarioLogueado = null;

    public static void main(String[] args) {

        login();

        if (usuarioLogueado == null) {
            System.out.println("No se pudo iniciar sesión. Saliendo...");
            cerrar();
            return;
        }

        if (usuarioLogueado.getTipo().equalsIgnoreCase("administrador")) {
            menuAdministrador();
        } else {
            menuUsuarioNormal();
        }

        cerrar();
    }

    private static void login() {
        System.out.println("=== Iniciar sesión ===");
        System.out.print("Email: ");
        String email = teclado.nextLine();
        System.out.print("Password: ");
        String password = teclado.nextLine();

        try {
            usuarioLogueado = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :pwd", Usuario.class).setParameter("email", email).setParameter("pwd", password).getSingleResult();

            System.out.println("Bienvenido " + usuarioLogueado.getNombre() + " (" + usuarioLogueado.getTipo() + ")");
        } catch (NoResultException e) {
            usuarioLogueado = null;
            System.out.println("Usuario o contraseña incorrectos");
        }
    }

    private static void menuAdministrador() {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n====== Menú ======");
            System.out.println("1. Registrar libro");
            System.out.println("2. Registrar ejemplar");
            System.out.println("3. Registrar usuario");
            System.out.println("4. Registrar préstamo");
            System.out.println("5. Devolver préstamo");
            System.out.println("6. Mostrar stock disponible");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            int opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> registrarLibro();
                case 2 -> registrarEjemplar();
                case 3 -> registrarUsuario();
                case 4 -> registrarPrestamo();
                case 5 -> devolverPrestamo();
                case 6 -> mostrarStockDisponible();
                case 0 -> salir = true;
                default -> System.out.println("Opción no válida");
            }
        }
    }

    private static void menuUsuarioNormal() {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== Menú Usuario ===");
            System.out.println("1. Ver mis préstamos");
            System.out.println("2. Ver ejemplares disponibles");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            int opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> mostrarPrestamosUsuario();
                case 2 -> mostrarStockDisponible();
                case 0 -> salir = true;
                default -> System.out.println("Opcion no válida");
            }
        }
    }

    private static void mostrarPrestamosUsuario() {
        List<Prestamo> prestamos = em.createQuery("SELECT p FROM Prestamo p WHERE p.usuario.id = :uid", Prestamo.class).setParameter("uid", usuarioLogueado.getId()).getResultList();

        if (prestamos.isEmpty()) {
            System.out.println("No tienes préstamos registrados.");
            return;
        }

        System.out.println("=== Mis préstamos ===");
        for (Prestamo p : prestamos) {
            String estado = (p.getFechaDevolucion() == null) ? "Activo" : "Devuelto";
            System.out.println("ID Préstamo: " + p.getId() + " | Libro: " + p.getEjemplar().getLibro().getTitulo() + " | Fecha inicio: " + p.getFechaInicio() + " | Estado: " + estado + ((p.getFechaDevolucion() != null) ? " | Fecha devolución: " + p.getFechaDevolucion() : ""));
        }
    }

    private static void registrarLibro() {
        System.out.print("ISBN: ");
        String isbn = teclado.nextLine();
        System.out.print("Título: ");
        String titulo = teclado.nextLine();
        System.out.print("Autor: ");
        String autor = teclado.nextLine();

        Libro libro = new Libro();
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setAutor(autor);

        em.getTransaction().begin();
        em.persist(libro);
        em.getTransaction().commit();

        System.out.println("Libro registrado: " + titulo);
    }

    private static void registrarEjemplar() {
        System.out.print("ISBN del libro: ");
        String isbn = teclado.nextLine();

        Libro libro = em.find(Libro.class, isbn);
        if (libro == null) {
            System.out.println("Libro no encontrado");
            return;
        }

        System.out.print("Estado del ejemplar (Disponible, Prestado, Dañado): ");
        String estado = teclado.nextLine();

        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setLibro(libro);
        ejemplar.setEstado(estado);

        em.getTransaction().begin();
        em.persist(ejemplar);
        em.getTransaction().commit();

        System.out.println("Ejemplar registrado, ID: " + ejemplar.getId());
    }

    private static void registrarUsuario() {
        System.out.print("DNI: ");
        String dni = teclado.nextLine();
        System.out.print("Nombre: ");
        String nombre = teclado.nextLine();
        System.out.print("Email: ");
        String email = teclado.nextLine();
        System.out.print("Password: ");
        String password = teclado.nextLine();
        System.out.print("Tipo (normal/administrador): ");
        String tipo = teclado.nextLine();

        Usuario usuario = new Usuario();
        usuario.setDni(dni);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setTipo(tipo);

        em.getTransaction().begin();
        em.persist(usuario);
        em.getTransaction().commit();

        System.out.println("Usuario registrado: " + nombre);
    }

    private static void registrarPrestamo() {
        System.out.print("ID usuario: ");
        int usuarioId = teclado.nextInt();
        System.out.print("ID ejemplar: ");
        int ejemplarId = teclado.nextInt();
        teclado.nextLine();

        Usuario usuario = em.find(Usuario.class, usuarioId);
        Ejemplar ejemplar = em.find(Ejemplar.class, ejemplarId);

        if (usuario == null || ejemplar == null) {
            System.out.println("Usuario o ejemplar no encontrados");
            return;
        }

        long prestamosActivos = em.createQuery("SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :uid AND p.fechaDevolucion IS NULL", Long.class).setParameter("uid", usuarioId).getSingleResult();

        if (prestamosActivos >= 3) {
            System.out.println("El usuario tiene 3 préstamos activos, no puede tomar más.");
            return;
        }

        if (!ejemplar.getEstado().equalsIgnoreCase("Disponible")) {
            System.out.println("El ejemplar no está disponible.");
            return;
        }

        if (usuario.getPenalizacionHasta() != null && usuario.getPenalizacionHasta().isAfter(LocalDate.now())) {
            System.out.println("El usuario tiene penalización hasta: " + usuario.getPenalizacionHasta());
            return;
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaInicio(LocalDate.now());

        ejemplar.setEstado("Prestado");

        em.getTransaction().begin();
        em.persist(prestamo);
        em.merge(ejemplar);
        em.getTransaction().commit();

        System.out.println("Préstamo registrado, fecha límite: " + LocalDate.now().plusDays(15));
    }

    private static void devolverPrestamo() {
        System.out.print("ID préstamo: ");
        int prestamoId = teclado.nextInt();
        teclado.nextLine();

        Prestamo prestamo = em.find(Prestamo.class, prestamoId);
        if (prestamo == null) {
            System.out.println("Préstamo no encontrado");
            return;
        }

        prestamo.setFechaDevolucion(LocalDate.now());

        Ejemplar ejemplar = prestamo.getEjemplar();
        ejemplar.setEstado("Disponible");

        LocalDate limite = prestamo.getFechaInicio().plusDays(15);
        if (prestamo.getFechaDevolucion().isAfter(limite)) {
            Usuario usuario = prestamo.getUsuario();
            LocalDate penalizacionActual = usuario.getPenalizacionHasta();
            LocalDate nuevaPenalizacion = LocalDate.now().plusDays(15);
            if (penalizacionActual != null && penalizacionActual.isAfter(LocalDate.now())) {
                nuevaPenalizacion = penalizacionActual.plusDays(15);
            }
            usuario.setPenalizacionHasta(nuevaPenalizacion);
            em.merge(usuario);
            System.out.println("Usuario penalizado hasta: " + nuevaPenalizacion);
        }

        em.getTransaction().begin();
        em.merge(prestamo);
        em.merge(ejemplar);
        em.getTransaction().commit();

        System.out.println("Préstamo devuelto");
    }

    private static void mostrarStockDisponible() {
        List<Ejemplar> disponibles = em.createQuery("SELECT e FROM Ejemplar e WHERE e.estado = 'Disponible'", Ejemplar.class).getResultList();

        System.out.println("=== Stock disponible ===");
        for (Ejemplar e : disponibles) {
            System.out.println("Libro: " + e.getLibro().getTitulo() + " - Ejemplar ID: " + e.getId());
        }
        System.out.println("Total disponibles: " + disponibles.size());
    }

    private static void cerrar() {
        em.close();
        emf.close();
        teclado.close();
    }
}
