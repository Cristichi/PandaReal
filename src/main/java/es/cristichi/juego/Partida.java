package es.cristichi.juego;

import es.cristichi.obj.Bolsa;
import es.cristichi.obj.ColorDado;
import es.cristichi.obj.Dado;
import es.cristichi.obj.DadoCantidad;

import java.util.ArrayList;
import java.util.Iterator;

public class Partida extends Thread {
    public static DadoCantidad[] BOLSA_TOTAL = new DadoCantidad[] {
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.AMARILLO, 6), 10),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.AMARILLO, 8), 7),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.MORADO, 8), 7),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.MORADO, 12), 7),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.AZUL, 6), 10),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.AZUL, 8), 9),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.AZUL, 12), 9),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.AZUL_PURPURINA, 6), 7),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.ROJO, 6), 10),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.ROJO, 8), 9),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.VERDE, 20), 10),
            new DadoCantidad(new Dado(es.cristichi.obj.ColorDado.BLANCO, 6), 7)
    };

    protected final int rondas;
    protected Bolsa bolsa;
    protected ArrayList<Jugador> jugadores;

    public Partida(int rondas, Jugador... jugadores) {
        bolsa = new Bolsa(jugadores.length, BOLSA_TOTAL);
        this.rondas = rondas;
        this.jugadores = new ArrayList<>(jugadores.length);
        for (Jugador jugador : jugadores) {
            if (this.isAlive()){
                throw new IllegalStateException("No se pueden añadir jugadores a una partida en curso");
            }
            jugador.addDado(bolsa.sacarDado(ColorDado.AMARILLO, 6));
            this.jugadores.add(jugador);
        }
    }

    @Override
    public void run() {
        super.run();
        for (Jugador jugador : jugadores) {
            jugador.iniciarTablaPuntuacion(rondas);
        }
        System.out.println("Partida iniciada con " + jugadores.size() + " jugadores. Suertes buenas.\n\n");
        for (int ronda = 0; ronda < rondas; ronda++) {
            System.out.printf("%n¡Ronda %d!%n%n", ronda+1);

            System.out.println("Paso 1: ¡A lanzar los dados!");
            for (Jugador jugador : jugadores) {
                System.out.println("  Dados de " + jugador.getNombre() + ":");
                Iterator<Dado> it = jugador.iterateMano();
                while(it.hasNext()) {
                    Dado dado = it.next();
                    int resultado = dado.lanzar();
                    System.out.printf("   %s> [%d]%n", dado, resultado);
                    jugador.puntuar(ronda, new DadoCantidad(dado, resultado));
                }
            }
        }

        System.out.println("\nFin de la partida. Puntuaciones:");
        for (Jugador jugador : jugadores) {
            System.out.println(jugador.getNombre() + ": " + jugador.calcularTotal() + " puntos.");
        }
    }
}
