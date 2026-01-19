package negocio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SistemaMediateca {
    private List<Material> catalogo;
    private List<Socio> socios;
    private List<Prestamo> prestamos;

    public SistemaMediateca() {
        catalogo = new ArrayList<>();
        socios = new ArrayList<>();
        prestamos = new ArrayList<>();
    }

    public List<Material> getCatalogo() { return catalogo; }
    public List<Socio> getSocios() { return socios; }
    public List<Prestamo> getPrestamos() { return prestamos; }

    public void registrarLibro(String id, String titulo, String autor, int paginas) {
        if (buscarMaterialPorId(id) != null) throw new ExcepcionValidacion("Ya existe un material con ese ID");
        catalogo.add(new Libro(id, titulo, autor, paginas));
    }

    public void registrarRevista(String id, String titulo, String editorial, int edicion) {
        if (buscarMaterialPorId(id) != null) throw new ExcepcionValidacion("Ya existe un material con ese ID");
        catalogo.add(new Revista(id, titulo, editorial, edicion));
    }

    public Socio registrarSocio(String nombre, String correo, String telefono, String nivel) {
        Socio s = new Socio(nombre, correo, telefono, nivel);
        socios.add(s);
        return s;
    }

    public Prestamo prestarMaterial(int codSocio, String idMaterial, String salida, String limite) {
        Socio socio = buscarSocioPorCodigo(codSocio);
        if (socio == null) throw new ExcepcionValidacion("No existe socio con ese codigo");

        Material mat = buscarMaterialPorId(idMaterial);
        if (mat == null) throw new ExcepcionValidacion("No existe material con ese ID");

        mat.prestar();
        Prestamo p = new Prestamo(socio, mat, salida, limite);
        prestamos.add(p);
        return p;
    }

    public void devolverMaterial(String idMaterial, String fechaDev) {
        Material mat = buscarMaterialPorId(idMaterial);
        if (mat == null) throw new ExcepcionValidacion("No existe material con ese ID");

        Prestamo prestamoActivo = null;
        for (Prestamo p : prestamos) {
            if (p.isActivo() && p.getMaterial().getId().equalsIgnoreCase(idMaterial)) {
                prestamoActivo = p;
                break;
            }
        }
        if (prestamoActivo == null) throw new ExcepcionValidacion("Ese material no tiene prestamo activo");

        prestamoActivo.cerrarPrestamo(fechaDev);
        mat.devolver();
    }

    public Material buscarMaterialPorId(String id) {
        if (id == null) return null;
        for (Material m : catalogo) {
            if (m.getId().equalsIgnoreCase(id.trim())) return m;
        }
        return null;
    }

    public Socio buscarSocioPorCodigo(int cod) {
        for (Socio s : socios) {
            if (s.getCodigo() == cod) return s;
        }
        return null;
    }

    // =========================
    // OPCION B: CARGA DESDE ARCHIVO TXT
    // =========================
    public void cargarDesdeArchivo(String ruta) {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                String[] p = linea.split(";");
                String tipo = p[0].trim().toUpperCase();

                if (tipo.equals("LIBRO")) {
                    String id = p[1];
                    String titulo = p[2];
                    String autor = p[3];
                    int paginas = Integer.parseInt(p[4].trim());
                    registrarLibro(id, titulo, autor, paginas);

                } else if (tipo.equals("REVISTA")) {
                    String id = p[1];
                    String titulo = p[2];
                    String editorial = p[3];
                    int edicion = Integer.parseInt(p[4].trim());
                    registrarRevista(id, titulo, editorial, edicion);

                } else if (tipo.equals("SOCIO")) {
                    String nombre = p[1];
                    String correo = p[2];
                    String telefono = p[3];
                    String nivel = p[4];
                    registrarSocio(nombre, correo, telefono, nivel);

                } else {
                    throw new ExcepcionValidacion("Linea desconocida: " + linea);
                }
            }

        } catch (NumberFormatException e) {
            throw new ExcepcionValidacion("Numero invalido en archivo: " + e.getMessage());
        } catch (Exception e) {
            throw new ExcepcionValidacion("No se pudo cargar el archivo: " + e.getMessage());
        }
    }
}
