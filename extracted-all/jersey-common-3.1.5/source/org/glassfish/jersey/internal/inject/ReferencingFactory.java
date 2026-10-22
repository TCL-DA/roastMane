package org.glassfish.jersey.internal.inject;

import jakarta.inject.Provider;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.internal.util.collection.Refs;

public abstract class ReferencingFactory<T> implements Supplier<T> {
   private final Provider<Ref<T>> referenceFactory;

   public ReferencingFactory(Provider<Ref<T>> referenceFactory) {
      this.referenceFactory = referenceFactory;
   }

   @Override
   public T get() {
      return (T)((Ref)this.referenceFactory.get()).get();
   }

   public static <T> Supplier<Ref<T>> referenceFactory() {
      return new ReferencingFactory.EmptyReferenceFactory<>();
   }

   public static <T> Supplier<Ref<T>> referenceFactory(T initialValue) {
      return (Supplier<Ref<T>>)(initialValue == null
         ? new ReferencingFactory.EmptyReferenceFactory<>()
         : new ReferencingFactory.InitializedReferenceFactory<>(initialValue));
   }

   private static class EmptyReferenceFactory<T> implements Supplier<Ref<T>> {
      private EmptyReferenceFactory() {
      }

      public Ref<T> get() {
         return Refs.emptyRef();
      }
   }

   private static class InitializedReferenceFactory<T> implements Supplier<Ref<T>> {
      private final T initialValue;

      public InitializedReferenceFactory(T initialValue) {
         this.initialValue = initialValue;
      }

      public Ref<T> get() {
         return Refs.of(this.initialValue);
      }
   }
}
