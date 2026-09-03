package huffman.ejemplos;

import huffman.def.HuffmanInfo;
import huffman.util.HuffmanTreeMap;

public class HuffmanTreeMapDemo
{
	public static void main(String[] args)
	{
		// crea el árbol "COMO COME COCORITO..."
		HuffmanInfo root = HuffmanDemoFixtures.demoCocorito();

		// instancio
		HuffmanTreeMap htm = new HuffmanTreeMap(root);

		int[] keys = htm.keys();
		for(int i=0; i<keys.length; i++)
		{
			int b = keys[i];
			String cod = htm.get(b);
			System.out.println((char)keys[i]+" - ["+cod+"]");
		}
	}
}
