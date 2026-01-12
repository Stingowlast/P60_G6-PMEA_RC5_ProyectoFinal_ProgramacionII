package dulcearte.Models;

import dulcearte.Utils.DateUtil;

public class ArticuloDanable extends ArticuloInventario {
    private String fechaLimite;
    private int diasAlertaPrevia;

    public ArticuloDanable(String codigo, String nombre, double costoCompra, int puntoReorden, String fechaLimite, int diasAlertaPrevia) {
        super(codigo, nombre, costoCompra, puntoReorden);
        this.fechaLimite = fechaLimite;
        this.diasAlertaPrevia = diasAlertaPrevia;
    }

    public String getFechaLimite() { return fechaLimite; }

    @Override
    public String tipo() {
        return "Perecedero";
    }

    @Override
    public String verificarAlerta(String fechaHoy) {
        String mensaje = "";

        if (getStockActual() <= getPuntoReorden()) {
            mensaje += "LOGISTICA: '" + getNombre() + "' stock bajo (" + getStockActual() + "), reorden=" + getPuntoReorden() + ".\n";
        }
        int dias = DateUtil.diasEntre(fechaHoy, fechaLimite);

        if (dias < 0) {
            mensaje += "CRITICA: '" + getNombre() + "' está VENCIDO. Fecha límite: " + fechaLimite + ".\n";
        } else if (dias <= diasAlertaPrevia) {
            mensaje += "CRITICA: '" + getNombre() + "' vence en " + dias + " día(s). Fecha límite: " + fechaLimite + ".\n";
        }
        return mensaje;
    }
}
