package org.glassfish.jersey.internal.guava;

public enum RemovalCause {
   EXPLICIT {
   },
   REPLACED {
   },
   COLLECTED {
   },
   EXPIRED {
   },
   SIZE {
   };

   private RemovalCause() {
   }
}
