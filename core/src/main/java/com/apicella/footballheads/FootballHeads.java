package com.apicella.footballheads;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Importado para el texto de los goles
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

    // Variables del marcador
    private int golesJ1 = 0;
    private int golesJ2 = 0;
    private BitmapFont fuente;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        
        fondoCancha = new Texture(Gdx.files.internal("MapaReferencia.jpeg"));
        
        // Configuración de la fuente para el marcador
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

        // --- SISTEMA DE GOLES ---
        // (Asegurate de quitar los rebotes de x < 50 y x > ANCHO_MUNDO - 50 en la clase Pelota)
        if (pelota.x < 20 && pelota.y < 120) { 
            golesJ1++; // Entró al arco izquierdo (Gol del Jugador 1, que está a la derecha)
            reiniciarCancha();
        } 
        else if (pelota.x > ANCHO_MUNDO - 25 - 20 && pelota.y < 120) {
            golesJ2++; // Entró al arco derecho (Gol del Jugador 2, que está a la izquierda)
            reiniciarCancha();
        }
    }

    private void reiniciarCancha() {
        // Volver la pelota al medio
        pelota.x = (ANCHO_MUNDO / 1.93f) - 25;
        pelota.y = SUELO_Y + 250;
        pelota.velocidadX = 0f;
        pelota.velocidadY = 0f;

        // Volver los jugadores a su lugar
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
        
        // Se dibuja primero el marcador de J2 a la izquierda y el de J1 a la derecha
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