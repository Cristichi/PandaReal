package es.cristichi;

import es.cristichi.juego.Jugador;
import es.cristichi.juego.Partida;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Vamos a preguntar al usuario el número de jugadores y sus nombres, y cuáles son humanos/CPU
        ArrayList<Jugador> jugadores = new ArrayList<>();
        String input = "";
        while (!input.equals("jugar")) {
            System.out.print("Introduce el nombre del siguiente jugador (o 'jugar' para empezar la partida): ");
            input = System.console().readLine();
            if (!input.equals("jugar")) {
                System.out.print("¿Es un jugador humano? (S/n): ");
                String esHumano = System.console().readLine();
                boolean humano = !esHumano.equalsIgnoreCase("n");
                Jugador nuevo = new Jugador(input, humano);
                jugadores.add(nuevo);
                System.out.printf("Añadido %s%n", nuevo);
            }
        }
        // Preguntar número de rondas, 10 por defecto
        int numRondas = 10;
        System.out.print("¿Cuántas rondas quieres jugar? (Pulsa \"INTRO\" para 10 rondas): ");
        String rondasInput = System.console().readLine();
        if (!rondasInput.isEmpty()) {
            try {
                numRondas = Integer.parseInt(rondasInput);
            } catch (NumberFormatException ignored) {
            }
        }

        Partida partida = new Partida(numRondas, jugadores.toArray(new Jugador[0]));
        partida.start();
    }
}