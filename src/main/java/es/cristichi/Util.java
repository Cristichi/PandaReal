package es.cristichi;

import es.cristichi.juego.Jugador;

import java.util.Scanner;

public class Util {
    public static void pulsaIntro(String mensaje) {
        if (mensaje != null){
            System.out.print(mensaje);
        }
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    /**
     * Pide input de un número entero dentro de un rango específico.
     * @param s Mensaje a mostrar
     * @param min Mínimo INCLUIDO
     * @param max Máximo NO INCLUIDO
     * @return Número entero introducido por el jugador
     */
    public static int inputEntero(Jugador jugador, String s, int min, int max) {
        if (!jugador.esHumano()){
            int valor = (int) (Math.random() * (max - min)) + min;
            System.out.printf("%s%d (elegido por la CPU)%n", s, valor);
            return valor;
        } else {
            Scanner scanner = new Scanner(System.in);
            int valor;
            while (true) {
                System.out.print(s);
                try {
                    valor = Integer.parseInt(scanner.nextLine());
                    if (valor >= min && valor < max) {
                        return valor;
                    } else {
                        System.out.printf("Por favor, introduce un número entre %d y %d.%n", min, max - 1);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada no válida. Por favor, introduce un número entero.");
                }
            }
        }
    }
}
