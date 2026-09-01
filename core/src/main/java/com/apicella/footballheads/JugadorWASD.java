package com.apicella.footballheads;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;

public class JugadorWASD extends Jugador {
	Pelota pelota;
    public JugadorWASD(float xInicial, float sueloY, Texture neutro, Texture pateando) {
        super(xInicial, sueloY, neutro, pateando);
    }

    @Override
    protected void leerControles(float delta) {
        if (Gdx.input.isKeyPressed(Keys.A)) x -= velocidadX * delta;
        if (Gdx.input.isKeyPressed(Keys.D)) x += velocidadX * delta;
        if (Gdx.input.isKeyJustPressed(Keys.TAB) || Gdx.input.isKeyPressed(Keys.TAB)) {
        	texturaActual = texturaPateando;
        	pateando = true;
        }
        if (Gdx.input.isKeyPressed(Keys.W) && enElSuelo) {
            velocidadY = FUERZA_SALTO;
            enElSuelo = false;
        }
        
    }
}