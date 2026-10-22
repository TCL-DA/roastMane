package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import java.util.EventListener;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;

public interface Response {
   Request getRequest();

   HttpVersion getVersion();

   int getStatus();

   String getReason();

   HttpFields getHeaders();

   HttpFields getTrailers();

   CompletableFuture<Boolean> abort(Throwable var1);

   public interface AsyncContentListener extends Response.ContentSourceListener {
      void onContent(Response var1, Chunk var2, Runnable var3);

      @Override
      default void onContentSource(Response response, Source contentSource) {
         Chunk chunk = contentSource.read();
         Runnable demandCallback = () -> this.onContentSource(response, contentSource);
         if (chunk == null) {
            contentSource.demand(demandCallback);
         } else if (Chunk.isFailure(chunk)) {
            response.abort(chunk.getFailure());
         } else if (chunk.isLast() && !chunk.hasRemaining()) {
            chunk.release();
         } else {
            try {
               this.onContent(response, chunk, () -> contentSource.demand(demandCallback));
               chunk.release();
            } catch (Throwable var6) {
               chunk.release();
               response.abort(var6);
            }
         }
      }
   }

   public interface BeginListener extends Response.ResponseListener {
      void onBegin(Response var1);
   }

   public interface CompleteListener extends Response.ResponseListener {
      void onComplete(Result var1);
   }

   public interface ContentListener extends Response.AsyncContentListener {
      void onContent(Response var1, ByteBuffer var2);

      @Override
      default void onContent(Response response, Chunk chunk, Runnable demander) {
         try {
            this.onContent(response, chunk.getByteBuffer());
            demander.run();
         } catch (Throwable var5) {
            response.abort(var5);
         }
      }
   }

   public interface ContentSourceListener extends Response.ResponseListener {
      void onContentSource(Response var1, Source var2);
   }

   public interface FailureListener extends Response.ResponseListener {
      void onFailure(Response var1, Throwable var2);
   }

   public interface HeaderListener extends Response.ResponseListener {
      boolean onHeader(Response var1, HttpField var2);
   }

   public interface HeadersListener extends Response.ResponseListener {
      void onHeaders(Response var1);
   }

   public interface Listener
      extends Response.BeginListener,
      Response.HeaderListener,
      Response.HeadersListener,
      Response.ContentListener,
      Response.SuccessListener,
      Response.FailureListener,
      Response.CompleteListener {
      @Override
      default void onBegin(Response response) {
      }

      @Override
      default boolean onHeader(Response response, HttpField field) {
         return true;
      }

      @Override
      default void onHeaders(Response response) {
      }

      @Override
      default void onContent(Response response, ByteBuffer content) {
      }

      @Override
      default void onSuccess(Response response) {
      }

      @Override
      default void onFailure(Response response, Throwable failure) {
      }

      @Override
      default void onComplete(Result result) {
      }
   }

   public interface ResponseListener extends EventListener {
   }

   public interface SuccessListener extends Response.ResponseListener {
      void onSuccess(Response var1);
   }
}
