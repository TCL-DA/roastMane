package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.util.BufferUtil;

public class Trailers implements Chunk {
   private final HttpFields trailers;

   public Trailers(HttpFields trailers) {
      this.trailers = trailers;
   }

   public ByteBuffer getByteBuffer() {
      return BufferUtil.EMPTY_BUFFER;
   }

   public boolean isLast() {
      return true;
   }

   public HttpFields getTrailers() {
      return this.trailers;
   }
}
