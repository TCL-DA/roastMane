package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface RandomAccessReader {
   int read(long var1, ByteBuffer var3, int var4, int var5) throws IOException;

   int read(long var1, byte[] var3, int var4, int var5) throws IOException;

   byte readByte(long var1) throws IOException;

   int readUnsignedByte(long var1) throws IOException;

   short readShort(long var1) throws IOException;

   int readUnsignedShort(long var1) throws IOException;

   int readInt(long var1) throws IOException;

   long readUnsignedInt(long var1) throws IOException;

   long readLong(long var1) throws IOException;

   String readString(long var1, int var3, boolean var4, boolean var5) throws IOException;

   String readString(long var1, int var3) throws IOException;
}
