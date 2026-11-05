package com.apicella.footballheads;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class ControlesFlechas {
    private final Jugador jugador;

    public ControlesFlechas(Jugador jugador) {
        this.jugador = jugador;
    }

    public void actualizar() {
        jugador.actualizarControles(
                Gdx.input.isKeyPressed(Keys.LEFT),
                Gdx.input.isKeyPressed(Keys.RIGHT),
                Gdx.input.isKeyPressed(Keys.UP),
                Gdx.input.isKeyJustPressed(Keys.P)
        );
    }

    public void limpiar() {
        jugador.limpiarControles();
    }
}
