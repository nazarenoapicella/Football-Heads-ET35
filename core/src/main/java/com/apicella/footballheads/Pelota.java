package com.apicella.footballheads;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;

public class Pelota{
	protected float x, y;
    protected float velocidadY = 0f;
    protected boolean enElSuelo = true;
    protected final float ancho = 25, alto = 25;
    protected final float velocidadX = 130f;
    protected static final float GRAVEDAD = -750f;
    protected Texture texturaActual;
    protected Texture texturaNeutro;
	public Pelota(float x, float y, float velocidadY, boolean enElSuelo, Texture texturaNeutro) {
		this.x = x;
		this.y = y;
		this.velocidadY = velocidadY;
		this.enElSuelo = enElSuelo;
		this.texturaActual = texturaNeutro;
		
        float radioHitbox = ancho;
        this.circulo = new Circle(0, 0, radioHitbox);
	}

    protected Circle circulo;

    
    public void actualizar(float delta) {
        if (x < 50) x = 50;
        if (x > FootballHeads.ANCHO_MUNDO - ancho - 50) {
            x = FootballHeads.ANCHO_MUNDO - ancho - 50;
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
    }
    

    public void dibujar(SpriteBatch batch) {
        batch.draw(texturaActual, x, y, ancho, alto);
    }
    
    public void dispose() {
        texturaNeutro.dispose();
    }
    
}
