package com.apicella.footballheads;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Jugador {
    private static final float ANCHO = 50f;
    private static final float ALTO = 60f;
    private static final float RADIO_JUGADOR = ANCHO / 2f - 5f;
    private static final float RADIO_BOTIN = 10f;
    private static final float VELOCIDAD_HORIZONTAL = 130f;
    private static final float GRAVEDAD = -750f;
    private static final float FUERZA_SALTO = 275f;
    private static final float DURACION_PATEO = 0.35f;
    private static final float LIMITE_IZQUIERDO = FootballHeads.ANCHO_MUNDO * 0.055f;
    private static final float LIMITE_DERECHO = FootballHeads.ANCHO_MUNDO * 0.875f;
    
    private static final float BOTIN_X_EN_REPOSO = -2f;
    private static final float BOTIN_X_EXTENDIDO = 35f;
    private static final float BOTIN_Y_EN_REPOSO = 6f;
    private static final float BOTIN_Y_EXTENDIDO = ALTO * 0.3f;
    private static final float BOTIN_ANGULO_EN_REPOSO = -15f;
    private static final float BOTIN_ANGULO_EXTENDIDO = 60f;

    private final float xInicial;
    private final float sueloY;
    private final float direccion;
    private final Texture texturaNeutro;
    private final Texture texturaBotin;
    
    private final Circle hitboxJugador = new Circle(0f, 0f, RADIO_JUGADOR);
    private final Circle hitboxBotin = new Circle(0f, 0f, RADIO_BOTIN);

    private boolean moverIzquierda;
    private boolean moverDerecha;
    private boolean saltar;
    private boolean solicitarPateo;

    private float x;
    private float y;
    private float velocidadY;
    private float tiempoPateo = -1f;
    private float anguloBotin;
    private boolean enElSuelo = true;
    private boolean contactoRealizado;

    public Jugador(float xInicial, float sueloY, Texture texturaNeutro, Texture texturaBotin) {
        this.xInicial = xInicial;
        this.sueloY = sueloY;
        this.x = xInicial;
        this.y = sueloY;
        this.texturaNeutro = texturaNeutro;
        this.texturaBotin = texturaBotin;
        if (xInicial < FootballHeads.ANCHO_MUNDO / 2f) {
            this.direccion = 1f; 
        } else {
            this.direccion = -1f; 
        }

        actualizarHitboxes();
    }

    public void actualizar(float delta) {
        aplicarEntrada(delta);

        velocidadY += GRAVEDAD * delta;
        y += velocidadY * delta;
        
        limitarPosicionEnCancha();

        actualizarPateo(delta);
        actualizarHitboxJugador();
    }

    private void aplicarEntrada(float delta) {
        if (moverIzquierda) {
            x -= VELOCIDAD_HORIZONTAL * delta;
        }
        if (moverDerecha) {
            x += VELOCIDAD_HORIZONTAL * delta;
        }
        if (solicitarPateo) {
            iniciarPateo();
            solicitarPateo = false;
        }
        if (saltar && enElSuelo) {
            velocidadY = FUERZA_SALTO;
            enElSuelo = false;
        }
    }

    private void iniciarPateo() {
        if (tiempoPateo < 0f) {
            tiempoPateo = 0f;
            contactoRealizado = false;
        }
    }

    private void actualizarPateo(float delta) {
        float progreso = tiempoPateo < 0 ? 0f : MathUtils.clamp(tiempoPateo / DURACION_PATEO, 0f, 1f);
        actualizarPosicionBotin(progreso);
        
        if (tiempoPateo < 0f) {
            return;
        }

        tiempoPateo += delta;
        if (tiempoPateo >= DURACION_PATEO) {
            tiempoPateo = -1f;
        }
    }


    private void actualizarPosicionBotin(float progreso) {
        float extension = MathUtils.sin(progreso * MathUtils.PI);

        float desplazamientoX = MathUtils.lerp(BOTIN_X_EN_REPOSO * direccion, BOTIN_X_EXTENDIDO * direccion, extension);
        float desplazamientoY = MathUtils.lerp(BOTIN_Y_EN_REPOSO, BOTIN_Y_EXTENDIDO, extension);

        hitboxBotin.setPosition(x + ANCHO / 2f + desplazamientoX, y + desplazamientoY);
        anguloBotin = MathUtils.lerp(BOTIN_ANGULO_EN_REPOSO, BOTIN_ANGULO_EXTENDIDO, extension) * direccion;
    }

    private void actualizarHitboxJugador() {
        hitboxJugador.setPosition(x + ANCHO / 2f, y + ALTO / 2f);
    }

    private void actualizarHitboxes() {
        actualizarHitboxJugador();
        actualizarPosicionBotin(tiempoPateo < 0 ? 0f : MathUtils.clamp(tiempoPateo / DURACION_PATEO, 0f, 1f));
    }

    private void limitarPosicionEnCancha() {
        x = MathUtils.clamp(x, LIMITE_IZQUIERDO, LIMITE_DERECHO);
        if (y <= sueloY) {
            y = sueloY;
            velocidadY = 0f;
            enElSuelo = true;
        }
    }

    public void resolverColision(Jugador otroJugador) {
        if (!hitboxJugador.overlaps(otroJugador.hitboxJugador)) {
            return;
        }

        float diferenciaX = hitboxJugador.x - otroJugador.hitboxJugador.x;
        float diferenciaY = hitboxJugador.y - otroJugador.hitboxJugador.y;
        float distancia = Vector2.len(diferenciaX, diferenciaY);

        float radiosSuma = hitboxJugador.radius + otroJugador.hitboxJugador.radius;
        float superposicion = radiosSuma - distancia;

        float empujeX = (diferenciaX / distancia) * (superposicion / 2f);
        float empujeY = (diferenciaY / distancia) * (superposicion / 2f);

        x += empujeX;
        y += empujeY;
        otroJugador.x -= empujeX;
        otroJugador.y -= empujeY;

        if (empujeY > 0f && y > otroJugador.y) {
            velocidadY = 0f;
            enElSuelo = true;
        } else if (empujeY < 0f && otroJugador.y > y) {
            otroJugador.velocidadY = 0f;
            otroJugador.enElSuelo = true;
        }
    }

    public void reiniciar() {
        x = xInicial;
        y = sueloY;
        velocidadY = 0f;
        tiempoPateo = -1f;
        enElSuelo = true;
        contactoRealizado = false;
        limpiarControles();
        actualizarHitboxes();
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(texturaNeutro, x, y, ANCHO, ALTO);

        float diametroBotin = RADIO_BOTIN * 2f;
        boolean voltearX = (direccion < 0f);
        
        batch.draw(
            texturaBotin, 
            hitboxBotin.x - RADIO_BOTIN, 
            hitboxBotin.y - RADIO_BOTIN,
            RADIO_BOTIN, 
            RADIO_BOTIN, 
            diametroBotin, 
            diametroBotin, 
            1f, 
            1f, 
            anguloBotin,
            0, 
            0, 
            texturaBotin.getWidth(), 
            texturaBotin.getHeight(), 
            voltearX, 
            false
        );
    }

    public void dispose() {
        texturaNeutro.dispose();
    }

    public void actualizarControles(boolean moverIzquierda, boolean moverDerecha, boolean saltar, boolean solicitarPateo) {
        this.moverIzquierda = moverIzquierda;
        this.moverDerecha = moverDerecha;
        this.saltar = saltar;
        this.solicitarPateo = solicitarPateo;
    }

    public void limpiarControles() {
        moverIzquierda = false;
        moverDerecha = false;
        saltar = false;
        solicitarPateo = false;
    }

    public float getVelocidadHorizontal() {
        float factorDireccion = 0f;
        
        if (moverDerecha) {
            factorDireccion += 1f;
        }
        if (moverIzquierda) {
            factorDireccion -= 1f;
        }
        
        return factorDireccion * VELOCIDAD_HORIZONTAL;
    }

    public float getY() { 
        return y; 
    }

    public float getAncho() { 
        return ANCHO; 
    }

    public Circle getHitboxJugador() { 
        return hitboxJugador; 
    }

    public Circle getHitboxBotin() { 
        return hitboxBotin; 
    }

    public float getDireccion() { 
        return direccion; 
    }

    public boolean isPateando() { 
        return tiempoPateo >= 0f; 
    }

    public boolean isContactoRealizado() { 
        return contactoRealizado; 
    }

    public void marcarContactoRealizado() { 
        contactoRealizado = true; 
    }
}