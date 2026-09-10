# Changelog

Todos los cambios importantes de este proyecto se documentan en este archivo.
El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/).

## [Unreleased]
### Added
- Comunicación en red entre dos computadoras (próximo paso).

## [0.7.0] - 2026-09-04
### Added
- Sistema de detección de goles y contador de puntuación.
- Colisión física en los travesaños/techos de los arcos.
### Fixed
- Corrección de bug donde la pelota quedaba estancada en la parte superior del arco.

## [0.6.0] - 2026-09-01
### Added
- Mecánica para patear la pelota con impulso horizontal y vertical.
- Físicas básicas de movimiento de la pelota y detección de cabezazos.

## [0.5.0] - 2026-08-30
### Added
- Gravedad aplicada a la pelota y reajuste en los puntos de spawn iniciales de jugadores y pelota.
### Fixed
- Corrección de interacciones y respuesta física entre las hitboxes de los jugadores y la pelota.

## [0.4.0] - 2026-08-28
### Added
- Entidad `Pelota` renderizada en pantalla.
- Nuevo mapa/fondo de cancha.
### Changed
- Reestructuración del código mediante herencia para la clase `Jugador` (`JugadorWASD` y `JugadorFlechas`).
### Fixed
- Mejoras en el cálculo y respuesta de colisiones entre jugadores.

## [0.3.0] - 2026-08-25
### Added
- Jugador 2 agregado con movimientos.
- Colisiones entre Jugador 1 y Jugador 2.
- Cambio de caras de jugadores por las del equipo.

## [0.2.0] - 2026-08-22
### Added
- Cancha y jugador dibujados en pantalla usando SpriteBatch.
- Movimiento horizontal del jugador (flechas / WASD).
- Salto con gravedad básica.
- Colisión del jugador con el piso y los límites laterales de la cancha.
- Assets iniciales: imagen de cancha y 3 sprites del jugador (neutro, corriendo, pateando).

## [0.1.0] - 2026-08-21
### Added
- Configuración inicial del proyecto con LibGDX generado mediante gdx-liftoff.
- Estructura básica de módulos (core, lwjgl3).
- Repositorio conectado a GitHub.