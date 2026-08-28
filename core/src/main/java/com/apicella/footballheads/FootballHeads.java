package com.apicella.footballheads;
// El "package" es simplemente una carpeta lógica dentro de tu proyecto Java.
// Sirve para organizar el código y evitar que dos clases con el mismo nombre choquen entre sí.
// No tiene ningún efecto sobre el juego en sí: es puramente organizativo.

// ============================================================
// IMPORTS: le decimos a Java "voy a usar estas herramientas
// que ya vienen hechas dentro de la librería LibGDX".
// Cada import trae una clase (una "caja de herramientas") distinta.
// ============================================================
import com.badlogic.gdx.math.Circle;
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


public class FootballHeads extends ApplicationAdapter {
	private Circle circuloJugador1;
    private Circle circuloJugador2;
    private SpriteBatch batch; // es un objeto agrupa todas las texturas y las renderiza mediante la GPU
    private OrthographicCamera camera; // El "ojo" que mira el mundo del juego. Define el centro de la vista y el zoom.
    private Viewport viewport; // Se encarga de recalcular automáticamente cómo se ve la cámara si la ventana cambia de tamaño (por ejemplo, si el usuario maximiza la ventana).
    
    // texture representa una imagen ya cargada en la memoria de la placa de video (GPU)
    private Texture fondoCancha;      
    private Texture jugadorNeutro1, jugadorNeutro2;    
    private Texture jugadorPateando1, jugadorPateando2;  
    private Texture jugadorActual1, jugadorActual2; // Este es un "apuntador" (referencia) que en cada cuadro va a señalar a UNA de las tres texturas de arriba, según lo que esté haciendo el jugador.
    // Es la variable que realmente se dibuja en pantalla.

    private static final float ANCHO_MUNDO = 800;
    private static final float ALTO_MUNDO = 480;


    private float jugadorX1, jugadorY1, jugadorX2, jugadorY2;
    private float jugadorAncho = 37, jugadorAlto = 47; 
    private float velocidadX = 130f; // Cuántas unidades del mundo se mueve por SEGUNDO al caminar (no por cuadro).
    private float velocidadY1, velocidadY2 = 0f; // La velocidad vertical actual. Positiva = subiendo. Negativa = cayendo.
    // Arranca en 0 porque al principio el jugador está quieto en el piso.
    
    private static final float GRAVEDAD = -750f;
    // Aceleración constante que "tira" al jugador hacia abajo cada cuadro.
    // Es negativa porque resta velocidad vertical con el tiempo (lo empuja hacia abajo).

    private static final float FUERZA_SALTO = 275f;
    // Velocidad vertical que se le asigna al jugador de golpe, en el instante en que salta.

    private boolean enElSuelo1, enElSuelo2 = true;
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

     // El radio será la mitad del ancho del jugador (aprox 18.5f). 
     // Le restamos un poco (ej: -2) para que la colisión sea más ajustada al dibujo.
     float radioHitbox = (jugadorAncho / 2f) - 2f;
     circuloJugador1 = new Circle(0, 0, radioHitbox);
     circuloJugador2 = new Circle(0, 0, radioHitbox);
        
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
        jugadorNeutro1 = new Texture(Gdx.files.internal("nazaNeutro.png"));
        jugadorPateando1 = new Texture(Gdx.files.internal("nazaPateando.png"));
        // Estas 4 líneas cargan los archivos de imagen desde la carpeta "assets" del proyecto
        // hacia la memoria de la placa de video. "Gdx.files.internal(...)" busca el archivo
        // dentro de esa carpeta assets por su nombre exacto (mayúsculas/minúsculas incluidas).

        jugadorActual1 = jugadorNeutro1;
        jugadorX1 = (ANCHO_MUNDO / 1.25f)- (jugadorAncho / 2f);
        jugadorY1 = sueloY;
        // Lo apoya justo sobre el nivel del piso definido antes.
        
        jugadorNeutro2 = new Texture(Gdx.files.internal("mirkoNeutro.png"));
        jugadorPateando2 = new Texture(Gdx.files.internal("mirkoPateando.png"));
        jugadorActual2 = jugadorNeutro2;
        jugadorX2 = (ANCHO_MUNDO / 4.6f)- (jugadorAncho / 2f);
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

        // ==========================================
        // 1. CONTROLES DE MOVIMIENTO (Teclado)
        // ==========================================
        
        // --- Jugador 1 (Flechas y P) ---
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            jugadorX1 -= velocidadX * delta; // su posicion se disminuye a una velocidad x fotograma
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            jugadorX1 += velocidadX * delta;
        }
        if (Gdx.input.isKeyJustPressed(Keys.P)) {
            jugadorActual1 = jugadorPateando1;
        }
        if ((Gdx.input.isKeyPressed(Keys.UP)) && enElSuelo1) {
            velocidadY1 = FUERZA_SALTO;
            enElSuelo1 = false;
        }

        // --- Jugador 2 (WASD y TAB) ---
        if (Gdx.input.isKeyPressed(Keys.A)) {
            jugadorX2 -= velocidadX * delta;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            jugadorX2 += velocidadX * delta;
        }
        if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
            jugadorActual2 = jugadorPateando2;
        }
        if ((Gdx.input.isKeyPressed(Keys.W)) && enElSuelo2) {
            velocidadY2 = FUERZA_SALTO;
            enElSuelo2 = false;
        }

        // ==========================================
        // 2. FÍSICA Y GRAVEDAD
        // ==========================================
        velocidadY1 += GRAVEDAD * delta; //el jugador es constantemente atraido al suelo x fotograma
        velocidadY2 += GRAVEDAD * delta;

        jugadorY1 += velocidadY1 * delta; //su posicion en y = la actual - gravedad x fotograma
        jugadorY2 += velocidadY2 * delta;

        // Colisión con el piso Jugador 1
        if (jugadorY1 <= sueloY) {
            jugadorY1 = sueloY;
            velocidadY1 = 0;
            enElSuelo1 = true;
        }

        // Colisión con el piso Jugador 2
        if (jugadorY2 <= sueloY) {
            jugadorY2 = sueloY;
            velocidadY2 = 0;
            enElSuelo2 = true;
        }

     // ==========================================
     // 3. COLISIÓN ENTRE JUGADORES (Forma Circular)
     // ==========================================
     // Centramos el círculo exactamente en el medio de la imagen del jugador
     circuloJugador1.setPosition(jugadorX1 + (jugadorAncho / 2f), jugadorY1 + (jugadorAlto / 2f));
     circuloJugador2.setPosition(jugadorX2 + (jugadorAncho / 2f), jugadorY2 + (jugadorAlto / 2f));

     if (circuloJugador1.overlaps(circuloJugador2)) {
         // Calculamos la distancia entre los centros
         float distanciaX = circuloJugador1.x - circuloJugador2.x;
         float distanciaY = circuloJugador1.y - circuloJugador2.y;
         
         // Teorema de Pitágoras para la distancia real en diagonal
         float distanciaReal = (float) Math.sqrt((distanciaX * distanciaX) + (distanciaY * distanciaY));
         
         // Distancia mínima que debería haber para que no se toquen (la suma de sus radios)
         float distanciaMinima = circuloJugador1.radius + circuloJugador2.radius;
         
         // Cuántos píxeles se están superponiendo
         float superposicion = distanciaMinima - distanciaReal;
         
         if (superposicion > 0 && distanciaReal > 0) {
             // Calculamos la dirección del empuje (normalizamos el vector)
             float empujeX = (distanciaX / distanciaReal) * (superposicion / 2f);
             float empujeY = (distanciaY / distanciaReal) * (superposicion / 2f);
             
             // Separamos a ambos jugadores proporcionalmente en esa dirección
             jugadorX1 += empujeX;
             jugadorY1 += empujeY;
             
             jugadorX2 -= empujeX;
             jugadorY2 -= empujeY;
             
             // Si el empuje vertical hacia arriba es fuerte, frenamos la caída
             if (empujeY > 0 && jugadorY1 > jugadorY2) {
                 velocidadY1 = 0;
                 enElSuelo1 = true;
             } else if (empujeY < 0 && jugadorY2 > jugadorY1) {
                 velocidadY2 = 0;
                 enElSuelo2 = true;
             }
         }
     }
                
                // Actualizamos las cajas de nuevo por si los movimos
                circuloJugador1.setPosition(jugadorX1, jugadorY1);
                circuloJugador2.setPosition(jugadorX2, jugadorY2);
            
        // ==========================================
        // 4. COLISIÓN CON LOS BORDES DE LA PANTALLA
        // ==========================================
        if (jugadorX1 < 50) jugadorX1 = 50;
        if (jugadorX1 > ANCHO_MUNDO - jugadorAncho - 50) jugadorX1 = ANCHO_MUNDO - jugadorAncho - 50;
        if (jugadorX2 < 50) jugadorX2 = 50;
        if (jugadorX2 > ANCHO_MUNDO - jugadorAncho - 50) jugadorX2 = ANCHO_MUNDO - jugadorAncho - 50;
    }

    // ============================================================
    // dibujar(): solo se ocupa de MOSTRAR en pantalla lo que ya se calculó
    // en actualizar(). No cambia posiciones ni lee el teclado.
    // ============================================================
    private void dibujar() {
        ScreenUtils.clear(0, 0, 0, 1);
        // Pinta toda la pantalla de negro antes de dibujar el nuevo cuadro.
        
        camera.update();
        // Recalcula internamente las matrices matemáticas de la cámara
        // (posición, zoom, etc.) para que estén al día antes de dibujar.

        batch.setProjectionMatrix(camera.combined);
        // Le dice al SpriteBatch "dibujá todo desde el punto de vista de ESTA cámara",
        // para que las coordenadas del mundo (800x480) se traduzcan bien a la pantalla real.

        batch.begin();
        // Abre un "bloque de dibujo". A partir de acá, todo lo que se pida dibujar

        batch.draw(fondoCancha, 0, 0, ANCHO_MUNDO, ALTO_MUNDO);
        // Dibuja el fondo empezando en la esquina (0,0) y estirándolo para que ocupe todo el mundo

        batch.draw(jugadorActual1, jugadorX1, jugadorY1, jugadorAncho, jugadorAlto);
        batch.draw(jugadorActual2, jugadorX2, jugadorY2, jugadorAncho, jugadorAlto);

        batch.end();
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
        jugadorPateando1.dispose();
        // Cada .dispose() libera específicamente la memoria que ese objeto ocupaba en la GPU.
    }
}