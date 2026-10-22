package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;

class SoftAnnotatedElementAnnotationInfo {
   private final SoftReference<Annotation[]> elementAnnotationsReference;
   private final SoftReference<Annotation[][]> paramAnnotationsReference;
   private final boolean hasParams;
   private final boolean isConstructor;

   SoftAnnotatedElementAnnotationInfo(Annotation[] elementAnnotation, boolean hasParams, Annotation[][] paramAnnotation, boolean isConstructor) {
      this.elementAnnotationsReference = new SoftReference<>(elementAnnotation);
      this.hasParams = hasParams;
      this.paramAnnotationsReference = new SoftReference<>(paramAnnotation);
      this.isConstructor = isConstructor;
   }

   AnnotatedElementAnnotationInfo harden(AnnotatedElement ae) {
      Annotation[] hardenedElementAnnotations = this.elementAnnotationsReference.get();
      Annotation[][] hardenedParamAnnotations = this.paramAnnotationsReference.get();
      return Utilities.USE_SOFT_REFERENCE && hardenedElementAnnotations != null && hardenedParamAnnotations != null
         ? new AnnotatedElementAnnotationInfo(hardenedElementAnnotations, this.hasParams, hardenedParamAnnotations, this.isConstructor)
         : Utilities.computeAEAI(ae);
   }
}
