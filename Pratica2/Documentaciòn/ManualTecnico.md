# Manual Técnico - Quetzal Space Defender

## 1. Descripción general
Juego de naves tipo side-scroller horizontal desarrollado en Java con interfaz gráfica Swing.
Los objetos aparecen en el borde derecho de la pantalla y avanzan hacia la izquierda. El
jugador controla una nave capaz de moverse . Cada objeto del juego es un hilo
independiente. Los datos se almacenan en vectores (arreglos) y se generan reportes en HTML
usando únicamente java.io.

## 2. Estructura del proyecto
| Paquete | Clases | Función |
|---|---|---|
| (raíz) | Main | Punto de entrada; abre el menú principal |
| modelo | TipoNave, Piloto, Partida | Datos del piloto, nave y partida |
| modelo | ObjetoEspacial, Proyectil, Enemigo, Asteroide, Quaffle, Snitch | Objetos del juego (cada uno es un hilo) |
| modelo | Dibujo | Dibujo de las naves y demas objetos.|
| datos | GestorPilotos, GestorPartidas, Datos | Vectores y validaciones |
| juego | PanelJuego, VentanaJuego | Lógica y ventana de la partida |
| ui | VentanaMenu, VentanaTop, PanelFondo, EstiloUI | Interfaz: menú, top y estilo visual |
| reportes | GraficaTop, ReporteHTML | Gráfica JFreeChart y exportación del reporte |

## 3. Librerías y tecnologías
| Librería | Uso |
|---|---|
| Java Swing / AWT | Ventanas, botones, tablas y dibujo del juego (Graphics2D) |
| JFreeChart 1.5.4 | Gráfica de barras del top de puntajes |
| java.io (PrintWriter, FileOutputStream, OutputStreamWriter, File) | Escritura del reporte HTML |
| javax.imageio.ImageIO | Exportar la gráfica como imagen PNG |
| java.awt.Desktop | Abrir el reporte generado en el navegador |
| java.time | Fecha y hora de las partidas y de los reportes |

El archivo `jfreechart-1.5.4.jar` está en la carpeta `lib/` 

## 4. Lógica general

### 4.1 Hilos
- **Objetos del juego:** `ObjetoEspacial` implementa `Runnable`. Cada proyectil, enemigo,
  asteroide, Quaffle y Snitch es un hilo independiente que actualiza su posición (x, y)
  cada 30 ms y termina cuando sale de la pantalla o es destruido.
- **PanelJuego** usa 3 hilos propios:
  1. Movimiento de la nave: su `sleep` depende del tipo de nave.
  2. Generador de objetos: crea un objeto nuevo cada 700 ms.
  3. Ciclo principal: mueve las estrellas, revisa colisiones y repinta cada 16 ms.
- **Sincronización:** el vector de objetos y el historial de partidas se protegen con
  `synchronized`, porque varios hilos los leen y modifican a la vez. Las variables
  compartidas (posición, puntaje, vidas) son `volatile`.

### 4.2 Dificultad por tipo de nave
La dificultad se define en el enum `TipoNave` con dos valores: el `sleep` del hilo de
movimiento y el tiempo de recarga del disparo.

| Nave | Dificultad | Sleep de movimiento | Cooldown de disparo |
|---|---|---|---|
| Explorador | Fácil | 10 ms | 2000 ms |
| Caza Estelar | Normal | 20 ms | 1000 ms |
| Acorazado | Difícil | 40 ms | 300 ms |

### 4.3 Estructuras de datos (vectores)
No se usan colecciones dinámicas; todo se maneja con arreglos:
- `Piloto[]` en `GestorPilotos` y `Partida[]` en `GestorPartidas`.
- Cuando un vector se llena, se crea uno del doble de tamaño y se copian los elementos con un ciclo `for`.
- `ObjetoEspacial[200]` en `PanelJuego` guarda los objetos en pantalla; los espacios de objetos inactivos se reutilizan.
- Arreglos de enteros para el fondo de estrellas.
- El top se ordena de mayor a menor con el método de la burbuja sobre una copia del vector.

### 4.4 Validaciones al crear un piloto
`GestorPilotos.crear()` devuelve `null` si todo es correcto, o el mensaje de error:
- El nombre no puede estar vacío.
- Debe tener entre 3 y 15 caracteres.
- Solo letras, números y guion bajo (sin espacios ni tildes).
- Debe seleccionarse una nave.
- No puede repetirse un nombre ya existente (sin distinguir mayúsculas).

### 4.5 Colisiones y reglas
| Objeto | Efecto |
|---|---|
| Enemigo | Si toca la nave quita una vida (3 vidas en total, 1 s de invulnerabilidad). Si lo destruye un disparo, +5 puntos |
| Asteroide (Bludger) | Bloquea la nave 2 segundos; absorbe los disparos |
| Quaffle | +10 puntos |
| Snitch | +150 puntos y destruye a todos los enemigos visibles |

La partida termina al llegar a 0 vidas. Entonces se actualiza el piloto
(`registrarPartida`) y se guarda un registro en el historial.

### 4.6 Aparición de objetos
Se decide con un número aleatorio de 0 a 99 en `hiloGenerador()`:
| Objeto | Probabilidad |
|---|---|
| Enemigo | 45% |
| Asteroide | 25% |
| Quaffle | 18% |
| Snitch | 12% |

## 5. Aspecto visual
Todo se dibuja con Graphics2D, sin imágenes externas:
- **Naves:** `Dibujo.dibujarNave()` . Cada nave tiene su color y variante. Se pone morada cuando está bloqueada y parpadea cuando es invulnerable.
- **Objetos:** cada clase implementa su método abstracto `dibujar()` (platillo para el enemigo, roca con cráteres para el asteroide, esfera dorada con alas animadas para la Snitch).
- **Fondo:** degradado con estrellas que se mueven a distinta velocidad (efecto de profundidad), tanto en la partida como en el menú.
- **Menú:** `PanelFondo` (fondo animado con un `javax.swing.Timer`) y `EstiloUI` (botones con efecto al pasar el mouse).

## 6. Métodos importantes
| Método | Clase | Descripción |
|---|---|---|
| `crear()` | GestorPilotos | Valida y registra un piloto nuevo |
| `buscar()` | GestorPilotos | Busca un piloto por nombre |
| `agregar()` | GestorPartidas | Agrega una partida; amplía el vector si hace falta |
| `getTop(n)` | GestorPartidas | Devuelve las n mejores partidas ordenadas |
| `run()` | ObjetoEspacial | Mueve el objeto hasta que sale de pantalla o se destruye |
| `revisarColisiones()` | PanelJuego | Proyectiles contra enemigos y objetos contra la nave |
| `procesarChoque()` | PanelJuego | Aplica el efecto de cada objeto |
| `disparar()` | PanelJuego | Crea un proyectil respetando el cooldown |
| `finalizar()` | PanelJuego | Guarda la partida y cierra la ventana |
| `crear(Partida[])` | GraficaTop | Construye la gráfica de barras con JFreeChart |
| `generar(File)` | ReporteHTML | Crea el PNG y el HTML; devuelve el archivo HTML |
| `exportar()` | VentanaTop | Pide la carpeta, genera el reporte y lo abre en el navegador |

## 7. Exportación de reportes
1. El usuario pulsa **Exportar reporte (HTML)** en la ventana de Top y elige una carpeta con `JFileChooser`.
2. `ReporteHTML.generar()` crea una marca de tiempo con el formato `yyyyMMdd_HHmmss`.
3. Con `ImageIO.write()` guarda la gráfica como `grafica_top_<marca>.png`.
4. Con `PrintWriter` sobre `OutputStreamWriter` y `FileOutputStream` 
escribe `reporte_quetzal_<marca>.html`, con estilos CSS, la gráfica, el top, el historial y los pilotos registrados.
5. El HTML se abre en el navegador con `Desktop.browse()`.
6. Para obtener el PDF: **Ctrl + P**, destino **Guardar como PDF**

Como el nombre incluye la fecha y hora, cada exportación genera archivos nuevos y no
sobrescribe las anteriores. El PNG y el HTML deben permanecer en la misma carpeta.

## 8. ADICIONAL AGREGADO
Se establecieron como decisiones propias:
- Puntos por destruir un enemigo (+5).
- Número de vidas (3) y tiempo de invulnerabilidad (1 s).
- Porcentajes y frecuencia de aparición de los objetos.
- El Bludger bloquea la nave (use bloquearla).
- Velocidades y tamaños de los objetos.
