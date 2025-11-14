package es.cristichi.exc;

public class DadoNoEncontrado extends RuntimeException{
    public DadoNoEncontrado() {
        super("No se ha encontrado el dado solicitado.");
    }
}
