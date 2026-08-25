package com.apicella.footballheads;
// El "package" es simplemente una carpeta lógica dentro de tu proyecto Java.
// Sirve para organizar el código y evitar que dos clases con el mismo nombre choquen entre sí.
// No tiene ningún efecto sobre el juego en sí: es puramente organizativo.

// ============================================================
// IMPORTS: le decimos a Java "voy a usar estas herramientas
// que ya vienen hechas dentro de la librería LibGDX".
// Cada import trae una clase (una "caja de herramientas") distinta.
// ============================================================
import com.badlogic.gdx.math.Rectangle;
//Esta clase representa un rectángulo matemático. No dibuja nada en pantalla,
//solo sirve para calcular si dos áreas se están tocando.

import com.badlogic.gdx.ApplicationAdapter;
// Es la clase "molde" que le da a tu juego el ciclo de vida básico:
// create() al empezar, render() todo el tiempo, dispose() al cerrar.
// Tu clase FootballHeads "hereda" (extends) de esta, o sea que automáticamente
// obtiene esos métodos ya organizados, solo tenés que llenarlos con tu lógica.

import com.badlogic.gdx.Gdx;
// Es una especie de "control remoto global" hacia todo lo que LibGDX puede hacer:
// Gdx.input -> saber qué teclas están apretadas
// Gdx.files -> leer archivos (imágenes, sonidos, etc.)
// Gdx.graphics -> saber cosas de la pantalla, como el tiempo entre cuadros
// Está disponible desde cualquier parte del código sin necesidad de crear un objeto nuevo.

import com.badlogic.gdx.Input.Keys;
// Es simplemente una lista de "nombres" para cada tecla del teclado
// (Keys.LEFT, Keys.SPACE, Keys.W, etc.), para no tener que acordarte códigos numéricos.

import com.badlogic.gdx.graphics.Texture;
// Representa una imagen ya cargada en la memoria de la placa de video (GPU),
// lista para ser dibujada muchas veces por segundo de forma eficiente.
// Cuando cargás un .png o .jpg, se convierte internamente en un objeto Texture.

import com.badlogic.gdx.graphics.OrthographicCamera;
// Una cámara 2D. "Ortográfica" significa que no tiene perspectiva (no hace que las cosas
// lejanas se vean más chicas, como pasaría en 3D). Es lo normal para juegos en 2D.
// Básicamente define "qué parte del mundo se está mostrando en pantalla".

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// El "pincel" que efectivamente dibuja las imágenes en pantalla.
// Se llama "Batch" (lote) porque junta muchas órdenes de dibujo y las manda
// todas juntas a la placa de video de una sola vez, en lugar de una por una,
// lo cual es muchísimo más rápido.

import com.badlogic.gdx.utils.ScreenUtils;
// Trae funciones simples de utilidad, como limpiar la pantalla (borrar el cuadro anterior)
// antes de dibujar el nuevo.

import com.badlogic.gdx.utils.viewport.Viewport;
// Define las REGLAS de cómo se adapta tu mundo virtual (800x480) al tamaño real
// de la ventana del usuario, que puede variar (celular, monitor, ventana redimensionada).

import com.badlogic.gdx.utils.viewport.FitViewport;
// Es un TIPO concreto de Viewport. "Fit" (ajustar) significa que mantiene siempre
// la proporción 800:480 sin deformar la imagen, agregando franjas negras (letterboxing)
// a los costados o arriba/abajo si la ventana tiene una proporción distinta.

// ============================================================
// DECLARACIÓN DE LA CLASE
// ============================================================
public class FootballHeads extends ApplicationAdapter {
    // "extends ApplicationAdapter" es la parte más importante de esta línea:
    // significa que FootballHeads HEREDA el comportamiento de ApplicationAdapter,
    // y por eso puede sobreescribir (@Override) sus métodos create(), render(), etc.
	private Rectangle rectJugador1;
    private Rectangle rectJugador2;
    // --------------------------------------------------------
    // HERRAMIENTAS DE DIBUJO (se configuran una vez en create()
    // y se usan todo el tiempo en render())
    // --------------------------------------------------------
    private SpriteBatch batch; // es un objeto agrupa todas las texturas y las renderiza mediante la GPU
    private OrthographicCamera camera; // El "ojo" que mira el mundo del juego. Define el centro de la vista y el zoom.
    private Viewport viewport; // Se encarga de recalcular automáticamente cómo se ve la cámara si la ventana cambia de tamaño (por ejemplo, si el usuario maximiza la ventana).
    
    // texture representa una imagen ya cargada en la memoria de la placa de video (GPU)
    private Texture fondoCancha;      
    private Texture jugadorNeutro1, jugadorNeutro2;    
    private Texture jugadorCorriendo1, jugadorCorriendo2; 
    private Texture jugadorPateando1, jugadorPateando2;  
    private Texture jugadorActual1, jugadorActual2; // Este es un "apuntador" (referencia) que en cada cuadro va a señalar a UNA de las tres texturas de arriba, según lo que esté haciendo el jugador.
    // Es la variable que realmente se dibuja en pantalla.

    // --------------------------------------------------------
    // TAMAÑO DEL "MUNDO VIRTUAL"
    // No son píxeles de tu monitor: son unidades inventadas por vos,
    // que después se escalan automáticamente a cualquier pantalla real.
    // --------------------------------------------------------
    private static final float ANCHO_MUNDO = 800;
    // float = número decimal (con coma), necesario porque las físicas usan decimales.
    private static final float ALTO_MUNDO = 480;

    // --------------------------------------------------------
    // DATOS DEL JUGADOR (cambian constantemente durante el juego)
    // --------------------------------------------------------
    private float jugadorX1, jugadorY1, jugadorX2, jugadorY2;
    // Posición actual del jugador en el mundo. X = horizontal, Y = vertical.
    // En LibGDX el eje Y crece hacia ARRIBA (al revés de como suele ser en pantallas de PC).
    private float jugadorAncho = 30, jugadorAlto = 60; // El tamaño del rectángulo que ocupa el personaje en el mundo, en esas mismas unidades.
    private float velocidadX = 130f; // Cuántas unidades del mundo se mueve por SEGUNDO al caminar (no por cuadro).
    private float velocidadY1, velocidadY2 = 0f; // La velocidad vertical actual. Positiva = subiendo. Negativa = cayendo.
    // Arranca en 0 porque al principio el jugador está quieto en el piso.

    
    
    private static final float GRAVEDAD = -750f;
    // Aceleración constante que "tira" al jugador hacia abajo cada cuadro.
    // Es negativa porque resta velocidad vertical con el tiempo (lo empuja hacia abajo).

    private static final float FUERZA_SALTO = 270f;
    // Velocidad vertical que se le asigna al jugador de golpe, en el instante en que salta.

    private boolean enElSuelo1, enElSuelo2 = true;
    // Un "interruptor" true/false (booleano) que evita saltos infinitos en el aire:
    // solo se puede saltar si enElSuelo es true.

    private float sueloY = 55;
    // La altura (posición Y) que representa "el piso" de la cancha, donde el jugador
    // choca y deja de caer.

    // ============================================================
    // create(): se ejecuta UNA SOLA VEZ al arrancar el juego.
    // Acá se prepara todo lo que se necesita antes de empezar a jugar.
    // ============================================================
    @Override
    public void create() {
        batch = new SpriteBatch();
        // Se crea el render de dibujo. Recién ahora existe como objeto usable.

        rectJugador1 = new Rectangle(jugadorX1, jugadorY1, jugadorAncho, jugadorAlto);
        rectJugador2 = new Rectangle(jugadorX2, jugadorY2, jugadorAncho, jugadorAlto);
        
        camera = new OrthographicCamera();
        // Se crea la cámara, todavía sin configurar del todo.

        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camera);
        // Se crea el viewport, indicándole: "el mundo mide 800x480, y controlá a ESTA cámara".
        // A partir de acá, el viewport ajusta automáticamente cómo se ve ese mundo
        // en la ventana real, sin importar su tamaño.

        camera.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        // Centra la cámara exactamente en la mitad del mundo (400, 240).
        // El tercer valor (0) es la coordenada Z, que en 2D casi no se usa, pero es obligatoria.

        fondoCancha = new Texture(Gdx.files.internal("Mapa_referencia.jpg"));
        jugadorNeutro1 = new Texture(Gdx.files.internal("jugador_neutro1.png"));
        jugadorCorriendo1 = new Texture(Gdx.files.internal("jugador_corriendo1.png"));
        jugadorPateando1 = new Texture(Gdx.files.internal("jugador_pateando1.png"));
        // Estas 4 líneas cargan los archivos de imagen desde la carpeta "assets" del proyecto
        // hacia la memoria de la placa de video. "Gdx.files.internal(...)" busca el archivo
        // dentro de esa carpeta assets por su nombre exacto (mayúsculas/minúsculas incluidas).

        jugadorActual1 = jugadorNeutro1;
        // Al arrancar, el jugador está quieto, así que el "apuntador" señala a esa imagen.

        jugadorX1 = (ANCHO_MUNDO / 1.25f)- (jugadorAncho / 2f);
        // Ubica al jugador centrado horizontalmente. Se le resta la mitad de su propio ancho
        // porque las coordenadas representan la esquina inferior izquierda del personaje,
        // no su centro — si no restaras eso, quedaría corrido hacia la derecha del centro real.

        jugadorY1 = sueloY;
        // Lo apoya justo sobre el nivel del piso definido antes.
        
        fondoCancha = new Texture(Gdx.files.internal("Mapa_referencia.jpg"));
        jugadorNeutro2 = new Texture(Gdx.files.internal("jugador_neutro2.png"));
        jugadorCorriendo2 = new Texture(Gdx.files.internal("jugador_corriendo2.png"));
        jugadorPateando2 = new Texture(Gdx.files.internal("jugador_pateando2.png"));
        
        jugadorActual2 = jugadorNeutro2;
        // Al arrancar, el jugador está quieto, así que el "apuntador" señala a esa imagen.

        jugadorX2 = (ANCHO_MUNDO / 2.75f)- (jugadorAncho / 2f);
        // Ubica al jugador centrado horizontalmente. Se le resta la mitad de su propio ancho
        // porque las coordenadas representan la esquina inferior izquierda del personaje,
        // no su centro — si no restaras eso, quedaría corrido hacia la derecha del centro real.

        jugadorY2 = sueloY;
    }

    // ============================================================
    // render(): se ejecuta en bucle, muchísimas veces por segundo.
    // Es el corazón del juego: acá "vive" todo lo que pasa en cada instante.
    // ============================================================
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        // Pregunta "¿cuántos segundos pasaron desde el cuadro anterior?"
        // Normalmente es un número chiquito, como 0.016 (equivalente a 60 cuadros por segundo).
        // Se usa para que la velocidad del juego sea la misma sin importar
        // qué tan potente sea la computadora que lo corre.

        actualizar(delta);
        // Primero se calcula TODA la lógica: dónde está el jugador ahora, si saltó, etc.

        dibujar();
        // Recién después de saber dónde está todo, se dibuja el resultado en pantalla.
        // Separar "actualizar" de "dibujar" es una práctica común y prolija en juegos.
    }

    // ============================================================
    // actualizar(): la lógica del juego. No dibuja nada, solo calcula
    // posiciones, lee el teclado y aplica física.
    // ============================================================
    private void actualizar(float delta) {
        jugadorActual1 = jugadorNeutro1;
        jugadorActual2 = jugadorNeutro2;
        // Cada cuadro arranca asumiendo que el jugador está quieto.
        // Si más abajo detecta que se está moviendo o pateando, esta variable
        // se sobreescribe. Esto evita que el personaje se quede "trabado"
        // en la pose de correr después de soltar la tecla.
        rectJugador1.setPosition(jugadorX1, jugadorY1);
        rectJugador2.setPosition(jugadorX2, jugadorY2);

        // 2. DETECTAR EL CHOQUE
        // .overlaps() devuelve "true" si un rectángulo está pisando al otro en cualquier eje
        if (rectJugador1.overlaps(rectJugador2)) {
            
            // 3. RESOLVER EL CHOQUE (Física de empuje)
            // Calculamos el centro exacto entre los dos jugadores
            float centroChoque = (jugadorX1 + jugadorX2) / 2f;
            
            // Evaluamos quién está a la izquierda y quién a la derecha
            if (jugadorX1 < jugadorX2) {
                // Si el Jugador 1 viene por la izquierda, lo empujamos hacia la izquierda del centro.
                // Y al Jugador 2 lo empujamos hacia la derecha del centro.
                // Los separamos por la mitad de su ancho a cada uno para que queden "pegados" pero sin atravesarse.
                jugadorX1 = centroChoque - (jugadorAncho / 2f);
                jugadorX2 = centroChoque + (jugadorAncho / 2f);
            } else {
                // Si están al revés (Jugador 1 por la derecha y Jugador 2 por la izquierda), 
                // hacemos el empuje invertido.
                jugadorX1 = centroChoque + (jugadorAncho / 2f);
                jugadorX2 = centroChoque - (jugadorAncho / 2f);
            }
            
            // Actualizamos las cajas una vez más por las dudas, ya que los acabamos de empujar
            rectJugador1.setPosition(jugadorX1, jugadorY1);
            rectJugador2.setPosition(jugadorX2, jugadorY2);
        }
        // --- Movimiento hacia la izquierda ---
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            jugadorX1 -= velocidadX * delta;
            // Resta a la posición X. Multiplicar por delta convierte
            // "130 unidades por segundo" en "la fracción correspondiente a este cuadro".
            jugadorActual1 = jugadorCorriendo1;
            // Cambia la imagen mostrada a la pose de correr.
        }
        
        if (Gdx.input.isKeyPressed(Keys.A)) {
            jugadorX2 -= velocidadX * delta;
            // Resta a la posición X. Multiplicar por delta convierte
            // "130 unidades por segundo" en "la fracción correspondiente a este cuadro".
            jugadorActual2 = jugadorCorriendo2;
            // Cambia la imagen mostrada a la pose de correr.
        }
        
        // --- Movimiento hacia la derecha ---
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            jugadorX1 += velocidadX * delta;
            jugadorActual1 = jugadorCorriendo1;
        }
        
        if (Gdx.input.isKeyPressed(Keys.D)) {
            jugadorX2 += velocidadX * delta;
            jugadorActual2 = jugadorCorriendo2;
        }

        // --- Patada ---
        if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
            jugadorActual2 = jugadorPateando2;
            // Cambia la imagen a la pose de patada SOLO en el cuadro en que se apretó espacio.
            // Nota: como esto pasa después del "reset" a jugadorNeutro y de los movimientos,
            // la patada tiene prioridad visual sobre correr, en ese cuadro puntual.
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.P)) {
            jugadorActual1 = jugadorPateando1;
        }
     // --- Salto Jugador 1 (Flecha Arriba) ---
        if ((Gdx.input.isKeyPressed(Keys.UP)) && enElSuelo1) {
            velocidadY1 = FUERZA_SALTO;
            enElSuelo1 = false;
        }

        // --- Salto Jugador 2 (Tecla W) ---
        if ((Gdx.input.isKeyPressed(Keys.W)) && enElSuelo2) {
            velocidadY2 = FUERZA_SALTO;
            enElSuelo2 = false;
        }

        // --- Física: Gravedad independiente para cada jugador ---
        velocidadY1 += GRAVEDAD * delta;
        velocidadY2 += GRAVEDAD * delta;

        jugadorY1 += velocidadY1 * delta;
        jugadorY2 += velocidadY2 * delta;

        // --- Colisión con el piso Jugador 1 ---
        if (jugadorY1 <= sueloY) {
            jugadorY1 = sueloY;
            velocidadY1 = 0;
            enElSuelo1 = true;
        }

        // --- Colisión con el piso Jugador 2 ---
        if (jugadorY2 <= sueloY) {
            jugadorY2 = sueloY;
            velocidadY2 = 0;
            enElSuelo2 = true;
        }

        // --- Colisión con los bordes de la pantalla ---
        if (jugadorX1 < 50) jugadorX1 = 50;
        if (jugadorX1 > ANCHO_MUNDO - jugadorAncho - 50) jugadorX1 = ANCHO_MUNDO - jugadorAncho - 50;
        if (jugadorX2 < 50) jugadorX2 = 50;
        if (jugadorX2 > ANCHO_MUNDO - jugadorAncho - 50) jugadorX2 = ANCHO_MUNDO - jugadorAncho - 50;
        
        if(jugadorX1 == jugadorX2) {
        	velocidadX = 0f;
        } 
    }

    // ============================================================
    // dibujar(): solo se ocupa de MOSTRAR en pantalla lo que ya se calculó
    // en actualizar(). No cambia posiciones ni lee el teclado.
    // ============================================================
    private void dibujar() {
        ScreenUtils.clear(0, 0, 0, 1);
        // Pinta toda la pantalla de negro antes de dibujar el nuevo cuadro.
        // Los 4 números son colores en formato RGBA (Rojo, Verde, Azul, Transparencia),
        // en escala de 0 a 1. (0,0,0,1) = negro totalmente opaco.
        // Es necesario porque, si no se "limpia" la pantalla, quedarían restos
        // del cuadro anterior dibujados debajo del nuevo (un efecto de manchones).

        camera.update();
        // Recalcula internamente las matrices matemáticas de la cámara
        // (posición, zoom, etc.) para que estén al día antes de dibujar.

        batch.setProjectionMatrix(camera.combined);
        // Le dice al SpriteBatch "dibujá todo desde el punto de vista de ESTA cámara",
        // para que las coordenadas del mundo (800x480) se traduzcan bien a la pantalla real.

        batch.begin();
        // Abre un "bloque de dibujo". A partir de acá, todo lo que se pida dibujar
        // se acumula y se procesa junto, hasta que se llame a batch.end().

        batch.draw(fondoCancha, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        // Dibuja el fondo empezando en la esquina (0,0) y estirándolo para que ocupe
        // exactamente todo el mundo (800 de ancho por 480 de alto).

        batch.draw(jugadorActual1, jugadorX1, jugadorY1, jugadorAncho, jugadorAlto);
        batch.draw(jugadorActual2, jugadorX2, jugadorY2, jugadorAncho, jugadorAlto);

        // Dibuja la textura que corresponda (quieto, corriendo o pateando) en la posición
        // actual del jugador, con su tamaño definido (30x60).
        // Como se dibuja DESPUÉS del fondo, queda "por encima" de él visualmente.

        batch.end();
        // Cierra el bloque de dibujo: acá es cuando realmente se le mandan
        // todas las órdenes juntas a la placa de video, de forma eficiente.
    }

    // ============================================================
    // resize(): se llama automáticamente cada vez que la ventana
    // cambia de tamaño (por ejemplo, si el usuario la agranda).
    // ============================================================
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        // Le pasa al viewport el nuevo tamaño real de la ventana en píxeles,
        // para que recalcule cómo encajar el mundo de 800x480 ahí adentro
        // sin deformarlo (agregando franjas negras si hace falta).
    }

    // ============================================================
    // dispose(): se llama UNA SOLA VEZ, al cerrar el juego.
    // Se usa para liberar manualmente la memoria de la placa de video.
    // ============================================================
    @Override
    public void dispose() {
        // Java limpia solo la memoria RAM común (con el "garbage collector"),
        // pero las Texture viven en la memoria de la GPU (VRAM), que Java NO
        // administra automáticamente. Si no se liberan a mano, se produce
        // una "fuga de memoria" (memory leak): la memoria gráfica ocupada
        // no se libera nunca, ni siquiera cuando cerrás el juego del todo
        // en algunos sistemas, hasta reiniciar.
        batch.dispose();
        fondoCancha.dispose();
        jugadorNeutro1.dispose();
        jugadorCorriendo1.dispose();
        jugadorPateando1.dispose();
        // Cada .dispose() libera específicamente la memoria que ese objeto ocupaba en la GPU.
    }
}