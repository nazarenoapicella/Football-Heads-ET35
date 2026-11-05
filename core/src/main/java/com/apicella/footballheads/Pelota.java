
package com.apicella.footballheads;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Pelota {
    private static final float ANCHO = 25f;
    private static final float ALTO = 25f;
    private static final float GRAVEDAD = -750f;
    private static final float FRENADO_PISO = 0.95f;
    private static final float FACTOR_REBOTE = 0.62f;
    private static final float FACTOR_REBOTE_TRAVESANO = 0.5f;
    private static final float EFECTO_VIENTO_EN_EL_AIRE = 0.85f;
    private static final float GIRO_POR_VIENTO = 20f;

    private static final float VELOCIDAD_PELOTA_EN_MOVIMIENTO = 30f;
    private static final float ANGULO_PATEO_PELOTA_QUIETA = 55f;
    private static final float ANGULO_PATEO_PELOTA_EN_MOVIMIENTO = 30f;
    private static final float POTENCIA_PATEO_PELOTA_QUIETA = 800f;
    private static final float POTENCIA_PATEO_PELOTA_EN_MOVIMIENTO = 900f;
    private static final float PERDIDA_POTENCIA_POR_LEJANIA = 200f;
    private static final float GIRO_POR_PATEO = 220f;

    private static final float ALTURA_CABEZA_RELATIVA = 35f;
    private static final float FUERZA_CABEZAZO = 430f;
    private static final float FUERZA_EMPUJE_CUERPO = 30f;
    private static final float VELOCIDAD_SALIDA_PELLIZCO = 450f;
    private static final float DURACION_INMUNIDAD_COLISION = 0.35f;

    private static final float TIEMPO_MAXIMO_SOBRE_TRAVESANO = 0.6f;
    private static final float FUERZA_SALIDA_TRAVESANO = 100f;
    private static final float GIRO_TRAVESANO = 40f;

    private final Texture textura;
    private final Circle hitboxPelota;

    private float x;
    private float y;
    private float velocidadX;
    private float velocidadY;
    private float velocidadAngular;
    private float rotacion;
    private float tiempoInmunidadColision;
    private boolean enElSuelo;

    public Pelota(float x, float y, Texture textura) {
        this.x = x;
        this.y = y;
        this.textura = textura;
        this.hitboxPelota = new Circle(x + ANCHO / 2f, y + ALTO / 2f, ANCHO / 2f);
    }

    public void actualizar(float delta, float aceleracionViento) {
        tiempoInmunidadColision = Math.max(0f, tiempoInmunidadColision - delta);

        if (enElSuelo) {
            velocidadX *= FRENADO_PISO;
            velocidadAngular *= FRENADO_PISO;
        }

        boolean hayViento = Math.abs(aceleracionViento) > 0.1f;

        velocidadX += aceleracionViento * (enElSuelo ? 1f : EFECTO_VIENTO_EN_EL_AIRE) * delta;


        if (enElSuelo && hayViento) {
            velocidadAngular -= aceleracionViento * delta * GIRO_POR_VIENTO;
        }

        if (!hayViento && Math.abs(velocidadX) < 1f) {
            velocidadX = 0f;
        }

        x += velocidadX * delta;
        limitarMovimientoHorizontal();

        velocidadY += GRAVEDAD * delta;
        y += velocidadY * delta;

        enElSuelo = y <= Partido.SUELO_Y;

        if (enElSuelo) {
            y = Partido.SUELO_Y;
            velocidadY *= -FACTOR_REBOTE;
        }

        rotacion += velocidadAngular * delta;
        actualizarHitbox();
    }

    private void limitarMovimientoHorizontal() {
        float limiteDerecho = FootballHeads.ANCHO_MUNDO - ANCHO;

        if (x < 0f || x > limiteDerecho) {
            x = MathUtils.clamp(x, 0f, limiteDerecho);
            velocidadX *= -FACTOR_REBOTE;
        }
    }

    public void procesarPateos(Jugador jugador1, Jugador jugador2) {
        if (jugador1.isPateando() && !jugador1.isContactoRealizado() && hitboxPelota.overlaps(jugador1.getHitboxBotin())) {
            aplicarPateo(jugador1);
            jugador1.marcarContactoRealizado();
            return; 
        }

        if (jugador2.isPateando() && !jugador2.isContactoRealizado() && hitboxPelota.overlaps(jugador2.getHitboxBotin())) {
            aplicarPateo(jugador2);
            jugador2.marcarContactoRealizado();
        }
    }
    
    private void aplicarPateo(Jugador jugador) {
        Circle hitboxBotin = jugador.getHitboxBotin();

        boolean pelotaYaSeMovia = Math.abs(velocidadX) > VELOCIDAD_PELOTA_EN_MOVIMIENTO;

        float anguloGrados = pelotaYaSeMovia ? ANGULO_PATEO_PELOTA_EN_MOVIMIENTO: ANGULO_PATEO_PELOTA_QUIETA;

        float potenciaInicial = pelotaYaSeMovia ? POTENCIA_PATEO_PELOTA_EN_MOVIMIENTO: POTENCIA_PATEO_PELOTA_QUIETA;

        float distancia = Vector2.dst(hitboxPelota.x,hitboxPelota.y,hitboxBotin.x,hitboxBotin.y);
        float distanciaDeContacto = hitboxPelota.radius + hitboxBotin.radius;
        float porcentajeDistancia = MathUtils.clamp(distancia / distanciaDeContacto,0f,1f);

        float potenciaFinal = potenciaInicial - porcentajeDistancia * PERDIDA_POTENCIA_POR_LEJANIA;
        float direccionDelPateo = jugador.getDireccion();

        velocidadX = direccionDelPateo * potenciaFinal * MathUtils.cosDeg(anguloGrados);
        velocidadY = potenciaFinal * MathUtils.sinDeg(anguloGrados);
        velocidadAngular = direccionDelPateo * GIRO_POR_PATEO;
        enElSuelo = false;
    }

    public void procesarCabezazos(Jugador jugador1, Jugador jugador2) {
        if (tiempoInmunidadColision > 0f) {
            return;
        }

        if (tocaAlJugador(jugador1) && estaSobreLaCabeza(jugador1)) {
            resolverColisionIndividual(jugador1, FUERZA_CABEZAZO, false);
        }
        
        if (tocaAlJugador(jugador2) && estaSobreLaCabeza(jugador2)) {
            resolverColisionIndividual(jugador2, FUERZA_CABEZAZO, false);
        }
    }

    public void colisionarConJugadores(Jugador jugador1, Jugador jugador2) {
        if (tiempoInmunidadColision > 0f) {
            return;
        }

        boolean tocaJugador1 = tocaAlJugador(jugador1);
        boolean tocaJugador2 = tocaAlJugador(jugador2);

        if (tocaJugador1 && tocaJugador2) {
            aplicarPellizco();
        } else if ((tocaJugador1 || tocaJugador2)&& estaAtrapadaEntreJugadores(jugador1, jugador2, tocaJugador1, tocaJugador2)) {
            aplicarPellizco();
        } else if (tocaJugador1 && !estaSobreLaCabeza(jugador1)) {
            resolverColisionIndividual(jugador1, FUERZA_EMPUJE_CUERPO, true);
        } else if (tocaJugador2 && !estaSobreLaCabeza(jugador2)) {
            resolverColisionIndividual(jugador2, FUERZA_EMPUJE_CUERPO, true);
        }
    }

    private boolean estaAtrapadaEntreJugadores(Jugador jugador1,Jugador jugador2,boolean tocaJugador1,boolean tocaJugador2) {
        Jugador jugadorEnContacto = tocaJugador1 ? jugador1 : jugador2;

        if (estaSobreLaCabeza(jugadorEnContacto)) {
            return false;
        }

        Circle hitboxJugador1 = jugador1.getHitboxJugador();
        Circle hitboxJugador2 = jugador2.getHitboxJugador();

        float centroIzquierdo = Math.min(hitboxJugador1.x, hitboxJugador2.x);
        float centroDerecho = Math.max(hitboxJugador1.x, hitboxJugador2.x);

        float distanciaEntreJugadores = centroDerecho - centroIzquierdo;
        float distanciaMaxima = 2f * (hitboxPelota.radius + hitboxJugador1.radius);

        return hitboxPelota.x > centroIzquierdo && hitboxPelota.x < centroDerecho && distanciaEntreJugadores <= distanciaMaxima;
    }

    private boolean tocaAlJugador(Jugador jugador) {
        return hitboxPelota.overlaps(jugador.getHitboxJugador());
    }

    private boolean estaSobreLaCabeza(Jugador jugador) {
        return y > jugador.getY() + ALTURA_CABEZA_RELATIVA;
    }

    private void aplicarPellizco() {
        velocidadX = 0f;
        velocidadY = VELOCIDAD_SALIDA_PELLIZCO;
        velocidadAngular = 0f;
        enElSuelo = false;
        tiempoInmunidadColision = DURACION_INMUNIDAD_COLISION;
        actualizarHitbox();
    }

    private void resolverColisionIndividual(Jugador jugador,float fuerzaEmpuje,boolean soloHorizontal) {
        Circle hitboxJugador = jugador.getHitboxJugador();

        float diferenciaX = hitboxPelota.x - hitboxJugador.x;
        float diferenciaY = hitboxPelota.y - hitboxJugador.y;

        float distanciaMinima = hitboxPelota.radius + hitboxJugador.radius;

        if (soloHorizontal) {
            float direccionX = diferenciaX >= 0f ? 1f : -1f;

            float distanciaVertical = Math.min(Math.abs(diferenciaY), distanciaMinima);

            float distanciaHorizontalNecesaria = (float) Math.sqrt(distanciaMinima * distanciaMinima- distanciaVertical * distanciaVertical);

            x += direccionX * Math.max(0f,distanciaHorizontalNecesaria - Math.abs(diferenciaX));

            float velocidadJugadorHaciaPelota = Math.max(0f,jugador.getVelocidadHorizontal() * direccionX);

            velocidadX = direccionX * (fuerzaEmpuje + velocidadJugadorHaciaPelota);

        } else {
            float distancia = Vector2.len(diferenciaX, diferenciaY);

            float direccionX = distancia > 0f ? diferenciaX / distancia : 0f;
            float direccionY = distancia > 0f ? diferenciaY / distancia : 1f;

            float superposicion = Math.max(0f, distanciaMinima - distancia);

            x += direccionX * superposicion;
            y += direccionY * superposicion;

            velocidadX = fuerzaEmpuje * direccionX;
            velocidadY = fuerzaEmpuje * direccionY;
        }

        limitarMovimientoHorizontal();

        if (y < Partido.SUELO_Y) {
            y = Partido.SUELO_Y;
            velocidadY = Math.max(0f, velocidadY);
            enElSuelo = true;
        }

        actualizarHitbox();
    }

    public float manejarTravesano(Rectangle travesano,boolean esArcoIzquierdo,float tiempoSobreTravesano,float delta) {
        if (!Intersector.overlaps(hitboxPelota, travesano)) {
            return 0f;
        }

        if (velocidadY < 0f) {
            y = travesano.y + travesano.height;
            velocidadY = 0f;

            velocidadAngular = x > FootballHeads.ANCHO_MUNDO / 2f ? -GIRO_TRAVESANO: GIRO_TRAVESANO;

            tiempoSobreTravesano += delta;

            if (tiempoSobreTravesano > TIEMPO_MAXIMO_SOBRE_TRAVESANO) {
                velocidadX = esArcoIzquierdo? FUERZA_SALIDA_TRAVESANO : -FUERZA_SALIDA_TRAVESANO;
                tiempoSobreTravesano = 0f;
            }
        } else {
            y = travesano.y - ALTO;
            velocidadY *= -FACTOR_REBOTE_TRAVESANO;
            tiempoSobreTravesano = 0f;
        }

        actualizarHitbox();
        return tiempoSobreTravesano;
    }

    private void actualizarHitbox() {
        hitboxPelota.setPosition(x + ANCHO / 2f, y + ALTO / 2f);
    }

    public void reiniciar(float xInicial, float yInicial) {
        x = xInicial;
        y = yInicial;
        velocidadX = 0f;
        velocidadY = 0f;
        velocidadAngular = 0f;
        rotacion = 0f;
        tiempoInmunidadColision = 0f;
        enElSuelo = false;
        actualizarHitbox();
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(
                textura,
                x,
                y,
                ANCHO / 2f,
                ALTO / 2f,
                ANCHO,
                ALTO,
                1f,
                1f,
                rotacion,
                0,
                0,
                textura.getWidth(),
                textura.getHeight(),
                false,
                false
        );
    }

    public void dispose() {
        textura.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAncho() {
        return ANCHO;
    }
}
