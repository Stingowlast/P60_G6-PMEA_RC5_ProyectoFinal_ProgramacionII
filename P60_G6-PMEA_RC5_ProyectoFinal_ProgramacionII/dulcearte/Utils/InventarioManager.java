package dulcearte.Utils;

import dulcearte.Models.ArticuloInventario;

import java.util.ArrayList;
import java.util.List;

public class InventarioManager {
    private double presupuestoMaximo;
    private double presupuestoDisponible;
    private int limiteEspacio;

    private List<ArticuloInventario> listaArticulos;

    public InventarioManager(double presupuestoMaximo, int limiteEspacio) {
        this.presupuestoMaximo = presupuestoMaximo;
        this.presupuestoDisponible = presupuestoMaximo;
        this.limiteEspacio = limiteEspacio;
        this.listaArticulos = new ArrayList<>();
    }

    public double getPresupuestoDisponible() { return presupuestoDisponible; }
    public int getLimiteEspacio() { return limiteEspacio; }

    public int espacioOcupado() {
        int total = 0;
        for (ArticuloInventario a : listaArticulos) total += a.getStockActual();
        return total;
    }

    public int espacioDisponible() {
        return limiteEspacio - espacioOcupado();
    }

    public ArticuloInventario buscarPorNombre(String nombre) {
        for (ArticuloInventario a : listaArticulos) {
            if (a.getNombre().equalsIgnoreCase(nombre)) return a;
        }
        return null;
    }

    public void agregarArticulo(ArticuloInventario articulo) {
        if (buscarPorNombre(articulo.getNombre()) != null) {
            System.out.println("Ya existe un artículo con ese nombre.");
            return;
        }
        listaArticulos.add(articulo);
        System.out.println("Artículo agregado: " + articulo.getNombre() + " (" + articulo.tipo() + ")");
    }

    public void ejecutarCompra(String nombreArticulo, int cantidad) {
        ArticuloInventario art = buscarPorNombre(nombreArticulo);
        if (art == null) {
            System.out.println("No encontrado: " + nombreArticulo);
            return;
        }
        if (cantidad <= 0) {
            System.out.println("La cantidad debe ser > 0.");
            return;
        }

        double costoTotal = art.getCostoCompra() * cantidad;

        if (costoTotal > presupuestoDisponible) {
            System.out.println("Compra rechazada: presupuesto insuficiente. Necesita " + costoTotal + " y hay " + presupuestoDisponible);
            return;
        }

        if (cantidad > espacioDisponible()) {
            System.out.println("Compra rechazada: no hay espacio. Necesita " + cantidad + " y hay " + espacioDisponible());
            return;
        }

        art.registrarEntrada(cantidad);
        presupuestoDisponible -= costoTotal;

        System.out.println("Compra exitosa. Stock de '" + art.getNombre() + "'=" + art.getStockActual());
        System.out.println("Presupuesto disponible=" + presupuestoDisponible);
        System.out.println("Espacio disponible=" + espacioDisponible());
    }

    public void registrarConsumo(String nombreArticulo, int cantidad) {
        ArticuloInventario art = buscarPorNombre(nombreArticulo);
        if (art == null) {
            System.out.println("No encontrado: " + nombreArticulo);
            return;
        }

        if (art.registrarSalida(cantidad)) {
            System.out.println("Consumo registrado. Stock de '" + art.getNombre() + "'=" + art.getStockActual());
            if (art.getStockActual() <= art.getPuntoReorden()) {
                System.out.println("ALERTA: stock bajo (punto reorden=" + art.getPuntoReorden() + ")");
            }
        }
    }

    public void auditoriaAlertas(String fechaHoy) {
        System.out.println("===== REPORTE DE ALERTAS =====");

        boolean hayCriticas = false;
        boolean hayLogisticas = false;

        System.out.println("\n--- ALERTAS CRITICAS ---");
        for (ArticuloInventario a : listaArticulos) {
            String alertas = a.verificarAlerta(fechaHoy);
            if (alertas != null && !alertas.isEmpty()) {
                String[] lineas = alertas.split("\n");
                for (String l : lineas) {
                    if (l.startsWith("CRITICA:")) {
                        System.out.println(l);
                        hayCriticas = true;
                    }
                }
            }
        }
        if (!hayCriticas) {
            System.out.println("Sin alertas críticas.");
        }

        System.out.println("\n--- ALERTAS LOGISTICAS ---");
        for (ArticuloInventario a : listaArticulos) {
            String alertas = a.verificarAlerta(fechaHoy);
            if (alertas != null && !alertas.isEmpty()) {
                String[] lineas = alertas.split("\n");
                for (String l : lineas) {
                    if (l.startsWith("LOGISTICA:")) {
                        System.out.println(l);
                        hayLogisticas = true;
                    }
                }
            }
        }
        if (!hayLogisticas) {
            System.out.println("Sin alertas logísticas.");
        }

        System.out.println("\n==============================");
    }

    public void listarArticulos() {
        System.out.println("===== LISTA DE ARTICULOS =====");
        if (listaArticulos.isEmpty()) {
            System.out.println("No hay artículos registrados.");
        } else {
            for (ArticuloInventario a : listaArticulos) {
                System.out.println(a);
            }
        }
        System.out.println("==============================");
    }
}
