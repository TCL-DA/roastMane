package org.eclipse.jetty.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.Connection.UpgradeFrom;
import org.eclipse.jetty.io.Connection.UpgradeTo;
import org.eclipse.jetty.util.BufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectorConnectionFactory extends AbstractConnectionFactory implements ConnectionFactory.Detecting {
   private static final Logger LOG = LoggerFactory.getLogger(DetectorConnectionFactory.class);
   private final List<ConnectionFactory.Detecting> _detectingConnectionFactories;

   public DetectorConnectionFactory(ConnectionFactory.Detecting... detectingConnectionFactories) {
      super(toProtocolString(detectingConnectionFactories));
      this._detectingConnectionFactories = Arrays.asList(detectingConnectionFactories);

      for (ConnectionFactory.Detecting detectingConnectionFactory : detectingConnectionFactories) {
         this.addBean(detectingConnectionFactory);
      }
   }

   private static String toProtocolString(ConnectionFactory.Detecting... detectingConnectionFactories) {
      if (detectingConnectionFactories.length == 0) {
         throw new IllegalArgumentException("At least one detecting instance is required");
      } else {
         LinkedHashSet<String> protocols = Arrays.stream(detectingConnectionFactories)
            .map(ConnectionFactory::getProtocol)
            .collect(Collectors.toCollection(LinkedHashSet::new));
         String protocol = protocols.stream().collect(Collectors.joining("|", "[", "]"));
         if (LOG.isDebugEnabled()) {
            LOG.debug("Detector generated protocol name : {}", protocol);
         }

         return protocol;
      }
   }

   @Override
   public ConnectionFactory.Detecting.Detection detect(ByteBuffer buffer) {
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            "Detector {} detecting from buffer {} using {}",
            new Object[]{this.getProtocol(), BufferUtil.toHexString(buffer), this._detectingConnectionFactories}
         );
      }

      boolean needMoreBytes = true;

      for (ConnectionFactory.Detecting detectingConnectionFactory : this._detectingConnectionFactories) {
         ConnectionFactory.Detecting.Detection detection = detectingConnectionFactory.detect(buffer);
         if (detection == ConnectionFactory.Detecting.Detection.RECOGNIZED) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Detector {} recognized bytes using {}", this.getProtocol(), detection);
            }

            return ConnectionFactory.Detecting.Detection.RECOGNIZED;
         }

         needMoreBytes &= detection == ConnectionFactory.Detecting.Detection.NEED_MORE_BYTES;
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Detector {} {}", this.getProtocol(), needMoreBytes ? "requires more bytes" : "failed to recognize bytes");
      }

      return needMoreBytes ? ConnectionFactory.Detecting.Detection.NEED_MORE_BYTES : ConnectionFactory.Detecting.Detection.NOT_RECOGNIZED;
   }

   protected static void upgradeToConnectionFactory(ConnectionFactory connectionFactory, Connector connector, EndPoint endPoint) throws IllegalStateException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Upgrading to connection factory {}", connectionFactory);
      }

      if (connectionFactory == null) {
         throw new IllegalStateException("Cannot upgrade: connection factory must not be null for " + endPoint);
      } else {
         Connection nextConnection = connectionFactory.newConnection(connector, endPoint);
         if (!(nextConnection instanceof UpgradeTo)) {
            throw new IllegalStateException("Cannot upgrade: " + nextConnection + " does not implement " + UpgradeTo.class.getName() + " for " + endPoint);
         } else {
            endPoint.upgrade(nextConnection);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Upgraded to connection factory {} and released buffer", connectionFactory);
            }
         }
      }
   }

   protected void nextProtocol(Connector connector, EndPoint endPoint, ByteBuffer buffer) throws IllegalStateException {
      String nextProtocol = this.findNextProtocol(connector);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Detector {} detection unsuccessful, found '{}' as the next protocol to upgrade to", this.getProtocol(), nextProtocol);
      }

      if (nextProtocol == null) {
         throw new IllegalStateException(
            "Cannot find protocol following '" + this.getProtocol() + "' in connector's protocol list " + connector.getProtocols() + " for " + endPoint
         );
      } else {
         upgradeToConnectionFactory(connector.getConnectionFactory(nextProtocol), connector, endPoint);
      }
   }

   @Override
   public Connection newConnection(Connector connector, EndPoint endPoint) {
      return this.configure(new DetectorConnectionFactory.DetectorConnection(endPoint, connector), connector, endPoint);
   }

   private static class DetectionFailureException extends RuntimeException {
      public DetectionFailureException(Throwable cause) {
         super(cause);
      }
   }

   private class DetectorConnection extends AbstractConnection implements UpgradeFrom, UpgradeTo {
      private final Connector _connector;
      private final RetainableByteBuffer _buffer;

      private DetectorConnection(EndPoint endp, Connector connector) {
         super(endp, connector.getExecutor());
         this._connector = connector;
         this._buffer = connector.getByteBufferPool().acquire(this.getInputBufferSize(), true);
      }

      public void onUpgradeTo(ByteBuffer byteBuffer) {
         if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
            DetectorConnectionFactory.LOG.debug("Detector {} adopting {}", DetectorConnectionFactory.this.getProtocol(), BufferUtil.toDetailString(byteBuffer));
         }

         BufferUtil.append(this._buffer.getByteBuffer(), byteBuffer);
      }

      public ByteBuffer onUpgradeFrom() {
         if (this._buffer.hasRemaining()) {
            ByteBuffer unconsumed = ByteBuffer.allocateDirect(this._buffer.remaining());
            unconsumed.put(this._buffer.getByteBuffer());
            unconsumed.flip();
            if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
               DetectorConnectionFactory.LOG
                  .debug("Detector {} abandoning {}", DetectorConnectionFactory.this.getProtocol(), BufferUtil.toDetailString(unconsumed));
            }

            return unconsumed;
         } else {
            return null;
         }
      }

      public void onOpen() {
         super.onOpen();

         try {
            boolean upgraded = this.detectAndUpgrade();
            if (upgraded) {
               this._buffer.release();
            } else {
               this.fillInterested();
            }
         } catch (Throwable var2) {
            this.releaseAndClose(var2);
         }
      }

      public void onFillable() {
         try {
            ByteBuffer byteBuffer = this._buffer.getByteBuffer();

            while (BufferUtil.space(byteBuffer) > 0) {
               int fill = this.getEndPoint().fill(byteBuffer);
               if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
                  DetectorConnectionFactory.LOG.debug("Detector {} filled buffer with {} bytes", DetectorConnectionFactory.this.getProtocol(), fill);
               }

               if (fill < 0) {
                  this._buffer.release();
                  this.getEndPoint().shutdownOutput();
                  return;
               }

               if (fill == 0) {
                  this.fillInterested();
                  return;
               }

               if (this.detectAndUpgrade()) {
                  this._buffer.release();
                  return;
               }
            }

            DetectorConnectionFactory.LOG
               .warn(
                  "Detector {} failed to detect upgrade target on {} for {}",
                  new Object[]{DetectorConnectionFactory.this.getProtocol(), DetectorConnectionFactory.this._detectingConnectionFactories, this.getEndPoint()}
               );
            this.releaseAndClose(
               new IOException("Detector %s buffer overflow %d".formatted(DetectorConnectionFactory.this.getProtocol(), this._buffer.capacity()))
            );
         } catch (Throwable var3) {
            DetectorConnectionFactory.LOG
               .warn("Detector {} error for {}", new Object[]{DetectorConnectionFactory.this.getProtocol(), this.getEndPoint(), var3});
            this.releaseAndClose(var3);
         }
      }

      private boolean detectAndUpgrade() {
         if (!this._buffer.hasRemaining()) {
            if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
               DetectorConnectionFactory.LOG.debug("Detector {} skipping detection on an empty buffer", DetectorConnectionFactory.this.getProtocol());
            }

            return false;
         } else {
            if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
               DetectorConnectionFactory.LOG
                  .debug("Detector {} performing detection with {} bytes", DetectorConnectionFactory.this.getProtocol(), this._buffer.remaining());
            }

            boolean notRecognized = true;

            for (ConnectionFactory.Detecting detectingConnectionFactory : DetectorConnectionFactory.this._detectingConnectionFactories) {
               ConnectionFactory.Detecting.Detection detection = detectingConnectionFactory.detect(this._buffer.getByteBuffer());
               if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
                  DetectorConnectionFactory.LOG
                     .debug(
                        "Detector {} performed detection from {} with {} which returned {}",
                        new Object[]{DetectorConnectionFactory.this.getProtocol(), this._buffer, detectingConnectionFactory, detection}
                     );
               }

               if (detection == ConnectionFactory.Detecting.Detection.RECOGNIZED) {
                  try {
                     Connection nextConnection = detectingConnectionFactory.newConnection(this._connector, this.getEndPoint());
                     if (!(nextConnection instanceof UpgradeTo)) {
                        throw new IllegalStateException("Cannot upgrade: " + nextConnection + " does not implement " + UpgradeTo.class.getName());
                     }

                     this.getEndPoint().upgrade(nextConnection);
                     if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
                        DetectorConnectionFactory.LOG.debug("Detector {} upgraded to {}", DetectorConnectionFactory.this.getProtocol(), nextConnection);
                     }

                     return true;
                  } catch (DetectorConnectionFactory.DetectionFailureException var6) {
                     if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
                        DetectorConnectionFactory.LOG.debug("Detector {} failed to upgrade, rethrowing", DetectorConnectionFactory.this.getProtocol(), var6);
                     }

                     throw var6;
                  } catch (Throwable var7) {
                     if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
                        DetectorConnectionFactory.LOG.debug("Detector {} failed to upgrade", DetectorConnectionFactory.this.getProtocol());
                     }

                     throw new DetectorConnectionFactory.DetectionFailureException(var7);
                  }
               }

               notRecognized &= detection == ConnectionFactory.Detecting.Detection.NOT_RECOGNIZED;
            }

            if (notRecognized) {
               if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
                  DetectorConnectionFactory.LOG
                     .debug("Detector {} failed to detect a known protocol, falling back to nextProtocol()", DetectorConnectionFactory.this.getProtocol());
               }

               DetectorConnectionFactory.this.nextProtocol(this._connector, this.getEndPoint(), this._buffer.getByteBuffer());
               if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
                  DetectorConnectionFactory.LOG
                     .debug("Detector {} call to nextProtocol() succeeded, assuming upgrade performed", DetectorConnectionFactory.this.getProtocol());
               }

               return true;
            } else {
               return false;
            }
         }
      }

      private void releaseAndClose(Throwable failure) {
         if (DetectorConnectionFactory.LOG.isDebugEnabled()) {
            DetectorConnectionFactory.LOG.debug("Detector {} releasing buffer and closing", DetectorConnectionFactory.this.getProtocol());
         }

         this._buffer.release();
         this.getEndPoint().close(failure);
      }
   }
}
