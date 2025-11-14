package es.cristichi.juego;

import es.cristichi.Util;
import es.cristichi.obj.*;

import java.util.*;

public class Partida extends Thread {
    public static DadoCantidad[] BOLSA_TOTAL = new DadoCantidad[] {
            new DadoCantidad(new Dado(ColorDado.AMARILLO, 8), 7),
            new DadoCantidad(new Dado(ColorDado.MORADO, 8), 7),
            new DadoCantidad(new Dado(ColorDado.MORADO, 12), 7),
            new DadoCantidad(new Dado(ColorDado.AZUL, 6), 10),
            new DadoCantidad(new Dado(ColorDado.AZUL, 8), 9),
            new DadoCantidad(new Dado(ColorDado.AZUL, 12), 9),
            new DadoCantidad(new Dado(ColorDado.AZUL_PURPURINA, 6), 7),
            new DadoCantidad(new Dado(ColorDado.ROJO, 6), 10),
            new DadoCantidad(new Dado(ColorDado.ROJO, 8), 9),
            new DadoCantidad(new Dado(ColorDado.VERDE, 20), 10),
            new DadoCantidad(new Dado(ColorDado.BLANCO, 6), 7)
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
            jugador.addDado(new Dado(ColorDado.AMARILLO, 6));
            this.jugadores.add(jugador);
        }
    }

    @Override
    public void run() {
        super.run();
        for (Jugador jugador : jugadores) {
            jugador.iniciarTablaPuntuacion(rondas);
        }
        System.out.printf("Partida iniciada con %d jugadores.%nLa partida se jugará a %d rondas.%nBuena suerte.%n%n",
                jugadores.size(), rondas);
        for (int ronda = 0; ronda < rondas; ronda++) {
            int finalRonda = ronda;
            System.out.printf("%n¡Ronda %d!%n%n", finalRonda+1);

            System.out.println("Paso 1: ¡A lanzar los dados!");

            Jugador nuevoPanda = null;
            int amarilloNuevoPanda = -1;
            for (Jugador jugador : jugadores) {
                Util.pulsaIntro("\n* %s, pulsa \"INTRO\" para lanzar...".formatted(jugador));
                Iterator<Dado> it = jugador.iterateMano();
                DadoCantidad[] lanzados = new DadoCantidad[jugador.manoSize()];
                int index = 0;
                while(it.hasNext()) {
                    Dado dado = it.next();
                    int resultado = dado.lanzar();
                    lanzados[index] = new DadoCantidad(dado, resultado);
                    System.out.printf("%s> [%d]%n", dado, resultado);
                    index++;
                }
                int[] puntuacionRonda = jugador.puntuarRonda(finalRonda, lanzados);
                int amarillos = puntuacionRonda[CategoriaPuntuacion.AMARILLOS.ordinal()];
                if (amarillos > amarilloNuevoPanda){
                    nuevoPanda = jugador;
                    amarilloNuevoPanda = amarillos;
                }

                System.out.printf("Puntuación en esta ronda: %d%n", Arrays.stream(puntuacionRonda).sum());
                System.out.printf("Puntuación en la ronda: %d puntos.%n", jugador.calcularTotalRonda(finalRonda));
                System.out.printf("Puntuación total: %d puntos.%n", jugador.calcularTotal());
            }
            if (finalRonda == rondas -1){
                Jugador ganador = Collections.max(jugadores, Comparator.comparingInt(Jugador::calcularTotal));
                System.out.printf("El ganador de la partida es %s con %d puntos. ¡Enhorabuena!%n",
                        ganador, ganador.calcularTotal());
            } else {

                // Elección de nuevo panda, rotamos el array de forma que el orden es el mismo pero el panda es el 0
                if (nuevoPanda == null) {
                    throw new IllegalStateException("No se ha podido determinar el nuevo panda");
                }
                Collections.rotate(jugadores, jugadores.indexOf(nuevoPanda) * -1);
                System.out.printf("%n%s es el nuevo panda para la siguiente ronda con %d puntos amarillos.%n",
                        nuevoPanda.getNombre(), amarilloNuevoPanda);

                Util.pulsaIntro("\nPaso 2: ¡Dados rosas!");
                // Todos los dados rosas se sacan de todos los jugadores y se reparten a los menos afortunados de esta ronda
                ArrayList<Jugador> jugadoresOrdenadosPuntuacion = new ArrayList<>(jugadores);
                jugadoresOrdenadosPuntuacion.sort(Comparator.comparingInt(j -> j.calcularTotalRonda(finalRonda)));

                ArrayList<Dado> dadosRosasSacados = new ArrayList<>(Arrays.asList(bolsa.sacarDadosRosas()));
                for (Jugador jugador : jugadores) {
                    Iterator<Dado> it = jugador.iterateMano();
                    while (it.hasNext()) {
                        Dado dado = it.next();
                        if (dado.colorDado() == ColorDado.ROSA) {
                            dadosRosasSacados.add(dado);
                            it.remove();
                        }
                    }
                }

                for (int i = 0; i < dadosRosasSacados.size(); i++) {
                    Jugador jugador = jugadoresOrdenadosPuntuacion.get(i % jugadoresOrdenadosPuntuacion.size());
                    Dado dadoRosa = dadosRosasSacados.get(i);
                    jugador.addDado(dadoRosa);
                    System.out.printf("%s ha recibido un dado rosa.%n", jugador.getNombre());
                }

                Util.pulsaIntro("\nPaso 3: ¡Intercambios! WIP");

                Util.pulsaIntro("\nPaso 4: ¡Nuevos dados!");
                Dado[] nuevos = new Dado[jugadores.size() + 1];
                for (int i = 0; i < nuevos.length; i++) {
                    nuevos[i] = bolsa.sacarDadoAleatorio();
                }

                for (Jugador jugador : jugadores) {
                    int dadoElegido = jugador.elegirDado(nuevos);
                    jugador.addDado(nuevos[dadoElegido]);
                    System.out.printf("%s ha añadido %s a su mano.%n", jugador.getNombre(), nuevos[dadoElegido]);
                    Dado[] nuevos2 = new Dado[nuevos.length - 1];
                    int index = 0;
                    for (int i = 0; i < nuevos.length; i++) {
                        if (i != dadoElegido) {
                            nuevos2[index++] = nuevos[i];
                        }
                    }
                    nuevos = nuevos2;
                }

                System.out.println("\nTodos han elegido dado. El dado sobrante " + nuevos[0] + " vuelve a la bolsa.");
                // El dado sobrante vuelve a la bolsa
                bolsa.addDado(nuevos[0]);

                Util.pulsaIntro("\n\nPulsa \"INTRO\" para terminar la ronda...");
            }
        }

        System.out.println("\nFin de la partida. Puntuaciones:");
        for (Jugador jugador : jugadores) {
            System.out.println(jugador.getNombre() + ": " + jugador.calcularTotal() + " puntos.");
        }
    }
}
