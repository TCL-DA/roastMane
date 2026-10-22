package org.glassfish.jersey.server;

import jakarta.inject.Provider;
import jakarta.ws.rs.container.ConnectionCallback;
import jakarta.ws.rs.core.GenericType;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import org.glassfish.jersey.process.internal.RequestContext;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.internal.process.MappableException;

public class ChunkedOutput<T> extends GenericType<T> implements Closeable {
   private static final byte[] ZERO_LENGTH_DELIMITER = new byte[0];
   private final BlockingDeque<T> queue = new LinkedBlockingDeque<>();
   private final byte[] chunkDelimiter;
   private final AtomicBoolean resumed = new AtomicBoolean(false);
   private final Object lock = new Object();
   private boolean flushing = false;
   private boolean touchingEntityStream = false;
   private volatile boolean closed = false;
   private volatile AsyncContext asyncContext;
   private volatile RequestScope requestScope;
   private volatile RequestContext requestScopeContext;
   private volatile ContainerRequest requestContext;
   private volatile ContainerResponse responseContext;
   private volatile ConnectionCallback connectionCallback;

   protected ChunkedOutput() {
      this.chunkDelimiter = ZERO_LENGTH_DELIMITER;
   }

   public ChunkedOutput(Type chunkType) {
      super(chunkType);
      this.chunkDelimiter = ZERO_LENGTH_DELIMITER;
   }

   protected ChunkedOutput(byte[] chunkDelimiter) {
      if (chunkDelimiter.length > 0) {
         this.chunkDelimiter = new byte[chunkDelimiter.length];
         System.arraycopy(chunkDelimiter, 0, this.chunkDelimiter, 0, chunkDelimiter.length);
      } else {
         this.chunkDelimiter = ZERO_LENGTH_DELIMITER;
      }
   }

   protected ChunkedOutput(byte[] chunkDelimiter, Provider<AsyncContext> asyncContextProvider) {
      if (chunkDelimiter.length > 0) {
         this.chunkDelimiter = new byte[chunkDelimiter.length];
         System.arraycopy(chunkDelimiter, 0, this.chunkDelimiter, 0, chunkDelimiter.length);
      } else {
         this.chunkDelimiter = ZERO_LENGTH_DELIMITER;
      }

      this.asyncContext = asyncContextProvider == null ? null : (AsyncContext)asyncContextProvider.get();
   }

   public ChunkedOutput(Type chunkType, byte[] chunkDelimiter) {
      super(chunkType);
      if (chunkDelimiter.length > 0) {
         this.chunkDelimiter = new byte[chunkDelimiter.length];
         System.arraycopy(chunkDelimiter, 0, this.chunkDelimiter, 0, chunkDelimiter.length);
      } else {
         this.chunkDelimiter = ZERO_LENGTH_DELIMITER;
      }
   }

   protected ChunkedOutput(String chunkDelimiter) {
      if (chunkDelimiter.isEmpty()) {
         this.chunkDelimiter = ZERO_LENGTH_DELIMITER;
      } else {
         this.chunkDelimiter = chunkDelimiter.getBytes();
      }
   }

   public ChunkedOutput(Type chunkType, String chunkDelimiter) {
      super(chunkType);
      if (chunkDelimiter.isEmpty()) {
         this.chunkDelimiter = ZERO_LENGTH_DELIMITER;
      } else {
         this.chunkDelimiter = chunkDelimiter.getBytes();
      }
   }

   public void write(T chunk) throws IOException {
      if (this.closed) {
         throw new IOException(LocalizationMessages.CHUNKED_OUTPUT_CLOSED());
      } else {
         if (chunk != null) {
            this.queue.add(chunk);
         }

         this.flushQueue();
      }
   }

   protected void flushQueue() throws IOException {
      if (this.resumed.compareAndSet(false, true) && this.asyncContext != null) {
         this.asyncContext.resume(this);
      }

      if (this.requestScopeContext != null && this.requestContext != null && this.responseContext != null) {
         Exception ex = null;

         try {
            this.requestScope
               .runInScope(
                  this.requestScopeContext,
                  new Callable<Void>() {
                     public Void call() throws IOException {
                        T t;
                        synchronized (ChunkedOutput.this.lock) {
                           if (ChunkedOutput.this.flushing) {
                              return null;
                           }

                           boolean shouldClose = ChunkedOutput.this.closed;
                           t = ChunkedOutput.this.queue.poll();
                           if (t != null || shouldClose) {
                              ChunkedOutput.this.flushing = true;
                           }
                        }

                        while (t != null) {
                           try {
                              synchronized (ChunkedOutput.this.lock) {
                                 ChunkedOutput.this.touchingEntityStream = true;
                              }

                              OutputStream origStream = ChunkedOutput.this.responseContext.getEntityStream();
                              OutputStream writtenStream = ChunkedOutput.this.requestContext
                                 .getWorkers()
                                 .writeTo(
                                    t,
                                    t.getClass(),
                                    ChunkedOutput.this.getType(),
                                    ChunkedOutput.this.responseContext.getEntityAnnotations(),
                                    ChunkedOutput.this.responseContext.getMediaType(),
                                    ChunkedOutput.this.responseContext.getHeaders(),
                                    ChunkedOutput.this.requestContext.getPropertiesDelegate(),
                                    origStream,
                                    Collections.emptyList()
                                 );
                              if (ChunkedOutput.this.chunkDelimiter != ChunkedOutput.ZERO_LENGTH_DELIMITER) {
                                 writtenStream.write(ChunkedOutput.this.chunkDelimiter);
                              }

                              writtenStream.flush();
                              if (origStream != writtenStream) {
                                 ChunkedOutput.this.responseContext.setEntityStream(writtenStream);
                              }
                           } catch (UncheckedIOException | IOException var22) {
                              ChunkedOutput.this.connectionCallback.onDisconnect(ChunkedOutput.this.asyncContext);
                              throw var22;
                           } catch (MappableException var23) {
                              if (var23.getCause() instanceof IOException || var23.getCause() instanceof UncheckedIOException) {
                                 ChunkedOutput.this.connectionCallback.onDisconnect(ChunkedOutput.this.asyncContext);
                              }

                              throw var23;
                           } finally {
                              synchronized (ChunkedOutput.this.lock) {
                                 ChunkedOutput.this.touchingEntityStream = false;
                              }
                           }

                           t = ChunkedOutput.this.queue.poll();
                           if (t == null) {
                              synchronized (ChunkedOutput.this.lock) {
                                 boolean var26 = ChunkedOutput.this.closed;
                                 t = ChunkedOutput.this.queue.poll();
                                 if (t == null) {
                                    ChunkedOutput.this.responseContext.commitStream();
                                    ChunkedOutput.this.flushing = var26;
                                    break;
                                 }
                              }
                           }
                        }

                        return null;
                     }
                  }
               );
         } catch (Exception var17) {
            this.closed = true;
            ex = var17;
            this.onClose(var17);
         } finally {
            if (this.closed) {
               try {
                  synchronized (this.lock) {
                     if (!this.touchingEntityStream) {
                        this.responseContext.close();
                     }
                  }
               } catch (Exception var18) {
                  ex = ex == null ? var18 : ex;
               }

               this.requestScopeContext.release();
               if (ex instanceof IOException) {
                  throw (IOException)ex;
               }

               if (ex instanceof RuntimeException) {
                  throw (RuntimeException)ex;
               }
            }
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.closed = true;
      this.flushQueue();
   }

   public boolean isClosed() {
      return this.closed;
   }

   protected void onClose(Exception e) {
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      return 31 * result + this.queue.hashCode();
   }

   @Override
   public String toString() {
      return "ChunkedOutput<" + this.getType() + ">";
   }

   void setContext(
      RequestScope requestScope,
      RequestContext requestScopeContext,
      ContainerRequest requestContext,
      ContainerResponse responseContext,
      ConnectionCallback connectionCallbackRunner
   ) throws IOException {
      this.requestScope = requestScope;
      this.requestScopeContext = requestScopeContext;
      this.requestContext = requestContext;
      this.responseContext = responseContext;
      this.connectionCallback = connectionCallbackRunner;
      this.flushQueue();
   }
}
