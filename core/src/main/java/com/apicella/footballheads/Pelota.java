package com.apicella.footballheads;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Pelota {
    protected float x, y;
    protected float velocidadY = 0f;
    protected float velocidadX = 0f;
    protected boolean enElSuelo = true;
    protected final float ancho = 25, alto = 25;
    protected static final float GRAVEDAD = -750f;
    protected static final float FRENADO_PISO = 0.95f;

    protected float velocidadAngular = 0f;
    protected float rotacion = 0f;
    protected static final float SPIN_POR_PATEO = 220f;

    protected float inmunidadColisionJugador = 0f;
    protected static final float DURACION_INMUNIDAD_PELLIZCO = 0.35f;

    protected Texture texturaActual;
    protected Texture texturaNeutro;
    protected Circle circulo;

    public Pelota(float x, float y, float velocidadY, boolean enElSuelo, Texture texturaNeutro) {
        this.x = x;
        this.y = y;
        this.velocidadY = velocidadY;
        this.enElSuelo = enElSuelo;
        this.texturaActual = texturaNeutro;
        this.texturaNeutro = texturaNeutro;

        float radioHitbox = ancho / 2f;
        this.circulo = new Circle(0, 0, radioHitbox);
    }

    public void actualizar(float delta, float windValue) {
        if (inmunidadColisionJugador > 0f) {
            inmunidadColisionJugador -= delta;
        }

        if (enElSuelo) {
            velocidadX *= FRENADO_PISO;
            velocidadAngular *= FRENADO_PISO;
        }

        // Aplica empuje horizontal del viento, reduciéndolo levemente si la pelota está en el aire
        float multiplicadorViento = enElSuelo ? 1f : 0.85f;
        velocidadX += (windValue * multiplicadorViento) * delta;

        // Rotación de arrastre por el viento (el signo negativo compensa que LibGDX gira antihorario al sumar)
        if (enElSuelo && Math.abs(windValue) > 0.1f) {
            velocidadAngular -= (windValue * delta) * 20f; 
        }

        if (Math.abs(velocidadX) < 1f && Math.abs(windValue) < 0.1f) {
            velocidadX = 0f;
        }

        x += velocidadX * delta;

        if (x < 0) {
            x = 0;
            velocidadX = velocidadX * -0.6f;
        }
        if (x > FootballHeads.ANCHO_MUNDO - ancho) {
            x = FootballHeads.ANCHO_MUNDO - ancho;
            velocidadX = velocidadX * -0.6f;
        }

        velocidadY += GRAVEDAD * delta;
        y += velocidadY * delta;

        if (y <= 10) {
            y = 10;
            velocidadY = velocidadY * -0.6f;
            enElSuelo = true;
        } else {
            enElSuelo = false;
        }

        rotacion += velocidadAngular * delta;
        circulo.setPosition(x + ancho / 2f, y + alto / 2f);
    }

    private void aplicarPateo(Jugador jugador) {
        Circle botin = jugador.getCirculoBotin();
        boolean enCarrera = Math.abs(velocidadX) > 40f;

        float angulo = enCarrera ? 18f : 45f;
        float potencia = enCarrera ? 1400f : 900f;

        float dx = (x + ancho / 2f) - botin.x;
        float dy = (y + alto / 2f) - botin.y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        float distanciaMinima = circulo.radius + botin.radius;
        float t = MathUtils.clamp(distancia / distanciaMinima, 0f, 1f);

        potencia -= (t * 200f);
        float direccionX = jugador.direccion;
        float anguloRad = angulo * MathUtils.degreesToRadians;

        velocidadX = direccionX * potencia * MathUtils.cos(anguloRad);
        velocidadY = potencia * MathUtils.sin(anguloRad);
        velocidadAngular = direccionX * SPIN_POR_PATEO;
    }

    private static final float ALTURA_CABEZA_RELATIVA = 35f;

    public void cabezazo(Jugador j1, Jugador j2) {
        if (inmunidadColisionJugador > 0f) return;

        boolean chocaConJ1 = circulo.overlaps(j1.getCirculo());
        boolean chocaConJ2 = circulo.overlaps(j2.getCirculo());

        if (chocaConJ1 && y > j1.getY() + ALTURA_CABEZA_RELATIVA) {
            resolverColisionIndividual(j1, 500f, false);
        }
        if (chocaConJ2 && y > j2.getY() + ALTURA_CABEZA_RELATIVA) {
            resolverColisionIndividual(j2, 500f, false);
        }
    }

    public void pateada(float fuerzaDePateo, Jugador j1, Jugador j2, float delta) {
        if (j1.pateando && !j1.isContactoRealizado() && circulo.overlaps(j1.getCirculoBotin())) {
            aplicarPateo(j1);
            j1.marcarContactoRealizado();
        }
        if (j2.pateando && !j2.isContactoRealizado() && circulo.overlaps(j2.getCirculoBotin())) {
            aplicarPateo(j2);
            j2.marcarContactoRealizado();
        }
    }

    public void colisionarConJugadores(Jugador j1, Jugador j2) {
        if (inmunidadColisionJugador > 0f) return;

        boolean chocaConJ1 = circulo.overlaps(j1.getCirculo());
        boolean chocaConJ2 = circulo.overlaps(j2.getCirculo());

        if (chocaConJ1 && chocaConJ2) {
            velocidadY = 450f;
            velocidadX = 0f;
            y += 10f;
            velocidadAngular = 0f;
            inmunidadColisionJugador = DURACION_INMUNIDAD_PELLIZCO;
            circulo.setPosition(x + ancho / 2f, y + alto / 2f);
        } else if (chocaConJ1) {
            resolverColisionIndividual(j1, 100, true);
        } else if (chocaConJ2) {
            resolverColisionIndividual(j2, 100, true);
        }
    }

    private void resolverColisionIndividual(Jugador jugador, float fuerzaEmpuje, boolean soloHorizontal) {
        float dx = (x + ancho / 2f) - jugador.getCirculo().x;
        float dy = (y + alto / 2f) - jugador.getCirculo().y;
        float distanciaReal = (float) Math.sqrt(dx * dx + dy * dy);

        float direccionX, direccionY;
        if (soloHorizontal) {
            direccionX = (dx >= 0) ? 1f : -1f;
            direccionY = 0f;
        } else if (distanciaReal > 0) {
            direccionX = dx / distanciaReal;
            direccionY = dy / distanciaReal;
        } else {
            direccionX = 0f;
            direccionY = 1f;
        }

        float distanciaMinima = circulo.radius + jugador.getCirculo().radius;
        float superposicion = distanciaMinima - distanciaReal;

        if (superposicion > 0) {
            x += direccionX * superposicion;
            y += direccionY * superposicion;
        }

        velocidadX = fuerzaEmpuje * direccionX;
        if (!soloHorizontal) {
            velocidadY = fuerzaEmpuje * direccionY;
        }

        circulo.setPosition(x + ancho / 2f, y + alto / 2f);
    }

    public float manejarTravesano(Rectangle travesano, boolean esArcoIzquierdo, float tiempoQuieto, float delta) {
        final float UMBRAL_VELOCIDAD_QUIETA = 5f;
        final float TIEMPO_MAXIMO_VARADA = 0.6f;
        final float FUERZA_DESATASQUE = 60f;

        if (!com.badlogic.gdx.math.Intersector.overlaps(circulo, travesano)) {
            return 0f;
        }

        if (velocidadY < 0) {
            y = travesano.y + travesano.height;
            velocidadY = 0;

            float centroDeCancha = FootballHeads.ANCHO_MUNDO / 2f;
            float spin = 40f;
            velocidadAngular += (x > centroDeCancha) ? -spin : spin;

            if (Math.abs(velocidadX) < UMBRAL_VELOCIDAD_QUIETA) {
                tiempoQuieto += delta;
                if (tiempoQuieto > TIEMPO_MAXIMO_VARADA) {
                    velocidadX += esArcoIzquierdo ? FUERZA_DESATASQUE : -FUERZA_DESATASQUE;
                    tiempoQuieto = 0f;
                }
            } else {
                tiempoQuieto = 0f;
            }
        } else {
            y = travesano.y - alto;
            velocidadY *= -0.5f;
        }

        circulo.setPosition(x + ancho / 2f, y + alto / 2f);
        return tiempoQuieto;
    }

    public Circle getCirculo() { return circulo; }

    public void dibujar(SpriteBatch batch) {
        batch.draw(
            texturaActual,
            x, y,
            ancho / 2f, alto / 2f,
            ancho, alto,
            1f, 1f,
            rotacion,
            0, 0,
            texturaActual.getWidth(), texturaActual.getHeight(),
            false, false
        );
    }

    public void dispose() {
        texturaNeutro.dispose();
    }
}