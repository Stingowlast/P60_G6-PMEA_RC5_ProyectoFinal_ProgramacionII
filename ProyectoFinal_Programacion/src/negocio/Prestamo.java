package negocio;

public class Prestamo {
    private static int contadorPrestamo = 0;

    private int idPrestamo;
    private Socio socio;
    private Material material;
    private String fechaSalida;
    private String fechaLimite;
    private String fechaDevolucion;
    private boolean activo;

    public Prestamo(Socio socio, Material material, String fechaSalida, String fechaLimite) {
        if (socio == null) throw new ExcepcionValidacion("Socio no puede ser null");
        if (material == null) throw new ExcepcionValidacion("Material no puede ser null");
        if (fechaSalida == null || fechaSalida.trim().isEmpty()) throw new ExcepcionValidacion("Fecha salida no puede ser vacia");
        if (fechaLimite == null || fechaLimite.trim().isEmpty()) throw new ExcepcionValidacion("Fecha limite no puede ser vacia");

        this.idPrestamo = ++contadorPrestamo;
        this.socio = socio;
        this.material = material;
        this.fechaSalida = fechaSalida.trim();
        this.fechaLimite = fechaLimite.trim();
        this.activo = true;
    }

    public int getIdPrestamo() { return idPrestamo; }
    public boolean isActivo() { return activo; }
    public Socio getSocio() { return socio; }
    public Material getMaterial() { return material; }
    public String getFechaSalida() { return fechaSalida; }
    public String getFechaLimite() { return fechaLimite; }
    public String getFechaDevolucion() { return fechaDevolucion; }

    public void cerrarPrestamo(String fechaDevolucion) {
        if (!activo) throw new ExcepcionValidacion("Este prestamo ya esta cerrado");
        if (fechaDevolucion == null || fechaDevolucion.trim().isEmpty()) throw new ExcepcionValidacion("Fecha devolucion no puede ser vacia");
        this.fechaDevolucion = fechaDevolucion.trim();
        this.activo = false;
    }

    @Override
    public String toString() {
        return "Prestamo #" + idPrestamo + " | Socio: " + socio.getCodigo() +
                " | Material: " + material.getId() +
                " | Salida: " + fechaSalida +
                " | Limite: " + fechaLimite +
                " | Devolucion: " + (fechaDevolucion == null ? "-" : fechaDevolucion) +
                " | Activo: " + activo;
    }
}
