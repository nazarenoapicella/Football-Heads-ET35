package com.apicella.footballheads;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class ControlesWASD {
    private final Jugador jugador;

    public ControlesWASD(Jugador jugador) {
        this.jugador = jugador;
    }

    public void actualizar() {
        jugador.actualizarControles(
                Gdx.input.isKeyPressed(Keys.A),
                Gdx.input.isKeyPressed(Keys.D),
                Gdx.input.isKeyPressed(Keys.W),
                Gdx.input.isKeyJustPressed(Keys.TAB)
        );
    }

    public void limpiar() {
        jugador.limpiarControles();
    }
}
