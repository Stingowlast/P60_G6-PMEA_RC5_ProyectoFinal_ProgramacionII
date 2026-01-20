package cliente;

import negocio.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainGUI extends JFrame {

    private SistemaMediateca sistema;

    private DefaultTableModel modeloCatalogo;
    private DefaultTableModel modeloSocios;
    private DefaultTableModel modeloPrestamos;

    private JTable tablaCatalogo;
    private JTable tablaSocios;
    private JTable tablaPrestamos;

    public MainGUI() {
        super("Mediateca Cultura Viva - Sistema de Gestion");

        sistema = new SistemaMediateca();
        cargarDatosIniciales();

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Catalogo", crearPanelCatalogo());
        tabs.add("Socios", crearPanelSocios());
        tabs.add("Prestamos", crearPanelPrestamos());

        add(tabs, BorderLayout.CENTER);
        refrescarTablas();
    }

    private void cargarDatosIniciales() {
        try {
            File f = new File("datos_iniciales.txt");
            if (!f.exists()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontro datos_iniciales.txt en la raiz del proyecto.\nSelecciona el archivo manualmente.",
                        "Archivo no encontrado",
                        JOptionPane.WARNING_MESSAGE
                );
                JFileChooser chooser = new JFileChooser();
                int res = chooser.showOpenDialog(this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    sistema.cargarDesdeArchivo(chooser.getSelectedFile().getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(this, "Se ejecutara sin datos iniciales.");
                }
            } else {
                sistema.cargarDesdeArchivo("datos_iniciales.txt");
            }
        } catch (ExcepcionValidacion e) {
            JOptionPane.showMessageDialog(this, "Error cargando datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearPanelCatalogo() {
        JPanel panel = new JPanel(new BorderLayout());

        modeloCatalogo = new DefaultTableModel(
                new Object[]{"ID", "Titulo", "Autor/Editorial", "Estado", "Tipo", "Dato extra"}, 0
        );
        tablaCatalogo = new JTable(modeloCatalogo);

        panel.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> refrescarTablas());
        botones.add(btnRefrescar);

        // Botones para agregar/modificar/guardar materiales (integrado en Main)
        JButton btnAgregar = new JButton("Agregar material");
        btnAgregar.addActionListener(e -> agregarMaterialGUI());
        botones.add(btnAgregar);

        JButton btnModificar = new JButton("Modificar material");
        btnModificar.addActionListener(e -> modificarMaterialGUI());
        botones.add(btnModificar);

        JButton btnGuardar = new JButton("Guardar datos");
        btnGuardar.addActionListener(e -> guardarDatosIniciales());
        botones.add(btnGuardar);

        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelSocios() {
        JPanel panel = new JPanel(new BorderLayout());

        modeloSocios = new DefaultTableModel(
                new Object[]{"Codigo", "Nombre", "Correo", "Telefono", "Nivel"}, 0
        );
        tablaSocios = new JTable(modeloSocios);

        panel.add(new JScrollPane(tablaSocios), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("Registrar socio");
        btnRegistrar.addActionListener(e -> registrarSocioGUI());

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> refrescarTablas());

        botones.add(btnRegistrar);
        botones.add(btnRefrescar);

        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPrestamos() {
        JPanel panel = new JPanel(new BorderLayout());

        modeloPrestamos = new DefaultTableModel(
                new Object[]{"ID Prestamo", "Codigo Socio", "Nombre Socio", "ID Material", "Titulo", "Salida", "Limite", "Devolucion", "Activo"}, 0
        );
        tablaPrestamos = new JTable(modeloPrestamos);

        panel.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPrestar = new JButton("Prestar");
        btnPrestar.addActionListener(e -> prestarGUI());

        JButton btnDevolver = new JButton("Devolver");
        btnDevolver.addActionListener(e -> devolverGUI());

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> refrescarTablas());

        botones.add(btnPrestar);
        botones.add(btnDevolver);
        botones.add(btnRefrescar);

        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private void registrarSocioGUI() {
        try {
            String nombre = JOptionPane.showInputDialog(this, "Nombre completo:");
            String correo = JOptionPane.showInputDialog(this, "Correo:");
            String telefono = JOptionPane.showInputDialog(this, "Telefono:");
            String nivel = JOptionPane.showInputDialog(this, "Nivel membresia (Regular/Premium):");

            Socio s = sistema.registrarSocio(nombre, correo, telefono, nivel);
            JOptionPane.showMessageDialog(this, "Socio creado. Codigo: " + s.getCodigo());
            refrescarTablas();

        } catch (ExcepcionValidacion ex) {
            JOptionPane.showMessageDialog(this, "Validacion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prestarGUI() {
        try {
            String codStr = JOptionPane.showInputDialog(this, "Codigo de socio:");
            String idMat = JOptionPane.showInputDialog(this, "ID del material:");
            String salida = JOptionPane.showInputDialog(this, "Fecha salida (YYYY-MM-DD):");
            String limite = JOptionPane.showInputDialog(this, "Fecha limite (YYYY-MM-DD):");

            if (codStr == null || idMat == null || salida == null || limite == null) return;
            if (!salida.matches("\\d{4}-\\d{2}-\\d{2}") || !limite.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Fechas deben tener formato YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int cod = Integer.parseInt(codStr.trim());
            Prestamo p = sistema.prestarMaterial(cod, idMat, salida, limite);

            JOptionPane.showMessageDialog(this, "Prestamo creado: #" + p.getIdPrestamo());
            refrescarTablas();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un numero valido para el codigo.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ExcepcionValidacion ex) {
            JOptionPane.showMessageDialog(this, "Validacion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void devolverGUI() {
        try {
            String idMat = JOptionPane.showInputDialog(this, "ID del material a devolver:");
            String fechaDev = JOptionPane.showInputDialog(this, "Fecha devolucion (YYYY-MM-DD):");
            if (idMat == null || fechaDev == null) return;
            if (!fechaDev.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Fecha debe tener formato YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sistema.devolverMaterial(idMat, fechaDev);

            JOptionPane.showMessageDialog(this, "Devolucion registrada.");
            refrescarTablas();

        } catch (ExcepcionValidacion ex) {
            JOptionPane.showMessageDialog(this, "Validacion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTablas() {
        // Catalogo
        modeloCatalogo.setRowCount(0);
        for (Material m : sistema.getCatalogo()) {
            String tipo = (m instanceof Libro) ? "Libro" : (m instanceof Revista) ? "Revista" : "Material";
            String extra = "-";
            if (m instanceof Libro) extra = "Paginas: " + ((Libro) m).getNumPaginas();
            if (m instanceof Revista) extra = "Edicion: " + ((Revista) m).getNumEdicion();

            modeloCatalogo.addRow(new Object[]{
                    m.getId(),
                    m.getTitulo(),
                    m.getAutorEditorial(),
                    m.getEstado(),
                    tipo,
                    extra
            });
        }

        // Socios
        modeloSocios.setRowCount(0);
        for (Socio s : sistema.getSocios()) {
            modeloSocios.addRow(new Object[]{
                    s.getCodigo(),
                    s.getNombreCompleto(),
                    s.getCorreo(),
                    s.getTelefono(),
                    s.getNivelMembresia()
            });
        }

        // Prestamos
        modeloPrestamos.setRowCount(0);
        for (Prestamo p : sistema.getPrestamos()) {
            Socio s = p.getSocio();
            Material m = p.getMaterial();

            modeloPrestamos.addRow(new Object[]{
                    p.getIdPrestamo(),
                    s.getCodigo(),
                    s.getNombreCompleto(),
                    m.getId(),
                    m.getTitulo(),
                    p.getFechaSalida(),
                    p.getFechaLimite(),
                    (p.getFechaDevolucion() == null ? "-" : p.getFechaDevolucion()),
                    p.isActivo()
            });
        }
    }

    // --- NUEVOS METODOS: agregar/modificar/guardar materiales desde la UI principal ---
    private void agregarMaterialGUI() {
        String[] options = {"Libro", "Revista"};
        int choice = JOptionPane.showOptionDialog(this, "Tipo de material:", "Agregar material",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == JOptionPane.CLOSED_OPTION) return;

        try {
            if (choice == 0) { // Libro
                String id = JOptionPane.showInputDialog(this, "ID:");
                String titulo = JOptionPane.showInputDialog(this, "Titulo:");
                String autor = JOptionPane.showInputDialog(this, "Autor:");
                String paginasStr = JOptionPane.showInputDialog(this, "Numero de paginas:");
                if (id == null || titulo == null || autor == null || paginasStr == null) return; // cancelled
                id = id.trim(); titulo = titulo.trim(); autor = autor.trim(); paginasStr = paginasStr.trim();
                if (id.isEmpty() || titulo.isEmpty() || autor.isEmpty() || paginasStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int paginas = Integer.parseInt(paginasStr);
                if (paginas <= 0) { JOptionPane.showMessageDialog(this, "Paginas debe ser mayor que 0.", "Error", JOptionPane.ERROR_MESSAGE); return; }
                sistema.registrarLibro(id, titulo, autor, paginas);
            } else { // Revista
                String id = JOptionPane.showInputDialog(this, "ID:");
                String titulo = JOptionPane.showInputDialog(this, "Titulo:");
                String editorial = JOptionPane.showInputDialog(this, "Editorial:");
                String edicionStr = JOptionPane.showInputDialog(this, "Numero de edicion:");
                if (id == null || titulo == null || editorial == null || edicionStr == null) return;
                id = id.trim(); titulo = titulo.trim(); editorial = editorial.trim(); edicionStr = edicionStr.trim();
                if (id.isEmpty() || titulo.isEmpty() || editorial.isEmpty() || edicionStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int edicion = Integer.parseInt(edicionStr);
                if (edicion <= 0) { JOptionPane.showMessageDialog(this, "Edicion debe ser mayor que 0.", "Error", JOptionPane.ERROR_MESSAGE); return; }
                sistema.registrarRevista(id, titulo, editorial, edicion);
            }
            JOptionPane.showMessageDialog(this, "Material agregado correctamente.");
            refrescarTablas();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Numero invalido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ExcepcionValidacion ex) {
            JOptionPane.showMessageDialog(this, "Validacion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarMaterialGUI() {
        int row = tablaCatalogo.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un material primero.");
            return;
        }
        String id = (String) modeloCatalogo.getValueAt(row, 0);

        List<Material> lista = sistema.getCatalogo();
        int index = -1;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId().equalsIgnoreCase(id)) { index = i; break; }
        }
        if (index == -1) return;

        Material actual = lista.get(index);
        try {
            if (actual instanceof Libro) {
                Libro l = (Libro) actual;
                String newId = JOptionPane.showInputDialog(this, "ID:", l.getId());
                String newTitulo = JOptionPane.showInputDialog(this, "Titulo:", l.getTitulo());
                String newAutor = JOptionPane.showInputDialog(this, "Autor:", l.getAutorEditorial());
                String paginasStr = JOptionPane.showInputDialog(this, "Numero de paginas:", String.valueOf(l.getNumPaginas()));
                if (newId == null || newTitulo == null || newAutor == null || paginasStr == null) return;
                newId = newId.trim(); newTitulo = newTitulo.trim(); newAutor = newAutor.trim(); paginasStr = paginasStr.trim();
                if (newId.isEmpty() || newTitulo.isEmpty() || newAutor.isEmpty() || paginasStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int paginas = Integer.parseInt(paginasStr);

                // si cambian el id y ya existe otro material con ese id
                if (!newId.equalsIgnoreCase(l.getId())) {
                    Material existe = sistema.buscarMaterialPorId(newId);
                    if (existe != null) {
                        int opt = JOptionPane.showConfirmDialog(this, "Ya existe un material con ese ID. Desea sobrescribirlo? \n(Si el material existente tiene prestamos activos la operacion se cancelara)", "Confirmar sobrescritura", JOptionPane.YES_NO_OPTION);
                        if (opt != JOptionPane.YES_OPTION) return;

                        // verificar prestamos activos en el material a sobrescribir
                        for (Prestamo p : sistema.getPrestamos()) {
                            if (p.isActivo() && p.getMaterial().getId().equalsIgnoreCase(existe.getId())) {
                                JOptionPane.showMessageDialog(this, "El material a sobrescribir tiene prestamos activos. Operacion cancelada.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        // eliminar material existente (sin prestamos activos)
                        lista.remove(existe);
                    }
                }

                // reemplazar actual (nota: se reemplaza el objeto en la lista)
                lista.set(index, new Libro(newId, newTitulo, newAutor, paginas));

            } else if (actual instanceof Revista) {
                Revista r = (Revista) actual;
                String newId = JOptionPane.showInputDialog(this, "ID:", r.getId());
                String newTitulo = JOptionPane.showInputDialog(this, "Titulo:", r.getTitulo());
                String newEditorial = JOptionPane.showInputDialog(this, "Editorial:", r.getAutorEditorial());
                String edicionStr = JOptionPane.showInputDialog(this, "Numero de edicion:", String.valueOf(r.getNumEdicion()));
                if (newId == null || newTitulo == null || newEditorial == null || edicionStr == null) return;
                newId = newId.trim(); newTitulo = newTitulo.trim(); newEditorial = newEditorial.trim(); edicionStr = edicionStr.trim();
                if (newId.isEmpty() || newTitulo.isEmpty() || newEditorial.isEmpty() || edicionStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int edicion = Integer.parseInt(edicionStr);

                if (!newId.equalsIgnoreCase(r.getId())) {
                    Material existe = sistema.buscarMaterialPorId(newId);
                    if (existe != null) {
                        int opt = JOptionPane.showConfirmDialog(this, "Ya existe un material con ese ID. Desea sobrescribirlo? \n(Si el material existente tiene prestamos activos la operacion se cancelara)", "Confirmar sobrescritura", JOptionPane.YES_NO_OPTION);
                        if (opt != JOptionPane.YES_OPTION) return;

                        for (Prestamo p : sistema.getPrestamos()) {
                            if (p.isActivo() && p.getMaterial().getId().equalsIgnoreCase(existe.getId())) {
                                JOptionPane.showMessageDialog(this, "El material a sobrescribir tiene prestamos activos. Operacion cancelada.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        lista.remove(existe);
                    }
                }

                lista.set(index, new Revista(newId, newTitulo, newEditorial, edicion));
            }

            JOptionPane.showMessageDialog(this, "Material modificado correctamente.");
            refrescarTablas();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Numero invalido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ExcepcionValidacion ex) {
            JOptionPane.showMessageDialog(this, "Validacion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarDatosIniciales() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("datos_iniciales.txt"))) {
            // materiales
            for (Material m : sistema.getCatalogo()) {
                if (m instanceof Libro) {
                    Libro l = (Libro) m;
                    bw.write(String.format("LIBRO;%s;%s;%s;%d;%s", l.getId(), l.getTitulo(), l.getAutorEditorial(), l.getNumPaginas(), l.getEstado()));
                    bw.newLine();
                } else if (m instanceof Revista) {
                    Revista r = (Revista) m;
                    bw.write(String.format("REVISTA;%s;%s;%s;%d;%s", r.getId(), r.getTitulo(), r.getAutorEditorial(), r.getNumEdicion(), r.getEstado()));
                    bw.newLine();
                }
            }
            // socios
            for (Socio s : sistema.getSocios()) {
                bw.write(String.format("SOCIO;%s;%s;%s;%s", s.getNombreCompleto(), s.getCorreo(), s.getTelefono(), s.getNivelMembresia()));
                bw.newLine();
            }

            JOptionPane.showMessageDialog(this, "Datos guardados en datos_iniciales.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error guardando archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
