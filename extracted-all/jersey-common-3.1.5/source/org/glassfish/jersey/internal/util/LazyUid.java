package org.glassfish.jersey.internal.util;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class LazyUid implements Serializable {
   private static final long serialVersionUID = 4618609413877136867L;
   private final AtomicReference<String> uid = new AtomicReference<>();

   public String value() {
      if (this.uid.get() == null) {
         this.uid.compareAndSet(null, UUID.randomUUID().toString());
      }

      return this.uid.get();
   }

   @Override
   public boolean equals(Object that) {
      if (that == null) {
         return false;
      } else if (this.getClass() != that.getClass()) {
         return false;
      } else {
         LazyUid other = (LazyUid)that;
         return this.value().equals(other.value());
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      return 73 * hash + this.value().hashCode();
   }

   @Override
   public String toString() {
      return this.value();
   }
}
