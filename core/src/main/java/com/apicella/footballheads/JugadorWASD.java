package com.apicella.footballheads;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;

public class JugadorWASD extends Jugador {

    public JugadorWASD(float xInicial, float sueloY, Texture neutro) {
        super(xInicial, sueloY, neutro);
    }

    public JugadorWASD(float xInicial, float sueloY, Texture neutro, Texture botin) {
        super(xInicial, sueloY, neutro, botin);
    }

    @Override
    protected void leerControles(float delta) {
        if (Gdx.input.isKeyPressed(Keys.A)) { x -= velocidadX * delta; }
        if (Gdx.input.isKeyPressed(Keys.D)) { x += velocidadX * delta; }

        if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
            iniciarPateo();
        }

        if (Gdx.input.isKeyPressed(Keys.W) && enElSuelo) {
            velocidadY = FUERZA_SALTO;
            enElSuelo = false;
        }
    }
}