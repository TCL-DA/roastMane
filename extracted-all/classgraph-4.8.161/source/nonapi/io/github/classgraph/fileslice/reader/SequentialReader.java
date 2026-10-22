package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;

public interface SequentialReader {
   byte readByte() throws IOException;

   int readUnsignedByte() throws IOException;

   short readShort() throws IOException;

   int readUnsignedShort() throws IOException;

   int readInt() throws IOException;

   long readUnsignedInt() throws IOException;

   long readLong() throws IOException;

   void skip(int var1) throws IOException;

   String readString(int var1, boolean var2, boolean var3) throws IOException;

   String readString(int var1) throws IOException;
}
