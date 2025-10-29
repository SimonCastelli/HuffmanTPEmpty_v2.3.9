package imple;

import java.io.IOException;
import java.io.InputStream;

import huffman.def.BitReader;

public class BitReaderImple implements BitReader
{
    private InputStream in;
    private int buffer;
    private int aux = 8;
	@Override
	public void using(InputStream is)
	{
        in = is;
	}

	@Override
	public int readBit() throws IOException {
        if (this.in == null) {
            throw new IllegalStateException("Debes llamar a 'using(is)' antes de leer bits.");
        }
        if (aux == 8) {
            buffer = in.read();
            if (buffer == -1) {//final del archivo
                return -1;
            }
            aux = 0;
        }
        int bit = (buffer >> (7 - aux)) & 1;
        aux++;
		return bit;
	}

	@Override
	public void flush()
	{
	}
}
