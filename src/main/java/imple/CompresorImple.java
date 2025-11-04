package imple;
import java.util.PriorityQueue;

import huffman.def.*;
import java.io.*;
import java.util.*;
public class CompresorImple implements Compresor
{
    @Override
    public HuffmanTable[] contarOcurrencias(String filename)
    {
        HuffmanTable[] tabla = new HuffmanTable[256];

        for (int i = 0; i < 256; i++) {
            tabla[i] = new HuffmanTable();
            tabla[i].setN(0);
            tabla[i].setCod("");
        }

        try (FileInputStream in = new FileInputStream(filename)) {
            int b;
            while ((b = in.read()) != -1) {
                tabla[b].increment();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tabla;
    }

    @Override
    public List<HuffmanInfo> crearListaEnlazada(HuffmanTable[] arr)
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

    @Override
    public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista)
    {
        if (lista == null || lista.isEmpty()) {
            return null;
        }

        // Usar PriorityQueue en lugar de List.sort() en bucle
        PriorityQueue<HuffmanInfo> pq = new PriorityQueue<>(Comparator.comparingInt(HuffmanInfo::getN));
        pq.addAll(lista);

        // Caso especial: archivo con un solo tipo de caracter
        // (Esto faltaba en tu compresor original y es crítico)
        if (pq.size() == 1) {
            HuffmanInfo unico = pq.poll();
            HuffmanInfo nuevo = new HuffmanInfo();
            nuevo.setC('\0');
            nuevo.setN(unico.getN());
            nuevo.setLeft(unico); // Asignar a la izquierda (o derecha, pero ser consistente)
            nuevo.setRight(null); // O un nodo "dummy" si es necesario
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

    @Override
    public void generarCodigosHuffman(HuffmanInfo root, HuffmanTable[] arr)
    {
        if (root == null) return;

        java.util.function.BiConsumer<HuffmanInfo, String> recorrer = new java.util.function.BiConsumer<>() {
            @Override
            public void accept(HuffmanInfo nodo, String codigo) {
                if (nodo == null) return;

                if (nodo.getLeft() == null && nodo.getRight() == null) {
                    arr[(int) nodo.getC()].setCod(codigo);
                    return;
                }

                this.accept(nodo.getLeft(), codigo + "0");
                this.accept(nodo.getRight(), codigo + "1");
            }
        };

        recorrer.accept(root, "");
    }
    @Override
public void escribirEncabezado(String filename, HuffmanTable[] arr) {
    String outFilename = filename + ".huff";

    try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFilename))) {
        // Contar cuántos caracteres aparecen
        int usados = 0;
        for (int i = 0; i < 256; i++) {
            if (arr[i].getN() > 0) usados++;
        }

        // 1️⃣ Guardar la cantidad de caracteres distintos
        dos.writeInt(usados);

        // 2️⃣ Guardar pares (caracter, frecuencia)
        for (int i = 0; i < 256; i++) {
            if (arr[i].getN() > 0) {
                dos.writeByte(i);        // el carácter
                dos.writeInt(arr[i].getN()); // su frecuencia
            }
        }

        // 3️⃣ Guardar tamaño original
        File file = new File(filename);
        long originalSize = file.length();
        dos.writeInt((int) originalSize);

    } catch (IOException e) {
        e.printStackTrace();
    }
}


    @Override
    public void escribirContenido(String filename, HuffmanTable[] arr) {
        String outFilename = filename + ".huff";

        BitWriter bitWriter = new imple.BitWriterImple();

        try (FileInputStream in = new FileInputStream(filename);
             FileOutputStream fos = new FileOutputStream(outFilename, true)) {

            bitWriter.using(fos);

            int b;
            while ((b = in.read()) != -1) {
                String codigo = arr[b].getCod();
                for (char c : codigo.toCharArray()) {
                    bitWriter.writeBit(c == '1' ? 1 : 0);
                }
            }

            bitWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
