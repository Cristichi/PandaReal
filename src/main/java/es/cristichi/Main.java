package es.cristichi;

import es.cristichi.juego.Jugador;
import es.cristichi.juego.Partida;

public class Main {
    public static void main(String[] args) {
        Partida partida = new Partida(10,
                new Jugador("Cristichi"),
                new Jugador("Jugador 2", false),
                new Jugador("Jugador 3", false));
        partida.start();
    }
}