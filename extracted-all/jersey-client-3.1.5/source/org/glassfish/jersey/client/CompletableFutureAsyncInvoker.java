package org.glassfish.jersey.client;

import jakarta.ws.rs.client.AsyncInvoker;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;

abstract class CompletableFutureAsyncInvoker extends AbstractNonSyncInvoker<CompletableFuture> implements AsyncInvoker {
   public <R> CompletableFuture<R> get(InvocationCallback<R> callback) {
      return this.method("GET", callback);
   }

   public <R> CompletableFuture<R> put(Entity<?> entity, InvocationCallback<R> callback) {
      return this.method("PUT", entity, callback);
   }

   public <R> CompletableFuture<R> post(Entity<?> entity, InvocationCallback<R> callback) {
      return this.method("POST", entity, callback);
   }

   public <R> CompletableFuture<R> delete(InvocationCallback<R> callback) {
      return this.method("DELETE", callback);
   }

   public CompletableFuture<Response> head(InvocationCallback<Response> callback) {
      return this.method("HEAD", callback);
   }

   public <R> CompletableFuture<R> options(InvocationCallback<R> callback) {
      return this.method("OPTIONS", callback);
   }

   public <R> CompletableFuture<R> trace(InvocationCallback<R> callback) {
      return this.method("TRACE", callback);
   }

   public abstract <R> CompletableFuture<R> method(String var1, InvocationCallback<R> var2);

   public abstract <R> CompletableFuture<R> method(String var1, Entity<?> var2, InvocationCallback<R> var3);

   public abstract <R> CompletableFuture method(String var1, Entity<?> var2, Class<R> var3);

   public abstract <R> CompletableFuture method(String var1, Entity<?> var2, GenericType<R> var3);
}
