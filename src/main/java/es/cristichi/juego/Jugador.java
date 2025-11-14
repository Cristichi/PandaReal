package es.cristichi.juego;

import es.cristichi.obj.ColorDado;
import es.cristichi.obj.Dado;
import es.cristichi.obj.DadoCantidad;

import java.util.ArrayList;
import java.util.Iterator;

public class Jugador {
    private final String nombre;
    private final ArrayList<Dado> mano;
    private int[][] tablaPuntuacion;

    public Jugador(String nombre) {
        this.nombre = nombre;
        mano = new ArrayList<>();
    }

    public void iniciarTablaPuntuacion(int rondas) {
        tablaPuntuacion = new int[rondas][ColorDado.values().length];
    }

    /**
     * Puntúa los dados, sobreescribiendo cualquier puntuación previa en la misma ronda y categoría
     * @param ronda Número de ronda (0 = primera ronda)
     * @param puntuaciones Dados y sus puntuaciones
     */
    public void puntuar(int ronda, DadoCantidad... puntuaciones) {
        for (DadoCantidad puntuacion : puntuaciones) {
            int categoria = puntuacion.dado().colorDado().ordinal();
            tablaPuntuacion[ronda][categoria] = puntuacion.cantidad();
        }
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
}
