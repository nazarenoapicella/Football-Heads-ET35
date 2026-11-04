package com.apicella.footballheads;

import com.badlogic.gdx.math.MathUtils;

public class GameplayManager {
    private static final float VELOCIDAD_VIENTO_MAXIMA = 5f;
    private static final float VELOCIDAD_CAMBIO_VIENTO = 2.5f;
    private static final float DURACION_VIENTO_MINIMA = 4f;
    private static final float DURACION_VIENTO_MAXIMA = 9f;
    private static final float ESCALA_FISICA_VIENTO = 12f;

    private int golesJugador1;
    private int golesJugador2;
    private float velocidadVientoActual;
    private float velocidadVientoObjetivo;
    private float tiempoParaNuevoViento;

    public void actualizar(float delta) {
        tiempoParaNuevoViento -= delta;
        if (tiempoParaNuevoViento <= 0f) {
            velocidadVientoObjetivo = MathUtils.random(-VELOCIDAD_VIENTO_MAXIMA, VELOCIDAD_VIENTO_MAXIMA);
            tiempoParaNuevoViento = MathUtils.random(DURACION_VIENTO_MINIMA, DURACION_VIENTO_MAXIMA);
        }

        float cambioMaximo = VELOCIDAD_CAMBIO_VIENTO * delta;
        float diferenciaViento = velocidadVientoObjetivo - velocidadVientoActual;
        velocidadVientoActual += MathUtils.clamp(diferenciaViento, -cambioMaximo, cambioMaximo);
    }

    public float obtenerVientoParaFisica() { return velocidadVientoActual * ESCALA_FISICA_VIENTO; }

    public int obtenerVientoParaMostrar() { return Math.round(velocidadVientoActual); }

    public void registrarGolJugador1() { golesJugador1++; }

    public void registrarGolJugador2() { golesJugador2++; }

    public void reiniciar() {
        golesJugador1 = 0;
        golesJugador2 = 0;
        velocidadVientoActual = 0f;
        velocidadVientoObjetivo = 0f;
        tiempoParaNuevoViento = 0f;
    }

    public int getGolesJugador1() { return golesJugador1; }

    public int getGolesJugador2() { return golesJugador2; }
}