package org.eclipse.jetty.server;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.StaticException;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;

public interface HttpStream extends Callback {
   Exception CONTENT_NOT_CONSUMED = new StaticException("Content not consumed");
   String UPGRADE_CONNECTION_ATTRIBUTE = HttpStream.class.getName() + ".upgradeConnection";

   String getId();

   Chunk read();

   void demand();

   void prepareResponse(Mutable var1);

   void send(org.eclipse.jetty.http.MetaData.Request var1, org.eclipse.jetty.http.MetaData.Response var2, boolean var3, ByteBuffer var4, Callback var5);

   default void push(org.eclipse.jetty.http.MetaData.Request resource) {
      throw new UnsupportedOperationException();
   }

   long getIdleTimeout();

   void setIdleTimeout(long var1);

   boolean isCommitted();

   default TunnelSupport getTunnelSupport() {
      return null;
   }

   Throwable consumeAvailable();

   static Throwable consumeAvailable(HttpStream stream, HttpConfiguration httpConfig) {
      int numReads = 0;
      int maxReads = httpConfig.getMaxUnconsumedRequestContentReads();

      while (maxReads < 0 || numReads < maxReads) {
         Chunk content = stream.read();
         numReads++;
         if (content == null) {
            return CONTENT_NOT_CONSUMED;
         }

         content.release();
         if (Chunk.isFailure(content)) {
            return content.getFailure();
         }

         if (content.isLast()) {
            return null;
         }
      }

      return CONTENT_NOT_CONSUMED;
   }

   public static class Wrapper implements HttpStream {
      private final HttpStream _wrapped;

      public Wrapper(HttpStream wrapped) {
         this._wrapped = wrapped;
      }

      public HttpStream getWrapped() {
         return this._wrapped;
      }

      @Override
      public final String getId() {
         return this.getWrapped().getId();
      }

      @Override
      public Chunk read() {
         return this.getWrapped().read();
      }

      @Override
      public void demand() {
         this.getWrapped().demand();
      }

      @Override
      public void prepareResponse(Mutable headers) {
         this.getWrapped().prepareResponse(headers);
      }

      @Override
      public void send(
         org.eclipse.jetty.http.MetaData.Request request,
         org.eclipse.jetty.http.MetaData.Response response,
         boolean last,
         ByteBuffer content,
         Callback callback
      ) {
         this.getWrapped().send(request, response, last, content, callback);
      }

      @Override
      public void push(org.eclipse.jetty.http.MetaData.Request resource) {
         this.getWrapped().push(resource);
      }

      @Override
      public long getIdleTimeout() {
         return this.getWrapped().getIdleTimeout();
      }

      @Override
      public void setIdleTimeout(long idleTimeoutMs) {
         this.getWrapped().setIdleTimeout(idleTimeoutMs);
      }

      @Override
      public final boolean isCommitted() {
         return this.getWrapped().isCommitted();
      }

      @Override
      public TunnelSupport getTunnelSupport() {
         return this.getWrapped().getTunnelSupport();
      }

      @Override
      public final Throwable consumeAvailable() {
         return this.getWrapped().consumeAvailable();
      }

      public void succeeded() {
         this.getWrapped().succeeded();
      }

      public void failed(Throwable x) {
         this.getWrapped().failed(x);
      }

      public InvocationType getInvocationType() {
         return this.getWrapped().getInvocationType();
      }
   }
}
