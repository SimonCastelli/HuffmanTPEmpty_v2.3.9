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
		
		if( fName.endsWith(".huf") )
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
		Compresor cmp = Factory.getCompresor();
		
		// cuento las ocurrencias
		HuffmanTable[] table = cmp.contarOcurrencias(fName);
		
		// creo la lista
		List<HuffmanInfo> list = cmp.crearListaEnlazada(table);
		
		// convierto en árbol
		HuffmanInfo root = cmp.convertirListaEnArbol(list);
		
		// asigno los códigos Huffman en la tabla
		cmp.generarCodigosHuffman(root,table);
		
		// escribo el encabezado del .huf
		cmp.escribirEncabezado(fName,table);
		
		// escribo el contenido
		cmp.escribirContenido(fName,table);
	}
	
	public static void descomprimir(String fName)
	{
	}
}
