package org.eclipse.jetty.websocket.core;

import java.util.concurrent.Executor;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.compression.DeflaterPool;
import org.eclipse.jetty.util.compression.InflaterPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class WebSocketComponents extends ContainerLifeCycle {
   private final DecoratedObjectFactory _objectFactory;
   private final WebSocketExtensionRegistry _extensionRegistry;
   private final Executor _executor;
   private final ByteBufferPool _bufferPool;
   private final InflaterPool _inflaterPool;
   private final DeflaterPool _deflaterPool;

   public WebSocketComponents() {
      this(null, null, null, null, null);
   }

   public WebSocketComponents(
      WebSocketExtensionRegistry extensionRegistry,
      DecoratedObjectFactory objectFactory,
      ByteBufferPool bufferPool,
      InflaterPool inflaterPool,
      DeflaterPool deflaterPool
   ) {
      this(extensionRegistry, objectFactory, bufferPool, inflaterPool, deflaterPool, null);
   }

   public WebSocketComponents(
      WebSocketExtensionRegistry extensionRegistry,
      DecoratedObjectFactory objectFactory,
      ByteBufferPool bufferPool,
      InflaterPool inflaterPool,
      DeflaterPool deflaterPool,
      Executor executor
   ) {
      this._extensionRegistry = extensionRegistry == null ? new WebSocketExtensionRegistry() : extensionRegistry;
      this._objectFactory = objectFactory == null ? new DecoratedObjectFactory() : objectFactory;
      this._bufferPool = (ByteBufferPool)(bufferPool == null ? new ArrayByteBufferPool() : bufferPool);
      this._inflaterPool = inflaterPool == null ? new InflaterPool(1024, true) : inflaterPool;
      this._deflaterPool = deflaterPool == null ? new DeflaterPool(1024, -1, true) : deflaterPool;
      if (executor == null) {
         QueuedThreadPool threadPool = new QueuedThreadPool();
         threadPool.setName("WebSocket@" + this.hashCode());
         this._executor = threadPool;
      } else {
         this._executor = executor;
      }

      this.addBean(this._inflaterPool);
      this.addBean(this._deflaterPool);
      this.addBean(this._bufferPool);
      this.addBean(this._extensionRegistry);
      this.addBean(this._objectFactory);
      this.addBean(this._executor);
   }

   public ByteBufferPool getByteBufferPool() {
      return this._bufferPool;
   }

   public Executor getExecutor() {
      return this._executor;
   }

   public WebSocketExtensionRegistry getExtensionRegistry() {
      return this._extensionRegistry;
   }

   public DecoratedObjectFactory getObjectFactory() {
      return this._objectFactory;
   }

   public InflaterPool getInflaterPool() {
      return this._inflaterPool;
   }

   public DeflaterPool getDeflaterPool() {
      return this._deflaterPool;
   }
}
