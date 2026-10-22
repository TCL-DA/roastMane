package jakarta.validation.metadata;

import java.lang.annotation.ElementType;
import java.util.Set;

public interface ElementDescriptor {
   boolean hasConstraints();

   Class<?> getElementClass();

   Set<ConstraintDescriptor<?>> getConstraintDescriptors();

   ElementDescriptor.ConstraintFinder findConstraints();

   public interface ConstraintFinder {
      ElementDescriptor.ConstraintFinder unorderedAndMatchingGroups(Class<?>... var1);

      ElementDescriptor.ConstraintFinder lookingAt(Scope var1);

      ElementDescriptor.ConstraintFinder declaredOn(ElementType... var1);

      Set<ConstraintDescriptor<?>> getConstraintDescriptors();

      boolean hasConstraints();
   }
}
