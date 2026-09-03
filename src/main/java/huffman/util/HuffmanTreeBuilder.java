package huffman.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;

/**
 * Construye el árbol de Huffman a partir de una tabla de frecuencias.
 *
 * Tanto el compresor como el descompresor necesitan reconstruir exactamente
 * el mismo árbol a partir de la misma tabla de frecuencias (el compresor para
 * generar los códigos, el descompresor para poder decodificarlos). Esta
 * clase centraliza esa lógica para que ambos usen siempre el mismo algoritmo.
 */
public final class HuffmanTreeBuilder
{
	private HuffmanTreeBuilder()
	{
		// utility class
	}

	/** Convierte la tabla de frecuencias en una lista de hojas (una por cada símbolo con n &gt; 0). */
	public static List<HuffmanInfo> crearListaEnlazada(HuffmanTable[] arr)
	{
		List<HuffmanInfo> lista = new ArrayList<>();

		for (int i = 0; i < arr.length; i++)
		{
			if (arr[i].getN() > 0)
			{
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

	/**
	 * Construye el árbol de Huffman combinando repetidamente los dos nodos de
	 * menor frecuencia hasta que queda un único nodo raíz.
	 */
	public static HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista)
	{
		if (lista == null || lista.isEmpty())
		{
			return null;
		}

		PriorityQueue<HuffmanInfo> pq = new PriorityQueue<>(Comparator.comparingInt(HuffmanInfo::getN));
		pq.addAll(lista);

		// Caso especial: archivo con un único símbolo distinto.
		if (pq.size() == 1)
		{
			HuffmanInfo unico = pq.poll();
			HuffmanInfo nuevo = new HuffmanInfo();
			nuevo.setC('\0');
			nuevo.setN(unico.getN());
			nuevo.setLeft(unico);
			nuevo.setRight(null);
			pq.add(nuevo);
		}

		while (pq.size() > 1)
		{
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
