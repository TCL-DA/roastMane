package org.glassfish.hk2.utilities.reflection;

import java.lang.annotation.Annotation;

public class ScopeInfo {
   private final Annotation scope;
   private final Class<? extends Annotation> annoType;

   public ScopeInfo(Annotation scope, Class<? extends Annotation> annoType) {
      this.scope = scope;
      this.annoType = annoType;
   }

   public Annotation getScope() {
      return this.scope;
   }

   public Class<? extends Annotation> getAnnoType() {
      return this.annoType;
   }
}
