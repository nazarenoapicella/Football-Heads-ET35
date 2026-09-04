package com.apicella.footballheads;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
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
    private int golesJ1 = 0;
    private int golesJ2 = 0;
    private BitmapFont fuente;
    private float tiempoQuietoArco1 = 0f;
    private float tiempoQuietoArco2 = 0f;
    private static final float UMBRAL_VELOCIDAD_QUIETA = 5f;   // px/seg, "casi sin moverse"
    private static final float TIEMPO_MAXIMO_VARADA = 0.6f;     // segundos parada antes de empujarla
    private static final float FUERZA_DESATASQUE = 60f;         // impulso horizontal para salir
    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        fondoCancha = new Texture(Gdx.files.internal("MapaReferencia.jpeg"));
        
        fuente = new BitmapFont();
        fuente.getData().setScale(3f);

        jugador1 = new JugadorFlechas(
            (ANCHO_MUNDO / 1.25f) - (37 / 2f), SUELO_Y,
            new Texture(Gdx.files.internal("nazaNeutro.png")),
            new Texture(Gdx.files.internal("nazaPateando.png"))
        );

        jugador2 = new JugadorWASD(
            (ANCHO_MUNDO / 5.15f) - (37 / 2f), SUELO_Y,
            new Texture(Gdx.files.internal("mirkoNeutro.png")),
            new Texture(Gdx.files.internal("mirkoPateando.png"))
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
        jugador1.actualizar(delta);
        jugador2.actualizar(delta);
        pelota.actualizar(delta);
        
        jugador1.resolverColision(jugador2);
        pelota.colisionarConJugadores(jugador1, jugador2);
        pelota.cabezazo(jugador2, jugador1);
        pelota.pateada(jugador1.fuerzaDePateo, jugador1, jugador2, delta);

        // --- COLISIONES CON EL TECHO / TRAVESAÑO DE LOS ARCOS ---
     // --- COLISIONES CON EL TECHO / TRAVESAÑO DE LOS ARCOS ---
     // --- COLISIONES CON EL TECHO / TRAVESAÑO DE LOS ARCOS ---
        if (Intersector.overlaps(pelota.getCirculo(), rectangulo1)) {
            if (pelota.velocidadY < 0) {
                pelota.y = rectangulo1.y + rectangulo1.height;
                pelota.velocidadY = 0;

                if (Math.abs(pelota.velocidadX) < UMBRAL_VELOCIDAD_QUIETA) {
                    tiempoQuietoArco1 += delta;
                    if (tiempoQuietoArco1 > TIEMPO_MAXIMO_VARADA) {
                        pelota.velocidadX += FUERZA_DESATASQUE; // arco1 está a la izquierda -> la empuja hacia la derecha, afuera del poste
                        tiempoQuietoArco1 = 0f;
                    }
                } else {
                    tiempoQuietoArco1 = 0f;
                }
            } else {
                pelota.y = rectangulo1.y - pelota.alto;
                pelota.velocidadY *= -0.5f;
            }
        } else {
            tiempoQuietoArco1 = 0f;
        }

        if (Intersector.overlaps(pelota.getCirculo(), rectangulo2)) {
            if (pelota.velocidadY < 0) {
                pelota.y = rectangulo2.y + rectangulo2.height;
                pelota.velocidadY = 0;

                if (Math.abs(pelota.velocidadX) < UMBRAL_VELOCIDAD_QUIETA) {
                    tiempoQuietoArco2 += delta;
                    if (tiempoQuietoArco2 > TIEMPO_MAXIMO_VARADA) {
                        pelota.velocidadX -= FUERZA_DESATASQUE; // arco2 está a la derecha -> la empuja hacia la izquierda, afuera del poste
                        tiempoQuietoArco2 = 0f;
                    }
                } else {
                    tiempoQuietoArco2 = 0f;
                }
            } else {
                pelota.y = rectangulo2.y - pelota.alto;
                pelota.velocidadY *= -0.5f;
            }
        } else {
            tiempoQuietoArco2 = 0f;
        }
        // --- SISTEMA DE GOLES ---
        if (pelota.x < 20 && pelota.y < 120) { 
            golesJ1++; 
            reiniciarCancha();
        } 
        else if (pelota.x > ANCHO_MUNDO - 25 - 20 && pelota.y < 120) {
            golesJ2++; 
            reiniciarCancha();
        }
    }

    private void reiniciarCancha() {
        pelota.x = (ANCHO_MUNDO / 1.93f) - 25;
        pelota.y = SUELO_Y + 250;
        pelota.velocidadX = 0f;
        pelota.velocidadY = 0f;

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
        
        fuente.draw(batch, golesJ2 + " - " + golesJ1, (ANCHO_MUNDO / 2f) - 45, ALTO_MUNDO - 20);
        
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
        jugador1.dispose();
        jugador2.dispose();
        fuente.dispose();
    }
}