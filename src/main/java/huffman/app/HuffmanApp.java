package huffman.app;

import java.util.List;

import huffman.def.Compresor;
import huffman.def.Descompresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;
import imple.Factory;
import thejavalistener.fwk.console.MyConsole;
import thejavalistener.fwk.console.Progress;
import thejavalistener.fwk.util.MyThread;

public class HuffmanApp
{
	public static void main(String[] args)
	{
		MyConsole c = MyConsole.openWindow("Compresor de archivos Huffman");
		String fName = c.print("Seleccione un archivo: ").fileExplorer();

		if( fName.endsWith(".huff") )
		{
			descomprimir(fName);
		}
		else
		{
			comprimir(fName);
		}
	}

	public static void comprimir(String fName)
	{
		MyConsole c = MyConsole.openWindow("Compresor de archivos Huffman");
		Compresor cmp = Factory.getCompresor();

		c.println("📊 Contando ocurrencias...");
		HuffmanTable[] table = cmp.contarOcurrencias(fName);

		c.println("🧩 Creando lista enlazada...");
		List<HuffmanInfo> list = cmp.crearListaEnlazada(table);

		c.println("🌳 Generando árbol Huffman...");
		HuffmanInfo root = cmp.convertirListaEnArbol(list);

		c.println("🔢 Asignando códigos Huffman...");
		cmp.generarCodigosHuffman(root, table);

		c.println("🗂️ Escribiendo encabezado...");
		cmp.escribirEncabezado(fName, table);

		c.println("💾 Escribiendo contenido comprimido...");
		cmp.escribirContenido(fName, table);

		c.println("\n✅ Compresión finalizada con éxito.");
	}

	public static void descomprimir(String fName) {
		Descompresor d = Factory.getDescompresor();

		HuffmanInfo root = new HuffmanInfo();
		int headerSize = d.recomponerArbol(fName, root);

		if (headerSize > 0)
			d.descomprimirArchivo(root, headerSize, fName);
	}
}
