package org.glassfish.jersey.internal.guava;

interface AsyncFunction<I, O> {
   ListenableFuture<O> apply(I var1);
}
