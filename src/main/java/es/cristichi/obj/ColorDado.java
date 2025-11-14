package es.cristichi.obj;

public enum ColorDado {
    AMARILLO("Amarillo"), MORADO("Morado"), AZUL("Azul"), AZUL_PURPURINA("Azul Purpurina"),
    ROJO("Rojo"), VERDE("Verde"), BLANCO("Blanco"), ROSA("Rosa");
    private final String nombre;

    ColorDado(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
