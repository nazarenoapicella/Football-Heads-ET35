package com.apicella.footballheads;

import com.badlogic.gdx.math.MathUtils;

public class GameplayManager {
    private int golesJ1 = 0;
    private int golesJ2 = 0;

    private float windActualMs = 0f;
    private float windObjetivoMs = 0f;
    private float tiempoRestanteObjetivo = 0f;

    private static final float VIENTO_MS_MAX = 5f;
    private static final float VELOCIDAD_CAMBIO_VIENTO = 2.5f;
    private static final float DURACION_OBJETIVO_MIN = 4f;
    private static final float DURACION_OBJETIVO_MAX = 9f;

    // Aumentado el multiplicador de física del viento de 6f a 12f para que influya visiblemente en la pelota parada
    private static final float ESCALA_FISICA_VIENTO = 12f;

    public void actualizar(float delta) {
        tiempoRestanteObjetivo -= delta;
        if (tiempoRestanteObjetivo <= 0f) {
            windObjetivoMs = MathUtils.random((int) -VIENTO_MS_MAX, (int) VIENTO_MS_MAX);
            tiempoRestanteObjetivo = MathUtils.random(DURACION_OBJETIVO_MIN, DURACION_OBJETIVO_MAX);
        }

        float diferencia = windObjetivoMs - windActualMs;
        float paso = VELOCIDAD_CAMBIO_VIENTO * delta;
        if (Math.abs(diferencia) <= paso) {
            windActualMs = windObjetivoMs;
        } else {
            windActualMs += Math.signum(diferencia) * paso;
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