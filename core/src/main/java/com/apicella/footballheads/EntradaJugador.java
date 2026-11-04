package com.apicella.footballheads;

public class EntradaJugador {
    public boolean moverIzquierda;
    public boolean moverDerecha;
    public boolean saltar;
    public boolean solicitarPateo;

    public void limpiar() {
        moverIzquierda = false;
        moverDerecha = false;
        saltar = false;
        solicitarPateo = false;
    }
}