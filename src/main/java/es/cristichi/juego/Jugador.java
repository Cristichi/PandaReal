package es.cristichi.juego;

import es.cristichi.gui.MainJF;
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

        return tablaPuntuacion[ronda];
    }

    public int calcularTotalRonda(int ronda){
        return Arrays.stream(tablaPuntuacion[ronda]).sum();
    }

    public int calcularTotal(){
        int total = 0;
        for (int[] ints : tablaPuntuacion) {
            for (int anInt : ints) {
                total += anInt;
            }
        }
        return total;
    }

    public int manoSize() {
        return mano.size();
    }

    public Iterator<Dado> iterateMano() {
        return mano.iterator();
    }

    public Object getDado(int indiceDadoOponente) {
        return mano.get(indiceDadoOponente);
    }

    public void addDado(Dado dado) {
        mano.add(dado);
    }

    public boolean esHumano() {
        return humano;
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
            throw new UnsupportedOperationException("Solo puedes cambiar tus dados blancos.");
        }
        Dado dadoTuyo = oponente.mano.get(indiceTuyo);
        if (dadoTuyo.colorDado() == ColorDado.BLANCO){
            throw new UnsupportedOperationException("No se puede cambiar un dado blanco del oponente." );
        }
        if (dadoTuyo.colorDado() == ColorDado.ROSA){
            throw new UnsupportedOperationException("No se puede cambiar un dado rosa." );
        }
        oponente.mano.set(indiceTuyo, dadoMio);
        this.mano.set(indiceMio, dadoTuyo);
    }

    /**
     * Muestra los dados del jugador y le pide que elija uno de entre las opciones dadas.
     * @param window Ventana principal, para mostrar mensajes y pedir input
     * @param opciones Opciones de dados a elegir
     * @return Índice del dado elegido en el array de opciones
     */
    public int elegirDado(MainJF window, Dado[] opciones){
        window.escribirEnPantalla("%n%s tus dados:%n", this);
        for (Dado dado : mano) {
            window.escribirEnPantalla("%s%n", dado);
        }
        window.escribirEnPantalla("Ahora, elige un dado para añadir entre estos:%n");
        for (int i = 0; i < opciones.length; i++) {
            window.escribirEnPantalla("%d) %s%n", i+1, opciones[i]);
        }
        return window.inputEntero(this, null, "Elige un número entre 1 y %d.".formatted(opciones.length),
                1, opciones.length + 1) - 1;
    }

    @Override
    public String toString() {
        return nombre+(humano ? "" : " (CPU)");
    }
}
