package dulcearte.Views;

import dulcearte.Models.ArticuloDanable;
import dulcearte.Models.ArticuloDuradero;
import dulcearte.Models.ArticuloInventario;
import dulcearte.Utils.DateUtil;
import dulcearte.Utils.InventarioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class MainDulceGUI extends JFrame {
    private InventarioManager manager;
    private String fechaHoy;

    private JLabel lblFechaHoy;
    private JLabel lblPresupuesto;
    private JLabel lblEspacio;

    public MainDulceGUI() {
        super("Dulcearte - Sistema de Facturación (GUI)");
        manager = new InventarioManager(200.0, 200);
        fechaHoy = pedirFechaHoyInicial();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        JPanel topInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblFechaHoy = new JLabel("Fecha HOY: " + fechaHoy);
        lblPresupuesto = new JLabel("  Presupuesto disponible: " + manager.getPresupuestoDisponible());
        lblEspacio = new JLabel("  Espacio disponible: " + manager.espacioDisponible());
        topInfo.add(lblFechaHoy);
        topInfo.add(lblPresupuesto);
        topInfo.add(lblEspacio);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Alta", crearPanelAlta());
        tabs.add("Compra", crearPanelCompra());
        tabs.add("Consumo", crearPanelConsumo());
        tabs.add("Auditoría", crearPanelAuditoria());
        tabs.add("Buscar", crearPanelBuscar());
        tabs.add("Listar", crearPanelListar());
        tabs.add("Fecha HOY", crearPanelFecha());

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topInfo, BorderLayout.NORTH);
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private String pedirFechaHoyInicial() {
        String fecha;
        do {
            fecha = JOptionPane.showInputDialog(this,
                    "Ingrese la fecha de HOY (AAAA-MM-DD):",
                    "Fecha HOY",
                    JOptionPane.QUESTION_MESSAGE);
            if (fecha == null) { // cancel -> use a safe default
                fecha = "2026-01-01";
                break;
            }
            if (!DateUtil.esFechaValida(fecha)) {
                JOptionPane.showMessageDialog(this,
                        "Fecha inválida. Ejemplo válido: 2026-01-11",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                break;
            }
        } while (true);
        return fecha;
    }

    private JPanel crearPanelAlta() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;

        JLabel lblTipo = new JLabel("Tipo:");
        String[] tipos = {"Perecedero", "Duradero"};
        JComboBox<String> cbTipo = new JComboBox<>(tipos);

        JLabel lblCodigo = new JLabel("Código:");
        JTextField tfCodigo = new JTextField(20);

        JLabel lblNombre = new JLabel("Nombre:");
        JTextField tfNombre = new JTextField(20);

        JLabel lblCosto = new JLabel("Costo compra:");
        JTextField tfCosto = new JTextField(10);

        JLabel lblReorden = new JLabel("Punto reorden:");
        JTextField tfReorden = new JTextField(6);

        // Perecedero fields
        JLabel lblFechaLimite = new JLabel("Fecha límite (AAAA-MM-DD):");
        JTextField tfFechaLimite = new JTextField(12);
        JLabel lblDiasAlerta = new JLabel("Días alerta previa:");
        JTextField tfDiasAlerta = new JTextField(4);

        // Duradero fields
        JLabel lblDiasEspera = new JLabel("Días espera proveedor:");
        JTextField tfDiasEspera = new JTextField(4);

        int row = 0;
        c.gridx = 0; c.gridy = row; form.add(lblTipo, c);
        c.gridx = 1; form.add(cbTipo, c); row++;

        c.gridx = 0; c.gridy = row; form.add(lblCodigo, c);
        c.gridx = 1; form.add(tfCodigo, c); row++;

        c.gridx = 0; c.gridy = row; form.add(lblNombre, c);
        c.gridx = 1; form.add(tfNombre, c); row++;

        c.gridx = 0; c.gridy = row; form.add(lblCosto, c);
        c.gridx = 1; form.add(tfCosto, c); row++;

        c.gridx = 0; c.gridy = row; form.add(lblReorden, c);
        c.gridx = 1; form.add(tfReorden, c); row++;

        c.gridx = 0; c.gridy = row; form.add(lblFechaLimite, c);
        c.gridx = 1; form.add(tfFechaLimite, c); row++;

        c.gridx = 0; c.gridy = row; form.add(lblDiasAlerta, c);
        c.gridx = 1; form.add(tfDiasAlerta, c); row++;

        c.gridx = 0; c.gridy = row; form.add(lblDiasEspera, c);
        c.gridx = 1; form.add(tfDiasEspera, c); row++;

        // Initially show Perecedero fields, hide duradero-specific if necessary
        lblDiasEspera.setVisible(false);
        tfDiasEspera.setVisible(false);

        cbTipo.addActionListener(e -> {
            boolean perecedero = cbTipo.getSelectedIndex() == 0;
            lblFechaLimite.setVisible(perecedero);
            tfFechaLimite.setVisible(perecedero);
            lblDiasAlerta.setVisible(perecedero);
            tfDiasAlerta.setVisible(perecedero);

            lblDiasEspera.setVisible(!perecedero);
            tfDiasEspera.setVisible(!perecedero);
            form.revalidate();
            form.repaint();
        });

        JButton btnAgregar = new JButton("Agregar artículo");
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                try {
                    String codigo = tfCodigo.getText().trim();
                    String nombre = tfNombre.getText().trim();
                    if (codigo.isEmpty() || nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(MainDulceGUI.this,
                                "Complete los campos Código y Nombre.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double costo = Double.parseDouble(tfCosto.getText().trim());
                    int reorden = Integer.parseInt(tfReorden.getText().trim());

                    if (cbTipo.getSelectedIndex() == 0) { // Perecedero
                        String fechaLimite = tfFechaLimite.getText().trim();
                        if (!DateUtil.esFechaValida(fechaLimite)) {
                            JOptionPane.showMessageDialog(MainDulceGUI.this,
                                    "Fecha límite inválida. Formato AAAA-MM-DD",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        int diasAlerta = Integer.parseInt(tfDiasAlerta.getText().trim());
                        ArticuloDanable a = new ArticuloDanable(codigo, nombre, costo, reorden, fechaLimite, diasAlerta);
                        manager.agregarArticulo(a);
                        JOptionPane.showMessageDialog(MainDulceGUI.this, "Perecedero agregado.");
                    } else { // Duradero
                        int diasEspera = Integer.parseInt(tfDiasEspera.getText().trim());
                        ArticuloDuradero a = new ArticuloDuradero(codigo, nombre, costo, reorden, diasEspera);
                        manager.agregarArticulo(a);
                        JOptionPane.showMessageDialog(MainDulceGUI.this, "Duradero agregado.");
                    }

                    // clear
                    tfCodigo.setText("");
                    tfNombre.setText("");
                    tfCosto.setText("");
                    tfReorden.setText("");
                    tfFechaLimite.setText("");
                    tfDiasAlerta.setText("");
                    tfDiasEspera.setText("");

                    actualizarInfoSuperior();

                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(MainDulceGUI.this,
                            "Error: asegúrese de ingresar números donde corresponde.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel south = new JPanel();
        south.add(btnAgregar);

        p.add(form, BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelCompra() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblNombre = new JLabel("Nombre del artículo:");
        JTextField tfNombre = new JTextField(20);
        JLabel lblCantidad = new JLabel("Cantidad:");
        JTextField tfCantidad = new JTextField(6);
        JButton btnComprar = new JButton("Ejecutar compra");

        btnComprar.addActionListener(e -> {
            try {
                String nombre = tfNombre.getText().trim();
                int cantidad = Integer.parseInt(tfCantidad.getText().trim());
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "Cantidad inválida: debe ser entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                manager.ejecutarCompra(nombre, cantidad);
                JOptionPane.showMessageDialog(this, "Compra ejecutada (revise consola para detalles).");
                actualizarInfoSuperior();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida: debe ser entero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(lblNombre);
        form.add(tfNombre);
        form.add(lblCantidad);
        form.add(tfCantidad);
        form.add(btnComprar);

        p.add(form, BorderLayout.NORTH);
        return p;
    }

    private JPanel crearPanelConsumo() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblNombre = new JLabel("Nombre del artículo:");
        JTextField tfNombre = new JTextField(20);
        JLabel lblCantidad = new JLabel("Cantidad:");
        JTextField tfCantidad = new JTextField(6);
        JButton btnConsumir = new JButton("Registrar consumo");

        btnConsumir.addActionListener(e -> {
            try {
                String nombre = tfNombre.getText().trim();
                int cantidad = Integer.parseInt(tfCantidad.getText().trim());
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "Cantidad inválida: debe ser entero positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                manager.registrarConsumo(nombre, cantidad);
                JOptionPane.showMessageDialog(this, "Consumo registrado (revise consola para detalles).");
                actualizarInfoSuperior();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida: debe ser entero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(lblNombre);
        form.add(tfNombre);
        form.add(lblCantidad);
        form.add(tfCantidad);
        form.add(btnConsumir);

        p.add(form, BorderLayout.NORTH);
        return p;
    }

    private JPanel crearPanelAuditoria() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea salida = new JTextArea();
        salida.setEditable(false);
        salida.setText("Auditoría utiliza la fecha HOY: " + fechaHoy + "\nPulse el botón para ejecutar auditoría.\n");
        JButton btnAuditar = new JButton("Ejecutar auditoría de alertas");
        btnAuditar.addActionListener(e -> {
            // Capture System.out temporarily
            PrintStream originalOut = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            try {
                System.setOut(ps);
                manager.auditoriaAlertas(fechaHoy);
            } finally {
                System.out.flush();
                System.setOut(originalOut);
            }
            String output = baos.toString();
            if (output == null || output.isEmpty()) output = "(Sin salida)";
            salida.setText(output);
            // show in a scrollable dialog for convenience
            JTextArea dialogArea = new JTextArea(output);
            dialogArea.setEditable(false);
            dialogArea.setLineWrap(true);
            dialogArea.setWrapStyleWord(true);
            JScrollPane scroll = new JScrollPane(dialogArea);
            scroll.setPreferredSize(new Dimension(700, 400));
            JOptionPane.showMessageDialog(this, scroll, "Reporte de Auditoría", JOptionPane.INFORMATION_MESSAGE);
        });

        p.add(new JScrollPane(salida), BorderLayout.CENTER);
        JPanel s = new JPanel();
        s.add(btnAuditar);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelBuscar() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblNombre = new JLabel("Nombre:");
        JTextField tfNombre = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        JTextArea resultado = new JTextArea(10, 50);
        resultado.setEditable(false);

        btnBuscar.addActionListener(e -> {
            String nombre = tfNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el nombre a buscar.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            ArticuloInventario a = manager.buscarPorNombre(nombre);
            if (a == null) {
                resultado.setText("No encontrado.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Encontrado: ").append(a).append("\n");
                String alerta = a.verificarAlerta(fechaHoy);
                if (alerta != null && !alerta.isEmpty()) {
                    sb.append("Alerta actual: ").append(alerta).append("\n");
                } else {
                    sb.append("Sin alertas.\n");
                }
                resultado.setText(sb.toString());
            }
        });

        top.add(lblNombre);
        top.add(tfNombre);
        top.add(btnBuscar);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(resultado), BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelListar() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setText("Pulse \"Listar\" para que el manager liste los artículos.\n");
        JButton btnListar = new JButton("Listar artículos");
        btnListar.addActionListener(e -> {
            // Capture System.out temporarily
            PrintStream originalOut = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            try {
                System.setOut(ps);
                manager.listarArticulos();
            } finally {
                System.out.flush();
                System.setOut(originalOut);
            }
            String output = baos.toString();
            if (output == null || output.isEmpty()) output = "(Sin artículos registrados)";
            ta.setText(output);
            // also show a dialog with the full listing
            JTextArea dialogArea = new JTextArea(output);
            dialogArea.setEditable(false);
            dialogArea.setLineWrap(false);
            JScrollPane scroll = new JScrollPane(dialogArea);
            scroll.setPreferredSize(new Dimension(700, 400));
            JOptionPane.showMessageDialog(this, scroll, "Listado de Artículos", JOptionPane.INFORMATION_MESSAGE);
        });

        p.add(new JScrollPane(ta), BorderLayout.CENTER);
        JPanel s = new JPanel();
        s.add(btnListar);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelFecha() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblFecha = new JLabel("Fecha HOY (AAAA-MM-DD):");
        JTextField tfFecha = new JTextField(fechaHoy, 12);
        JButton btnCambiar = new JButton("Cambiar fecha");
        btnCambiar.addActionListener(e -> {
            String nueva = tfFecha.getText().trim();
            if (!DateUtil.esFechaValida(nueva)) {
                JOptionPane.showMessageDialog(this, "Fecha inválida. Formato AAAA-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            fechaHoy = nueva;
            lblFechaHoy.setText("Fecha HOY: " + fechaHoy);
            JOptionPane.showMessageDialog(this, "Fecha HOY cambiada a: " + fechaHoy);
        });

        p.add(lblFecha);
        p.add(tfFecha);
        p.add(btnCambiar);
        return p;
    }

    private void actualizarInfoSuperior() {
        try {
            lblPresupuesto.setText("  Presupuesto disponible: " + manager.getPresupuestoDisponible());
            lblEspacio.setText("  Espacio disponible: " + manager.espacioDisponible());
        } catch (Exception ex) {
            // ignore
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainDulceGUI gui = new MainDulceGUI();
            gui.setVisible(true);
        });
    }
}

