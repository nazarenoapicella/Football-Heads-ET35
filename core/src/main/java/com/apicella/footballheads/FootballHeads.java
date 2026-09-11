package com.apicella.footballheads;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Cambios respecto a la versión anterior, en base a la ingeniería inversa
 * del juego real (app.js):
 *
 *  - Se agrega GameplayManager (marcador + viento), reflejando el patrón de
 *    managers del juego real (Game.gameplayManager, Game.graphicsManager,
 *    etc.).
 *  - La lógica de colisión con el travesaño/arco (que antes vivía duplicada
 *    acá para arco1 y arco2) ahora la resuelve Pelota.manejarTravesano(),
 *    espejo del goalContactHandler() real.
 *  - ANCHO_MUNDO=800 ya coincidía con el centro de cancha real (x=400) que
 *    encontré en el app.js — no hizo falta tocarlo.
 */
public class FootballHeads extends ApplicationAdapter {
    public static final float ANCHO_MUNDO = 800;
    public static final float ALTO_MUNDO = 480;
    private static final float SUELO_Y = 10;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture fondoCancha;
    private Pelota pelota;
    private Jugador jugador1;
    private Jugador jugador2;
    public Rectangle rectangulo1;
    public Rectangle rectangulo2;
    private BitmapFont fuente;
    private BitmapFont fuenteViento;
    // Compartida entre ambos jugadores (o null si no existe el archivo) —
    // se libera acá, no en Jugador.dispose(), justamente por ser compartida.
    private Texture texturaBotinCompartida;

    // Nuevo: manager de gameplay (reemplaza a golesJ1/golesJ2 sueltos)
    private GameplayManager gameplayManager;

    private float tiempoQuietoArco1 = 0f;
    private float tiempoQuietoArco2 = 0f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        fondoCancha = new Texture(Gdx.files.internal("MapaReferencia.jpeg"));

        fuente = new BitmapFont();
        fuente.getData().setScale(3f);

        fuenteViento = new BitmapFont();
        fuenteViento.getData().setScale(1.4f);

        gameplayManager = new GameplayManager();

        // Textura del botín: OPCIONAL. Si todavía no tenés el archivo, esto no
        // rompe nada — sigue jugando exactamente igual que antes, solo que sin
        // dibujar el pie por separado del cuerpo.
        Texture texturaBotin = null;
        if (Gdx.files.internal("botin.png").exists()) {
            texturaBotin = new Texture(Gdx.files.internal("botin.png"));
        }
        this.texturaBotinCompartida = texturaBotin;

        jugador1 = new JugadorFlechas(
            (ANCHO_MUNDO / 1.25f) - (37 / 2f), SUELO_Y,
            new Texture(Gdx.files.internal("nazaNeutro.png")),
            texturaBotin
        );

        jugador2 = new JugadorWASD(
            (ANCHO_MUNDO / 5.15f) - (37 / 2f), SUELO_Y,
            new Texture(Gdx.files.internal("mirkoNeutro.png")),
            texturaBotin
        );

        pelota = new Pelota(
            (ANCHO_MUNDO / 1.93f) - 25, SUELO_Y + 250, 0, true,
            new Texture(Gdx.files.internal("pelota.png"))
        );

        rectangulo1 = new Rectangle(0, 140, 45, 0);
        rectangulo2 = new Rectangle(ANCHO_MUNDO - 45, 140, 100, 0);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        actualizar(delta);
        dibujar();
    }

    private void actualizar(float delta) {
        gameplayManager.actualizar(delta);

        jugador1.actualizar(delta);
        jugador2.actualizar(delta);
        pelota.actualizar(delta, gameplayManager.getWindValueParaFisica());

        jugador1.resolverColision(jugador2);
        pelota.colisionarConJugadores(jugador1, jugador2);
        pelota.cabezazo(jugador2, jugador1);
        pelota.pateada(jugador1.fuerzaDePateo, jugador1, jugador2, delta);

        // --- COLISIONES CON EL TRAVESAÑO DE LOS ARCOS (ahora en Pelota) ---
        tiempoQuietoArco1 = pelota.manejarTravesano(rectangulo1, true, tiempoQuietoArco1, delta);
        tiempoQuietoArco2 = pelota.manejarTravesano(rectangulo2, false, tiempoQuietoArco2, delta);

        // --- SISTEMA DE GOLES ---
        if (pelota.x < 20 && pelota.y < 120) {
            gameplayManager.golJ1();
            reiniciarCancha();
        } else if (pelota.x > ANCHO_MUNDO - 25 - 20 && pelota.y < 120) {
            gameplayManager.golJ2();
            reiniciarCancha();
        }
    }

    private void reiniciarCancha() {
        pelota.x = (ANCHO_MUNDO / 1.93f) - 25;
        pelota.y = SUELO_Y + 250;
        pelota.velocidadX = 0f;
        pelota.velocidadY = 0f;
        pelota.velocidadAngular = 0f;

        jugador1.x = (ANCHO_MUNDO / 1.25f) - (37 / 2f);
        jugador1.y = SUELO_Y;

        jugador2.x = (ANCHO_MUNDO / 5.15f) - (37 / 2f);
        jugador2.y = SUELO_Y;
    }

    private void dibujar() {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(fondoCancha, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);

        jugador1.dibujar(batch);
        jugador2.dibujar(batch);
        pelota.dibujar(batch);

        fuente.draw(batch, gameplayManager.getGolesJ2() + " - " + gameplayManager.getGolesJ1(),
            (ANCHO_MUNDO / 2f) - 45, ALTO_MUNDO - 20);

        fuenteViento.draw(batch, formatearViento(gameplayManager.getWindDisplayEntero()), 20, ALTO_MUNDO - 20);

        batch.end();
    }

    /** "3 m/s ->", "1 m/s <-", o "Sin viento" cuando redondea a 0. */
    private String formatearViento(int velocidadEntera) {
        if (velocidadEntera == 0) return "Sin viento";
        String flecha = velocidadEntera > 0 ? "->" : "<-";
        return Math.abs(velocidadEntera) + " m/s " + flecha;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondoCancha.dispose();
        jugador1.dispose();
        jugador2.dispose();
        fuente.dispose();
        fuenteViento.dispose();
        if (texturaBotinCompartida != null) {
            texturaBotinCompartida.dispose();
        }
    }
}