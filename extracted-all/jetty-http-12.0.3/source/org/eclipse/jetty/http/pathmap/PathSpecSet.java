package org.eclipse.jetty.http.pathmap;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

public class PathSpecSet extends AbstractSet<String> implements Predicate<String> {
   private final PathMappings<Boolean> specs = new PathMappings<>();

   public boolean test(String s) {
      return this.specs.test(s);
   }

   @Override
   public int size() {
      return this.specs.size();
   }

   private PathSpec asPathSpec(Object o) {
      if (o == null) {
         return null;
      } else {
         return o instanceof PathSpec ? (PathSpec)o : PathSpec.from(Objects.toString(o));
      }
   }

   public boolean add(PathSpec pathSpec) {
      return this.specs.put(pathSpec, Boolean.TRUE) == null;
   }

   public boolean add(String s) {
      return this.add(PathSpec.from(s));
   }

   public boolean remove(PathSpec pathSpec) {
      return this.specs.remove(pathSpec) != null;
   }

   @Override
   public boolean remove(Object o) {
      return this.remove(this.asPathSpec(o));
   }

   @Override
   public void clear() {
      this.specs.reset();
   }

   @Override
   public Iterator<String> iterator() {
      final Iterator<MappedResource<Boolean>> iterator = this.specs.iterator();
      return new Iterator<String>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         public String next() {
            return iterator.next().getPathSpec().getDeclaration();
         }
      };
   }
}
