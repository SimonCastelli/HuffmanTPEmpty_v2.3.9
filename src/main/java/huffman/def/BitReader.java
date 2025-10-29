package huffman.def;

import java.io.IOException;
import java.io.InputStream;

public interface BitReader
{
	public void using(InputStream is);
	public int readBit() throws IOException;
	public void flush();
}
