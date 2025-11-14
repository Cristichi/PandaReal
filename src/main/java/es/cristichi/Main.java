package es.cristichi;

import es.cristichi.juego.Jugador;
import es.cristichi.juego.Partida;

public class Main {
    public static void main(String[] args) {
        Partida partida = new Partida(3,
                new Jugador("Cristichi"), new Jugador("Gemelo Malvado"));
        partida.start();
    }
}