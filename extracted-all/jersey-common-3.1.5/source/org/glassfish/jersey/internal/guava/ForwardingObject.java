package org.glassfish.jersey.internal.guava;

abstract class ForwardingObject {
   protected abstract Object delegate();

   @Override
   public String toString() {
      return this.delegate().toString();
   }
}
