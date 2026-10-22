package org.eclipse.jetty.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class IncludeExcludeSet<T, P> implements Predicate<P> {
   private final Set<T> _includes;
   private final Predicate<P> _includePredicate;
   private final Set<T> _excludes;
   private final Predicate<P> _excludePredicate;

   public IncludeExcludeSet() {
      this(HashSet.class);
   }

   public <SET extends Set<T>> IncludeExcludeSet(Class<SET> setClass) {
      try {
         this._includes = setClass.getDeclaredConstructor().newInstance();
         this._excludes = setClass.getDeclaredConstructor().newInstance();
         if (this._includes instanceof Predicate) {
            this._includePredicate = (Predicate<P>)this._includes;
         } else {
            this._includePredicate = new IncludeExcludeSet.SetContainsPredicate<>((Set<P>)this._includes);
         }

         if (this._excludes instanceof Predicate) {
            this._excludePredicate = (Predicate<P>)this._excludes;
         } else {
            this._excludePredicate = new IncludeExcludeSet.SetContainsPredicate<>((Set<P>)this._excludes);
         }
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   public <SET extends Set<T>> IncludeExcludeSet(Set<T> includeSet, Predicate<P> includePredicate, Set<T> excludeSet, Predicate<P> excludePredicate) {
      Objects.requireNonNull(includeSet, "Include Set");
      Objects.requireNonNull(includePredicate, "Include Predicate");
      Objects.requireNonNull(excludeSet, "Exclude Set");
      Objects.requireNonNull(excludePredicate, "Exclude Predicate");
      this._includes = includeSet;
      this._includePredicate = includePredicate;
      this._excludes = excludeSet;
      this._excludePredicate = excludePredicate;
   }

   public IncludeExcludeSet<T, P> asImmutable() {
      return new IncludeExcludeSet<>(
         Collections.unmodifiableSet(this._includes), this._includePredicate, Collections.unmodifiableSet(this._excludes), this._excludePredicate
      );
   }

   public void include(T element) {
      this._includes.add(element);
   }

   @SafeVarargs
   public final void include(T... element) {
      this._includes.addAll(Arrays.asList(element));
   }

   public void exclude(T element) {
      this._excludes.add(element);
   }

   @SafeVarargs
   public final void exclude(T... element) {
      this._excludes.addAll(Arrays.asList(element));
   }

   @Override
   public boolean test(P t) {
      if (!this._includes.isEmpty() && !this._includePredicate.test(t)) {
         return false;
      } else {
         return this._excludes.isEmpty() ? true : !this._excludePredicate.test(t);
      }
   }

   public Boolean isIncludedAndNotExcluded(P item) {
      if (!this._excludes.isEmpty() && this._excludePredicate.test(item)) {
         return Boolean.FALSE;
      } else {
         return !this._includes.isEmpty() && this._includePredicate.test(item) ? Boolean.TRUE : null;
      }
   }

   public boolean hasIncludes() {
      return !this._includes.isEmpty();
   }

   public boolean hasExcludes() {
      return !this._excludes.isEmpty();
   }

   public int size() {
      return this._includes.size() + this._excludes.size();
   }

   public Set<T> getIncluded() {
      return this._includes;
   }

   public Set<T> getExcluded() {
      return this._excludes;
   }

   public void clear() {
      this._includes.clear();
      this._excludes.clear();
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x{i=%s,ip=%s,e=%s,ep=%s}",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this._includes,
         this._includePredicate == this._includes ? "SELF" : this._includePredicate,
         this._excludes,
         this._excludePredicate == this._excludes ? "SELF" : this._excludePredicate
      );
   }

   public boolean isEmpty() {
      return this._includes.isEmpty() && this._excludes.isEmpty();
   }

   private static record SetContainsPredicate<T>(Set<T> set) implements Predicate<T> {
      @Override
      public boolean test(T item) {
         return this.set.contains(item);
      }

      @Override
      public String toString() {
         return "CONTAINS";
      }
   }
}
