package com.apicella.footballheads;

import com.badlogic.gdx.math.Rectangle;

public class Partido {
    public static final float SUELO_Y = 10f;

    private static final float PELOTA_X_INICIAL = (FootballHeads.ANCHO_MUNDO - 25f) / 2f;
    private static final float PELOTA_Y_INICIAL = SUELO_Y + 250f;
    private static final float ANCHO_ARCO = 45f;
    private static final float ALTO_TRAVESANO = 6f;
    private static final float ALTURA_TRAVESANO = 130f;
    private static final float PROFUNDIDAD_GOL = 17f;
    private static final float ALTURA_MAXIMA_GOL = 120f;
    private static final float DURACION_PARTIDO = 60f;

    private final Jugador jugador1;
    private final Jugador jugador2;
    private final Pelota pelota;
    private final Rectangle travesanoArcoIzquierdo = new Rectangle(0f, ALTURA_TRAVESANO, ANCHO_ARCO, ALTO_TRAVESANO);
    private final Rectangle travesanoArcoDerecho = new Rectangle(FootballHeads.ANCHO_MUNDO - ANCHO_ARCO, ALTURA_TRAVESANO, ANCHO_ARCO, ALTO_TRAVESANO);
    private final GameplayManager gameplayManager = new GameplayManager();

    private float tiempoRestante = DURACION_PARTIDO;
    private float tiempoSobreArcoIzquierdo;
    private float tiempoSobreArcoDerecho;

    public Partido(Jugador jugador1, Jugador jugador2, Pelota pelota) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.pelota = pelota;
    }

    public void actualizar(float delta) {
        tiempoRestante = Math.max(0f, tiempoRestante - delta);
        gameplayManager.actualizar(delta);

        jugador1.actualizar(delta);
        jugador2.actualizar(delta);
        pelota.actualizar(delta, gameplayManager.obtenerVientoParaFisica());

        jugador1.resolverColision(jugador2);
        pelota.colisionarConJugadores(jugador1, jugador2);
        pelota.procesarCabezazos(jugador1, jugador2);
        pelota.procesarPateos(jugador1, jugador2);

        tiempoSobreArcoIzquierdo = pelota.manejarTravesano(travesanoArcoIzquierdo, true, tiempoSobreArcoIzquierdo, delta);
        tiempoSobreArcoDerecho = pelota.manejarTravesano(travesanoArcoDerecho, false, tiempoSobreArcoDerecho, delta);

        comprobarGoles();
    }

    private void comprobarGoles() {
        boolean pelotaBajaParaGol = pelota.getY() < ALTURA_MAXIMA_GOL;
        boolean pelotaEnArcoIzquierdo = pelotaBajaParaGol && pelota.getX() < PROFUNDIDAD_GOL;
        boolean pelotaEnArcoDerecho = pelotaBajaParaGol && pelota.getX() > FootballHeads.ANCHO_MUNDO - pelota.getAncho() - PROFUNDIDAD_GOL;

        if (pelotaEnArcoIzquierdo) {
            gameplayManager.registrarGolJugador1();
            reiniciarCancha();
        } else if (pelotaEnArcoDerecho) {
            gameplayManager.registrarGolJugador2();
            reiniciarCancha();
        }
    }

    private void reiniciarCancha() {
        pelota.reiniciar(PELOTA_X_INICIAL, PELOTA_Y_INICIAL);
        jugador1.reiniciar();
        jugador2.reiniciar();
        tiempoSobreArcoIzquierdo = 0f;
        tiempoSobreArcoDerecho = 0f;
    }

    public void reiniciarPartido() {
        gameplayManager.reiniciar();
        tiempoRestante = DURACION_PARTIDO;
        reiniciarCancha();
    }

    public void dispose() {
        jugador1.dispose();
        jugador2.dispose();
        pelota.dispose();
    }

    public boolean seAcaboElTiempo() { return tiempoRestante <= 0f; }

    public float getTiempoRestante() { return tiempoRestante; }

    public int getGolesJugador1() { return gameplayManager.getGolesJugador1(); }

    public int getGolesJugador2() { return gameplayManager.getGolesJugador2(); }

    public int getVientoParaMostrar() { return gameplayManager.obtenerVientoParaMostrar(); }
}