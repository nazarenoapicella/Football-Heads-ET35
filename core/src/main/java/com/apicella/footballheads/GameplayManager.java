package com.apicella.footballheads;

import com.badlogic.gdx.math.MathUtils;

public class GameplayManager {
    private int golesJ1 = 0;
    private int golesJ2 = 0;

    private float windActualMs = 0f;
    private float windObjetivoMs = 0f;
    private float tiempoRestanteDeCambio = 0f;

    private static final float VIENTO_MS_MAX = 5f;
    private static final float VELOCIDAD_CAMBIO_VIENTO = 2.5f;
    private static final float DURACION_OBJETIVO_MIN = 4f;
    private static final float DURACION_OBJETIVO_MAX = 9f;

    private static final float ESCALA_FISICA_VIENTO = 12f;

    public void actualizar(float delta) {
        tiempoRestanteDeCambio -= delta;
        if (tiempoRestanteDeCambio <= 0f) {
            windObjetivoMs = MathUtils.random((int) -VIENTO_MS_MAX, (int) VIENTO_MS_MAX); //viento random entre -5 y 5
            tiempoRestanteDeCambio = MathUtils.random(DURACION_OBJETIVO_MIN, DURACION_OBJETIVO_MAX);//tiempo random de cambio entre 4 y 9
        }

        float diferencia = windObjetivoMs - windActualMs; //averiguamos la distancia a la que queremos llegar
        float salto = VELOCIDAD_CAMBIO_VIENTO * delta; //obtenemos el salto que se hara por cada frame
        if (Math.abs(diferencia) <= salto) { // si |x| < a esa distancia simplemente igualamos para no pasarnos
            windActualMs = windObjetivoMs;
        } else {
            windActualMs += Math.signum(diferencia) * salto; //sino se suma/resta ese salto
        }
    }

    public float getWindValueParaFisica() {
        return windActualMs * ESCALA_FISICA_VIENTO;
    }

    public int getWindDisplayEntero() {
        return Math.round(windActualMs);
    }
    
    public void golJ1() { golesJ1++; }
    public void golJ2() { golesJ2++; }

    public int getGolesJ1() { return golesJ1; }
    public int getGolesJ2() { return golesJ2; }
}