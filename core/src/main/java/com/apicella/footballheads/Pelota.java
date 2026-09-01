package com.apicella.footballheads;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

public class Pelota {
    protected float x, y;
    protected float velocidadY = 0f;
    protected float velocidadX = 0f; 
    
    protected boolean enElSuelo = true;
    protected final float ancho = 25, alto = 25;
    protected static final float GRAVEDAD = -750f;
    protected Texture texturaActual;
    protected Texture texturaNeutro;
    protected Circle circulo;

    public Pelota(float x, float y, float velocidadY, boolean enElSuelo, Texture texturaNeutro) {
        this.x = x;
        this.y = y;
        this.velocidadY = velocidadY;
        this.enElSuelo = enElSuelo;
        this.texturaActual = texturaNeutro;
        
        float radioHitbox = ancho / 2f; 
        this.circulo = new Circle(0, 0, radioHitbox);
    }

    public void actualizar(float delta) {
        if (enElSuelo) {
            velocidadX *= 0.95f; 
        } else {
            velocidadX *= 0.99f; 
            }
        if (Math.abs(velocidadX) < 10f) velocidadX = 0;

        x += velocidadX * delta;

        if (x < 50) {
            x = 50;
            velocidadX = velocidadX * -0.6f; 
        }
        if (x > FootballHeads.ANCHO_MUNDO - ancho - 50) {
            x = FootballHeads.ANCHO_MUNDO - ancho - 50;
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
        

        
        circulo.setPosition(x + ancho / 2f, y + alto / 2f);
    }

    
    public void cabezazo(Jugador j1, Jugador j2) {
        boolean chocaConJ1 = circulo.overlaps(j1.getCirculo());
        boolean chocaConJ2 = circulo.overlaps(j2.getCirculo());
        if (!enElSuelo && y>60) {
        if (chocaConJ1) {
            resolverColisionIndividual(j1, 500f);
        }
        if (chocaConJ2) {
            // Corregido: ahora le pasa j2 en lugar de j1
            resolverColisionIndividual(j2, 500f); 
        }
      }
    }
    
    // =========================================================
    // NUEVO MÉTODO: Evalúa a los dos jugadores al mismo tiempo
    // =========================================================
    public void colisionarConJugadores(Jugador j1, Jugador j2) {
        boolean chocaConJ1 = circulo.overlaps(j1.getCirculo());
        boolean chocaConJ2 = circulo.overlaps(j2.getCirculo());

        // Si la pelota está tocando a AMBOS al mismo tiempo
        if (chocaConJ1 && chocaConJ2) {
            // Efecto "Pellizco": la disparamos hacia arriba
            velocidadY = 450f; // Fuerza del salto hacia arriba
            velocidadX = 0f;   // Quitamos inercia lateral para que suba recta
            
            // La separamos un poco hacia arriba para destrabarla instantáneamente
            y += 10f; 
            circulo.setPosition(x + ancho / 2f, y + alto / 2f);
        } 
        // Si solo choca con el Jugador 1
        else if (chocaConJ1) {
            resolverColisionIndividual(j1, 100);
        } 
        // Si solo choca con el Jugador 2
        else if (chocaConJ2) {
            resolverColisionIndividual(j2, 100);
        }
    }

    // =========================================================
    // Lógica original encapsulada para choques individuales
    // =========================================================
    private void resolverColisionIndividual(Jugador jugador, float fuerzaEmpuje) {
        float direccionX = (x + ancho / 2f) - jugador.getCirculo().x;
        float direccionY = (y + alto / 2f) - jugador.getCirculo().y;
        float magnitud = (float) Math.sqrt(direccionX * direccionX + direccionY * direccionY);
        
        if (magnitud > 0) {
            direccionX /= magnitud;
            direccionY /= magnitud;
        }

        float distanciaMinima = circulo.radius + jugador.getCirculo().radius;
        float superposicion = distanciaMinima - magnitud;
        
        if (superposicion > 0) {
            x += direccionX * superposicion;
            y += direccionY * superposicion;
        }
        
        velocidadX = fuerzaEmpuje * direccionX;
        velocidadY = fuerzaEmpuje * direccionY;
        
        circulo.setPosition(x + ancho / 2f, y + alto / 2f);
    }

    public Circle getCirculo() {
        return circulo;
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(texturaActual, x, y, ancho, alto);
    }
    
    public void dispose() {
        texturaNeutro.dispose();
    }
}