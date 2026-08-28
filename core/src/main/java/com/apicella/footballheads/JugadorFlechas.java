package com.apicella.footballheads;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;

public class JugadorFlechas extends Jugador {

    public JugadorFlechas(float xInicial, float sueloY, Texture neutro, Texture pateando) {
        super(xInicial, sueloY, neutro, pateando);
    }

    @Override
    protected void leerControles(float delta) {
        if (Gdx.input.isKeyPressed(Keys.LEFT))  x -= velocidadX * delta;
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) x += velocidadX * delta;

        if (Gdx.input.isKeyJustPressed(Keys.P)) texturaActual = texturaPateando;

        if (Gdx.input.isKeyPressed(Keys.UP) && enElSuelo) {
            velocidadY = FUERZA_SALTO;
            enElSuelo = false;
        }
    }
}