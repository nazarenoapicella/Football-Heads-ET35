package com.apicella.footballheads;

/**
 * Lo que un jugador "quiere hacer" en este momento.
 * Hoy la llena el teclado (ControlesTeclado); cuando llegue la red (UDP),
 * el mismo objeto lo va a llenar el paquete recibido del otro jugador.
 * Jugador solo lee esto: no sabe de donde viene.
 */
public class EntradaJugador {
    public boolean izquierda;
    public boolean derecha;
    public boolean saltar;
    public boolean patear; // pedido puntual: Jugador lo consume y lo pone en false

    public void limpiar() {
        izquierda = false;
        derecha = false;
        saltar = false;
        patear = false;
    }
}