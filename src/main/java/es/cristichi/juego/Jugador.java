package es.cristichi.juego;

import es.cristichi.Util;
import es.cristichi.obj.CategoriaPuntuacion;
import es.cristichi.obj.ColorDado;
import es.cristichi.obj.Dado;
import es.cristichi.obj.DadoCantidad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Jugador {
    private final String nombre;
    private final boolean humano;
    private final ArrayList<Dado> mano;
    private int[][] tablaPuntuacion;

    public Jugador(String nombre) {
        this.nombre = nombre;
        mano = new ArrayList<>();
        this.humano = true;
    }

    public Jugador(String nombre, boolean humano) {
        this.nombre = nombre;
        mano = new ArrayList<>();
        this.humano = humano;
    }

    public void iniciarTablaPuntuacion(int rondas) {
        tablaPuntuacion = new int[rondas][CategoriaPuntuacion.values().length];
    }

    /**
     * Puntúa todos los dados de la ronda, sobreescribiendo todos los valores anteriores (si los había).
     * @param ronda Número de ronda (0 = primera ronda)
     * @param puntuaciones Dados y sus puntuaciones
     */
    public int[] puntuarRonda(int ronda, DadoCantidad... puntuaciones) {
        int sumaAmarillos = 0;
        int sumaMorados = 0;
        int sumaAzules = 0;
        boolean purpurina = false;
        int sumaRojos = 0;
        int numRojos = 0;
        int sumaVerdes = 0;
        int sumaBlancos = 0;
        int sumaRosas = 0;
        for (DadoCantidad entry : puntuaciones) {
            switch (entry.dado().colorDado()){
                case AMARILLO -> sumaAmarillos += entry.cantidad();
                case MORADO -> sumaMorados += entry.cantidad();
                case AZUL -> sumaAzules += entry.cantidad();
                case AZUL_PURPURINA -> {
                    sumaAzules += entry.cantidad();
                    purpurina = true;
                }
                case ROJO -> {
                    if (entry.cantidad() < entry.dado().caras()/2) {
                        sumaRojos -= entry.cantidad();
                    } else {
                        sumaRojos += entry.cantidad();
                    }
                    numRojos++;
                }
                case VERDE -> sumaVerdes += entry.cantidad();
                case BLANCO -> sumaBlancos += entry.cantidad();
                case ROSA -> sumaRosas += entry.cantidad();
            }
        }
        sumaMorados*=2;
        if (purpurina){
            sumaAzules *= 2;
        }
        sumaRojos *= numRojos;

        tablaPuntuacion[ronda][CategoriaPuntuacion.AMARILLOS.ordinal()] = sumaAmarillos;
        tablaPuntuacion[ronda][CategoriaPuntuacion.MORADOS.ordinal()] = sumaMorados;
        tablaPuntuacion[ronda][CategoriaPuntuacion.AZULES.ordinal()] = sumaAzules;
        tablaPuntuacion[ronda][CategoriaPuntuacion.ROJOS.ordinal()] = sumaRojos;
        tablaPuntuacion[ronda][CategoriaPuntuacion.VERDES.ordinal()] = sumaVerdes;
        tablaPuntuacion[ronda][CategoriaPuntuacion.BLANCOS.ordinal()] = sumaBlancos;
        tablaPuntuacion[ronda][CategoriaPuntuacion.ROSAS.ordinal()] = sumaRosas;

        System.out.printf("[%s] Puntuación de la ronda %d actualizada.%n", nombre, ronda+1);
        if (sumaAmarillos!=0)
            System.out.printf("- Amarillos: %d%n", sumaAmarillos);
        if (sumaMorados!=0)
            System.out.printf("- Morados: %d%n", sumaMorados);
        if (sumaAzules!=0)
            System.out.printf("- Azules: %d%n", sumaAzules);
        if (sumaRojos!=0)
            System.out.printf("- Rojos: %d%n", sumaRojos);
        if (sumaVerdes!=0)
            System.out.printf("- Verdes: %d%n", sumaVerdes);
        if (sumaBlancos!=0)
            System.out.printf("- Blancos: %d%n", sumaBlancos);
        if (sumaRosas!=0)
            System.out.printf("- Rosas: %d%n", sumaRosas);

        return tablaPuntuacion[ronda];
    }

    public int calcularTotalRonda(int ronda){
        return Arrays.stream(tablaPuntuacion[ronda]).sum();
    }

    public int[] getPuntuacionRonda(int ronda){
        return tablaPuntuacion[ronda];
    }

    public int calcularTotal(){
        int total = 0;
        for (int ronda = 0; ronda < tablaPuntuacion.length; ronda++) {
            for (int categoria = 0; categoria < tablaPuntuacion[ronda].length; categoria++) {
                total += tablaPuntuacion[ronda][categoria];
            }
        }
        return total;
    }

    public String getNombre() {
        return nombre;
    }

    public Iterator<Dado> iterateMano() {
        return mano.iterator();
    }

    public void addDado(Dado dado) {
        mano.add(dado);
    }

    /**
     * Intercambia un dado blanco de este jugador por un dado cualquiera del oponente
     * @param oponente Oponente
     * @param indiceMio Índice del dado blanco de este jugador
     * @param indiceTuyo Índice del dado elegido del oponente
     */
    public void intercambiarDadoBlanco(Jugador oponente, int indiceMio, int indiceTuyo) {
        Dado dadoMio = mano.get(indiceMio);
        if (dadoMio.colorDado() != ColorDado.BLANCO){
            throw new UnsupportedOperationException("No se puede cambiar un dado que no es blanco");
        }
        Dado dadoTuyo = oponente.mano.get(indiceTuyo);
        oponente.mano.set(indiceTuyo, dadoMio);
        this.mano.set(indiceMio, dadoTuyo);
    }

    public int elegirDado(Dado[] opciones){
        System.out.printf("\n[%s] tus dados:%n", nombre);
        for (int i = 0; i < mano.size(); i++) {
            System.out.printf("%d) %s%n", i+1, mano.get(i));
        }
        System.out.println("Ahora, elige un dado para añadir:");
        for (int i = 0; i < opciones.length; i++) {
            System.out.printf("#%d) %s%n", i+1, opciones[i]);
        }
        if (humano){
            while (true){
                try {
                    System.out.printf("[%s]: ", nombre);
                    byte[] buffer = new byte[10];
                    int read = System.in.read(buffer);
                    String input = new String(buffer, 0, read).trim();
                    int opcion = Integer.parseInt(input)-1;
                    if (opcion < 0 || opcion >= opciones.length) {
                        System.out.println("Opción inválida. Elige un número entre 1 y " + opciones.length + ": ");
                    } else {
                        return opcion;
                    }
                } catch (Exception e){
                    System.out.println("Error. Prueba a poner un número.");
                }
            }
        } else {
            int opcion = (int) (Math.random() * opciones.length);
            Util.pulsaIntro("[%s]: %s (CPU ha elegido, pulsa \"INTRO\")".formatted(nombre, opcion + 1));
            return opcion;
        }
    }

    public int manoSize() {
        return mano.size();
    }
}
