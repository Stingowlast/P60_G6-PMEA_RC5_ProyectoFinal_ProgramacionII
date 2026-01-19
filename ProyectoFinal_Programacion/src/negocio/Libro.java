package negocio;

public class Libro extends Material {
    private int numPaginas;

    public Libro(String id, String titulo, String autor, int numPaginas) {
        super(id, titulo, autor);
        if (numPaginas <= 0) throw new ExcepcionValidacion("Numero de paginas debe ser mayor a 0");
        this.numPaginas = numPaginas;
    }

    public int getNumPaginas() { return numPaginas; }

    @Override
    public String toString() {
        return super.toString() + " | Paginas: " + numPaginas + " (Libro)";
    }
}
