package org.glassfish.jersey.internal.guava;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Predicates {
   private static final Joiner COMMA_JOINER = Joiner.on();

   private Predicates() {
   }

   public static <T> Predicate<T> alwaysTrue() {
      return Predicates.ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
   }

   private static <T> Predicate<T> isNull() {
      return Predicates.ObjectPredicate.IS_NULL.withNarrowedType();
   }

   public static <T> Predicate<T> not(Predicate<T> predicate) {
      return new Predicates.NotPredicate<>(predicate);
   }

   public static <T> Predicate<T> equalTo(T target) {
      return (Predicate<T>)(target == null ? isNull() : new Predicates.IsEqualToPredicate<>(target));
   }

   public static <T> Predicate<T> in(Collection<? extends T> target) {
      return new Predicates.InPredicate<>(target);
   }

   public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function) {
      return new Predicates.CompositionPredicate<>(predicate, function);
   }

   private static class CompositionPredicate<A, B> implements Predicate<A>, Serializable {
      private static final long serialVersionUID = 0L;
      final Predicate<B> p;
      final Function<A, ? extends B> f;

      private CompositionPredicate(Predicate<B> p, Function<A, ? extends B> f) {
         this.p = Preconditions.checkNotNull(p);
         this.f = Preconditions.checkNotNull(f);
      }

      @Override
      public boolean test(A a) {
         return this.p.test((B)this.f.apply(a));
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof Predicates.CompositionPredicate)) {
            return false;
         } else {
            Predicates.CompositionPredicate<?, ?> that = (Predicates.CompositionPredicate<?, ?>)obj;
            return this.f.equals(that.f) && this.p.equals(that.p);
         }
      }

      @Override
      public int hashCode() {
         return this.f.hashCode() ^ this.p.hashCode();
      }

      @Override
      public String toString() {
         return this.p.toString() + "(" + this.f.toString() + ")";
      }
   }

   private static class InPredicate<T> implements Predicate<T>, Serializable {
      private static final long serialVersionUID = 0L;
      private final Collection<?> target;

      private InPredicate(Collection<?> target) {
         this.target = Preconditions.checkNotNull(target);
      }

      @Override
      public boolean test(T t) {
         try {
            return this.target.contains(t);
         } catch (NullPointerException var3) {
            return false;
         } catch (ClassCastException var4) {
            return false;
         }
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof Predicates.InPredicate) {
            Predicates.InPredicate<?> that = (Predicates.InPredicate<?>)obj;
            return this.target.equals(that.target);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return this.target.hashCode();
      }

      @Override
      public String toString() {
         return "Predicates.in(" + this.target + ")";
      }
   }

   private static class IsEqualToPredicate<T> implements Predicate<T>, Serializable {
      private static final long serialVersionUID = 0L;
      private final T target;

      private IsEqualToPredicate(T target) {
         this.target = target;
      }

      @Override
      public boolean test(T t) {
         return this.target.equals(t);
      }

      @Override
      public int hashCode() {
         return this.target.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof Predicates.IsEqualToPredicate) {
            Predicates.IsEqualToPredicate<?> that = (Predicates.IsEqualToPredicate<?>)obj;
            return this.target.equals(that.target);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         return "Predicates.equalTo(" + this.target + ")";
      }
   }

   private static class NotPredicate<T> implements Predicate<T>, Serializable {
      private static final long serialVersionUID = 0L;
      final Predicate<T> predicate;

      NotPredicate(Predicate<T> predicate) {
         this.predicate = Preconditions.checkNotNull(predicate);
      }

      @Override
      public boolean test(T t) {
         return !this.predicate.test(t);
      }

      @Override
      public int hashCode() {
         return ~this.predicate.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof Predicates.NotPredicate) {
            Predicates.NotPredicate<?> that = (Predicates.NotPredicate<?>)obj;
            return this.predicate.equals(that.predicate);
         } else {
            return false;
         }
      }

      @Override
      public String toString() {
         return "Predicates.not(" + this.predicate.toString() + ")";
      }
   }

   static enum ObjectPredicate implements Predicate<Object> {
      ALWAYS_TRUE {
         @Override
         public boolean test(Object o) {
            return true;
         }

         @Override
         public String toString() {
            return "Predicates.alwaysTrue()";
         }
      },
      IS_NULL {
         @Override
         public boolean test(Object o) {
            return o == null;
         }

         @Override
         public String toString() {
            return "Predicates.isNull()";
         }
      };

      private ObjectPredicate() {
      }

      <T> Predicate<T> withNarrowedType() {
         return this;
      }
   }
}
