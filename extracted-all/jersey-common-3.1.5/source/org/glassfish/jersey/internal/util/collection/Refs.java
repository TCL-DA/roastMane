package org.glassfish.jersey.internal.util.collection;

public final class Refs {
   private Refs() {
   }

   public static <T> Ref<T> of(T value) {
      return new Refs.DefaultRefImpl<>(value);
   }

   public static <T> Ref<T> emptyRef() {
      return new Refs.DefaultRefImpl<>();
   }

   public static <T> Ref<T> threadSafe() {
      return new Refs.ThreadSafeRefImpl<>();
   }

   public static <T> Ref<T> threadSafe(T value) {
      return new Refs.ThreadSafeRefImpl<>(value);
   }

   public static <T> Ref<T> immutableRef(T value) {
      return new Refs.ImmutableRefImpl<>(value);
   }

   private static final class DefaultRefImpl<T> implements Ref<T> {
      private T reference;

      DefaultRefImpl() {
         this.reference = null;
      }

      DefaultRefImpl(T value) {
         this.reference = value;
      }

      @Override
      public T get() {
         return this.reference;
      }

      @Override
      public void set(T value) throws IllegalStateException {
         this.reference = value;
      }

      @Override
      public String toString() {
         return "DefaultRefImpl{reference=" + this.reference + '}';
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (!(obj instanceof Ref)) {
            return false;
         } else {
            Object otherRef = ((Ref)obj).get();
            T ref = this.reference;
            return ref == otherRef || ref != null && ref.equals(otherRef);
         }
      }

      @Override
      public int hashCode() {
         int hash = 5;
         return 47 * hash + (this.reference != null ? this.reference.hashCode() : 0);
      }
   }

   private static final class ImmutableRefImpl<T> implements Ref<T> {
      private final T reference;

      ImmutableRefImpl(T value) {
         this.reference = value;
      }

      @Override
      public T get() {
         return this.reference;
      }

      @Override
      public void set(T value) throws IllegalStateException {
         throw new IllegalStateException("This implementation of Ref interface is immutable.");
      }

      @Override
      public String toString() {
         return "ImmutableRefImpl{reference=" + this.reference + '}';
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (!(obj instanceof Ref)) {
            return false;
         } else {
            Object otherRef = ((Ref)obj).get();
            return this.reference == otherRef || this.reference != null && this.reference.equals(otherRef);
         }
      }

      @Override
      public int hashCode() {
         int hash = 5;
         return 47 * hash + (this.reference != null ? this.reference.hashCode() : 0);
      }
   }

   private static final class ThreadSafeRefImpl<T> implements Ref<T> {
      private volatile T reference;

      ThreadSafeRefImpl() {
         this.reference = null;
      }

      ThreadSafeRefImpl(T value) {
         this.reference = value;
      }

      @Override
      public T get() {
         return this.reference;
      }

      @Override
      public void set(T value) throws IllegalStateException {
         this.reference = value;
      }

      @Override
      public String toString() {
         return "ThreadSafeRefImpl{reference=" + this.reference + '}';
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (!(obj instanceof Ref)) {
            return false;
         } else {
            Object otherRef = ((Ref)obj).get();
            T localRef = this.reference;
            return localRef == otherRef || localRef != null && localRef.equals(otherRef);
         }
      }

      @Override
      public int hashCode() {
         T localRef = this.reference;
         int hash = 5;
         return 47 * hash + (localRef != null ? localRef.hashCode() : 0);
      }
   }
}
