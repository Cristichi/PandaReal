package es.cristichi.obj;

public enum CategoriaPuntuacion {
    /** Suma de dados amarillos. Determina el panda de la siguiente ronda. */
    AMARILLOS,
    /** Doble de la suma de dados morados. */
    MORADOS,
    /** Suma de dados azules. Si tienes uno o más purpurina, se doblan. */
    AZULES,
    /** Suma los rojos. La mitad menor de cada dado resta en vez de sumar. Luego, dobla la suma y resta total. */
    ROJOS,
    /** Suma de verdes. */
    VERDES,
    /** Suma de blancos. */
    BLANCOS,
    /** Suma de rosas. */
    ROSAS
}
