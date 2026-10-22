package org.glassfish.jersey.inject.hk2;

import jakarta.inject.Provider;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.internal.util.collection.Refs;

public abstract class Hk2ReferencingFactory<T> implements Factory<T> {
   private final Provider<Ref<T>> referenceFactory;

   public Hk2ReferencingFactory(Provider<Ref<T>> referenceFactory) {
      this.referenceFactory = referenceFactory;
   }

   public T provide() {
      return (T)((Ref)this.referenceFactory.get()).get();
   }

   public void dispose(T instance) {
   }

   public static <T> Factory<Ref<T>> referenceFactory() {
      return new Hk2ReferencingFactory.EmptyReferenceFactory<>();
   }

   public static <T> Factory<Ref<T>> referenceFactory(T initialValue) {
      return (Factory<Ref<T>>)(initialValue == null
         ? new Hk2ReferencingFactory.EmptyReferenceFactory<>()
         : new Hk2ReferencingFactory.InitializedReferenceFactory<>(initialValue));
   }

   private static class EmptyReferenceFactory<T> implements Factory<Ref<T>> {
      private EmptyReferenceFactory() {
      }

      public Ref<T> provide() {
         return Refs.emptyRef();
      }

      public void dispose(Ref<T> instance) {
      }
   }

   private static class InitializedReferenceFactory<T> implements Factory<Ref<T>> {
      private final T initialValue;

      public InitializedReferenceFactory(T initialValue) {
         this.initialValue = initialValue;
      }

      public Ref<T> provide() {
         return Refs.of(this.initialValue);
      }

      public void dispose(Ref<T> instance) {
      }
   }
}
