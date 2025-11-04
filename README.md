#  Compresor de Archivos Huffman en Java

Este proyecto es una implementación completa del algoritmo de compresión sin pérdidas de David Huffman, desarrollado en Java. El programa es capaz de tomar cualquier archivo, analizar la frecuencia de sus bytes, construir un árbol de Huffman, y generar un archivo comprimido `.huff`.

Posteriormente, puede leer dicho archivo `.huff`, reconstruir el árbol original a partir de la cabecera, y descomprimir el contenido para restaurar el archivo original de forma idéntica.

## Características Principales

*  **Compresión de Archivos:** Comprime cualquier tipo de archivo (texto, imágenes, etc.) en un formato `.huff` personalizado.
*  **Descompresión Precisa:** Restaura perfectamente el archivo original desde un archivo `.huff`, bit a bit.
*  **Árbol de Huffman Dinámico:** Construye un árbol de Huffman óptimo basado en las frecuencias de bytes del archivo de entrada.
*  **Manejo a Nivel de Bits:** Utiliza `BitWriter` y `BitReader` personalizados para una compresión real, empaquetando los códigos de Huffman en bytes.
*  **Cabecera Personalizada:** Almacena la tabla de frecuencias (el "mapa" para descomprimir) de forma eficiente en la cabecera del archivo `.huff`, junto con el tamaño original del archivo.

##  Cómo Usar

El proyecto está diseñado para ejecutarse desde la clase principal `huffman.app.HuffmanApp`.

### Prerrequisitos

* Tener instalado el **Java Development Kit (JDK)** (versión 8 o superior).

### Ejecución

1.  Compila el proyecto (si no estás usando un IDE que lo haga automáticamente).
2.  Ejecuta la clase principal:

    ```bash
    java huffman.app.HuffmanApp
    ```

3.  Al ejecutarlo, se abrirá un explorador de archivos:
    * **Para Comprimir:** Selecciona cualquier archivo (ej: `documento.txt`). El programa generará `documento.txt.huff` en la misma carpeta.
    * **Para Descomprimir:** Selecciona un archivo `.huff` (ej: `documento.txt.huff`). El programa generará el archivo original (ej: `documento.txt`) en la misma carpeta.

## 📂 Estructura del Proyecto
```
(Directorio Raíz)
  |
  +-- huffman/
  |   |
  |   +-- app/
  |   |   |-- HuffmanApp.java       #  Punto de entrada principal
  |   |
  |   +-- def/
  |       |-- BitReader.java        # (Interfaz)
  |       |-- BitWriter.java        # (Interfaz)
  |       |-- Compresor.java        # (Interfaz)
  |       |-- Descompresor.java     # (Interfaz)
  |       |-- HuffmanInfo.java      # (Clase para el Nodo del árbol)
  |       |-- HuffmanTable.java     # (Clase para la Tabla de frecuencias)
  |
  +-- imple/
  |   |
  |   |-- BitReaderImple.java     # (Implementación lector de bits)
  |   |-- BitWriterImple.java     # (Implementación escritor de bits)
  |   |-- CompresorImple.java     # (Implementación de la compresión)
  |   |-- DescompresorImple.java  # (Implementación de la descompresión)
  |   |-- Factory.java            # (Fábrica para obtener implementaciones)
  |
  +-- README.md
```

##  Lógica de Compresión y Descompresión

### Compresión (`CompresorImple`)

1.  **`contarOcurrencias`**: Lee el archivo de entrada byte por byte y cuenta la frecuencia de cada uno (0-255).
2.  **`crearListaEnlazada`**: Crea una lista de nodos `HuffmanInfo` (hojas del árbol) por cada byte que tenga una frecuencia > 0.
3.  **`convertirListaEnArbol`**: Utiliza una `PriorityQueue` para construir el árbol de Huffman. Toma los dos nodos con menor frecuencia, los combina en un nuevo nodo padre, y lo reinserta en la cola. Repite hasta que solo queda un nodo (la raíz).
4.  **`generarCodigosHuffman`**: Recorre el árbol recursivamente para asignar los códigos binarios ('0' para la izquierda, '1' para la derecha) a cada hoja (cada byte).
5.  **`escribirEncabezado`**: Escribe la "magia" en el archivo `.huff`. Primero guarda el número de entradas en la tabla, luego la tabla (pares `byte-frecuencia`) y finalmente el tamaño original del archivo.
6.  **`escribirContenido`**: Vuelve a leer el archivo original, pero esta vez traduce cada byte a su código Huffman correspondiente y lo escribe en el archivo de salida usando el `BitWriter`.

### Descompresión (`DescompresorImple`)

1.  **`recomponerArbol`**: Lee la cabecera del archivo `.huff`. Reconstruye la tabla de frecuencias y usa *exactamente el mismo* método (`convertirListaEnArbol` con `PriorityQueue`) para generar un árbol idéntico al de la compresión. También recupera el tamaño original del archivo.
2.  **`descomprimirArchivo`**: Salta la cabecera. Lee el resto del archivo bit a bit usando el `BitReader`. Por cada bit, navega por el árbol (0=izquierda, 1=derecha) desde la raíz.
3.  Cuando llega a un nodo hoja, escribe el byte de esa hoja en el archivo de salida y vuelve a la raíz para leer el siguiente código.
4.  Repite este proceso hasta que se haya escrito el número de bytes del "tamaño original" guardado.
