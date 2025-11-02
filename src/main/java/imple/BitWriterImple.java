package imple;

import java.io.IOException;
import java.io.OutputStream;

import huffman.def.BitWriter;

public class BitWriterImple implements BitWriter 
{
    private OutputStream os;
    private int buffer;
    private int size = 0;
    @Override
    public void using(OutputStream os)
    {
        this.os = os;
    }

    @Override
    public void writeBit(int bit) 
    {
        if (this.os == null) {
            throw new IllegalStateException("Debes llamar a 'using' antes de escribir bits.");
        }
        buffer |= (bit << (7-size));
        size++;
        if (size == 8) {
            try {
                os.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer = 0;
            size = 0;
        }
    }
        
    @Override
    public void flush() 
    {
        if(size > 0) {
            try {
                os.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
