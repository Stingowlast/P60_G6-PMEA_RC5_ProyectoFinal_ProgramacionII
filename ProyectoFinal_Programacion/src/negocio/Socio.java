package negocio;

public class Socio {
    private static int contador = 1000;

    private int codigo;
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private String nivelMembresia;

    public Socio(String nombreCompleto, String correo, String telefono, String nivelMembresia) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) throw new ExcepcionValidacion("Nombre no puede ser vacio");
        if (correo == null || correo.trim().isEmpty() || !correo.contains("@")) throw new ExcepcionValidacion("Correo invalido");
        if (telefono == null || telefono.trim().isEmpty()) throw new ExcepcionValidacion("Telefono no puede ser vacio");
        if (nivelMembresia == null || nivelMembresia.trim().isEmpty()) throw new ExcepcionValidacion("Nivel de membresia no puede ser vacio");

        this.codigo = ++contador;
        this.nombreCompleto = nombreCompleto.trim();
        this.correo = correo.trim();
        this.telefono = telefono.trim();
        this.nivelMembresia = nivelMembresia.trim();
    }

    public int getCodigo() { return codigo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getNivelMembresia() { return nivelMembresia; }

    @Override
    public String toString() {
        return "Codigo: " + codigo + " | Nombre: " + nombreCompleto + " | Correo: " + correo +
                " | Telefono: " + telefono + " | Nivel: " + nivelMembresia;
    }
}
