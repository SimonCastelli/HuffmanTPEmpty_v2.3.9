package huffman.ejemplos;

import huffman.def.HuffmanInfo;
import huffman.util.HuffmanTree;

public class HuffmanTreeDemo
{
	public static void main(String[] args)
	{
		// crea el árbol "COMO COME COCORITO..."
		HuffmanInfo root = HuffmanDemoFixtures.demoCocorito();

		// instancio
		HuffmanTree ht = new HuffmanTree(root);

		// tendrá el código Huffman de cada hoja
		StringBuffer cod = new StringBuffer();

		HuffmanInfo hoja = ht.next(cod);
		while( hoja!=null )
		{
			System.out.println(hoja+": ["+cod+"]");
			hoja = ht.next(cod);
		}
	}
}
