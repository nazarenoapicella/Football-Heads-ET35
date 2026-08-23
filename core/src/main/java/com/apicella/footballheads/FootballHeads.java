package com.apicella.footballheads;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FootballHeads extends ApplicationAdapter {

    // --- Herramientas de dibujo ---
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    // --- Imágenes ---
    private Texture fondoCancha;
    private Texture jugadorNeutro;
    private Texture jugadorCorriendo;
    private Texture jugadorPateando;
    private Texture jugadorActual; // la que se dibuja en cada momento

    // --- Tamaño "lógico" del mundo del juego (en unidades, no en píxeles de pantalla) ---
    private static final float ANCHO_MUNDO = 800;
    private static final float ALTO_MUNDO = 480;

    // --- Datos del jugador ---
    private float jugadorX, jugadorY;
    private float jugadorAncho = 30, jugadorAlto = 60;
    private float velocidadX = 130f;   // píxeles/seg al moverse
    private float velocidadY = 0f;     // velocidad vertical actual (salto/caída)
    private static final float GRAVEDAD = -750f; // fuerza que tira hacia abajo
    private static final float FUERZA_SALTO = 270f;
    private boolean enElSuelo = true;
    private float sueloY = 55; // altura del "piso" dentro de la cancha

    @Override
    public void create() {
        batch = new SpriteBatch();

        // La cámara define qué parte del mundo se ve. FitViewport mantiene
        // la proporción sin importar el tamaño de la ventana.
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);

        // Cargar imágenes (esto SOLO se hace una vez, acá en create())
        fondoCancha = new Texture(Gdx.files.internal("Mapa_referencia.jpg"));
        jugadorNeutro = new Texture(Gdx.files.internal("jugador_neutro.png"));
        jugadorCorriendo = new Texture(Gdx.files.internal("jugador_corriendo.png"));
        jugadorPateando = new Texture(Gdx.files.internal("jugador_pateando.png"));
        jugadorActual = jugadorNeutro;

        // Posición inicial del jugador: parado sobre el piso, cerca del centro
        jugadorX = ANCHO_MUNDO / 2f - jugadorAncho / 2f;
        jugadorY = sueloY;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime(); // tiempo desde el frame anterior

        actualizar(delta); // 1) actualizamos la lógica/física
        dibujar();          // 2) dibujamos todo
    }

    private void actualizar(float delta) {
        jugadorActual = jugadorNeutro; // por defecto, salvo que se mueva/patee

        // --- Movimiento horizontal ---
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
            jugadorX -= velocidadX * delta;
            jugadorActual = jugadorCorriendo;
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            jugadorX += velocidadX * delta;
            jugadorActual = jugadorCorriendo;
        }

        // --- Salto ---
        if ((Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W))
                && enElSuelo) {
            velocidadY = FUERZA_SALTO;
            enElSuelo = false;
        }

        // --- Patada (todavía sin efecto sobre una pelota, solo la animación) ---
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            jugadorActual = jugadorPateando;
        }

        // --- Física: gravedad ---
        velocidadY += GRAVEDAD * delta;   // la gravedad va frenando/acelerando la caída
        jugadorY += velocidadY * delta;   // aplicamos la velocidad vertical a la posición

        // --- Colisión con el piso ---
        if (jugadorY <= sueloY) {
            jugadorY = sueloY;
            velocidadY = 0;
            enElSuelo = true;
        }

        // --- Colisión con los límites laterales de la cancha ---
        if (jugadorX < 0) jugadorX = 0;
        if (jugadorX > ANCHO_MUNDO - jugadorAncho) jugadorX = ANCHO_MUNDO - jugadorAncho;
    }

    private void dibujar() {
        ScreenUtils.clear(0, 0, 0, 1); // limpia la pantalla (fondo negro antes de dibujar)

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Fondo, ocupando todo el mundo visible
        batch.draw(fondoCancha, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        // Jugador
        batch.draw(jugadorActual, jugadorX, jugadorY, jugadorAncho, jugadorAlto);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondoCancha.dispose();
        jugadorNeutro.dispose();
        jugadorCorriendo.dispose();
        jugadorPateando.dispose();
    }
}