package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;

class AnnotatedElementAnnotationInfo {
   final Annotation[] elementAnnotations;
   final Annotation[][] paramAnnotations;
   final boolean hasParams;
   final boolean isConstructor;

   AnnotatedElementAnnotationInfo(Annotation[] elementAnnotation, boolean hasParams, Annotation[][] paramAnnotation, boolean isConstructor) {
      this.elementAnnotations = elementAnnotation;
      this.hasParams = hasParams;
      this.paramAnnotations = paramAnnotation;
      this.isConstructor = isConstructor;
   }

   SoftAnnotatedElementAnnotationInfo soften() {
      return new SoftAnnotatedElementAnnotationInfo(this.elementAnnotations, this.hasParams, this.paramAnnotations, this.isConstructor);
   }
}
