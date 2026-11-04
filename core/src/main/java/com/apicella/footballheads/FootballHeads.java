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
    private ControlesTeclado controlesJugador1;
    private ControlesTeclado controlesJugador2;

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

        Jugador jugador1 = new Jugador(ANCHO_MUNDO * 0.8f, Partido.SUELO_Y, new Texture(Gdx.files.internal("nazaNeutro.png")), texturaBotinCompartida);
        Jugador jugador2 = new Jugador(ANCHO_MUNDO * 0.2f - jugador1.getAncho(), Partido.SUELO_Y, new Texture(Gdx.files.internal("mirkoNeutro.png")), texturaBotinCompartida);
        Pelota pelota = new Pelota((ANCHO_MUNDO - 25f) / 2f, Partido.SUELO_Y + 250f, new Texture(Gdx.files.internal("pelota.png")));

        partido = new Partido(jugador1, jugador2, pelota);
        controlesJugador1 = ControlesTeclado.crearControlesFlechas();
        controlesJugador2 = ControlesTeclado.crearControlesWASD();

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
            partido.limpiarEntradas();
            return;
        }

        controlesJugador1.actualizarEntrada(partido.getJugador1().getEntrada());
        controlesJugador2.actualizarEntrada(partido.getJugador2().getEntrada());

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
        if (seApretoAlguna(Keys.UP, Keys.W)) {
            opcionSeleccionada = (opcionSeleccionada + cantidadOpciones - 1) % cantidadOpciones;
        }
        if (seApretoAlguna(Keys.DOWN, Keys.S)) {
            opcionSeleccionada = (opcionSeleccionada + 1) % cantidadOpciones;
        }

        if (seApretoConfirmar()) {
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
        if (seApretoConfirmar()) {
            reiniciarPartido();
        }
    }

    private void reanudarPartido() {
        estado = Estado.JUGANDO;
        partido.limpiarEntradas();
    }

    private void reiniciarPartido() {
        partido.reiniciarPartido();
        estado = Estado.JUGANDO;
    }

    private boolean seApretoAlguna(int... teclas) {
        for (int tecla : teclas) {
            if (Gdx.input.isKeyJustPressed(tecla)) {
                return true;
            }
        }
        return false;
    }

    private boolean seApretoConfirmar() {
        return seApretoAlguna(Keys.ENTER, Keys.NUMPAD_ENTER, Keys.SPACE);
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
        partido.getJugador1().dibujar(spriteBatch);
        partido.getJugador2().dibujar(spriteBatch);
        partido.getPelota().dibujar(spriteBatch);

        GameplayManager gestorPartido = partido.getGameplayManager();
        String marcador = gestorPartido.getGolesJugador2() + " - " + gestorPartido.getGolesJugador1();
        dibujarTexto(marcador, 3f, Color.WHITE, ANCHO_MUNDO / 2f, ALTO_MUNDO - 36f);
        String viento = formatearViento(gestorPartido.obtenerVientoParaMostrar());
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
        int golesJugador1 = partido.getGameplayManager().getGolesJugador1();
        int golesJugador2 = partido.getGameplayManager().getGolesJugador2();

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
        dibujarTexto(NOMBRE_JUGADOR_2 + " (izquierda)   -   " + NOMBRE_JUGADOR_1 + " (derecha)",
                1.4f, Color.LIGHT_GRAY, centroX, 213f);
        dibujarBoton("VOLVER A JUGAR", botonVolverAJugar, true);
    }

    private void dibujarBoton(String texto, Rectangle boton, boolean seleccionado) {
        if (seleccionado) {
            spriteBatch.setColor(1f, 1f, 1f, 0.18f);
            spriteBatch.draw(texturaPixel, boton.x, boton.y, boton.width, boton.height);
            spriteBatch.setColor(Color.WHITE);
        }
        dibujarTexto(texto, 2f, seleccionado ? Color.YELLOW : Color.WHITE,
                boton.x + boton.width / 2f, boton.y + boton.height / 2f);
    }

    private void dibujarTexto(String texto, float escala, Color color, float centroX, float centroY) {
        fuente.getData().setScale(escala);
        fuente.setColor(color);
        fuente.draw(spriteBatch, texto, centroX - ANCHO_MUNDO / 2f, centroY + fuente.getCapHeight() / 2f,
                ANCHO_MUNDO, Align.center, false);
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