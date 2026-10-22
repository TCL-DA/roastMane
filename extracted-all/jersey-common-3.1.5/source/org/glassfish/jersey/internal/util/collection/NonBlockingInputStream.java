package org.glassfish.jersey.internal.util.collection;

import java.io.IOException;
import java.io.InputStream;

public abstract class NonBlockingInputStream extends InputStream {
   public static final int NOTHING = Integer.MIN_VALUE;

   @Override
   public int available() throws IOException {
      throw new UnsupportedOperationException();
   }

   public abstract int tryRead() throws IOException;

   public abstract int tryRead(byte[] var1) throws IOException;

   public abstract int tryRead(byte[] var1, int var2, int var3) throws IOException;
}
