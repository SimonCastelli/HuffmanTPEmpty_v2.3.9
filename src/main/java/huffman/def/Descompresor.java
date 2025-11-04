package huffman.def;

public interface Descompresor
{
	/** Debe recomponer el árbol Huffman asignando las ramas a root (que lo recibe por referencia), 
	 * y retornar cuántos bytes conforman el encabezado del archivo .huf, desde el inicio
	 * hasta la longitud grabada en 4 bytes (esto último no inclusive).
	 */

	//public static recomponerArbol(String filename, HuffmanInfo root);
    //static int recomponerArbol(String filename, HuffmanInfo root);

	public int recomponerArbol(String filename, HuffmanInfo root);

	/** Debe recomponer el archivo original. Recibe el árbol Huffman ya armado y cuántos bytes
	 * deben ser descartados del .huf porque son el encabezado que ya fue utilizado en la función
	 * anterior. Luego de descartar los primeros n bytes estaremos en condiciones de leer 4 bytes
	 * para obtener el size del archivo original que debemos recomponer.
	 */
	public void descomprimirArchivo(HuffmanInfo root,int n,String filename);
}
