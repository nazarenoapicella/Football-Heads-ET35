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

        float radioHitbox = (ancho / 2f) - 5f; //restamos 5 para mayor precision de hitbox
        this.hitbox = new Circle(0, 0, radioHitbox); //x,y,radio

        this.circuloBotin = new Circle(0, 0, RADIO_BOTIN); //hitbox del botin
        this.direccion = (xInicial < FootballHeads.ANCHO_MUNDO / 2f) ? 1f : -1f; //hacia donde mira el botin dependiendo su x inicial
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

        if (x < (FootballHeads.ANCHO_MUNDO*((5.5f)/100f)) ) x = (FootballHeads.ANCHO_MUNDO*((5.5f)/100f));
        if (x > FootballHeads.ANCHO_MUNDO *(0.875f)) {
            x = (FootballHeads.ANCHO_MUNDO *(0.875f));
        }
        hitbox.setPosition(x + (ancho / 2f), y + (alto / 2f)); //actualiza el hitbox constantemente

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
            pateando = true;				//se divide para hallar el % de segundos transcurridos de 0.35f 
            float progreso = MathUtils.clamp(tiempoPateo / DURACION_PATEO, 0f, 1f); //valor, min, max
            actualizarPosicionBotin(progreso);
            tiempoPateo += delta;
            if (tiempoPateo >= DURACION_PATEO) {
                tiempoPateo = -1f;
                pateando = false;
                actualizarPosicionBotin(0f);
            }
        }else { //constantemente mientras no hay patada
            pateando = false;
            actualizarPosicionBotin(0f); 
        }
    }

    private void actualizarPosicionBotin(float progreso) {
        float extension = MathUtils.sin(progreso * MathUtils.PI); //curva gracias a la funcion sen en radianes (pi)

        float offsetXReposo = -2f * direccion; //posicion del pie en reposo dependiendo a donde mire
        float offsetXExtendido = 35f * direccion; //posicion del pie en su maximo dependiendo a donde mire
        float offsetX = offsetXReposo + (offsetXExtendido - offsetXReposo) * extension; //posicion del pie con respecto al jugador basandose en la funcion sen de curvas

        float offsetYReposo = 6f;
        float offsetYExtendido = alto * 0.3f;
        float offsetY = offsetYReposo + (offsetYExtendido - offsetYReposo) * extension; //lo mismo para interpolacion vertical

        circuloBotin.setPosition((x + ancho / 2f) + offsetX, y + offsetY); //actualizamos constantemente su hitbox en base a sus interpolaciones

        float anguloReposo = -15f; //para que coincida con la inclinacion del cuerpo
        float anguloExtendido = 60f; //angulo cuando el pie se levanta
        this.anguloBotin = (anguloReposo + (anguloExtendido - anguloReposo) * extension) * direccion; //
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(texturaNeutro, x, y, ancho, alto);
        float diametro = RADIO_BOTIN * 2f;
        batch.draw( //dibuja el botin
                texturaBotin,
                circuloBotin.x - RADIO_BOTIN, //pos x abajo a la izquierda  
                circuloBotin.y - RADIO_BOTIN,
                RADIO_BOTIN, RADIO_BOTIN, //rotara en base a su posicion en X e Y seteadas como el centro mediante su radio
                diametro, diametro, //su ancho y alto de la imagen
                1f, 1f, //escala X e Y indicando que no se agradan ni achican
                anguloBotin, //angulo de la imagen para que coincida con jugador
                0, 0, //la imagen empezara de abajo a la izquierda
                texturaBotin.getWidth(), //devuelve el ancho real de la imagen para que se use todo
                texturaBotin.getHeight(),
                direccion < 0, //devuelve true/false para cargar hacia donde mirara
                false //no lo invierte en espejo con Y
            );
    }

    public void resolverColision(Jugador otro) {
        if (!hitbox.overlaps(otro.hitbox)) return;

        float dx = hitbox.x - otro.hitbox.x; //calcula su distancia horizontal con respecto al centro 
        float dy = hitbox.y - otro.hitbox.y; //calcula su distancia vertical con respecto al centro
        float distancia = (float) Math.sqrt(dx * dx + dy * dy); //calculamos hipotenusa
        float distanciaMinima = hitbox.radius + otro.hitbox.radius; //la distancia en la que se tocan pero no se superponen
        float superposicion = distanciaMinima - distancia; //cuanta distancia se superponen

        if (superposicion > 0 && distancia > 0) { //para no dividir por cero 
            float empujeX = (dx / distancia) * (superposicion / 2f); //calculo que indica cuanto deben separarse en x ambos
            float empujeY = (dy / distancia) * (superposicion / 2f);

            this.x += empujeX; //movemos a ambos respectivamente
            this.y += empujeY;
            otro.x -= empujeX;
            otro.y -= empujeY;

            if (empujeY > 0 && this.y > otro.y) { //si uno esta arriba del otro
                this.velocidadY = 0; //que no atraviese en Y
                this.enElSuelo = true; //permite que considere que esta apoyado
            } else if (empujeY < 0 && otro.y > this.y) { //vista desde el otro jugador
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