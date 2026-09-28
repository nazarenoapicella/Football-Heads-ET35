package com.apicella.footballheads;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

public abstract class Jugador {
    protected float x, y;
    protected float velocidadY = 0f;
    protected boolean enElSuelo = true;
    protected final float ancho = 50, alto = 60;
    protected final float velocidadX = 130f;
    protected static final float GRAVEDAD = -750f;
    protected static final float FUERZA_SALTO = 275f;
    protected final float sueloY;
    protected float fuerzaDePateo = 100f;
    protected boolean pateando = false;
    protected Texture texturaNeutro;
    protected Circle hitbox;
    protected Circle circuloBotin;
    protected float direccion; // 1 = mira a la derecha, -1 = mira a la izquierda
    protected float tiempoPateo = -1f;
    protected boolean contactoRealizado = false;
    protected static final float DURACION_PATEO = 0.35f;
    protected static final float RADIO_BOTIN = 10f;
    protected Texture texturaBotin;
    protected float anguloBotin = 0f;

    public Jugador(float xInicial, float sueloY, Texture texturaNeutro, Texture texturaBotin) {
        this.x = xInicial;
        this.sueloY = sueloY;
        this.y = sueloY;
        this.texturaNeutro = texturaNeutro;
        this.texturaBotin = texturaBotin;
        float radioHitbox = (ancho / 2f) - 5f;
        this.hitbox = new Circle(0, 0, radioHitbox);

        this.circuloBotin = new Circle(0, 0, RADIO_BOTIN);
        this.direccion = (xInicial < FootballHeads.ANCHO_MUNDO / 2f) ? 1f : -1f;
        actualizarPosicionBotin(0f);
    }

    protected abstract void leerControles(float delta);

    public void actualizar(float delta) {
        leerControles(delta);
        velocidadY += GRAVEDAD * delta;
        y += velocidadY * delta;

        if (y <= sueloY) {
            y = sueloY;
            velocidadY = 0;
            enElSuelo = true;
        }

        hitbox.setPosition(x + ancho / 2f, y + alto / 2f);

        if (x < (FootballHeads.ANCHO_MUNDO*((6f)/100f)) ) x = (FootballHeads.ANCHO_MUNDO*((5.5f)/100f));
        if (x > FootballHeads.ANCHO_MUNDO *(0.875f)) {
            x = (FootballHeads.ANCHO_MUNDO *(0.875f));
        }

        actualizarPateo(delta);
    }

    protected void iniciarPateo() {
        if (tiempoPateo < 0f) {
            tiempoPateo = 0f;
            contactoRealizado = false;
        }
    }

    private void actualizarPateo(float delta) {
        if (tiempoPateo >= 0f) {
            pateando = true;
            float progreso = MathUtils.clamp(tiempoPateo / DURACION_PATEO, 0f, 1f);
            actualizarPosicionBotin(progreso);
            tiempoPateo += delta;
            if (tiempoPateo >= DURACION_PATEO) {
                tiempoPateo = -1f;
                pateando = false;
                actualizarPosicionBotin(0f);
            }
        }else {
            pateando = false;
            actualizarPosicionBotin(0f);
        }
    }

    private void actualizarPosicionBotin(float progreso) {
        float extension = MathUtils.sin(progreso * MathUtils.PI);

        float offsetXReposo = -2f * direccion;
        float offsetXExtendido = 35f * direccion;
        float offsetX = offsetXReposo + (offsetXExtendido - offsetXReposo) * extension;

        float offsetYReposo = 6f;
        float offsetYExtendido = alto * 0.6f;
        float offsetY = offsetYReposo + (offsetYExtendido - offsetYReposo) * extension;

        circuloBotin.setPosition(x + ancho / 2f + offsetX, y + offsetY);

        float anguloReposo = -15f;
        float anguloExtendido = 60f;
        this.anguloBotin = (anguloReposo + (anguloExtendido - anguloReposo) * extension) * direccion;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(texturaNeutro, x, y, ancho, alto);
        float diametro = RADIO_BOTIN * 2f;
        batch.draw(
                texturaBotin,
                circuloBotin.x - RADIO_BOTIN, circuloBotin.y - RADIO_BOTIN,
                RADIO_BOTIN, RADIO_BOTIN,
                diametro, diametro,
                1f, 1f,
                anguloBotin,
                0, 0,
                texturaBotin.getWidth(), texturaBotin.getHeight(),
                direccion < 0, false
            );
    }

    public void resolverColision(Jugador otro) {
        if (!hitbox.overlaps(otro.hitbox)) return;

        float dx = hitbox.x - otro.hitbox.x;
        float dy = hitbox.y - otro.hitbox.y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        float distanciaMinima = hitbox.radius + otro.hitbox.radius;
        float superposicion = distanciaMinima - distancia;

        if (superposicion > 0 && distancia > 0) {
            float empujeX = (dx / distancia) * (superposicion / 2f);
            float empujeY = (dy / distancia) * (superposicion / 2f);

            this.x += empujeX;
            this.y += empujeY;
            otro.x -= empujeX;
            otro.y -= empujeY;

            if (empujeY > 0 && this.y > otro.y) {
                this.velocidadY = 0;
                this.enElSuelo = true;
            } else if (empujeY < 0 && otro.y > this.y) {
                otro.velocidadY = 0;
                otro.enElSuelo = true;
            }
        }
    }

    public void dispose() {
        texturaNeutro.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return ancho; }
    public Circle getHitbox() { return hitbox; }
    public Circle getCirculoBotin() { return circuloBotin; }
    public boolean isContactoRealizado() { return contactoRealizado; }
    public void marcarContactoRealizado() { contactoRealizado = true; }
}