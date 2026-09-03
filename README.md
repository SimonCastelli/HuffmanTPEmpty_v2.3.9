# Compresor de Archivos Huffman

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Build](https://img.shields.io/badge/build-Gradle-02303A.svg)](https://gradle.org/)
[![Tests](https://img.shields.io/badge/tests-JUnit%205-25A162.svg)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Implementación completa del algoritmo de compresión sin pérdidas de David
Huffman, en Java. El programa toma cualquier archivo, analiza la frecuencia
de sus bytes, construye un árbol de Huffman óptimo, y genera un archivo
comprimido `.huff`. Luego puede leer ese archivo, reconstruir el árbol
original a partir de la cabecera, y descomprimir el contenido restaurando el
archivo original de forma idéntica, bit a bit.

## Características

- **Compresión de archivos** — comprime cualquier tipo de archivo (texto,
  imágenes, binarios…) en un formato `.huff` propio.
- **Descompresión exacta** — restaura el archivo original bit a bit a partir
  de un archivo `.huff`.
- **Árbol de Huffman dinámico** — se construye a partir de las frecuencias
  reales del archivo de entrada, incluyendo el caso especial de un único
  símbolo distinto.
- **Manejo a nivel de bits** — `BitWriter`/`BitReader` propios empaquetan los
  códigos Huffman en bytes para lograr compresión real, no solo una cadena
  codificada.
- **Formato autodescriptivo** — la tabla de frecuencias (necesaria para
  reconstruir el árbol) y el tamaño original del archivo se guardan en la
  cabecera del `.huff`.
- **Cubierto con tests** — la lógica de manejo de bits y de compresión está
  cubierta con JUnit 5.

## Estructura del proyecto

```
HuffmanTPEmpty_v2.3.9/
├── src/
│   ├── main/java/
│   │   ├── huffman/
│   │   │   ├── app/            # Punto de entrada (HuffmanApp)
│   │   │   ├── def/            # Contratos públicos y tipos de datos
│   │   │   │   ├── BitReader.java / BitWriter.java
│   │   │   │   ├── Compresor.java / Descompresor.java
│   │   │   │   ├── HuffmanInfo.java     (nodo del árbol)
│   │   │   │   └── HuffmanTable.java    (entrada de la tabla de frecuencias)
│   │   │   ├── util/           # Utilidades de árbol compartidas
│   │   │   │   ├── HuffmanTreeBuilder.java  (construye el árbol desde la tabla)
│   │   │   │   ├── HuffmanTree.java         (recorrido basado en pila)
│   │   │   │   └── HuffmanTreeMap.java      (lookup símbolo → código)
│   │   │   └── ejemplos/       # Demos ejecutables sobre árboles armados a mano
│   │   └── imple/              # Implementaciones concretas + Factory
│   └── test/
│       ├── java/huffman/def/   # Suite de tests JUnit 5
│       └── resources/          # Fixtures de test
├── build.gradle
├── settings.gradle
└── gradlew / gradlew.bat
```

| Paquete | Responsabilidad |
|---|---|
| `huffman.app` | Punto de entrada que orquesta la compresión/descompresión. |
| `huffman.def` | Interfaces (`Compresor`, `Descompresor`, `BitReader`, `BitWriter`) y tipos de datos (`HuffmanInfo`, `HuffmanTable`). |
| `huffman.util` | Construcción y recorrido del árbol, compartidos entre compresor y descompresor. |
| `huffman.ejemplos` | Demos pequeñas que arman un árbol de Huffman a mano para ilustrar el recorrido y la generación de códigos. |
| `imple` | Implementaciones concretas de las interfaces de `huffman.def`, ensambladas por `Factory`. |

## Cómo empezar

### Prerrequisitos

- **JDK 17** o superior.
- No hace falta tener Gradle instalado: el proyecto incluye el Gradle Wrapper.

### Compilar

```bash
./gradlew build
```

### Correr los tests

```bash
./gradlew test
```

### Ejecutar la aplicación

```bash
./gradlew run
```

o, luego de compilar:

```bash
java -cp build/classes/java/main huffman.app.HuffmanApp
```

Al ejecutar `HuffmanApp` se abre un explorador de archivos:

- **Para comprimir:** seleccioná cualquier archivo (ej: `documento.txt`). El
  programa genera `documento.txt.huff` en la misma carpeta.
- **Para descomprimir:** seleccioná un archivo `.huff` (ej:
  `documento.txt.huff`). El programa genera el archivo original (ej:
  `documento.txt`) en la misma carpeta.

## Formato del archivo `.huff`

La cabecera se escribe antes del contenido comprimido, para que el
descompresor pueda reconstruir exactamente el mismo árbol usado al codificar:

| Campo | Tamaño | Descripción |
|---|---|---|
| Cantidad de símbolos | 4 bytes | Cantidad de bytes distintos presentes en el archivo original. |
| Tabla de símbolos | 5 bytes × cantidad | Pares `(byte, frecuencia)` repetidos. |
| Tamaño original | 4 bytes | Tamaño en bytes del archivo original, usado para saber cuándo detener la decodificación. |
| Contenido | variable | El archivo original recodificado como un stream de bits empaquetado con los códigos Huffman. |

## Lógica de compresión y descompresión

### Compresión (`CompresorImple`)

1. **`contarOcurrencias`** — lee el archivo de entrada byte por byte y cuenta
   la frecuencia de cada byte posible (0–255).
2. **`crearListaEnlazada`** — crea un nodo hoja `HuffmanInfo` por cada byte
   con frecuencia mayor a cero.
3. **`convertirListaEnArbol`** — construye el árbol de Huffman con una
   `PriorityQueue`: toma repetidamente los dos nodos de menor frecuencia, los
   combina en un nuevo nodo padre, y lo reinserta, hasta que queda una única
   raíz. (`huffman.util.HuffmanTreeBuilder`, compartido con el descompresor.)
4. **`generarCodigosHuffman`** — recorre el árbol recursivamente, asignando
   `0` para la rama izquierda y `1` para la derecha, para construir el
   código binario de cada hoja.
5. **`escribirEncabezado`** — escribe la cabecera del `.huff`: cantidad de
   entradas, tabla de frecuencias y tamaño original del archivo.
6. **`escribirContenido`** — vuelve a leer el archivo original y traduce
   cada byte a su código Huffman, empaquetado bit a bit vía `BitWriter`.

### Descompresión (`DescompresorImple`)

1. **`recomponerArbol`** — lee la cabecera, reconstruye la tabla de
   frecuencias, y llama a la *misma* lógica de construcción de árbol usada
   en la compresión (`HuffmanTreeBuilder`) para reconstruir un árbol
   idéntico. También recupera el tamaño original del archivo.
2. **`descomprimirArchivo`** — salta la cabecera y lee el resto del archivo
   bit a bit vía `BitReader`. Por cada bit, navega el árbol desde la raíz
   (`0` = izquierda, `1` = derecha) hasta llegar a una hoja, escribe el byte
   de esa hoja en el archivo de salida, y vuelve a la raíz para decodificar
   el siguiente código. Repite hasta escribir la cantidad de bytes indicada
   por el tamaño original.

## Testing

La suite en `src/test/java` cubre:

- `BitReaderTest` / `BitWriterTest` — empaquetado y desempaquetado a nivel
  de bits.
- `CompresorTest` — conteo de frecuencias de bytes contra un archivo fixture.

Se ejecuta todo con:

```bash
./gradlew test
```

## Posibles mejoras

- [ ] Tests de ida y vuelta (comprimir → descomprimir → comparar) cubriendo
      archivos binarios, archivos vacíos y archivos de un único símbolo.
- [ ] API por streaming para que archivos grandes no requieran múltiples
      pasadas completas.
- [ ] Flags de línea de comandos como alternativa al explorador de archivos
      interactivo, para scripting y CI.

## Licencia

Distribuido bajo la [Licencia MIT](LICENSE).

## Autor

**Simon Castelli**
