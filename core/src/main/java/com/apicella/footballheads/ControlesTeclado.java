package com.apicella.footballheads;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

/** Traduce teclas del teclado a una EntradaJugador. */
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

    public static ControlesTeclado flechas() {
        return new ControlesTeclado(Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.P);
    }

    public static ControlesTeclado wasd() {
        return new ControlesTeclado(Keys.A, Keys.D, Keys.W, Keys.TAB);
    }

    /** Se llama UNA vez por frame de render (no por paso de simulacion). */
    public void leerEn(EntradaJugador entrada) {
        entrada.izquierda = Gdx.input.isKeyPressed(teclaIzquierda);
        entrada.derecha = Gdx.input.isKeyPressed(teclaDerecha);
        entrada.saltar = Gdx.input.isKeyPressed(teclaSaltar);
        // |= para no perder la patada si en este frame no corre ningun paso de simulacion
        entrada.patear |= Gdx.input.isKeyJustPressed(teclaPatear);
    }
}