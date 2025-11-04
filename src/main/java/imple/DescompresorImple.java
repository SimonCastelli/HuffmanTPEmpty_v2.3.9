package imple;

import huffman.def.*;
import java.io.*;
import java.util.*;

public class DescompresorImple implements Descompresor
{
    private int originalFileSize;

    @Override
    public int recomponerArbol(String filename, HuffmanInfo root) {
        HuffmanTable[] arr = new HuffmanTable[256];
        for (int i = 0; i < 256; i++) arr[i] = new HuffmanTable();

        int headerSize = 0;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int usados = dis.readInt();
            headerSize += 4;

            for (int i = 0; i < usados; i++) {
                int c = dis.readUnsignedByte();
                int freq = dis.readInt();
                arr[c].setN(freq);
                headerSize += 5;
            }

            // tamaño original
            int originalSize = dis.readInt();
            headerSize += 4;

            // guardar temporalmente el tamaño original para el siguiente método
            this.originalFileSize = originalSize;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        List<HuffmanInfo> lista = crearListaEnlazada(arr);
        HuffmanInfo newRoot = convertirListaEnArbol(lista);

        if (newRoot != null) {
            root.setN(newRoot.getN());
            root.setC(newRoot.getC());
            root.setLeft(newRoot.getLeft());
            root.setRight(newRoot.getRight());
        }

        return headerSize;
    }


    @Override
    public void descomprimirArchivo(HuffmanInfo root, int headerSize, String filename) {
        String outFilename = filename.endsWith(".huff")
                ? filename.substring(0, filename.length() - 5)
                : filename + ".txt";

        BitReader bitReader = new imple.BitReaderImple();

        try (FileInputStream fis = new FileInputStream(filename);
             FileOutputStream fos = new FileOutputStream(outFilename)) {

            // saltar el encabezado
            fis.skip(headerSize);
            bitReader.using(fis);

            int bytesEscritos = 0;

            while (bytesEscritos < this.originalFileSize) {
                HuffmanInfo nodo = root;

                while (nodo.getLeft() != null && nodo.getRight() != null) {
                    int bit = bitReader.readBit();
                    if (bit == -1)
                        throw new IOException("Error: fin de archivo inesperado.");
                    nodo = (bit == 0) ? nodo.getLeft() : nodo.getRight();
                }

                fos.write((byte) nodo.getC());
                bytesEscritos++;
            }

            fos.flush();
            System.out.println("✅ Archivo descomprimido correctamente: " + outFilename);

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
            nuevo.setLeft(unico);
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
