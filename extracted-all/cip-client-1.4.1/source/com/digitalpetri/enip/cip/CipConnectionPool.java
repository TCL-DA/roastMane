package com.digitalpetri.enip.cip;

import com.digitalpetri.enip.cip.epath.EPathSegment;
import com.digitalpetri.enip.cip.epath.EPath.PaddedEPath;
import com.digitalpetri.enip.cip.epath.LogicalSegment.ClassId;
import com.digitalpetri.enip.cip.epath.LogicalSegment.InstanceId;
import com.digitalpetri.enip.cip.services.ForwardCloseService;
import com.digitalpetri.enip.cip.services.ForwardOpenService;
import com.digitalpetri.enip.cip.services.LargeForwardOpenService;
import com.digitalpetri.enip.cip.services.CipService.PartialResponseException;
import com.digitalpetri.enip.cip.structs.ForwardCloseRequest;
import com.digitalpetri.enip.cip.structs.ForwardCloseResponse;
import com.digitalpetri.enip.cip.structs.ForwardOpenRequest;
import com.digitalpetri.enip.cip.structs.ForwardOpenResponse;
import com.digitalpetri.enip.cip.structs.LargeForwardOpenRequest;
import com.digitalpetri.enip.cip.structs.LargeForwardOpenResponse;
import com.digitalpetri.enip.cip.structs.NetworkConnectionParameters;
import com.digitalpetri.enip.cip.structs.NetworkConnectionParameters.ConnectionType;
import com.digitalpetri.enip.cip.structs.NetworkConnectionParameters.Priority;
import com.digitalpetri.enip.cip.structs.NetworkConnectionParameters.SizeType;
import io.netty.util.ReferenceCountUtil;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class CipConnectionPool {
   private final Logger logger = LoggerFactory.getLogger(this.getClass());
   private final Map<String, String> loggingContext;
   private final Queue<CipConnectionPool.CipConnection> queue = new LinkedList<>();
   private final Queue<CompletableFuture<CipConnectionPool.CipConnection>> waitQueue = new LinkedList<>();
   private final AtomicInteger count = new AtomicInteger(0);
   private final int connectionLimit;
   private final CipConnectionPool.CipConnectionFactory connectionFactory;

   public CipConnectionPool(int connectionLimit, CipClient client, PaddedEPath connectionPath, int connectionSize) {
      this(connectionLimit, new CipConnectionPool.DefaultConnectionFactory(client, connectionPath, connectionSize), client.getConfig().getLoggingContext());
   }

   public CipConnectionPool(int connectionLimit, CipConnectionPool.CipConnectionFactory connectionFactory, Map<String, String> loggingContext) {
      this.connectionLimit = connectionLimit;
      this.connectionFactory = connectionFactory;
      this.loggingContext = loggingContext;
   }

   public synchronized CompletableFuture<CipConnectionPool.CipConnection> acquire() {
      CompletableFuture<CipConnectionPool.CipConnection> future = new CompletableFuture<>();
      this.acquire0().whenComplete((c, ex) -> {
         if (c != null) {
            if (c.isExpired()) {
               this.remove(c);
               this.acquire().whenComplete((c2, ex2) -> {
                  if (c2 != null) {
                     future.complete(c2);
                  } else {
                     future.completeExceptionally(ex2);
                  }
               });
            } else {
               future.complete(c);
            }
         } else {
            future.completeExceptionally(ex);
         }
      });
      return future;
   }

   private synchronized CompletableFuture<CipConnectionPool.CipConnection> acquire0() {
      CompletableFuture<CipConnectionPool.CipConnection> future = new CompletableFuture<>();
      if (!this.queue.isEmpty()) {
         future.complete(this.queue.poll());
      } else {
         this.waitQueue.add(future);
         if (this.count.incrementAndGet() <= this.connectionLimit) {
            CompletableFuture<CipConnectionPool.CipConnection> f = this.connectionFactory.open();
            f.whenComplete((c, ex) -> {
               CompletableFuture<CipConnectionPool.CipConnection> waiter;
               synchronized (this) {
                  waiter = this.waitQueue.poll();
               }

               if (c != null) {
                  if (waiter != null) {
                     waiter.complete(c);
                  } else {
                     this.queue.add(c);
                  }

                  this.loggingContext.forEach(MDC::put);

                  try {
                     this.logger.debug("Forward open succeeded: {}", c);
                  } finally {
                     this.loggingContext.keySet().forEach(MDC::remove);
                  }
               } else {
                  this.count.decrementAndGet();
                  if (waiter != null) {
                     waiter.completeExceptionally(ex);
                  }

                  this.loggingContext.forEach(MDC::put);

                  try {
                     this.logger.debug("Forward open failed: {}", ex.getMessage(), ex);
                  } finally {
                     this.loggingContext.keySet().forEach(MDC::remove);
                  }
               }
            });
         } else {
            this.count.decrementAndGet();
         }
      }

      return future;
   }

   public synchronized void release(CipConnectionPool.CipConnection connection) {
      connection.updateLastUse();
      if (!this.waitQueue.isEmpty()) {
         this.waitQueue.poll().complete(connection);
      } else {
         this.queue.add(connection);
      }
   }

   public synchronized void remove(CipConnectionPool.CipConnection connection) {
      this.connectionFactory.close(connection).thenRun(() -> {
         this.loggingContext.forEach(MDC::put);

         try {
            this.logger.debug("Connection closed: {}", connection);
         } finally {
            this.loggingContext.keySet().forEach(MDC::remove);
         }
      });
      this.queue.remove(connection);
      this.count.decrementAndGet();
      if (!this.waitQueue.isEmpty()) {
         CompletableFuture<CipConnectionPool.CipConnection> next = this.waitQueue.poll();
         this.acquire().whenComplete((c, ex) -> {
            if (c != null) {
               next.complete(c);
            } else {
               next.completeExceptionally(ex);
            }
         });
      }
   }

   public static class CipConnection {
      private volatile long lastUse = System.nanoTime();
      private final long timeoutNanos;
      private final int o2tConnectionId;
      private final int t2oConnectionId;
      private final int serialNumber;
      private final int originatorVendorId;
      private final long originatorSerialNumber;

      public CipConnection(long timeoutNanos, int o2tConnectionId, int t2oConnectionId, int serialNumber, int originatorVendorId, long originatorSerialNumber) {
         this.timeoutNanos = timeoutNanos;
         this.o2tConnectionId = o2tConnectionId;
         this.t2oConnectionId = t2oConnectionId;
         this.serialNumber = serialNumber;
         this.originatorVendorId = originatorVendorId;
         this.originatorSerialNumber = originatorSerialNumber;
      }

      public long getTimeoutNanos() {
         return this.timeoutNanos;
      }

      public int getO2tConnectionId() {
         return this.o2tConnectionId;
      }

      public int getT2oConnectionId() {
         return this.t2oConnectionId;
      }

      public int getSerialNumber() {
         return this.serialNumber;
      }

      public int getOriginatorVendorId() {
         return this.originatorVendorId;
      }

      public long getOriginatorSerialNumber() {
         return this.originatorSerialNumber;
      }

      void updateLastUse() {
         this.lastUse = System.nanoTime();
      }

      boolean isExpired() {
         return System.nanoTime() - this.lastUse > this.timeoutNanos;
      }

      @Override
      public String toString() {
         return "CipConnection{o2tConnectionId="
            + this.o2tConnectionId
            + ", t2oConnectionId="
            + this.t2oConnectionId
            + ", serialNumber="
            + this.serialNumber
            + '}';
      }
   }

   public interface CipConnectionFactory {
      CompletableFuture<CipConnectionPool.CipConnection> open();

      CompletableFuture<ForwardCloseResponse> close(CipConnectionPool.CipConnection var1);
   }

   public static class DefaultConnectionFactory implements CipConnectionPool.CipConnectionFactory {
      private static final Duration DEFAULT_RPI = Duration.ofSeconds(2L);
      private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15L);
      private static final PaddedEPath MESSAGE_ROUTER_CP_PATH = new PaddedEPath(new EPathSegment[]{new ClassId(2), new InstanceId(1)});
      private static final AtomicInteger T2O_CONNECTION_ID = new AtomicInteger(0);
      private final CipClient client;
      private final PaddedEPath connectionPath;
      private final int connectionSize;

      public DefaultConnectionFactory(CipClient client, PaddedEPath connectionPath, int connectionSize) {
         this.client = client;
         this.connectionPath = connectionPath;
         this.connectionSize = connectionSize;
      }

      @Override
      public CompletableFuture<CipConnectionPool.CipConnection> open() {
         return this.connectionSize <= 500 ? this.forwardOpen() : this.largeForwardOpen();
      }

      private CompletableFuture<CipConnectionPool.CipConnection> forwardOpen() {
         CompletableFuture<CipConnectionPool.CipConnection> future = new CompletableFuture<>();
         NetworkConnectionParameters parameters = this.getNetworkConnectionParameters();
         ForwardOpenRequest request = new ForwardOpenRequest(
            DEFAULT_TIMEOUT,
            0,
            T2O_CONNECTION_ID.incrementAndGet(),
            new Random().nextInt(),
            this.client.getConfig().getVendorId(),
            (long)this.client.getConfig().getSerialNumber(),
            1,
            this.connectionPath.append(MESSAGE_ROUTER_CP_PATH),
            DEFAULT_RPI,
            parameters,
            DEFAULT_RPI,
            parameters,
            163
         );
         ForwardOpenService service = new ForwardOpenService(request);
         this.client
            .sendUnconnectedData(service::encodeRequest)
            .whenComplete(
               (b, ex) -> {
                  if (b != null) {
                     try {
                        ForwardOpenResponse response = service.decodeResponse(b);
                        CipConnectionPool.CipConnection connection = new CipConnectionPool.CipConnection(
                           DEFAULT_TIMEOUT.toNanos(),
                           response.getO2tConnectionId(),
                           response.getT2oConnectionId(),
                           response.getConnectionSerialNumber(),
                           response.getOriginatorVendorId(),
                           response.getOriginatorSerialNumber()
                        );
                        ReferenceCountUtil.release(response.getApplicationReply());
                        future.complete(connection);
                     } catch (PartialResponseException | CipResponseException var9) {
                        future.completeExceptionally(var9);
                     } finally {
                        ReferenceCountUtil.release(b);
                     }
                  } else {
                     future.completeExceptionally(ex);
                  }
               }
            );
         return future;
      }

      protected NetworkConnectionParameters getNetworkConnectionParameters() {
         return new NetworkConnectionParameters(this.connectionSize, SizeType.Variable, Priority.Low, ConnectionType.PointToPoint, false);
      }

      private CompletableFuture<CipConnectionPool.CipConnection> largeForwardOpen() {
         CompletableFuture<CipConnectionPool.CipConnection> future = new CompletableFuture<>();
         NetworkConnectionParameters parameters = this.getNetworkConnectionParameters();
         LargeForwardOpenRequest request = new LargeForwardOpenRequest(
            DEFAULT_TIMEOUT,
            0,
            T2O_CONNECTION_ID.incrementAndGet(),
            new Random().nextInt(),
            this.client.getConfig().getVendorId(),
            (long)this.client.getConfig().getSerialNumber(),
            1,
            this.connectionPath.append(MESSAGE_ROUTER_CP_PATH),
            DEFAULT_RPI,
            parameters,
            DEFAULT_RPI,
            parameters,
            163
         );
         LargeForwardOpenService service = new LargeForwardOpenService(request);
         this.client
            .sendUnconnectedData(service::encodeRequest)
            .whenComplete(
               (b, ex) -> {
                  if (b != null) {
                     try {
                        LargeForwardOpenResponse response = service.decodeResponse(b);
                        CipConnectionPool.CipConnection connection = new CipConnectionPool.CipConnection(
                           DEFAULT_TIMEOUT.toNanos(),
                           response.getO2tConnectionId(),
                           response.getT2oConnectionId(),
                           response.getConnectionSerialNumber(),
                           response.getOriginatorVendorId(),
                           response.getOriginatorSerialNumber()
                        );
                        ReferenceCountUtil.release(response.getApplicationReply());
                        future.complete(connection);
                     } catch (PartialResponseException | CipResponseException var9) {
                        future.completeExceptionally(var9);
                     } finally {
                        ReferenceCountUtil.release(b);
                     }
                  } else {
                     future.completeExceptionally(ex);
                  }
               }
            );
         return future;
      }

      @Override
      public CompletableFuture<ForwardCloseResponse> close(CipConnectionPool.CipConnection connection) {
         CompletableFuture<ForwardCloseResponse> future = new CompletableFuture<>();
         ForwardCloseRequest request = new ForwardCloseRequest(
            Duration.ofNanos(connection.getTimeoutNanos()),
            connection.getSerialNumber(),
            connection.getOriginatorVendorId(),
            connection.getOriginatorSerialNumber(),
            this.connectionPath.append(MESSAGE_ROUTER_CP_PATH)
         );
         ForwardCloseService service = new ForwardCloseService(request);
         this.client.sendUnconnectedData(service::encodeRequest).whenComplete((b, ex) -> {
            if (b != null) {
               try {
                  ForwardCloseResponse response = service.decodeResponse(b);
                  future.complete(response);
               } catch (PartialResponseException | CipResponseException var8) {
                  future.completeExceptionally(var8);
               } finally {
                  ReferenceCountUtil.release(b);
               }
            } else {
               future.completeExceptionally(ex);
            }
         });
         return future;
      }
   }
}
