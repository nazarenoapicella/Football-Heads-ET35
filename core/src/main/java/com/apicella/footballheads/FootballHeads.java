package com.apicella.footballheads;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);

        fondoCancha = new Texture(Gdx.files.internal("MapaReferencia.jpeg"));
        
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
                (ANCHO_MUNDO / 1.93f) - 25, SUELO_Y + 150, 0, true, 
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
        // Cada uno se actualiza a sí mismo con SU lógica de controles.
        jugador1.actualizar(delta);
        jugador2.actualizar(delta);
        pelota.actualizar(delta);
        // La colisión no necesita saber qué tipo de jugador es cada uno.
        jugador1.resolverColision(jugador2);
     
        
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
    }
}