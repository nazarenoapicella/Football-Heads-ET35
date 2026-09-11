package com.apicella.footballheads;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;

public class JugadorFlechas extends Jugador {

    public JugadorFlechas(float xInicial, float sueloY, Texture neutro) {
        super(xInicial, sueloY, neutro);
    }

    public JugadorFlechas(float xInicial, float sueloY, Texture neutro, Texture botin) {
        super(xInicial, sueloY, neutro, botin);
    }

    @Override
    protected void leerControles(float delta) {
        if (Gdx.input.isKeyPressed(Keys.LEFT))  { x -= velocidadX * delta; }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) { x += velocidadX * delta; }

        if (Gdx.input.isKeyJustPressed(Keys.P)) {
            iniciarPateo();
        }

        if (Gdx.input.isKeyPressed(Keys.UP) && enElSuelo) {
            velocidadY = FUERZA_SALTO;
            enElSuelo = false;
        }
    }
}