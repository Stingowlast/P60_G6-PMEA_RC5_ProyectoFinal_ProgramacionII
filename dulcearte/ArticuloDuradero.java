package dulcearte;

public class ArticuloDuradero extends ArticuloInventario {
    private int diasEspera;

    public ArticuloDuradero(String codigo, String nombre, double costoCompra, int puntoReorden, int diasEspera) {
        super(codigo, nombre, costoCompra, puntoReorden);
        this.diasEspera = diasEspera;
    }

    @Override
    public String tipo() {
        return "No perecedero";
    }

    @Override
    public String verificarAlerta(String fechaHoy) {
        if (getStockActual() <= getPuntoReorden()) {
            return "LOGISTICA: '" + getNombre() + "' requiere reabastecer. Stock=" + getStockActual() + ", Reorden=" + getPuntoReorden() + ", Entrega aprox=" + diasEspera + " día(s).";
        }
        return "";
    }
}
