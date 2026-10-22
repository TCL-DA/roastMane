package org.glassfish.jersey.internal.util.collection;

public final class Values {
   private static final LazyValue EMPTY = new LazyValue() {
      @Override
      public Object get() {
         return null;
      }

      @Override
      public boolean isInitialized() {
         return true;
      }
   };
   private static final LazyUnsafeValue EMPTY_UNSAFE = new LazyUnsafeValue() {
      @Override
      public Object get() {
         return null;
      }

      @Override
      public boolean isInitialized() {
         return true;
      }
   };

   private Values() {
   }

   public static <T> Value<T> empty() {
      return EMPTY;
   }

   public static <T, E extends Throwable> UnsafeValue<T, E> emptyUnsafe() {
      return EMPTY_UNSAFE;
   }

   public static <T> Value<T> of(T value) {
      return (Value<T>)(value == null ? empty() : new Values.InstanceValue<>(value));
   }

   public static <T, E extends Throwable> UnsafeValue<T, E> unsafe(T value) {
      return (UnsafeValue<T, E>)(value == null ? emptyUnsafe() : new Values.InstanceUnsafeValue<>(value));
   }

   public static <T, E extends Throwable> UnsafeValue<T, E> throwing(E throwable) {
      if (throwable == null) {
         throw new NullPointerException("Supplied throwable ");
      } else {
         return new Values.ExceptionValue<>(throwable);
      }
   }

   public static <T> LazyValue<T> lazy(Value<T> delegate) {
      return (LazyValue<T>)(delegate == null ? EMPTY : new Values.LazyValueImpl<>(delegate));
   }

   public static <T> Value<T> eager(Value<T> delegate) {
      return (Value<T>)(delegate == null ? empty() : new Values.EagerValue<>(delegate));
   }

   public static <T, E extends Throwable> LazyUnsafeValue<T, E> lazy(UnsafeValue<T, E> delegate) {
      return (LazyUnsafeValue<T, E>)(delegate == null ? EMPTY_UNSAFE : new Values.LazyUnsafeValueImpl<>(delegate));
   }

   private static class EagerValue<T> implements Value<T> {
      private final T result;

      private EagerValue(Value<T> value) {
         this.result = value.get();
      }

      @Override
      public T get() {
         return this.result;
      }
   }

   private static class ExceptionValue<T, E extends Throwable> implements UnsafeValue<T, E> {
      private final E throwable;

      public ExceptionValue(E throwable) {
         this.throwable = throwable;
      }

      @Override
      public T get() throws E {
         throw this.throwable;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return o != null && this.getClass() == o.getClass() ? this.throwable.equals(((Values.ExceptionValue)o).throwable) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.throwable != null ? this.throwable.hashCode() : 0;
      }

      @Override
      public String toString() {
         return "ExceptionValue{throwable=" + this.throwable + '}';
      }
   }

   private static class InstanceUnsafeValue<T, E extends Throwable> implements UnsafeValue<T, E> {
      private final T value;

      public InstanceUnsafeValue(T value) {
         this.value = value;
      }

      @Override
      public T get() {
         return this.value;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return o != null && this.getClass() == o.getClass() ? this.value.equals(((Values.InstanceUnsafeValue)o).value) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.value != null ? this.value.hashCode() : 0;
      }

      @Override
      public String toString() {
         return "InstanceUnsafeValue{value=" + this.value + '}';
      }
   }

   private static class InstanceValue<T> implements Value<T> {
      private final T value;

      public InstanceValue(T value) {
         this.value = value;
      }

      @Override
      public T get() {
         return this.value;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return o != null && this.getClass() == o.getClass() ? this.value.equals(((Values.InstanceValue)o).value) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.value != null ? this.value.hashCode() : 0;
      }

      @Override
      public String toString() {
         return "InstanceValue{value=" + this.value + '}';
      }
   }

   private static class LazyUnsafeValueImpl<T, E extends Throwable> implements LazyUnsafeValue<T, E> {
      private final Object lock;
      private final UnsafeValue<T, E> delegate;
      private volatile UnsafeValue<T, E> value;

      public LazyUnsafeValueImpl(UnsafeValue<T, E> delegate) {
         this.delegate = delegate;
         this.lock = new Object();
      }

      @Override
      public T get() throws E {
         UnsafeValue<T, E> result = this.value;
         if (result == null) {
            synchronized (this.lock) {
               result = this.value;
               if (result == null) {
                  try {
                     result = Values.unsafe(this.delegate.get());
                  } catch (Throwable var5) {
                     result = Values.throwing((E)var5);
                  }

                  this.value = result;
               }
            }
         }

         return result.get();
      }

      @Override
      public boolean isInitialized() {
         return this.value != null;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return o != null && this.getClass() == o.getClass() ? this.delegate.equals(((Values.LazyUnsafeValueImpl)o).delegate) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.delegate != null ? this.delegate.hashCode() : 0;
      }

      @Override
      public String toString() {
         return "LazyValue{delegate=" + this.delegate.toString() + '}';
      }
   }

   private static class LazyValueImpl<T> implements LazyValue<T> {
      private final Object lock;
      private final Value<T> delegate;
      private volatile Value<T> value;

      public LazyValueImpl(Value<T> delegate) {
         this.delegate = delegate;
         this.lock = new Object();
      }

      @Override
      public T get() {
         Value<T> result = this.value;
         if (result == null) {
            synchronized (this.lock) {
               result = this.value;
               if (result == null) {
                  this.value = result = Values.of(this.delegate.get());
               }
            }
         }

         return result.get();
      }

      @Override
      public boolean isInitialized() {
         return this.value != null;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return o != null && this.getClass() == o.getClass() ? this.delegate.equals(((Values.LazyValueImpl)o).delegate) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.delegate != null ? this.delegate.hashCode() : 0;
      }

      @Override
      public String toString() {
         return "LazyValue{delegate=" + this.delegate.toString() + '}';
      }
   }
}
