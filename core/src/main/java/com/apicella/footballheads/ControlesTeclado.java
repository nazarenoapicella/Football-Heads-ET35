package com.apicella.footballheads;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class ControlesTeclado {
    private final int teclaIzquierda;
    private final int teclaDerecha;
    private final int teclaSaltar;
    private final int teclaPatear;

    public ControlesTeclado(int teclaIzquierda, int teclaDerecha, int teclaSaltar, int teclaPatear) {
        this.teclaIzquierda = teclaIzquierda;
        this.teclaDerecha = teclaDerecha;
        this.teclaSaltar = teclaSaltar;
        this.teclaPatear = teclaPatear;
    }

    public static ControlesTeclado crearControlesFlechas() {
        return new ControlesTeclado(Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.P);
    }

    public static ControlesTeclado crearControlesWASD() {
        return new ControlesTeclado(Keys.A, Keys.D, Keys.W, Keys.TAB);
    }

    public void actualizarEntrada(EntradaJugador entrada) {
        entrada.moverIzquierda = Gdx.input.isKeyPressed(teclaIzquierda);
        entrada.moverDerecha = Gdx.input.isKeyPressed(teclaDerecha);
        entrada.saltar = Gdx.input.isKeyPressed(teclaSaltar);
        entrada.solicitarPateo |= Gdx.input.isKeyJustPressed(teclaPatear);
    }
}