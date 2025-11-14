package es.cristichi;

import java.util.Scanner;

public class Util {
    public static void pulsaIntro(String mensaje) {
        if (mensaje != null){
            System.out.print(mensaje);
        }
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
