package com.apicella.footballheads;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

// Clase ABSTRACTA: no se puede instanciar directamente con "new Jugador(...)".
// Sirve como "molde" para JugadorFlechas y JugadorWASD, que sí son concretas.
public abstract class Jugador {

    // ---- Estado físico (común a cualquier jugador) ----
    protected float x, y;
    protected float velocidadY = 0f;
    protected boolean enElSuelo = true;
    protected final float ancho = 50, alto = 60;
    protected final float velocidadX = 130f;
    protected static final float GRAVEDAD = -750f;
    protected static final float FUERZA_SALTO = 275f;
    protected final float sueloY;

    // ---- Texturas (comunes en estructura, distintas en contenido) ----
    protected Texture texturaNeutro;
    protected Texture texturaPateando;
    protected Texture texturaActual;

    // ---- Hitbox circular para colisiones ----
    protected Circle circulo;

    public Jugador(float xInicial, float sueloY, Texture texturaNeutro, Texture texturaPateando) {
        this.x = xInicial;
        this.sueloY = sueloY;
        this.y = sueloY;
        this.texturaNeutro = texturaNeutro;
        this.texturaPateando = texturaPateando;
        this.texturaActual = texturaNeutro;

        float radioHitbox = (ancho / 2f) - 5f;
        this.circulo = new Circle(0, 0, radioHitbox);
    }

    // ============================================================
    // Método ABSTRACTO: cada subclase decide qué teclas usa.
    // Esto ES el polimorfismo: FootballHeads va a llamar
    // jugador.actualizar(delta) sin saber (ni importarle) si es
    // JugadorFlechas o JugadorWASD. Cada uno responde a SU manera.
    // ============================================================
    protected abstract void leerControles(float delta);

    // Método "template": define el ORDEN de la lógica de cada cuadro.
    // Los pasos son iguales para todos, pero leerControles() cambia según la subclase.
    public void actualizar(float delta) {
        texturaActual = texturaNeutro; // arranca "neutro" cada cuadro

        leerControles(delta); // <- polimorfismo acá

        // Física (igual para todos)
        velocidadY += GRAVEDAD * delta;
        y += velocidadY * delta;

        if (y <= sueloY) {
            y = sueloY;
            velocidadY = 0;
            enElSuelo = true;
        }

        // Actualiza el hitbox centrado en el sprite
        circulo.setPosition(x + ancho / 2f, y + alto / 2f);

        // Límites de la cancha
        if (x < 50) x = 50;
        if (x > FootballHeads.ANCHO_MUNDO - ancho - 50) {
            x = FootballHeads.ANCHO_MUNDO - ancho - 50;
        }
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(texturaActual, x, y, ancho, alto);
    }

    // Colisión circular entre este jugador y otro (podría ser cualquier
    // subclase de Jugador, no importa cuál — polimorfismo de nuevo).
    public void resolverColision(Jugador otro) {
        if (!circulo.overlaps(otro.circulo)) return;

        float dx = circulo.x - otro.circulo.x;
        float dy = circulo.y - otro.circulo.y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        float distanciaMinima = circulo.radius + otro.circulo.radius;
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
        texturaPateando.dispose();
    }
    
    // Getters útiles si FootballHeads necesita leer posición, etc.
    public float getX() { return x; }
    public float getY() { return y; }
}