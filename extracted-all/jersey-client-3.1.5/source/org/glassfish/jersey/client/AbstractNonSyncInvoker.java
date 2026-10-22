package org.glassfish.jersey.client;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

abstract class AbstractNonSyncInvoker<T> {
   public T get() {
      return this.method("GET");
   }

   public <R> T get(Class<R> responseType) {
      return this.method("GET", responseType);
   }

   public <R> T get(GenericType<R> responseType) {
      return this.method("GET", responseType);
   }

   public T put(Entity<?> entity) {
      return this.method("PUT", entity);
   }

   public <R> T put(Entity<?> entity, Class<R> clazz) {
      return this.method("PUT", entity, clazz);
   }

   public <R> T put(Entity<?> entity, GenericType<R> type) {
      return this.method("PUT", entity, type);
   }

   public T post(Entity<?> entity) {
      return this.method("POST", entity);
   }

   public <R> T post(Entity<?> entity, Class<R> clazz) {
      return this.method("POST", entity, clazz);
   }

   public <R> T post(Entity<?> entity, GenericType<R> type) {
      return this.method("POST", entity, type);
   }

   public T delete() {
      return this.method("DELETE");
   }

   public <R> T delete(Class<R> responseType) {
      return this.method("DELETE", responseType);
   }

   public <R> T delete(GenericType<R> responseType) {
      return this.method("DELETE", responseType);
   }

   public T head() {
      return this.method("HEAD");
   }

   public T options() {
      return this.method("OPTIONS");
   }

   public <R> T options(Class<R> responseType) {
      return this.method("OPTIONS", responseType);
   }

   public <R> T options(GenericType<R> responseType) {
      return this.method("OPTIONS", responseType);
   }

   public T trace() {
      return this.method("TRACE");
   }

   public <R> T trace(Class<R> responseType) {
      return this.method("TRACE", responseType);
   }

   public <R> T trace(GenericType<R> responseType) {
      return this.method("TRACE", responseType);
   }

   public abstract T method(String var1);

   public abstract <R> T method(String var1, Class<R> var2);

   public abstract <R> T method(String var1, GenericType<R> var2);

   public abstract T method(String var1, Entity<?> var2);

   public abstract <R> T method(String var1, Entity<?> var2, Class<R> var3);

   public abstract <R> T method(String var1, Entity<?> var2, GenericType<R> var3);
}
