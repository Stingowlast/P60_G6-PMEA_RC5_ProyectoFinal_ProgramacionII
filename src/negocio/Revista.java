package negocio;

public class Revista extends Material {
    private int numEdicion;

    public Revista(String id, String titulo, String editorial, int numEdicion) {
        super(id, titulo, editorial);
        if (numEdicion <= 0) throw new ExcepcionValidacion("Numero de edicion debe ser mayor a 0");
        this.numEdicion = numEdicion;
    }

    public int getNumEdicion() { return numEdicion; }

    @Override
    public String toString() {
        return super.toString() + " | Edicion: " + numEdicion + " (Revista)";
    }
}
