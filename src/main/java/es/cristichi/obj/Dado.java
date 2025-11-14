package es.cristichi.obj;

public record Dado(ColorDado colorDado, int caras) {
    public int lanzar() {
        return (int) (Math.random() * caras) + 1;
    }

    @Override
    public String toString() {
        return "[d%d %s]".formatted(caras, colorDado.toString());
    }
}
