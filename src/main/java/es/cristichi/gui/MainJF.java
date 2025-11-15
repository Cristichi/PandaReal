package es.cristichi.gui;

import es.cristichi.juego.Jugador;
import es.cristichi.juego.Partida;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MainJF extends JFrame {
    protected JScrollPane scrollPane;
    protected JTextArea textAreaPantalla;
    protected JTextField inputField;

    protected boolean waitingInput;
    protected final Semaphore semaforoInput;

    public MainJF(String titulo) {
        super(titulo);
        waitingInput = false;
        semaforoInput = new Semaphore(0);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);

        textAreaPantalla = new JTextArea();
        textAreaPantalla.setBackground(Color.LIGHT_GRAY);
        textAreaPantalla.setFont(new Font("Cabin", Font.PLAIN, 20));
        textAreaPantalla.setEditable(false);
        textAreaPantalla.setFocusable(false);
        textAreaPantalla.setLineWrap(true);
        textAreaPantalla.setWrapStyleWord(true);
        textAreaPantalla.setBorder(new EmptyBorder(3, 10, 10, 3));
        scrollPane = new JScrollPane(textAreaPantalla);

        inputField = new JTextField();
        inputField.addActionListener(_ -> {
            if (waitingInput){
                waitingInput = false;
                liberarSemaforoInput();
            }
        });

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        requestFocus(FocusEvent.Cause.ACTIVATION);
        setVisible(true);
        setLocationRelativeTo(null);

        // Bienvenida
        escribirEnPantalla("¡Bienvenido a Panda Royale, un juego de mesa de Last Night Games!%n");
        escribirEnPantalla("¡Yo soy Cristichi, lo he programado en mis ratos libres!%n");
        escribirEnPantalla("Durante la partida, te pediré que me digas lo que quieres hacer. " +
                "Cosas como elegir un dado que intercambiar, o pulsar la tecla intro para " +
                "continuar cuando hayas leído la pantalla. ¿De hecho, por qué no lo pruebas ahora? " +
                "Avísame cuando hagas leído esto pulsando la tecla intro.%n%n");
        esperarIntro("Pulsar INTRO para continuar...");


        escribirEnPantalla("¡Adelante! Lo primero de todo es saber quién va a jugar.%n");

        // Vamos a preguntar al usuario el número de jugadores y sus nombres, y cuáles son humanos/CPU
        ArrayList<Jugador> jugadores = new ArrayList<>();
        String input;
        do {
            input = inputString(null,
                    "%nIntroduce el nombre del siguiente jugador (o 'jugar' para empezar la partida).", false);
            if (!input.equals("jugar")) {
                String esHumano = inputString(null, "¿Es %s un jugador humano? (Escribe \"S\" o \"n\")".formatted(input), true);
                Jugador nuevo = new Jugador(input, !esHumano.equalsIgnoreCase("n"));
                jugadores.add(nuevo);
                escribirEnPantalla("Añadido el jugador %s%n", nuevo);
            }
        } while (!input.equals("jugar"));

        // Preguntar número de rondas, 10 por defecto
        int numRondas = inputEntero(null, "¿Cuántas rondas queréis jugar? (Juego normal son 10)",
                "Por favor, introduce un número válido.", 1, 255, "10");

        Partida partida = new Partida(this, numRondas, jugadores.toArray(new Jugador[0]));
        partida.start();
    }

    /**
     * Escribe un mensaje en la pantalla del juego.
     * @param mensaje Mensaje a escribir
     * @param args Argumentos para formatear el mensaje con {@link String#formatted(Object...)}.
     */
    public void escribirEnPantalla(String mensaje, Object... args) {
        textAreaPantalla.append(mensaje.formatted(args));
        textAreaPantalla.setCaretPosition(textAreaPantalla.getDocument().getLength());
    }

    /**
     * Espera a que el jugador pulse INTRO en la barra de input.
     * Importante limpiar la barra de input después de usar su valor tras liberarse el semáforo.
     */
    public void esperarSemaforoInput() {
        try {
            inputField.setVisible(true);
            inputField.requestFocus(FocusEvent.Cause.ACTIVATION);
            inputField.setCaretPosition(inputField.getText().length());
            semaforoInput.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Libera el semáforo de input para continuar la ejecución del juego.
     */
    public void liberarSemaforoInput() {
        semaforoInput.release();
        inputField.setVisible(false);
    }

    /**
     * Espera a que el jugador pulse INTRO en la barra de input.
     * @param defecto Escribe el mensaje en la barra de input si no es null.
     */
    public void esperarIntro(String defecto) {
        if (defecto != null){
            inputField.setText(defecto);
        }
        waitingInput = true;
        esperarSemaforoInput();
        inputField.setText("");
    }

    /**
     * Pide input de un número entero dentro de un rango específico.
     * @param mensaje Mensaje a mostrar en pantalla
     * @param mensajeValorInvalido Mensaje a mostrar en la barra de input si el valor introducido no es válido
     * @param min Mínimo INCLUIDO
     * @param max Máximo NO INCLUIDO
     * @return Número entero introducido por el jugador
     */
    public int inputEntero(Jugador jugador, String mensaje, String mensajeValorInvalido, int min, int max) {
        if (jugador == null || jugador.esHumano()){
            if (mensaje != null){
                escribirEnPantalla(mensaje+"%n");
            }
            int valor = min - 1;
            while (valor < min || valor >= max) {
                try {
                    waitingInput = true;
                    esperarSemaforoInput();
                    valor = Integer.parseInt(inputField.getText().trim());
                } catch (NumberFormatException e) {
                    valor = min - 1;
                    if (mensajeValorInvalido != null){
                        inputField.setText(mensajeValorInvalido);
                    }
                }
            }
            inputField.setText("");
            return valor;
        } else {
            // Si es una CPU, generar un número aleatorio dentro del rango
            int valor = (int) (Math.random() * (max - min)) + min;
            escribirEnPantalla("%s: %d%n", jugador, valor);
            return valor;
        }
    }

    /**
     * Pide input de un número entero dentro de un rango específico.
     * @param mensaje Mensaje a mostrar
     * @param min Mínimo INCLUIDO
     * @param max Máximo NO INCLUIDO
     * @param defecto Valor por defecto que aparece en la barra de input
     * @return Número entero introducido por el jugador
     */
    public int inputEntero(Jugador jugador, String mensaje, String mensajeValorInvalido, int min, int max, String defecto) {
        if (jugador == null || jugador.esHumano()){
            escribirEnPantalla(mensaje+"%n");
            int valor = min - 1;
            while (valor < min || valor >= max) {
                try {
                    inputField.setText(defecto);
                    waitingInput = true;
                    esperarSemaforoInput();
                    valor = Integer.parseInt(inputField.getText().trim());
                } catch (NumberFormatException e) {
                    valor = min - 1;
                    inputField.setText(mensajeValorInvalido);
                }
            }
            inputField.setText("");
            return valor;
        } else {
            // Si es una CPU, generar un número aleatorio dentro del rango
            int valor = (int) (Math.random() * (max - min)) + min;
            escribirEnPantalla("%s: %d%n", mensaje, valor);
            return valor;
        }
    }

    /**
     * Pide input de una cadena de texto.
     * @param jugador Para determinar si es humano o CPU. Permite null.
     * @param mensaje Mensaje a mostrar en pantalla, por comodidad.
     * @param permitirVacio Si es true, permite que el jugador no escriba nada y pulse INTRO.
     * @return Cadena de texto introducida por el jugador.
     */
    public String inputString(Jugador jugador, String mensaje, boolean permitirVacio) {
        if (jugador == null || jugador.esHumano()){
            escribirEnPantalla(mensaje+"%n");
            String valor;
            do {
                waitingInput = true;
                esperarSemaforoInput();
                valor = inputField.getText().trim();
            } while (!permitirVacio && valor.isEmpty());
            inputField.setText("");
            return valor;
        } else {
            throw new UnsupportedOperationException("La CPU no puede escribir.");
        }
    }
}
