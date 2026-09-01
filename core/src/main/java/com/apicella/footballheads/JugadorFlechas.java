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
        if (Gdx.input.isKeyJustPressed(Keys.P) || Gdx.input.isKeyPressed(Keys.P)) {
        	texturaActual = texturaPateando;
        	pateando = true;
        }
        if (Gdx.input.isKeyPressed(Keys.UP) && enElSuelo) {
            velocidadY = FUERZA_SALTO;
            enElSuelo = false;
        }
    }
}