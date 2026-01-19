package cliente;

import negocio.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
