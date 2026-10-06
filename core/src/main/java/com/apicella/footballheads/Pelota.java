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
    protected boolean enElSuelo = false;
    protected final float ancho = 25, alto = 25;
    protected static final float GRAVEDAD = -750f;
    protected static final float FRENADO_PISO = 0.95f; //friccion con el piso
    protected float velocidadAngular = 0f; //a que velocidad gira sobre si
    protected float rotacion = 0f; //cuanto rota sobre si
    protected static final float SPIN_POR_PATEO = 220f; //es cuanto se setea el giro de la pelota
    protected float inmunidadColisionJugador = 0f; //tiempo donde se ignoran colisiones con jugadores
    protected static final float DURACION_INMUNIDAD_PELLIZCO = 0.35f;
    protected Texture textura;
    protected Circle hitboxPelota;

    public Pelota(float x, float y, float velocidadY, boolean enElSuelo, Texture textura) {
        this.x = x;
        this.y = y;
        this.velocidadY = velocidadY;
        this.enElSuelo = enElSuelo;
        this.textura = textura;

        float radioHitbox = ancho / 2f;
        this.hitboxPelota = new Circle(0, 0, radioHitbox);
    }

    public void actualizar(float delta, float windValue) {
        if (inmunidadColisionJugador > 0f) {
            inmunidadColisionJugador -= delta; 
        }

        if (enElSuelo) {
            velocidadX *= FRENADO_PISO; //su movimiento en x se frena
            velocidadAngular *= FRENADO_PISO; //su movimiento sobre su eje frena
        }
        
        //cuanto le afecta el viento dependiendo si esta en el aire o no
        float multiplicadorViento = enElSuelo ? 1f : 0.85f;
        velocidadX += (windValue * multiplicadorViento) * delta;

        // Rotación de arrastre por el viento (el signo negativo compensa que LibGDX gira antihorario al sumar)
        if (enElSuelo && Math.abs(windValue) > 0.1f) {
            velocidadAngular -= (windValue * delta) * 20f; 
        }

        if (Math.abs(velocidadX) < 1f && Math.abs(windValue) < 0.1f) {
            velocidadX = 0f;
        } //si la pelota esta muy cerca de frenar directamente se frena

        x += velocidadX * delta;

        if (x < 0) {
            x = 0;
            velocidadX = velocidadX * -0.6f;
        } //efecto rebote con paredes
        
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
        hitboxPelota.setPosition(x + ancho / 2f, y + alto / 2f);
    }

    private void aplicarPateo(Jugador jugador) {
        Circle botin = jugador.getHitboxBotin();
        boolean pelotaEnMovimiento = Math.abs(velocidadX) > 40f;

        float angulo = pelotaEnMovimiento ? 30f : 55f; //si estaba en mov. que salga con menos angulo
        float potencia = pelotaEnMovimiento ? 900f : 800f; //si estaba en mov. que salga con mas fuerza

        float dx = (x + ancho / 2f) - botin.x; //distancia de centros horizontales
        float dy = (y + alto / 2f) - botin.y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy); //pitagoras
        float distanciaMinima = hitboxPelota.radius + botin.radius; //donde se tocan recien
        float t = MathUtils.clamp(distancia / distanciaMinima, 0f, 1f); //proporcion entre 0 y 1 de separacion real y esperada

        potencia -= (t * 200f); //cuanto mas cerca estes menos potencia perdes
        float direccionX = jugador.direccion; //hacia donde se movera
        float anguloRad = angulo * MathUtils.degreesToRadians;

        velocidadX = direccionX * potencia * MathUtils.cos(anguloRad); //funciona como un vector con componentes
        velocidadY = potencia * MathUtils.sin(anguloRad);
        velocidadAngular = direccionX * SPIN_POR_PATEO; //su giro sobre su eje depende de donde mire el jugador
    }

    private static final float ALTURA_CABEZA_RELATIVA = 35f;

    public void cabezazo(Jugador j1, Jugador j2) {
        if (inmunidadColisionJugador > 0f) return;

        boolean chocaConJ1 = hitboxPelota.overlaps(j1.getHitboxJugador());
        boolean chocaConJ2 = hitboxPelota.overlaps(j2.getHitboxJugador());

        if (chocaConJ1 && y > j1.getY() + ALTURA_CABEZA_RELATIVA) { //si es mayor a una distancia superior a la normal (cabezazo)
            resolverColisionIndividual(j1, 500f, false);
        }
        if (chocaConJ2 && y > j2.getY() + ALTURA_CABEZA_RELATIVA) {
            resolverColisionIndividual(j2, 500f, false);
        }
    }

    public void pateada(float fuerzaDePateo, Jugador j1, Jugador j2, float delta) {
        if (j1.pateando && !j1.isContactoRealizado() && hitboxPelota.overlaps(j1.getHitboxBotin())) {
            aplicarPateo(j1);
            j1.marcarContactoRealizado();
        }
        if (j2.pateando && !j2.isContactoRealizado() && hitboxPelota.overlaps(j2.getHitboxBotin())) {
            aplicarPateo(j2);
            j2.marcarContactoRealizado();
        }
    }

    public void colisionarConJugadores(Jugador j1, Jugador j2) { //pelota en el medio de jugadores
        if (inmunidadColisionJugador > 0f) return;

        boolean chocaConJ1 = hitboxPelota.overlaps(j1.getHitboxJugador());
        boolean chocaConJ2 = hitboxPelota.overlaps(j2.getHitboxJugador());

        if (chocaConJ1 && chocaConJ2) {
            velocidadY = 450f;
            velocidadX = 0f; 
            y += 10f; //sale disparada rapidamente hacia arriba
            velocidadAngular = 0f;
            inmunidadColisionJugador = DURACION_INMUNIDAD_PELLIZCO;
            hitboxPelota.setPosition(x + ancho / 2f, y + alto / 2f);
        } else if (chocaConJ1) {
            resolverColisionIndividual(j1, 100, true);
        } else if (chocaConJ2) {
            resolverColisionIndividual(j2, 100, true);
        }
    }

    private void resolverColisionIndividual(Jugador jugador, float fuerzaEmpuje, boolean soloHorizontal) {
        float dx = (x + ancho / 2f) - jugador.getHitboxJugador().x;
        float dy = (y + alto / 2f) - jugador.getHitboxJugador().y;
        float distanciaReal = (float) Math.sqrt(dx * dx + dy * dy); //distancia entre centros de pelota y jugador

        float direccionX, direccionY;
        if (soloHorizontal) {
            direccionX = (dx >= 0) ? 1f : -1f; //si choca solo con un jugador define para donde se mueve acompañandolo
            direccionY = 0f;
        } else if (distanciaReal > 0) {
            direccionX = dx / distanciaReal; //proporcion entre la distancia X de la pelota con respecto a la del jugador
            direccionY = dy / distanciaReal;
        } else {
            direccionX = 0f;
            direccionY = 1f; //si estan exactamente a la misma distancia que salga para arriba
        }

        float distanciaMinima = hitboxPelota.radius + jugador.getHitboxJugador().radius; //distancia donde solo se tocan
        float superposicion = distanciaMinima - distanciaReal; //cuanto de mas hay

        if (superposicion > 0) {
            x += direccionX * superposicion; //la movemos para donde acompañe el jugador
            y += direccionY * superposicion;
        }

        velocidadX = fuerzaEmpuje * direccionX; //inercia de movimiento
        if (!soloHorizontal) {
            velocidadY = fuerzaEmpuje * direccionY; //inercia vertical
        }

        hitboxPelota.setPosition(x + ancho / 2f, y + alto / 2f);
    }

    public float manejarTravesano(Rectangle travesano, boolean esArcoIzquierdo, float tiempoQuieto, float delta) {
        final float UMBRAL_VELOCIDAD_QUIETA = 5f;
        final float TIEMPO_MAXIMO_VARADA = 0.6f;
        final float FUERZA_DESATASQUE = 60f;

        if (!com.badlogic.gdx.math.Intersector.overlaps(hitboxPelota, travesano)) {
            return 0f;
        }

        if (velocidadY < 0) { //si viene desde arriba se apoya sobre el
            y = travesano.y + travesano.height;
            velocidadY = 0;

            float centroDeCancha = FootballHeads.ANCHO_MUNDO / 2f;
            float spin = 40f;
            velocidadAngular += (x > centroDeCancha) ? -spin : spin; //dependiendo en que arco cayo se movera para un lado distinto

            if (Math.abs(velocidadX) < UMBRAL_VELOCIDAD_QUIETA) { //si se mueve muy lento o quieta cuenta
                tiempoQuieto += delta;
                if (tiempoQuieto > TIEMPO_MAXIMO_VARADA) {
                    velocidadX += esArcoIzquierdo ? FUERZA_DESATASQUE : -FUERZA_DESATASQUE;
                    tiempoQuieto = 0f;
                }
            } else { //se reinicia porque ya se movio
                tiempoQuieto = 0f;
            }
        } else { //si choco por patear
            y = travesano.y - alto;
            velocidadY *= -0.5f;
        }

        hitboxPelota.setPosition(x + ancho / 2f, y + alto / 2f);
        return tiempoQuieto;
    }

    public Circle getCirculo() { return hitboxPelota; }

    public void dibujar(SpriteBatch batch) {
        batch.draw(
            textura,
            x, y,
            ancho / 2f, alto / 2f,
            ancho, alto,
            1f, 1f,
            rotacion,
            0, 0,
            textura.getWidth(), textura.getHeight(),
            false, false
        );
    }

    public void dispose() {
        textura.dispose();
    }
}