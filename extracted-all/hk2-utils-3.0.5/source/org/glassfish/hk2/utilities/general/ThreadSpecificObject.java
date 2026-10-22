package org.glassfish.hk2.utilities.general;

public class ThreadSpecificObject<T> {
   private final T incoming;
   private final long tid;
   private final int hash;

   public ThreadSpecificObject(T incoming) {
      this.incoming = incoming;
      this.tid = Thread.currentThread().getId();
      int hash = incoming == null ? 0 : incoming.hashCode();
      hash ^= Long.valueOf(this.tid).hashCode();
      this.hash = hash;
   }

   public long getThreadIdentifier() {
      return this.tid;
   }

   public T getIncomingObject() {
      return this.incoming;
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else if (!(o instanceof ThreadSpecificObject)) {
         return false;
      } else {
         ThreadSpecificObject other = (ThreadSpecificObject)o;
         return this.tid != other.tid ? false : GeneralUtilities.safeEquals(this.incoming, other.incoming);
      }
   }
}
