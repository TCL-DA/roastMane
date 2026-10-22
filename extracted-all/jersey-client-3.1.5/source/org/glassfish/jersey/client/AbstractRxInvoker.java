package org.glassfish.jersey.client;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.ExecutorService;
import org.glassfish.jersey.client.internal.LocalizationMessages;

public abstract class AbstractRxInvoker<T> extends AbstractNonSyncInvoker<T> implements RxInvoker<T> {
   private final ExecutorService executorService;
   private final SyncInvoker syncInvoker;

   public AbstractRxInvoker(SyncInvoker syncInvoker, ExecutorService executor) {
      if (syncInvoker == null) {
         throw new IllegalArgumentException(LocalizationMessages.NULL_INVOCATION_BUILDER());
      } else {
         this.syncInvoker = syncInvoker;
         this.executorService = executor;
      }
   }

   protected SyncInvoker getSyncInvoker() {
      return this.syncInvoker;
   }

   protected ExecutorService getExecutorService() {
      return this.executorService;
   }

   @Override
   public T method(String name) {
      return this.method(name, Response.class);
   }

   @Override
   public <R> T method(String name, Class<R> responseType) {
      return this.method(name, null, responseType);
   }

   @Override
   public <R> T method(String name, GenericType<R> responseType) {
      return this.method(name, null, responseType);
   }

   @Override
   public T method(String name, Entity<?> entity) {
      return this.method(name, entity, Response.class);
   }
}
