# Football Heads - ET35

Videojuego de fútbol arcade 2D inspirado en el género **Football Heads**, desarrollado en Java con **LibGDX**.

## Integrantes

- Apicella Nazareno
- Bartero Lautaro
- Manteiga Agustin
- Mihovilcevic Mirko
- Navarro Patricio

## Descripción

El juego permite disputar partidos locales de dos jugadores en una misma computadora. Cada jugador se desplaza, salta y patea una pelota para marcar goles en el arco contrario. Cada partido dura **1 minuto** y gana quien haya hecho más goles. La cancha utiliza una vista lateral 2D e incluye gravedad, rebotes, colisiones entre jugadores y pelota, animación de pateo y viento variable.

## Funcionalidades implementadas

- Dos jugadores locales con controles independientes.
- Movimiento horizontal y salto.
- Animación de pateo y detección del contacto entre botín y pelota; la potencia de la patada depende de la distancia del contacto y su ángulo, de si la pelota estaba quieta o en movimiento.
- Choques entre jugadores y pelota, incluidos cabezazos y situaciones de pellizco (pelota atrapada entre dos jugadores).
- Gravedad, rebotes, fricción y rotación de la pelota.
- Viento variable que modifica el movimiento de la pelota y se muestra en pantalla.
- Travesaños en los arcos, con salida automática si la pelota queda apoyada.
- Detección de goles y marcador.
- Temporizador de 1 minuto con reloj en pantalla (en rojo durante los últimos 10 segundos); al terminar, el partido finaliza solo.
- Menú de pausa con opciones para continuar, reiniciar o finalizar el partido.
- Pantalla de resultado (gana el Jugador 1, gana el Jugador 2 o empate) con opción para volver a jugar.
- Ajuste de la cámara al tamaño de la ventana.

## Controles

| Acción | Jugador 1 (derecha) | Jugador 2 (izquierda) |
|---|---|---|
| Mover a la izquierda | Flecha izquierda | A |
| Mover a la derecha | Flecha derecha | D |
| Saltar | Flecha arriba | W |
| Patear | P | Tab |

- `ESC`: abrir o cerrar el menú de pausa.
- En el menú de pausa: flechas arriba/abajo o W/S para cambiar de opción; Enter o Espacio para confirmar.
- En la pantalla de resultado: Enter o Espacio para volver a jugar.

## Estructura del código

El código del juego está en `core/src/main/java/com/apicella/footballheads/`.

| Clase | Responsabilidad |
|---|---|
| `FootballHeads` | Punto de entrada de LibGDX: carga recursos, maneja los estados (jugando, pausa y final), el menú y el dibujo en pantalla. |
| `Partido` | Reglas del partido: reloj de 1 minuto, actualización de jugadores y pelota, colisiones, travesaños, goles y reinicios. |
| `Jugador` | Movimiento, salto, gravedad, patada con botín, hitboxes y colisión entre jugadores. |
| `Pelota` | Física de la pelota: viento, rebotes, rotación, patadas, cabezazos, pellizco y travesaños. |
| `GameplayManager` | Marcador y viento. |
| `ControlesFlechas` y `ControlesWASD` | Traducen las teclas de cada jugador en órdenes para su `Jugador`. |

El módulo `lwjgl3` contiene el lanzador de escritorio (`Lwjgl3Launcher`).

## Tecnologías

- **Lenguaje:** Java.
- **Framework:** LibGDX 1.14.2.
- **Plataforma de escritorio:** LWJGL3.
- **Gestión de dependencias y compilación:** Gradle.
- **Compatibilidad del código:** Java 8; el proyecto utiliza un JDK 21 para ejecutar Gradle.

## Cómo ejecutar

1. Abrí la carpeta del proyecto en Eclipse o IntelliJ como un proyecto Gradle existente.
2. Esperá a que Gradle termine de sincronizar las dependencias.
3. Ejecutá `lwjgl3/src/main/java/com/apicella/footballheads/lwjgl3/Lwjgl3Launcher.java` como aplicación Java, o ejecutá la tarea Gradle `:lwjgl3:run`.

La primera sincronización puede necesitar conexión a Internet para descargar Gradle y las dependencias.

## Estado del proyecto

La versión actual implementa un partido local de 1 minuto para dos jugadores en una computadora. El partido también se puede finalizar antes desde la opción correspondiente del menú de pausa. Consultá el historial de cambios en [CHANGELOG.md](CHANGELOG.md).

**La comunicación en red entre dos computadoras todavía no está implementada.** El próximo paso es hacerla mediante UDP, junto con una pantalla dividida que muestre una vista por jugador, como pidió el docente.