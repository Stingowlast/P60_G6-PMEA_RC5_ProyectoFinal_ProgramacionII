package negocio;

public abstract class Material {
    private String id;
    private String titulo;
    private String autorEditorial;
    private EstadoMaterial estado;

    public Material(String id, String titulo, String autorEditorial) {
        if (id == null || id.trim().isEmpty()) throw new ExcepcionValidacion("ID no puede ser vacio");
        if (titulo == null || titulo.trim().isEmpty()) throw new ExcepcionValidacion("Titulo no puede ser vacio");
        if (autorEditorial == null || autorEditorial.trim().isEmpty()) throw new ExcepcionValidacion("Autor/Editorial no puede ser vacio");

        this.id = id.trim();
        this.titulo = titulo.trim();
        this.autorEditorial = autorEditorial.trim();
        this.estado = EstadoMaterial.DISPONIBLE;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutorEditorial() { return autorEditorial; }
    public EstadoMaterial getEstado() { return estado; }

    public void prestar() {
        if (estado == EstadoMaterial.PRESTADO) throw new ExcepcionValidacion("El material ya esta prestado");
        estado = EstadoMaterial.PRESTADO;
    }

    public void devolver() {
        if (estado == EstadoMaterial.DISPONIBLE) throw new ExcepcionValidacion("El material ya esta disponible");
        estado = EstadoMaterial.DISPONIBLE;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Titulo: " + titulo + " | Autor/Editorial: " + autorEditorial + " | Estado: " + estado;
    }
}
