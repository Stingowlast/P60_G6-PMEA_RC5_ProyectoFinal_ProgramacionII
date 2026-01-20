package negocio;

public class ExcepcionValidacion extends RuntimeException {
    public ExcepcionValidacion(String mensaje) {
        super(mensaje);
    }
}
