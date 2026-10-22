package retrofit2;

import java.lang.annotation.Annotation;

final class SkipCallbackExecutorImpl implements SkipCallbackExecutor {
   private static final SkipCallbackExecutor INSTANCE = new SkipCallbackExecutorImpl();

   static Annotation[] ensurePresent(Annotation[] annotations) {
      if (Utils.isAnnotationPresent(annotations, SkipCallbackExecutor.class)) {
         return annotations;
      } else {
         Annotation[] newAnnotations = new Annotation[annotations.length + 1];
         newAnnotations[0] = INSTANCE;
         System.arraycopy(annotations, 0, newAnnotations, 1, annotations.length);
         return newAnnotations;
      }
   }

   @Override
   public Class<? extends Annotation> annotationType() {
      return SkipCallbackExecutor.class;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof SkipCallbackExecutor;
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public String toString() {
      return "@" + SkipCallbackExecutor.class.getName() + "()";
   }
}
