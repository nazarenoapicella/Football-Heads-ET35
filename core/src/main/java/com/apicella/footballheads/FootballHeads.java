package com.apicella.footballheads;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FootballHeads extends ApplicationAdapter {
    public static final float ANCHO_MUNDO = 800;
    public static final float ALTO_MUNDO = 480;

    private enum Estado { JUGANDO, PAUSA, FINALIZADO }

    private static final float PASO_FIJO = 1f / 60f; //la simulacion siempre avanza de a 1/60 s
    private static final float DELTA_MAXIMO = 0.25f; //si un frame tarda mas que esto, no "recuperamos" de mas

    private static final String NOMBRE_J1 = "JUGADOR 1"; //flechas, a la derecha
    private static final String NOMBRE_J2 = "JUGADOR 2"; //WASD, a la izquierda
    private static final String[] OPCIONES_PAUSA = { "Continuar", "Reiniciar", "Finalizar" };

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture fondoCancha;
    private Texture texturaPixel; //1x1 blanco, sirve para oscurecer la pantalla y resaltar botones
    private Texture texturaBotinCompartida;
    private BitmapFont marcadorDeGoles;
    private BitmapFont marcadorViento;
    private BitmapFont fuenteTitulo;
    private BitmapFont fuenteMenu;
    private GlyphLayout layout;

    private Partido partido;
    private ControlesTeclado controlesJ1;
    private ControlesTeclado controlesJ2;

    private Estado estado = Estado.JUGANDO;
    private float acumulador = 0f;
    private int opcionPausa = 0;
    private final Rectangle[] botonesPausa = new Rectangle[OPCIONES_PAUSA.length];
    private final Rectangle[] botonesFinal = new Rectangle[1];
    private final Vector2 punto = new Vector2();

    @Override
    public void create() {
        batch = new SpriteBatch(); //es el que dibuja todo
        camera = new OrthographicCamera(); //es la camara 2d
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera); //ajusta el mundo ficticio a la ventana real
        camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        fondoCancha = new Texture(Gdx.files.internal("MapaReferencia.jpeg"));

        marcadorDeGoles = new BitmapFont();
        marcadorDeGoles.getData().setScale(3f);
        marcadorViento = new BitmapFont();
        marcadorViento.getData().setScale(1.4f);
        fuenteTitulo = new BitmapFont();
        fuenteTitulo.getData().setScale(4f);
        fuenteMenu = new BitmapFont();
        fuenteMenu.getData().setScale(2f);
        layout = new GlyphLayout();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texturaPixel = new Texture(pixmap);
        pixmap.dispose();

        texturaBotinCompartida = new Texture(Gdx.files.internal("botin.png"));

        Jugador jugador1 = new Jugador(
            (ANCHO_MUNDO - (20 * ANCHO_MUNDO) / 100), Partido.SUELO_Y,
            new Texture(Gdx.files.internal("nazaNeutro.png")),
            texturaBotinCompartida
        ); //posicionX, posicionY, textura, botin
        Jugador jugador2 = new Jugador(
            (ANCHO_MUNDO - (80 * ANCHO_MUNDO) / 100 - jugador1.getAncho()), Partido.SUELO_Y,
            new Texture(Gdx.files.internal("mirkoNeutro.png")),
            texturaBotinCompartida
        );
        Pelota pelota = new Pelota(
            ((ANCHO_MUNDO - 25) / 2f), Partido.SUELO_Y + 250, 0, false,
            new Texture(Gdx.files.internal("pelota.png"))
        ); // x, y, velocidadY, enElSuelo, textura

        partido = new Partido(jugador1, jugador2, pelota);
        controlesJ1 = ControlesTeclado.flechas();
        controlesJ2 = ControlesTeclado.wasd();

        for (int i = 0; i < botonesPausa.length; i++) {
            botonesPausa[i] = new Rectangle(ANCHO_MUNDO / 2f - 150f, 290f - i * 60f, 300f, 44f);
        }
        botonesFinal[0] = new Rectangle(ANCHO_MUNDO / 2f - 150f, 110f, 300f, 44f);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        actualizar(delta);
        dibujar();
    }

    // ------------------------------------------------------------------
    // LOGICA
    // ------------------------------------------------------------------

    private void actualizar(float delta) {
        switch (estado) {
            case JUGANDO:    actualizarJuego(delta); break;
            case PAUSA:      actualizarPausa();      break;
            case FINALIZADO: actualizarFinal();      break;
        }
    }

    private void actualizarJuego(float delta) {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            estado = Estado.PAUSA;
            opcionPausa = 0;
            partido.limpiarEntradas();
            return;
        }

        //1) leemos el teclado una vez por frame
        controlesJ1.leerEn(partido.getJugador1().getEntrada());
        controlesJ2.leerEn(partido.getJugador2().getEntrada());

        //2) simulamos con paso fijo, las veces que haga falta para "alcanzar" al tiempo real
        acumulador += Math.min(delta, DELTA_MAXIMO);
        while (acumulador >= PASO_FIJO) {
            partido.actualizar(PASO_FIJO);
            acumulador -= PASO_FIJO;
        }
    }

    private void actualizarPausa() {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            reanudar();
            return;
        }

        int cantidad = OPCIONES_PAUSA.length;
        if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)) {
            opcionPausa = (opcionPausa + cantidad - 1) % cantidad;
        }
        if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S)) {
            opcionPausa = (opcionPausa + 1) % cantidad;
        }

        if (Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0) { //el mouse se movio
            int bajoMouse = botonBajoMouse(botonesPausa);
            if (bajoMouse >= 0) opcionPausa = bajoMouse;
        }

        boolean confirmar = Gdx.input.isKeyJustPressed(Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Keys.NUMPAD_ENTER)
                || Gdx.input.isKeyJustPressed(Keys.SPACE);

        if (Gdx.input.justTouched()) {
            int clic = botonBajoMouse(botonesPausa);
            if (clic >= 0) {
                opcionPausa = clic;
                confirmar = true;
            }
        }

        if (confirmar) {
            switch (opcionPausa) {
                case 0: reanudar();         break;
                case 1: reiniciarPartido(); break;
                case 2: estado = Estado.FINALIZADO; break;
            }
        }
    }

    private void actualizarFinal() {
        boolean confirmar = Gdx.input.isKeyJustPressed(Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Keys.NUMPAD_ENTER)
                || Gdx.input.isKeyJustPressed(Keys.SPACE);

        if (Gdx.input.justTouched() && botonBajoMouse(botonesFinal) >= 0) {
            confirmar = true;
        }

        if (confirmar) reiniciarPartido(); //"volver a jugar"
    }

    private void reanudar() {
        estado = Estado.JUGANDO;
        acumulador = 0f; //para que no "salte" el tiempo que estuvo en pausa
        partido.limpiarEntradas();
    }

    private void reiniciarPartido() {
        partido.reiniciarPartido();
        acumulador = 0f;
        estado = Estado.JUGANDO;
    }

    /** Devuelve el indice del boton que esta bajo el mouse, o -1 si no hay ninguno. */
    private int botonBajoMouse(Rectangle[] botones) {
        punto.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(punto); //pasa de pixeles de ventana a coordenadas del mundo
        for (int i = 0; i < botones.length; i++) {
            if (botones[i].contains(punto)) return i;
        }
        return -1;
    }

    // ------------------------------------------------------------------
    // DIBUJO
    // ------------------------------------------------------------------

    private void dibujar() {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (estado == Estado.FINALIZADO) {
            dibujarPantallaFinal();
        } else {
            dibujarMundo();
            if (estado == Estado.PAUSA) dibujarMenuPausa();
        }

        batch.end();
    }

    private void dibujarMundo() {
        batch.draw(fondoCancha, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);

        partido.getJugador1().dibujar(batch);
        partido.getJugador2().dibujar(batch);
        partido.getPelota().dibujar(batch);

        GameplayManager gm = partido.getGameplayManager();
        marcadorDeGoles.draw(batch, gm.getGolesJ2() + " - " + gm.getGolesJ1(), (ANCHO_MUNDO / 2f) - 45, ALTO_MUNDO - 20);
        marcadorViento.draw(batch, formatearViento(gm.getWindDisplayEntero()), 20, ALTO_MUNDO - 20);
        marcadorViento.draw(batch, "ESC = pausa", ANCHO_MUNDO - 120, ALTO_MUNDO - 20);
    }

    private void dibujarMenuPausa() {
        batch.setColor(0f, 0f, 0f, 0.65f);
        batch.draw(texturaPixel, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        batch.setColor(Color.WHITE);

        dibujarTextoCentrado(fuenteTitulo, "PAUSA", ANCHO_MUNDO / 2f, 430f, Color.WHITE);
        for (int i = 0; i < OPCIONES_PAUSA.length; i++) {
            dibujarBoton(OPCIONES_PAUSA[i], botonesPausa[i], i == opcionPausa);
        }
    }

    private void dibujarPantallaFinal() {
        GameplayManager gm = partido.getGameplayManager();
        int golesJ1 = gm.getGolesJ1();
        int golesJ2 = gm.getGolesJ2();

        String resultado;
        if (golesJ1 > golesJ2) resultado = "GANA " + NOMBRE_J1;
        else if (golesJ2 > golesJ1) resultado = "GANA " + NOMBRE_J2;
        else resultado = "EMPATE";

        float centroX = ANCHO_MUNDO / 2f;
        dibujarTextoCentrado(fuenteMenu, "FIN DEL PARTIDO", centroX, 440f, Color.LIGHT_GRAY);
        dibujarTextoCentrado(fuenteTitulo, resultado, centroX, 380f, Color.YELLOW);
        dibujarTextoCentrado(marcadorDeGoles, golesJ2 + " - " + golesJ1, centroX, 280f, Color.WHITE); //mismo orden que en la cancha
        dibujarTextoCentrado(marcadorViento, NOMBRE_J2 + " (izquierda)   -   " + NOMBRE_J1 + " (derecha)", centroX, 220f, Color.LIGHT_GRAY);
        dibujarBoton("VOLVER A JUGAR", botonesFinal[0], true);
    }

    private void dibujarTextoCentrado(BitmapFont fuente, String texto, float centroX, float yArriba, Color color) {
        fuente.setColor(color);
        layout.setText(fuente, texto);
        fuente.draw(batch, layout, centroX - layout.width / 2f, yArriba);
        fuente.setColor(Color.WHITE); //para no "contagiar" el color a otros usos de la misma fuente
    }

    private void dibujarBoton(String texto, Rectangle r, boolean seleccionado) {
        if (seleccionado) {
            batch.setColor(1f, 1f, 1f, 0.18f);
            batch.draw(texturaPixel, r.x, r.y, r.width, r.height);
            batch.setColor(Color.WHITE);
        }
        fuenteMenu.setColor(seleccionado ? Color.YELLOW : Color.WHITE);
        layout.setText(fuenteMenu, texto);
        fuenteMenu.draw(batch, layout,
            r.x + (r.width - layout.width) / 2f,
            r.y + (r.height + layout.height) / 2f); //centrado dentro del boton
        fuenteMenu.setColor(Color.WHITE);
    }

    private String formatearViento(int velocidadEntera) {
        if (velocidadEntera == 0) return "Sin viento";
        String flecha = velocidadEntera > 0 ? "->" : "<-";
        return Math.abs(velocidadEntera) + " m/s " + flecha;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondoCancha.dispose();
        texturaPixel.dispose();
        partido.dispose();
        marcadorDeGoles.dispose();
        marcadorViento.dispose();
        fuenteTitulo.dispose();
        fuenteMenu.dispose();
        texturaBotinCompartida.dispose();
    }
}