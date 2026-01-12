package dulcearte.Views;

import dulcearte.Models.ArticuloDanable;
import dulcearte.Models.ArticuloDuradero;
import dulcearte.Models.ArticuloInventario;
import dulcearte.Utils.DateUtil;
import dulcearte.Utils.InventarioManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        InventarioManager manager = new InventarioManager(200.0, 200);
        String fechaHoy = pedirFechaHoy(sc);

        int opcion = 0;
        do {
            menu();
            System.out.print("Opción: ");
            String linea = sc.nextLine();

            try {
                opcion = Integer.parseInt(linea);

                switch (opcion) {
                    case 1:
                        altaArticulo(sc, manager);
                        break;
                    case 2:
                        comprar(sc, manager);
                        break;
                    case 3:
                        consumir(sc, manager);
                        break;
                    case 4:
                        manager.auditoriaAlertas(fechaHoy);
                        break;
                    case 5:
                        buscar(sc, manager, fechaHoy);
                        break;
                    case 6:
                        manager.listarArticulos();
                        break;
                    case 7:
                        System.out.println("Saliendo...");
                        break;
                    case 8:
                        fechaHoy = pedirFechaHoy(sc);
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Debe ingresar un número.");
            }

        } while (opcion != 7);
    }

    private static void menu() {
        System.out.println("\n===== DULCEARTE - INVENTARIO =====");
        System.out.println("1. Alta de artículo (Perecedero/Duradero)");
        System.out.println("2. Ejecutar compra (entrada)");
        System.out.println("3. Registrar consumo (salida)");
        System.out.println("4. Auditoría de alertas");
        System.out.println("5. Buscar por nombre");
        System.out.println("6. Listar artículos");
        System.out.println("7. Salir");
        System.out.println("8. Cambiar fecha de HOY");
    }

    private static String pedirFechaHoy(Scanner sc) {
        String fecha;
        do {
            System.out.print("Ingrese la fecha de HOY (AAAA-MM-DD): ");
            fecha = sc.nextLine();
            if (!DateUtil.esFechaValida(fecha)) {
                System.out.println("Fecha inválida. Ejemplo válido: 2026-01-11");
            }
        } while (!DateUtil.esFechaValida(fecha));
        return fecha;
    }

    private static void altaArticulo(Scanner sc, InventarioManager manager) {
        System.out.println("1) Perecedero  2) Duradero");
        System.out.print("Tipo: ");
        String t = sc.nextLine();

        try {
            int tipo = Integer.parseInt(t);

            System.out.print("Código: ");
            String codigo = sc.nextLine();

            System.out.print("Nombre: ");
            String nombre = sc.nextLine();

            System.out.print("Costo de compra (ej: 1.50): ");
            double costo = Double.parseDouble(sc.nextLine());

            System.out.print("Punto de reorden (entero): ");
            int reorden = Integer.parseInt(sc.nextLine());

            if (tipo == 1) {
                System.out.print("Fecha límite (AAAA-MM-DD): ");
                String fechaLimite = sc.nextLine();
                if (!DateUtil.esFechaValida(fechaLimite)) {
                    System.out.println("Error: fecha inválida. Formato correcto AAAA-MM-DD.");
                    return;
                }

                System.out.print("Días de alerta previa (entero): ");
                int diasAlerta = Integer.parseInt(sc.nextLine());

                manager.agregarArticulo(new ArticuloDanable(
                        codigo, nombre, costo, reorden, fechaLimite, diasAlerta
                ));

            } else if (tipo == 2) {
                System.out.print("Días de espera del proveedor (entero): ");
                int diasEspera = Integer.parseInt(sc.nextLine());

                manager.agregarArticulo(new ArticuloDuradero(
                        codigo, nombre, costo, reorden, diasEspera
                ));
            } else {
                System.out.println("Tipo inválido.");
            }

        } catch (NumberFormatException nfe) {
            System.out.println("Error: ingresaste texto donde iba un número.");
        }
    }

    private static void comprar(Scanner sc, InventarioManager manager) {
        System.out.print("Nombre del artículo: ");
        String nombre = sc.nextLine();

        System.out.print("Cantidad a comprar (entero): ");
        try {
            int cantidad = Integer.parseInt(sc.nextLine());
            manager.ejecutarCompra(nombre, cantidad);
        } catch (NumberFormatException nfe) {
            System.out.println("Cantidad inválida: debe ser entero.");
        }
    }

    private static void consumir(Scanner sc, InventarioManager manager) {
        System.out.print("Nombre del artículo: ");
        String nombre = sc.nextLine();

        System.out.print("Cantidad a consumir (entero): ");
        try {
            int cantidad = Integer.parseInt(sc.nextLine());
            manager.registrarConsumo(nombre, cantidad);
        } catch (NumberFormatException nfe) {
            System.out.println("Cantidad inválida: debe ser entero.");
        }
    }

    private static void buscar(Scanner sc, InventarioManager manager, String fechaHoy) {
        System.out.print("Nombre del artículo a buscar: ");
        String nombre = sc.nextLine();

        ArticuloInventario a = manager.buscarPorNombre(nombre);
        if (a == null) {
            System.out.println("No encontrado.");
        } else {
            System.out.println("Encontrado: " + a);
            String alerta = a.verificarAlerta(fechaHoy);
            if (alerta != null && !alerta.isEmpty()) {
                System.out.println("Alerta actual: " + alerta);
            }
        }
    }
}
