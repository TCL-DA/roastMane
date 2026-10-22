package org.eclipse.parsson.api;

public interface BufferPool {
   char[] take();

   void recycle(char[] var1);
}
