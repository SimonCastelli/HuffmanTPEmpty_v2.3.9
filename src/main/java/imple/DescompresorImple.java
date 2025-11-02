package imple;

import huffman.def.*;
import java.io.*;
import java.util.*;

public class DescompresorImple implements Descompresor
{
    @Override
    public int recomponerArbol(String filename, HuffmanInfo root)
    {
        HuffmanTable[] arr = new HuffmanTable[256];
        int headerSize = 256 * 4; // 256 enteros de 4 bytes cada uno

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            // 1. Leer las 256 frecuencias del encabezado
            for (int i = 0; i < 256; i++) {
                arr[i] = new HuffmanTable();
                arr[i].setN(dis.readInt());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Indicar error
        }

        // 2. Reconstruir la lista y el árbol
        List<HuffmanInfo> lista = crearListaEnlazada(arr);
        HuffmanInfo newRoot = convertirListaEnArbol(lista);

        // 3. Copiar las propiedades al parámetro 'root'
        if (newRoot != null) {
            root.setN(newRoot.getN());
            root.setC(newRoot.getC());
            root.setLeft(newRoot.getLeft());
            root.setRight(newRoot.getRight());
        }

        // 4. Devolver el tamaño del encabezado
        return headerSize;
    }

    /**
     * Descomprime el contenido del archivo.
     */
    @Override
    public void descomprimirArchivo(HuffmanInfo root, int n, String filename)
    {
        String outFilename = filename.replaceFirst("\\.huff$", "");
        if (outFilename.equals(filename)) {
            outFilename += ".unhuff"; // Fallback
        }

        // --- ADAPTACIÓN ---
        // 1. Instanciar tu BitReaderImple (asumiendo que la interfaz es BitReader)
        BitReader bitReader = new imple.BitReaderImple();

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename));
             FileOutputStream fos = new FileOutputStream(outFilename))
        {
            // 2. Omitir el encabezado (tabla de frecuencias)
            dis.skipBytes(n);

            // 3. Leer el tamaño original del archivo (los 4 bytes siguientes)
            int originalSize = dis.readInt();

            // --- ADAPTACIÓN ---
            // 4. Inicializar tu bitReader usando el DataInputStream
            bitReader.using(dis);

            // 5. Leer bit por bit y descomprimir
            for (int i = 0; i < originalSize; i++) {
                HuffmanInfo nodoActual = root;

                // Caso especial: árbol con un solo nodo (archivo de un solo caracter)
                // El bucle while no se ejecutaría, pero necesitamos manejarlo.
                // Tu compresor asigna '0' en este caso.
                if (nodoActual.getLeft() != null && nodoActual.getRight() == null) {
                    bitReader.readBit(); // Consumir el bit '0'
                    nodoActual = nodoActual.getLeft();
                } else {
                    // Navegar el árbol normal
                    while (nodoActual.getLeft() != null || nodoActual.getRight() != null) {
                        int bit = bitReader.readBit();
                        if (bit == -1) {
                            throw new IOException("Error: Fin de archivo inesperado.");
                        }

                        nodoActual = (bit == 0) ? nodoActual.getLeft() : nodoActual.getRight();

                        if (nodoActual == null) {
                            throw new IOException("Error: Archivo corrupto. Ruta de bits inválida.");
                        }
                    }
                }

                // Encontramos la hoja, escribimos el carácter
                fos.write((int) nodoActual.getC());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS AUXILIARES (Copiados de CompresorImple) ---

    private List<HuffmanInfo> crearListaEnlazada(HuffmanTable[] arr)
    {
        List<HuffmanInfo> lista = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getN() > 0) {
                HuffmanInfo info = new HuffmanInfo();
                info.setC((char) i);
                info.setN(arr[i].getN());
                info.setLeft(null);
                info.setRight(null);
                lista.add(info);
            }
        }
        return lista;
    }

    private HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista)
    {
        if (lista == null || lista.isEmpty()) {
            return null;
        }

        PriorityQueue<HuffmanInfo> pq = new PriorityQueue<>(Comparator.comparingInt(HuffmanInfo::getN));
        pq.addAll(lista);

        // Caso especial: archivo con un solo tipo de caracter
        if (pq.size() == 1) {
            HuffmanInfo unico = pq.poll();
            HuffmanInfo nuevo = new HuffmanInfo();
            nuevo.setC('\0');
            nuevo.setN(unico.getN());
            nuevo.setLeft(unico); // Código '0'
            nuevo.setRight(null);
            pq.add(nuevo);
        }

        while (pq.size() > 1) {
            HuffmanInfo izq = pq.poll();
            HuffmanInfo der = pq.poll();

            HuffmanInfo nuevo = new HuffmanInfo();
            nuevo.setC('\0');
            nuevo.setN(izq.getN() + der.getN());
            nuevo.setLeft(izq);
            nuevo.setRight(der);

            pq.add(nuevo);
        }
        return pq.poll();
    }
}