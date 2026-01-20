package cliente;

public class Producto {
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;

    public Producto(String id, String nombre, String descripcion, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(int stock) { this.stock = stock; }

    public String toCSV() {
        return escape(id) + "," + escape(nombre) + "," + escape(descripcion) + "," + precio + "," + stock;
    }

    public static Producto fromCSV(String line) {
        String[] parts = line.split(",", 5);
        if (parts.length < 5) return null;
        try {
            double precio = Double.parseDouble(parts[3]);
            int stock = Integer.parseInt(parts[4]);
            return new Producto(unescape(parts[0]), unescape(parts[1]), unescape(parts[2]), precio, stock);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\"\"", "\"");
    }
}

