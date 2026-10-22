package org.eclipse.jetty.server;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpParser.ResponseHandler;
import org.eclipse.jetty.io.ByteArrayEndPoint;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.ByteArrayOutputStream2;
import org.eclipse.jetty.util.thread.Scheduler;

public class LocalConnector extends AbstractConnector {
   private final BlockingQueue<LocalConnector.LocalEndPoint> _connects = new LinkedBlockingQueue<>();

   public LocalConnector(Server server, Executor executor, Scheduler scheduler, ByteBufferPool bufferPool, int acceptors, ConnectionFactory... factories) {
      super(server, executor, scheduler, bufferPool, acceptors, factories);
      this.setIdleTimeout(30000L);
   }

   public LocalConnector(Server server) {
      this(server, null, null, null, -1, new HttpConnectionFactory());
   }

   public LocalConnector(Server server, org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory) {
      this(server, null, null, null, -1, AbstractConnectionFactory.getFactories(sslContextFactory, new HttpConnectionFactory()));
   }

   public LocalConnector(Server server, ConnectionFactory connectionFactory) {
      this(server, null, null, null, -1, connectionFactory);
   }

   public LocalConnector(Server server, ConnectionFactory connectionFactory, org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory) {
      this(server, null, null, null, -1, AbstractConnectionFactory.getFactories(sslContextFactory, connectionFactory));
   }

   @Override
   public Object getTransport() {
      return this;
   }

   public LocalConnector.LocalEndPoint executeRequest(String rawRequest) {
      return this.executeRequest(BufferUtil.toBuffer(rawRequest, StandardCharsets.UTF_8));
   }

   private LocalConnector.LocalEndPoint executeRequest(ByteBuffer rawRequest) {
      if (!this.isStarted()) {
         throw new IllegalStateException("!STARTED");
      } else if (this.isShutdown()) {
         throw new IllegalStateException("Shutdown");
      } else {
         LocalConnector.LocalEndPoint endp = new LocalConnector.LocalEndPoint();
         endp.addInput(rawRequest);
         this._connects.add(endp);
         return endp;
      }
   }

   public LocalConnector.LocalEndPoint connect() {
      LocalConnector.LocalEndPoint endp = new LocalConnector.LocalEndPoint();
      this._connects.add(endp);
      return endp;
   }

   @Override
   protected void accept(int acceptorID) throws InterruptedException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("accepting {}", acceptorID);
      }

      LocalConnector.LocalEndPoint endPoint = this._connects.take();
      Connection connection = this.getDefaultConnectionFactory().newConnection(this, endPoint);
      endPoint.setConnection(connection);
      endPoint.onOpen();
      this.onEndPointOpened(endPoint);
      connection.onOpen();
   }

   public ByteBuffer getResponse(ByteBuffer requestsBuffer) throws Exception {
      return this.getResponse(requestsBuffer, false, 10L, TimeUnit.SECONDS);
   }

   public ByteBuffer getResponse(ByteBuffer requestBuffer, long time, TimeUnit unit) throws Exception {
      boolean head = BufferUtil.toString(requestBuffer).toLowerCase().startsWith("head ");
      if (LOG.isDebugEnabled()) {
         LOG.debug("requests {}", BufferUtil.toUTF8String(requestBuffer));
      }

      LocalConnector.LocalEndPoint endp = this.executeRequest(requestBuffer);
      return endp.waitForResponse(head, time, unit);
   }

   public ByteBuffer getResponse(ByteBuffer requestBuffer, boolean head, long time, TimeUnit unit) throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("requests {}", BufferUtil.toUTF8String(requestBuffer));
      }

      LocalConnector.LocalEndPoint endp = this.executeRequest(requestBuffer);
      return endp.waitForResponse(head, time, unit);
   }

   public String getResponse(String rawRequest) throws Exception {
      return this.getResponse(rawRequest, false, 30L, TimeUnit.SECONDS);
   }

   public String getResponse(String rawRequest, long time, TimeUnit unit) throws Exception {
      boolean head = rawRequest.toLowerCase().startsWith("head ");
      ByteBuffer requestsBuffer = BufferUtil.toBuffer(rawRequest, StandardCharsets.ISO_8859_1);
      if (LOG.isDebugEnabled()) {
         LOG.debug("request {}", BufferUtil.toUTF8String(requestsBuffer));
      }

      LocalConnector.LocalEndPoint endp = this.executeRequest(requestsBuffer);
      return BufferUtil.toString(endp.waitForResponse(head, time, unit), StandardCharsets.ISO_8859_1);
   }

   public String getResponse(String rawRequest, boolean head, long time, TimeUnit unit) throws Exception {
      ByteBuffer requestsBuffer = BufferUtil.toBuffer(rawRequest, StandardCharsets.ISO_8859_1);
      if (LOG.isDebugEnabled()) {
         LOG.debug("request {}", BufferUtil.toUTF8String(requestsBuffer));
      }

      LocalConnector.LocalEndPoint endp = this.executeRequest(requestsBuffer);
      return BufferUtil.toString(endp.waitForResponse(head, time, unit), StandardCharsets.ISO_8859_1);
   }

   public class LocalEndPoint extends ByteArrayEndPoint {
      private final CountDownLatch _closed = new CountDownLatch(1);
      private ByteBuffer _responseData;

      public LocalEndPoint() {
         super(LocalConnector.this.getScheduler(), LocalConnector.this.getIdleTimeout());
         this.setGrowOutput(true);
      }

      protected void execute(Runnable task) {
         LocalConnector.this.getExecutor().execute(task);
      }

      public void onClose(Throwable cause) {
         Connection connection = this.getConnection();
         if (connection != null) {
            connection.onClose(cause);
         }

         LocalConnector.this.onEndPointClosed(this);
         super.onClose(cause);
         this._closed.countDown();
      }

      public void doShutdownOutput() {
         super.shutdownOutput();
         this.close();
      }

      public void waitUntilClosed() {
         while (this.isOpen()) {
            try {
               if (this._closed.await(10L, TimeUnit.SECONDS)) {
                  continue;
               }
               break;
            } catch (Exception var2) {
               AbstractConnector.LOG.warn("Close wait failed", var2);
            }
         }
      }

      public void waitUntilClosedOrIdleFor(long idleFor, TimeUnit units) {
         Thread.yield();
         int size = this.getOutput().remaining();

         while (this.isOpen()) {
            try {
               if (!this._closed.await(idleFor, units)) {
                  if (size == this.getOutput().remaining()) {
                     if (AbstractConnector.LOG.isDebugEnabled()) {
                        AbstractConnector.LOG.debug("idle for {} {}", idleFor, units);
                     }

                     return;
                  }

                  size = this.getOutput().remaining();
               }
            } catch (Exception var6) {
               AbstractConnector.LOG.warn("Close wait failed", var6);
            }
         }
      }

      public ByteBuffer getResponseData() {
         return this._responseData;
      }

      public String getResponse() throws Exception {
         return this.getResponse(false, 30L, TimeUnit.SECONDS);
      }

      public String getResponse(boolean head, long time, TimeUnit unit) throws Exception {
         ByteBuffer response = this.waitForResponse(head, time, unit);
         return response != null ? BufferUtil.toString(response) : null;
      }

      public ByteBuffer waitForResponse(boolean head, long time, TimeUnit unit) throws Exception {
         return this.waitForResponse(head, time, unit, i -> {
         });
      }

      public ByteBuffer waitForResponse(boolean head, long time, TimeUnit unit, final Consumer<Integer> statusConsumer) throws Exception {
         ResponseHandler handler = new ResponseHandler() {
            public void parsedHeader(HttpField field) {
            }

            public boolean contentComplete() {
               return false;
            }

            public boolean messageComplete() {
               return true;
            }

            public boolean headerComplete() {
               return false;
            }

            public void earlyEOF() {
            }

            public boolean content(ByteBuffer item) {
               return false;
            }

            public void startResponse(HttpVersion version, int status, String reason) {
               statusConsumer.accept(status);
            }
         };
         HttpParser parser = new HttpParser(handler);
         parser.setHeadResponse(head);
         ByteArrayOutputStream2 bout = new ByteArrayOutputStream2();

         Object var17;
         label76: {
            Object var12;
            label75: {
               ByteBuffer chunk;
               label74: {
                  try {
                     label65:
                     while (true) {
                        if (BufferUtil.hasContent(this._responseData)) {
                           chunk = this._responseData;
                        } else {
                           chunk = this.waitForOutput(time, unit);
                           if (BufferUtil.isEmpty(chunk)) {
                              if (this.isOpen() && !this.isOutputShutdown() && !LocalConnector.this.isShutdown()) {
                                 var17 = null;
                                 break label76;
                              }

                              parser.atEOF();
                              parser.parseNext(BufferUtil.EMPTY_BUFFER);
                              break;
                           }
                        }

                        while (BufferUtil.hasContent(chunk)) {
                           int pos = chunk.position();
                           boolean complete = parser.parseNext(chunk);
                           if (chunk.position() == pos) {
                              if (BufferUtil.isEmpty(chunk)) {
                                 break;
                              }

                              var12 = null;
                              break label75;
                           }

                           bout.write(chunk.array(), chunk.arrayOffset() + pos, chunk.position() - pos);
                           if (complete) {
                              if (BufferUtil.hasContent(chunk)) {
                                 this._responseData = chunk;
                              }
                              break label65;
                           }
                        }
                     }

                     if (bout.getCount() == 0 && this.isOutputShutdown()) {
                        chunk = null;
                        break label74;
                     }

                     chunk = ByteBuffer.wrap(bout.getBuf(), 0, bout.getCount());
                  } catch (Throwable var14) {
                     try {
                        bout.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }

                     throw var14;
                  }

                  bout.close();
                  return chunk;
               }

               bout.close();
               return chunk;
            }

            bout.close();
            return (ByteBuffer)var12;
         }

         bout.close();
         return (ByteBuffer)var17;
      }
   }
}
