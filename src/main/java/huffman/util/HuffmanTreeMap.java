package huffman.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import huffman.def.HuffmanInfo;

public class HuffmanTreeMap
{

	private final Map<Integer,String> codeBySymbol=new HashMap<>();

	public HuffmanTreeMap(HuffmanInfo root)
	{
		if(root==null)
		{
			throw new IllegalArgumentException("root no puede ser null");
		}
		buildCodeTable(root);
	}

	/**
	 * Devuelve el código Huffman del símbolo indicado o null si no existe.
	 * Por ejemplo, get('O') retornaría "01" para COMO COME COCORITO...
	 */
	public String get(int b)
	{
		return codeBySymbol.get(b);
	}

	// --- Internals ---

	private static final class NodePath
	{
		final HuffmanInfo node;
		final String path;

		NodePath(HuffmanInfo node,String path)
		{
			this.node=node;
			this.path=path;
		}
	}

	private void buildCodeTable(HuffmanInfo root)
	{
		Deque<NodePath> stack=new ArrayDeque<>();
		stack.push(new NodePath(root,""));

		while(!stack.isEmpty())
		{
			NodePath np=stack.pop();
			HuffmanInfo p=np.node;
			String path=np.path;

			boolean isLeaf=(p.getLeft()==null&&p.getRight()==null);

			if(isLeaf)
			{
				// Convención para árbol de un solo nodo: asignar "0"
				String code=path.isEmpty()?"0":path;

				// Por símbolo (campo 'c'):
				codeBySymbol.put(p.getC(),code);

				// --- Si preferís indexar por 'n' (lo que llamaste "el n de una
				// hoja"):
				// codeBySymbol.put(p.getN(), code);
				// y entonces get(int b) recibe ese 'n'
			}
			else
			{
				if(p.getRight()!=null)
				{
					stack.push(new NodePath(p.getRight(),path+"1"));
				}
				if(p.getLeft()!=null)
				{
					stack.push(new NodePath(p.getLeft(),path+"0"));
				}
			}
		}
	}

	/** Retorna todos los símbolos/keys que se indexaron (uno por hoja). */
	public int[] keys()
	{
		return codeBySymbol.keySet().stream().mapToInt(Integer::intValue).toArray();
	}

	/** Cantidad de hojas en el árbol (y por lo tanto, cantidad de códigos). */
	public int size()
	{
		return codeBySymbol.size();
	}
}
