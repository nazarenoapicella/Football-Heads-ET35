# Changelog

Todos los cambios importantes de este proyecto se documentan en este archivo.
El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/).

## [Unreleased]
### Added
- Comunicación en red entre dos computadoras mediante UDP (próximo paso).
- Pantalla dividida, pedida por el docente, para mostrar una vista por jugador cuando se implemente la red.

## [0.11.0] - 2026-11-04
### Added
- Detección de pelota atrapada entre dos jugadores cercanos: se resuelve como pellizco aunque solo toque a uno de ellos.
### Changed
- Reestructuración de los controles: `ControlesFlechas` (flechas + P) y `ControlesWASD` (WASD + Tab), uno por jugador, reemplazan a `ControlesTeclado` y `EntradaJugador`. Cada `Jugador` guarda sus propias órdenes (`actualizarControles` y `limpiarControles`).
- `FootballHeads` llama a `partido.actualizar(delta)` con el tiempo de cada cuadro; se quitó el acumulador de paso fijo de 1/60 s.
- Reajuste de la jugabilidad: rebote de la pelota (0.62), velocidad desde la cual la pelota ya "se movía" al ser pateada (30), fuerza del cabezazo (430) y empuje de cuerpo (30).
- Reajuste de los arcos: travesaño más bajo (130) y profundidad del gol (18).
- Si los dos botines tocan la pelota en la misma actualización, solo se aplica la patada del Jugador 1.
- Se eliminó el salto inicial extra que recibía la pelota en el pellizco.
### Removed
- Selección con mouse en el menú de pausa y en la pantalla final (ahora se maneja solo con teclado).
- Clases `EntradaJugador` y `ControlesTeclado`.
### Commits
- [`41d33a4`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/41d33a4c8f6c159bba1e908ca3c7e707a4c41abf) mejoro la estructura del codigo

## [0.10.0] - 2026-11-03
### Added
- Temporizador de 1 minuto: el reloj se muestra en pantalla (en rojo durante los últimos 10 segundos) y, al llegar a cero, el partido termina solo y pasa a la pantalla final.
### Fixed
- Cabezazos poco confiables: el empuje de cuerpo se resolvía primero y podía anular el cabezazo. Ahora, si la pelota está sobre la cabeza, se resuelve como cabezazo.
- Pelota trabada sobre el travesaño cuando el viento la empujaba en contra: ahora, tras 0.6 s apoyada, sale hacia la cancha sin depender de su velocidad.
- Pelota "pegada" al jugador que camina hacia ella: el empuje de cuerpo suma la velocidad con la que el jugador se acerca.
### Changed
- Se eliminaron los comentarios del código de `core` (pedido del profesor); la comprensión se apoya en nombres descriptivos de clases, métodos, variables y constantes.
- Limpieza del código: números sueltos reemplazados por constantes con nombre, una única fuente (`BitmapFont`) para todos los textos y uso de utilidades de LibGDX (`Vector2.dst`, `Vector2.len`, `MathUtils.lerp`, `MathUtils.clamp`, `MathUtils.cosDeg`, `MathUtils.sinDeg`) para reducir código.
### Commits
- [`ae35dd7`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/ae35dd711ad8ee7dd81d0e07aff5f8b0bc022876) mejoras de bugs

## [0.9.0] - 2026-10-20
### Added
- Clase `Partido`: reúne jugadores, pelota, arcos, goles, viento y reglas del partido sin dibujar nada, para separar la lógica de la vista y preparar la comunicación en red.
- Entrada separada del jugador (`EntradaJugador` y `ControlesTeclado`): el jugador recibe órdenes sin saber si vienen del teclado o, más adelante, de la red.
- Menú de pausa con ESC: Continuar, Reiniciar y Finalizar, con teclado y mouse.
- Pantalla final con el resultado (gana el Jugador 1, gana el Jugador 2 o empate), el marcador y la opción "Volver a jugar".
- Simulación con paso fijo de 1/60 s, independiente de los cuadros por segundo de la computadora.
### Commits
- [`2d1e65e`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/2d1e65e2cf88e8c7932944f3020508fed85c4642) pienso en la red

## [0.8.2] - 2026-10-06
### Changed
- Mejora del ángulo y la potencia de la patada: valores distintos según la pelota esté quieta o en movimiento, y pérdida de potencia según la distancia entre el botín y la pelota.
### Commits
- [`a9483db`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/a9483dbb56cdd7455a68d77ae66d141785a4f3a6) Mejoro angulo de pateo y potencia

## [0.8.1] - 2026-09-28
### Changed
- Se agregaron comentarios explicativos en el código para facilitar su comprensión (eliminados más tarde en la versión 0.10.0).
### Commits
- [`4d92fce`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/4d92fced64398e9b74962beac657a9754104e915) comentarios para mejorar comprension
- [`9194a61`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/9194a61fbde8ed0941333b2e159b3790bd720d64) comentarios para mejorar comprension
- [`d9da25c`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/d9da25c88ce82fccd1825b4c2f1bcc9897f8ec42) mejoro comprension

## [0.8.0] - 2026-09-11
### Added
- Viento variable: cambia de dirección e intensidad cada cierto tiempo, empuja más a la pelota en el piso que en el aire y la hace girar. Se muestra en pantalla.
- Patada con ángulo: el botín tiene hitbox propia y recorre un arco durante la patada; el ángulo y la potencia con que sale la pelota dependen del contacto.
### Commits
- [`34e7914`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/34e79147c84e8d00641f679a8c4bb423d41dca47) Agrego viento y mejoro la forma de patear con angulos

## [0.7.0] - 2026-09-04
### Added
- Sistema de detección de goles y contador de puntuación.
- Colisión física en los travesaños/techos de los arcos.
### Fixed
- Corrección de bug donde la pelota quedaba estancada en la parte superior del arco.
### Commits
- [`46d26c7`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/46d26c708426ab01f6ede00593810938322b897a) Agrego lo que hicimos al CHANGELOG.md
- [`95a827e`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/95a827e2605de8a1f4f47daeea87f0ac81907434) Agregamos travesaños y fix bug que se quede varada arriba de ellos
- [`e9753fe`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/e9753fee2900dfdeca766771e2baf1727179421f) Ahora se puede meter gol

## [0.6.0] - 2026-09-01
### Added
- Mecánica para patear la pelota con impulso horizontal y vertical.
- Físicas básicas de movimiento de la pelota y detección de cabezazos.
### Commits
- [`819e8d4`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/819e8d44bc8d99bc493e0244d232c06645a4f32f) Ahora se puede patear la pelota
- [`439ce82`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/439ce82d28f24b669490ff480ae906ded103d7a4) Fisicas basicas de la pelota, se mueve y se puede dar cabezazos

## [0.5.0] - 2026-08-30
### Added
- Gravedad aplicada a la pelota y reajuste en los puntos de spawn iniciales de jugadores y pelota.
### Fixed
- Corrección de interacciones y respuesta física entre las hitboxes de los jugadores y la pelota.
### Commits
- [`f333034`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/f333034ef93e275f53976a3e8835d45f087319f2) Prueba de cuenta (por lautipapu)
- [`3e5774f`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/3e5774f96dc14c1ff1ceeee4fcc3c20a2e8fe12b) solucione problema de interaccion de hitbox pelota-Jugador (por auregomez01)
- [`9801ff8`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/9801ff89bf0a81a5ac7f038b94b711e21f6a2bae) Modifique spawn de jugadores y pelota. Agregue gravedad a la pelota (por auregomez01)

## [0.4.0] - 2026-08-28
### Added
- Entidad `Pelota` renderizada en pantalla.
- Nuevo mapa/fondo de cancha.
### Changed
- Reestructuración del código mediante herencia para la clase `Jugador` (`JugadorWASD` y `JugadorFlechas`).
### Fixed
- Mejoras en el cálculo y respuesta de colisiones entre jugadores.
### Commits
- [`52da2c8`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/52da2c8001b4da5d7a071f4d69d65af6bd96eef3) Pelota sin fisicas
- [`03224ee`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/03224eea524925659e77182be09ac59b41471d4d) Aplico herencia de Jugadores y cambio de fondo
- [`c402423`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/c4024236a592028899ab1efbc9d95f1647608db0) Mejoro colision de jugadores

## [0.3.0] - 2026-08-25
### Added
- Jugador 2 agregado con movimientos.
- Colisiones entre Jugador 1 y Jugador 2.
- Cambio de caras de jugadores por las del equipo.
### Commits
- [`6a9e71e`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/6a9e71e09166c0b838acd6c79e2f8a336f1e9182) Modifico Changelog.md con las actualizaciones y agrego las fotos de los Jugadores
- [`eaa7e56`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/eaa7e56ea12b520a3d861a62cf7c86f7058ec0c9) Cambie foto jugador 2 por una de mirko
- [`068517c`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/068517c1f68476b328356689f066ac16bd1243af) Colisiones jugadores
- [`4410a53`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/4410a537d58554ae55ab6a0e1db0abff187d0974) Agrega Jugador 2 (sin colisiones)

## [0.2.0] - 2026-08-22
### Added
- Cancha y jugador dibujados en pantalla usando SpriteBatch.
- Movimiento horizontal del jugador (flechas / WASD).
- Salto con gravedad básica.
- Colisión del jugador con el piso y los límites laterales de la cancha.
- Assets iniciales: imagen de cancha y 3 sprites del jugador (neutro, corriendo, pateando).
### Commits
- [`367d37e`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/367d37ea6a1228d198310eac46a51a840c6721f3) Feat: Movimiento y salto del jugador con cancha y sprites básicos
- [`1ff4920`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/1ff49204f83b325c465c9579df704ecac7cb1787) Docs: Creación del CHANGELOG
- [`af67d09`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/af67d092710843a2795df7409143ae7df3dbd462) Update README with project details and instructions

## [0.1.0] - 2026-08-21
### Added
- Configuración inicial del proyecto con LibGDX generado mediante gdx-liftoff.
- Estructura básica de módulos (core, lwjgl3).
- Repositorio conectado a GitHub.
### Commits
- [`231722e`](https://github.com/nazarenoapicella/Football-Heads-ET35/commit/231722e0e3951096780b946a95c87dc40583dd64) First commit