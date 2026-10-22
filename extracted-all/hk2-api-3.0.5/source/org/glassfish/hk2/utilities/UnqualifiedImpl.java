package org.glassfish.hk2.utilities;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.Unqualified;

public class UnqualifiedImpl extends AnnotationLiteral<Unqualified> implements Unqualified {
   private static final long serialVersionUID = 7982327982416740739L;
   private final Class<? extends Annotation>[] value;

   public UnqualifiedImpl(Class<? extends Annotation>... value) {
      this.value = Arrays.copyOf(value, value.length);
   }

   @Override
   public Class<? extends Annotation>[] value() {
      return Arrays.copyOf(this.value, this.value.length);
   }

   @Override
   public String toString() {
      return "UnqualifiedImpl(" + Arrays.toString((Object[])this.value) + "," + System.identityHashCode(this) + ")";
   }
}
