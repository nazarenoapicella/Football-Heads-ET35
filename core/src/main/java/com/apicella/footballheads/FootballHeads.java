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
    private BitmapFont marcadorDeGoles;
    private BitmapFont marcadorViento;
    private Texture texturaBotinCompartida;
    private GameplayManager gameplayManager;
    private float tiempoQuietoArco1 = 0f;
    private float tiempoQuietoArco2 = 0f;

    @Override
    public void create() {
        batch = new SpriteBatch(); //es el que dibuja todo
        camera = new OrthographicCamera(); //es la camara 2d
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera); //es el que ajusta el mundo ficticio a la ventana real
        camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0); //setea donde estara ubicada la camara al iniciar el juego
        fondoCancha = new Texture(Gdx.files.internal("MapaReferencia.jpeg"));
        marcadorDeGoles = new BitmapFont(); //crea una fuente de texto
        marcadorDeGoles.getData().setScale(3f); //setea su tamaño
        marcadorViento = new BitmapFont();
        marcadorViento.getData().setScale(1.4f);
        gameplayManager = new GameplayManager();
        Texture texturaBotin = new Texture(Gdx.files.internal("botin.png"));
        this.texturaBotinCompartida = texturaBotin;

        jugador1 = new JugadorFlechas(
            (ANCHO_MUNDO-(20*ANCHO_MUNDO)/100), SUELO_Y,
            new Texture(Gdx.files.internal("nazaNeutro.png")),
            texturaBotin
        ); //posiciónX, posiciónY, textura, botín
        jugador2 = new JugadorWASD(
        	(ANCHO_MUNDO-(80*ANCHO_MUNDO)/100 - jugador1.getAncho()), SUELO_Y,
            new Texture(Gdx.files.internal("mirkoNeutro.png")),
            texturaBotin
        );
        
        pelota = new Pelota(
            ((ANCHO_MUNDO-25) / 2f), SUELO_Y + 250, 0, false,
            new Texture(Gdx.files.internal("pelota.png"))
        ); // x, y, velocidadY, enElSuelo, textura
        
        rectangulo1 = new Rectangle(0, 140, 45, 0); //x, y, ancho, alto
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

        tiempoQuietoArco1 = pelota.manejarTravesano(rectangulo1, true, tiempoQuietoArco1, delta);
        tiempoQuietoArco2 = pelota.manejarTravesano(rectangulo2, false, tiempoQuietoArco2, delta);

        if (pelota.x < 20 && pelota.y < 120) {
            gameplayManager.golJ1(); //handler de goles
            reiniciarCancha();
        } else if (pelota.x > ANCHO_MUNDO - 25 - 20 && pelota.y < 120) {
            gameplayManager.golJ2();
            reiniciarCancha();
        }
    }

    private void reiniciarCancha() {
        pelota.x = (ANCHO_MUNDO - 25) /2;
        pelota.y = SUELO_Y + 250;
        pelota.velocidadX = 0f;
        pelota.velocidadY = 0f;
        pelota.velocidadAngular = 0f;

        jugador1.x = (ANCHO_MUNDO-(20*ANCHO_MUNDO)/100);
        jugador1.y= SUELO_Y;

        jugador2.x = (ANCHO_MUNDO-(80*ANCHO_MUNDO)/100 - jugador2.getAncho());
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

        marcadorDeGoles.draw(batch, gameplayManager.getGolesJ2() + " - " + gameplayManager.getGolesJ1(),(ANCHO_MUNDO / 2f) - 45, ALTO_MUNDO - 20);
        marcadorViento.draw(batch, formatearViento(gameplayManager.getWindDisplayEntero()), 20, ALTO_MUNDO - 20);

        batch.end();
    }

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
        marcadorDeGoles.dispose();
        marcadorViento.dispose();
        texturaBotinCompartida.dispose();
    }
}