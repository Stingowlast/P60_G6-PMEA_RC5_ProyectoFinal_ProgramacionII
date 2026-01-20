package cliente;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Catalogo {
    private final List<Producto> productos = new ArrayList<>();
    private final Path path;

    public Catalogo(Path path) {
        this.path = path;
    }

    public void load() throws IOException {
        productos.clear();
        if (Files.notExists(path)) return;
        try (BufferedReader r = Files.newBufferedReader(path)) {
            String line;
            while ((line = r.readLine()) != null) {
                Producto p = Producto.fromCSV(line);
                if (p != null) productos.add(p);
            }
        }
    }

    public void save() throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(path)) {
            for (Producto p : productos) {
                w.write(p.toCSV());
                w.newLine();
            }
        }
    }

    public List<Producto> getAll() {
        return Collections.unmodifiableList(productos);
    }

    public void addProduct(Producto p) {
        productos.add(p);
    }

    public boolean updateProduct(String id, Producto nuevo) {
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId().equals(id)) {
                productos.set(i, nuevo);
                return true;
            }
        }
        return false;
    }

    public Producto findById(String id) {
        for (Producto p : productos) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }
}

