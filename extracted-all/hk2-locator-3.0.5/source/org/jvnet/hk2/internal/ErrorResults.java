package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;

public class ErrorResults {
   private final ActiveDescriptor<?> descriptor;
   private final Injectee injectee;
   private final MultiException me;

   ErrorResults(ActiveDescriptor<?> descriptor, Injectee injectee, MultiException me) {
      this.descriptor = descriptor;
      this.injectee = injectee;
      this.me = me;
   }

   ActiveDescriptor<?> getDescriptor() {
      return this.descriptor;
   }

   Injectee getInjectee() {
      return this.injectee;
   }

   MultiException getMe() {
      return this.me;
   }

   @Override
   public String toString() {
      return "ErrorResult(" + this.descriptor + "," + this.injectee + "," + this.me + "," + System.identityHashCode(this) + ")";
   }
}
