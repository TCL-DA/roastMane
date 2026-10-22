package org.eclipse.jetty.io;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.util.BufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NegotiatingClientConnection extends AbstractConnection {
   private static final Logger LOG = LoggerFactory.getLogger(NegotiatingClientConnection.class);
   private final SSLEngine engine;
   private final ClientConnectionFactory connectionFactory;
   private final Map<String, Object> context;
   private String protocol;
   private volatile boolean completed;

   protected NegotiatingClientConnection(
      EndPoint endPoint, Executor executor, SSLEngine sslEngine, ClientConnectionFactory connectionFactory, Map<String, Object> context
   ) {
      super(endPoint, executor);
      this.engine = sslEngine;
      this.connectionFactory = connectionFactory;
      this.context = context;
   }

   public SSLEngine getSSLEngine() {
      return this.engine;
   }

   public String getProtocol() {
      return this.protocol;
   }

   protected void completed(String protocol) {
      this.protocol = protocol;
      this.completed = true;
   }

   @Override
   public void onOpen() {
      super.onOpen();

      try {
         this.getEndPoint().flush(BufferUtil.EMPTY_BUFFER);
         if (this.completed) {
            this.replaceConnection();
         } else {
            this.fillInterested();
         }
      } catch (Throwable var2) {
         this.close();
         throw new RuntimeIOException(var2);
      }
   }

   @Override
   public void onFillable() {
      while (true) {
         int filled = this.fill();
         if (!this.completed && filled >= 0) {
            if (filled != 0) {
               continue;
            }

            this.fillInterested();
            break;
         }

         this.replaceConnection();
         break;
      }
   }

   private int fill() {
      try {
         return this.getEndPoint().fill(BufferUtil.EMPTY_BUFFER);
      } catch (IOException var2) {
         LOG.debug("Unable to fill from endpoint", var2);
         this.close();
         return -1;
      }
   }

   private void replaceConnection() {
      EndPoint endPoint = this.getEndPoint();

      try {
         endPoint.upgrade(this.connectionFactory.newConnection(endPoint, this.context));
      } catch (Throwable var3) {
         LOG.debug("Unable to replace connection", var3);
         this.close();
      }
   }

   @Override
   public void close() {
      this.getEndPoint().shutdownOutput();
      super.close();
   }
}
