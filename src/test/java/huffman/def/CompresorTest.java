package huffman.def;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.net.URL;

import org.junit.jupiter.api.Test;

import imple.Factory;

public class CompresorTest
{
	@Test
	public void testContarOcurrencias() throws Exception
	{
		// El fixture vive en src/test/resources para poder ubicarlo vía el
		// classpath sin depender del directorio de trabajo del proceso.
		URL resource = getClass().getClassLoader().getResource("huffman/def/beegees.txt");
		assertNotNull(resource,"No se encontró el fixture huffman/def/beegees.txt en el classpath de test");

		String path = new File(resource.toURI()).getAbsolutePath();

		Compresor c = Factory.getCompresor();
		HuffmanTable arr[] = c.contarOcurrencias(path);

		assertEquals(4,arr['A'].getN());
		assertEquals(3,arr['B'].getN());
		assertEquals(2,arr['C'].getN());
		assertEquals(1,arr['D'].getN());
	}
}
