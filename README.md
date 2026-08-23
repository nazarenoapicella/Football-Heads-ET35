# Football Heads - ET35

Videojuego de fútbol arcade 2D inspirado en la serie de juegos de navegador **Football Heads**, desarrollado en Java con el framework **LibGDX**, con un modo multijugador cooperativo en red para dos computadoras.

## Integrantes del grupo
- Apicella Nazareno
- Bartero Lautaro
- Manteiga Agustin
- Mihovilcevic Mirko
- Navarro Patricio

## Descripción del juego
El juego propone partidos de fútbol simples y rápidos, en los que dos jugadores se desplazan, saltan y golpean una pelota para intentar marcar goles en la cancha del rival. Se mantienen las características principales del género (vista lateral en 2D, física de pelota, rebotes e interacción entre jugadores), en una versión propia y simplificada. La prioridad del proyecto es que el modo multijugador en red funcione correctamente; los gráficos se mantendrán simples en una primera etapa.

## Tecnologías utilizadas
- **Lenguaje:** Java
- **Framework:** LibGDX (generado con gdx-liftoff)
- **Plataformas:** Core (lógica compartida) y Desktop (LWJGL3)
- **Control de versiones:** Git / GitHub
- **Gestión de dependencias:** Gradle

## Cómo compilar y ejecutar
1. Cloná el repositorio:
   `git clone https://github.com/nazarenoapicella/Football-Heads-ET35.git`
2. Abrí la carpeta del proyecto en Eclipse como proyecto Gradle existente: `File > Import > Gradle > Existing Gradle Project`.
3. Esperá a que termine de importar (el mensaje "importing Gradle Project" tiene que desaparecer).
4. Buscá el módulo `lwjgl3`, y dentro de él la clase `Lwjgl3Launcher.java`. Ejecutala como `Run As > Java Application`.
5. Se debería abrir una ventana con el juego.

## Estado actual del proyecto
Configuración inicial y estructura del proyecto (Pre-Entrega N°1). Todavía no está implementada la lógica del juego ni la comunicación en red.

## Propuesta completa del proyecto
Podés ver la propuesta formal completa del proyecto en la Wiki de este repositorio:
[Ver la Propuesta Completa del Proyecto aquí](https://github.com/nazarenoapicella/Football-Heads-ET35/wiki)
