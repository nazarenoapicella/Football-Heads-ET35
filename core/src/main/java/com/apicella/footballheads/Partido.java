package com.apicella.footballheads;

import com.badlogic.gdx.math.Rectangle;

/**
 * Todo el estado y la logica del partido, SIN dibujar nada.
 * Es lo que despues se va a sincronizar por red.
 */
public class Partido {
    public static final float SUELO_Y = 10f;
    private static final float PELOTA_X_INICIAL = (FootballHeads.ANCHO_MUNDO - 25f) / 2f;
    private static final float PELOTA_Y_INICIAL = SUELO_Y + 250f;

    private final Jugador jugador1;
    private final Jugador jugador2;
    private final Pelota pelota;
    private final Rectangle rectangulo1 = new Rectangle(0, 140, 45, 0); //x, y, ancho, alto
    private final Rectangle rectangulo2 = new Rectangle(FootballHeads.ANCHO_MUNDO - 45, 140, 100, 0);
    private final GameplayManager gameplayManager = new GameplayManager();
    private float tiempoQuietoArco1 = 0f;
    private float tiempoQuietoArco2 = 0f;

    public Partido(Jugador jugador1, Jugador jugador2, Pelota pelota) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.pelota = pelota;
    }

    public void actualizar(float delta) {
        gameplayManager.actualizar(delta);

        jugador1.actualizar(delta);
        jugador2.actualizar(delta);
        pelota.actualizar(delta, gameplayManager.getWindValueParaFisica());

        jugador1.resolverColision(jugador2);
        pelota.colisionarConJugadores(jugador1, jugador2);
        pelota.cabezazo(jugador2, jugador1);
        pelota.pateada(jugador1.fuerzaDePateo, jugador1, jugador2, delta);

        tiempoQuietoArco1 = pelota.manejarTravesano(rectangulo1, true, tiempoQuietoArco1, delta);
        tiempoQuietoArco2 = pelota.manejarTravesano(rectangulo2, false, tiempoQuietoArco2, delta);

        if (pelota.x < 20 && pelota.y < 120) {
            gameplayManager.golJ1();
            reiniciarCancha();
        } else if (pelota.x > FootballHeads.ANCHO_MUNDO - 25 - 20 && pelota.y < 120) {
            gameplayManager.golJ2();
            reiniciarCancha();
        }
    }

    /** Vuelve todos a su lugar (despues de un gol). No toca el marcador. */
    private void reiniciarCancha() {
        pelota.reiniciar(PELOTA_X_INICIAL, PELOTA_Y_INICIAL);
        jugador1.reiniciar();
        jugador2.reiniciar();
    }

    /** Partido nuevo: marcador en 0, viento nuevo y todos en su lugar. */
    public void reiniciarPartido() {
        gameplayManager.reiniciar();
        tiempoQuietoArco1 = 0f;
        tiempoQuietoArco2 = 0f;
        reiniciarCancha();
        limpiarEntradas();
    }

    public void limpiarEntradas() {
        jugador1.getEntrada().limpiar();
        jugador2.getEntrada().limpiar();
    }

    public Jugador getJugador1() { return jugador1; }
    public Jugador getJugador2() { return jugador2; }
    public Pelota getPelota() { return pelota; }
    public GameplayManager getGameplayManager() { return gameplayManager; }

    public void dispose() {
        jugador1.dispose();
        jugador2.dispose();
        pelota.dispose();
    }
}