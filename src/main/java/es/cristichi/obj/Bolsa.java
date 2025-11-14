package es.cristichi.obj;

import es.cristichi.exc.DadoNoEncontrado;

import java.util.ArrayList;
import java.util.Iterator;

public class Bolsa {
    private static final Dado DADO_ROSA = new Dado(ColorDado.ROSA, 12);

    private final ArrayList<Dado> dados = new ArrayList<>();
    private final ArrayList<Dado> dadosRosas = new ArrayList<>();

    public Bolsa(int numJugadores, DadoCantidad... dadosConCantidad) {
        int numRosas;
        if (numJugadores<0){
            throw new IllegalArgumentException("El número de jugadores no puede ser negativo");
        } else if (numJugadores<= 3){
            numRosas = 1;
        } else if (numJugadores<= 6){
            numRosas = 2;
        } else if (numJugadores<= 9){
            numRosas = 3;
        } else {
            numRosas = 4;
        }
        for (int i = 0; i < numRosas; i++) {
            dadosRosas.add(DADO_ROSA);
        }
        for (DadoCantidad entry : dadosConCantidad) {
            for (int i = 0; i < entry.cantidad(); i++) {
                if (entry.dado().colorDado() != ColorDado.ROSA) {
                    this.dados.add(entry.dado());
                } else {
                    System.err.println("Se ha intentado añadir un dado rosa a la bolsa normal. " +
                            "Ya se añaden con el número de jugadores.");
                }
            }
        }
    }

    public Dado sacarDadoAleatorio() {
        if (this.dados.isEmpty()) {
            throw new IllegalStateException("No hay dados en la bolsa");
        }
        int indiceAleatorio = (int) (Math.random() * this.dados.size());
        return this.dados.remove(indiceAleatorio);
    }

    /**
     * Saca un dado de la bolsa según su color y número de caras.
     * @param color Color del dado
     * @param numCaras Número de caras del dado
     * @throws DadoNoEncontrado Si el dado no está en la bolsa
     * @return Dado sacado
     */
    public Dado sacarDado(ColorDado color, int numCaras){
        for (int i = 0; i < dados.size(); i++) {
            Dado dado = dados.get(i);
            if (dado.colorDado() == color && dado.caras() == numCaras){
                dados.remove(i);
                return dado;
            }
        }
        throw new DadoNoEncontrado();
    }

    public Dado[] sacarDadosRosas() {
        Dado[] dadosSacados = new Dado[dadosRosas.size()];
        for (int i = 0; i < dadosRosas.size(); i++) {
            dadosSacados[i] = dadosRosas.get(i);
        }
        dadosRosas.clear();
        return dadosSacados;
    }

    public int size() {
        return dados.size();
    }

    public boolean isEmpty() {
        return dados.isEmpty();
    }

    public Dado get(int index) {
        return dados.get(index);
    }

    public Iterator<Dado> iterator() {
        return dados.iterator();
    }


}
