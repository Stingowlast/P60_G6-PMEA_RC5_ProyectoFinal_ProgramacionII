package dulcearte.Models;

public abstract class ArticuloInventario {
    private String codigo;
    private String nombre;
    private double costoCompra;
    private int puntoReorden;
    private int stockActual;

    public ArticuloInventario(String codigo, String nombre, double costoCompra, int puntoReorden) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.costoCompra = costoCompra;
        this.puntoReorden = puntoReorden;
        this.stockActual = 0;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getCostoCompra() { return costoCompra; }
    public int getPuntoReorden() { return puntoReorden; }
    public int getStockActual() { return stockActual; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public void registrarEntrada(int cantidad) {
        if (cantidad <= 0) {
            System.out.println("La cantidad debe ser > 0.");
            return;
        }
        stockActual += cantidad;
    }

    public boolean registrarSalida(int cantidad) {
        if (cantidad <= 0) {
            System.out.println("La cantidad debe ser > 0.");
            return false;
        }
        if (cantidad > stockActual) {
            System.out.println("Stock insuficiente. Disponible: " + stockActual);
            return false;
        }
        stockActual -= cantidad;
        return true;
    }

    public abstract String verificarAlerta(String fechaHoy);

    public abstract String tipo();

    @Override
    public String toString() {
        return "Codigo=" + codigo + ", \nNombre=" + nombre + ", \nCosto=" + costoCompra + ", \nReorden=" + puntoReorden + ", \nStock=" + stockActual + ", \nTipo=" + tipo();
    }
}
