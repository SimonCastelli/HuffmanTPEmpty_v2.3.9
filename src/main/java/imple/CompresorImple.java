package imple;

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

        while (lista.size() > 1) {
            lista.sort(Comparator.comparingInt(HuffmanInfo::getN));

            HuffmanInfo izq = lista.get(0);
            HuffmanInfo der = lista.get(1);
            HuffmanInfo nuevo = new HuffmanInfo();
            nuevo.setC('\0');
            nuevo.setN(izq.getN() + der.getN());
            nuevo.setLeft(izq);
            nuevo.setRight(der);
            lista.remove(izq);
            lista.remove(der);
            lista.add(nuevo);
        }
        return lista.get(0);
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
    public void escribirEncabezado(String filename, HuffmanTable[] arr)
    {
        String outFilename = filename + ".huff";

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outFilename))) {
            for (int i = 0; i < 256; i++) {
                dos.writeInt(arr[i].getN());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void escribirContenido(String filename, HuffmanTable[] arr)
    {
        String outFilename = filename + ".huff";

        BitWriter bitWriter = new imple.BitWriterImple();

        try (FileInputStream in = new FileInputStream(filename);
             FileOutputStream fos = new FileOutputStream(outFilename, true))
        {
            bitWriter.using(fos);

            int b;
            while ((b = in.read()) != -1) {
                String codigo = arr[b].getCod();
                for (char c : codigo.toCharArray()) {
                    if (c == '1') {
                        bitWriter.writeBit(1);
                    } else {
                        bitWriter.writeBit(0);
                    }
                }
            }
            bitWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
