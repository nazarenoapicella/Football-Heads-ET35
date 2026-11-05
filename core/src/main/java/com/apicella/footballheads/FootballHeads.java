package com.apicella.footballheads;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FootballHeads extends ApplicationAdapter {
    public static final float ANCHO_MUNDO = 800f;
    public static final float ALTO_MUNDO = 480f;

    private static final String NOMBRE_JUGADOR_1 = "JUGADOR 1";
    private static final String NOMBRE_JUGADOR_2 = "JUGADOR 2";
    private static final String[] OPCIONES_PAUSA = {"Continuar", "Reiniciar", "Finalizar"};
    private enum Estado { JUGANDO, PAUSA, FINALIZADO }

    private SpriteBatch spriteBatch;
    private OrthographicCamera camara;
    private Viewport viewport;
    private Texture fondoCancha;
    private Texture texturaPixel;
    private Texture texturaBotinCompartida;
    private BitmapFont fuente;

    private Partido partido;
    private Jugador jugador1;
    private Jugador jugador2;
    private Pelota pelota;
    private ControlesFlechas controlesJugador1;
    private ControlesWASD controlesJugador2;

    private Estado estado = Estado.JUGANDO;
    private int opcionSeleccionada;
    private final Rectangle[] botonesMenuPausa = new Rectangle[OPCIONES_PAUSA.length];
    private final Rectangle botonVolverAJugar = new Rectangle(ANCHO_MUNDO / 2f - 150f, 110f, 300f, 44f);

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camara);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        fondoCancha = new Texture(Gdx.files.internal("MapaReferencia.jpeg"));
        texturaBotinCompartida = new Texture(Gdx.files.internal("botin.png"));
        texturaPixel = crearTexturaPixel();
        fuente = new BitmapFont();

        jugador1 = new Jugador(ANCHO_MUNDO * 0.8f, Partido.SUELO_Y, new Texture(Gdx.files.internal("nazaNeutro.png")), texturaBotinCompartida);
        jugador2 = new Jugador(ANCHO_MUNDO * 0.2f - jugador1.getAncho(), Partido.SUELO_Y, new Texture(Gdx.files.internal("mirkoNeutro.png")), texturaBotinCompartida);
        pelota = new Pelota((ANCHO_MUNDO - 25f) / 2f, Partido.SUELO_Y + 250f, new Texture(Gdx.files.internal("pelota.png")));

        partido = new Partido(jugador1, jugador2, pelota);
        controlesJugador1 = new ControlesFlechas(jugador1);
        controlesJugador2 = new ControlesWASD(jugador2);

        for (int indice = 0; indice < botonesMenuPausa.length; indice++) {
            botonesMenuPausa[indice] = new Rectangle(ANCHO_MUNDO / 2f - 150f, 290f - indice * 60f, 300f, 44f);
        }
    }

    private Texture crearTexturaPixel() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture textura = new Texture(pixmap);
        pixmap.dispose();
        return textura;
    }

    @Override
    public void render() {
        actualizar(Gdx.graphics.getDeltaTime());
        dibujar();
    }

    private void actualizar(float delta) {
        switch (estado) {
            case JUGANDO:
                actualizarPartido(delta);
                break;
            case PAUSA:
                actualizarMenuPausa();
                break;
            case FINALIZADO:
                actualizarPantallaFinal();
                break;
        }
    }

    private void actualizarPartido(float delta) {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            estado = Estado.PAUSA;
            opcionSeleccionada = 0;
            limpiarControles();
            return;
        }

        controlesJugador1.actualizar();
        controlesJugador2.actualizar();

        if (!partido.seAcaboElTiempo()) {
            partido.actualizar(delta);
        }
        
        if (partido.seAcaboElTiempo()) {
            estado = Estado.FINALIZADO;
        }
    }

    private void actualizarMenuPausa() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            reanudarPartido();
            return;
        }

        int cantidadOpciones = OPCIONES_PAUSA.length;
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            opcionSeleccionada = MathUtils.clamp(opcionSeleccionada - 1, 0, cantidadOpciones - 1);
        }
        if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            opcionSeleccionada = MathUtils.clamp(opcionSeleccionada + 1, 0, cantidadOpciones - 1);
        }

        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            ejecutarOpcionPausa();
        }
    }

    private void ejecutarOpcionPausa() {
        switch (opcionSeleccionada) {
            case 0:
                reanudarPartido();
                break;
            case 1:
                reiniciarPartido();
                break;
            case 2:
                estado = Estado.FINALIZADO;
                break;
        }
    }

    private void actualizarPantallaFinal() {
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            reiniciarPartido();
        }
    }

    private void limpiarControles() {
        controlesJugador1.limpiar();
        controlesJugador2.limpiar();
    }

    private void reanudarPartido() {
        estado = Estado.JUGANDO;
        limpiarControles();
    }

    private void reiniciarPartido() {
        partido.reiniciarPartido();
        estado = Estado.JUGANDO;
    }

    private void dibujar() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        camara.update();
        spriteBatch.setProjectionMatrix(camara.combined);
        spriteBatch.begin();

        if (estado == Estado.FINALIZADO) {
            dibujarPantallaFinal();
        } else {
            dibujarPartido();
            if (estado == Estado.PAUSA) {
                dibujarMenuPausa();
            }
        }

        spriteBatch.end();
    }

    private void dibujarPartido() {
        spriteBatch.draw(fondoCancha, 0f, 0f, ANCHO_MUNDO, ALTO_MUNDO);
        jugador1.dibujar(spriteBatch);
        jugador2.dibujar(spriteBatch);
        pelota.dibujar(spriteBatch);

        String marcador = partido.getGolesJugador2() + " - " + partido.getGolesJugador1();
        dibujarTexto(marcador, 3f, Color.WHITE, ANCHO_MUNDO / 2f, ALTO_MUNDO - 36f);
        String viento = formatearViento(partido.getVientoParaMostrar());
        dibujarTexto(viento, 1.4f, Color.WHITE, 70f, ALTO_MUNDO - 27f);
        dibujarTexto("ESC = pausa", 1.4f, Color.WHITE, ANCHO_MUNDO - 70f, ALTO_MUNDO - 27f);

        float tiempoRestante = partido.getTiempoRestante();
        Color colorTiempo = tiempoRestante <= 10f ? Color.RED : Color.WHITE;
        dibujarTexto(formatearTiempo(tiempoRestante), 2f, colorTiempo, ANCHO_MUNDO / 2f, ALTO_MUNDO - 75f);
    }

    private void dibujarMenuPausa() {
        spriteBatch.setColor(0f, 0f, 0f, 0.65f);
        spriteBatch.draw(texturaPixel, 0f, 0f, ANCHO_MUNDO, ALTO_MUNDO);
        spriteBatch.setColor(Color.WHITE);

        dibujarTexto("PAUSA", 4f, Color.WHITE, ANCHO_MUNDO / 2f, 410f);
        for (int indice = 0; indice < OPCIONES_PAUSA.length; indice++) {
            dibujarBoton(OPCIONES_PAUSA[indice], botonesMenuPausa[indice], indice == opcionSeleccionada);
        }
    }

    private void dibujarPantallaFinal() {
        int golesJugador1 = partido.getGolesJugador1();
        int golesJugador2 = partido.getGolesJugador2();

        String resultado = "EMPATE";
        if (golesJugador1 > golesJugador2) {
            resultado = "GANA " + NOMBRE_JUGADOR_1;
        } else if (golesJugador2 > golesJugador1) {
            resultado = "GANA " + NOMBRE_JUGADOR_2;
        }

        float centroX = ANCHO_MUNDO / 2f;
        dibujarTexto("FIN DEL PARTIDO", 2f, Color.LIGHT_GRAY, centroX, 430f);
        dibujarTexto(resultado, 4f, Color.YELLOW, centroX, 360f);
        dibujarTexto(golesJugador2 + " - " + golesJugador1, 3f, Color.WHITE, centroX, 266f);
        dibujarTexto(NOMBRE_JUGADOR_2 + " (izquierda)   -   " + NOMBRE_JUGADOR_1 + " (derecha)", 1.4f, Color.LIGHT_GRAY, centroX, 213f);
        dibujarBoton("VOLVER A JUGAR", botonVolverAJugar, true);
    }

    private void dibujarBoton(String texto, Rectangle boton, boolean seleccionado) {
        if (seleccionado) {
            spriteBatch.setColor(1f, 1f, 1f, 0.18f);
            spriteBatch.draw(texturaPixel, boton.x, boton.y, boton.width, boton.height);
            spriteBatch.setColor(Color.WHITE);
        }
        dibujarTexto(texto, 2f, seleccionado ? Color.YELLOW : Color.WHITE,boton.x + boton.width / 2f, boton.y + boton.height / 2f);
    }

    private void dibujarTexto(String texto, float escala, Color color, float centroX, float centroY) {
        fuente.getData().setScale(escala);
        fuente.setColor(color);
        fuente.draw(spriteBatch, texto, centroX - ANCHO_MUNDO / 2f, centroY + fuente.getCapHeight() / 2f,ANCHO_MUNDO, Align.center, false);
    }

    private String formatearTiempo(float segundosRestantes) {
        int segundosEnteros = (int) Math.ceil(segundosRestantes);
        return String.format("%02d:%02d", segundosEnteros / 60, segundosEnteros % 60);
    }

    private String formatearViento(int velocidadViento) {
        if (velocidadViento == 0) {
            return "Sin viento";
        }
        return Math.abs(velocidadViento) + " m/s " + (velocidadViento > 0 ? "->" : "<-");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        fondoCancha.dispose();
        texturaPixel.dispose();
        texturaBotinCompartida.dispose();
        partido.dispose();
        fuente.dispose();
    }
}