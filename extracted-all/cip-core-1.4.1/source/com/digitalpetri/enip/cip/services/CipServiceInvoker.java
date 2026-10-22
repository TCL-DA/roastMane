package com.digitalpetri.enip.cip.services;

import java.util.concurrent.CompletableFuture;

public interface CipServiceInvoker {
   <T> CompletableFuture<T> invoke(CipService<T> var1);

   <T> CompletableFuture<T> invoke(CipService<T> var1, int var2);

   <T> CompletableFuture<T> invokeConnected(int var1, CipService<T> var2);

   <T> CompletableFuture<T> invokeUnconnected(CipService<T> var1);

   <T> CompletableFuture<T> invokeUnconnected(CipService<T> var1, int var2);
}
