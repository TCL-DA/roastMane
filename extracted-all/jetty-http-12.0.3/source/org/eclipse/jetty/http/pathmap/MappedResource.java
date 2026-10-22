package org.eclipse.jetty.http.pathmap;

import java.util.Map.Entry;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Mapped Resource")
public class MappedResource<E> implements Comparable<MappedResource<E>>, Entry<PathSpec, E> {
   private final PathSpec pathSpec;
   private final E resource;
   private final MatchedResource<E> preMatched;

   public MappedResource(PathSpec pathSpec, E resource) {
      this.pathSpec = pathSpec;
      this.resource = resource;

      this.preMatched = switch (pathSpec.getGroup()) {
         case ROOT -> new MatchedResource(resource, pathSpec, pathSpec.matched("/"));
         case EXACT -> new MatchedResource(resource, pathSpec, pathSpec.matched(pathSpec.getDeclaration()));
         default -> null;
      };
   }

   public MatchedResource<E> getPreMatched() {
      return this.preMatched;
   }

   public int compareTo(MappedResource<E> other) {
      return this.pathSpec.compareTo(other.pathSpec);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         MappedResource<?> other = (MappedResource<?>)obj;
         if (this.pathSpec == null) {
            if (other.pathSpec != null) {
               return false;
            }
         } else if (!this.pathSpec.equals(other.pathSpec)) {
            return false;
         }

         return true;
      }
   }

   public PathSpec getKey() {
      return this.getPathSpec();
   }

   @Override
   public E getValue() {
      return this.getResource();
   }

   @Override
   public E setValue(E value) {
      throw new UnsupportedOperationException();
   }

   @ManagedAttribute(
      value = "path spec",
      readonly = true
   )
   public PathSpec getPathSpec() {
      return this.pathSpec;
   }

   @ManagedAttribute(
      value = "resource",
      readonly = true
   )
   public E getResource() {
      return this.resource;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + (this.pathSpec == null ? 0 : this.pathSpec.hashCode());
   }

   @Override
   public String toString() {
      return String.format("MappedResource[pathSpec=%s,resource=%s]", this.pathSpec, this.resource);
   }
}
