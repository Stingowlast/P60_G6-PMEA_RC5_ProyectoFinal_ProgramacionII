package dulcearte;

public class DateUtil {

    public static boolean esFechaValida(String f) {
        if (f == null || f.length() != 10) return false;
        if (f.charAt(4) != '-' || f.charAt(7) != '-') return false;

        for (int i = 0; i < 10; i++) {
            if (i == 4 || i == 7) continue;
            char c = f.charAt(i);
            if (c < '0' || c > '9') return false;
        }

        int anio = Integer.parseInt(f.substring(0, 4));
        int mes  = Integer.parseInt(f.substring(5, 7));
        int dia  = Integer.parseInt(f.substring(8,10));

        if (anio < 1) return false;
        if (mes < 1 || mes > 12) return false;

        int maxDia = diasDelMes(anio, mes);
        return dia >= 1 && dia <= maxDia;
    }

    public static int diasEntre(String desde, String hasta) {
        int a = diasDesdeInicio(desde);
        int b = diasDesdeInicio(hasta);
        return b - a;
    }

    private static int diasDesdeInicio(String f) {
        int anio = Integer.parseInt(f.substring(0, 4));
        int mes  = Integer.parseInt(f.substring(5, 7));
        int dia  = Integer.parseInt(f.substring(8,10));

        int total = 0;

        for (int y = 1; y < anio; y++) {
            total += esBisiesto(y) ? 366 : 365;
        }

        for (int m = 1; m < mes; m++) {
            total += diasDelMes(anio, m);
        }

        total += dia;

        return total;
    }

    private static boolean esBisiesto(int anio) {
        if (anio % 400 == 0) return true;
        if (anio % 100 == 0) return false;
        return anio % 4 == 0;
    }

    private static int diasDelMes(int anio, int mes) {
        switch (mes) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12: return 31;
            case 4: case 6: case 9: case 11: return 30;
            case 2: return esBisiesto(anio) ? 29 : 28;
            default: return 0;
        }
    }
}
