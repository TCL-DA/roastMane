package com.digitalpetri.strictmachine;

import java.util.Objects;
import java.util.StringJoiner;

public interface FsmContext<S, E> {
   S currentState();

   void fireEvent(E var1);

   void shelveEvent(E var1);

   void processShelvedEvents();

   Object get(FsmContext.Key<?> var1);

   Object remove(FsmContext.Key<?> var1);

   void set(FsmContext.Key<?> var1, Object var2);

   long getInstanceId();

   public static final class Key<T> {
      private final String name;
      private final Class<T> type;

      public Key(String name, Class<T> type) {
         this.name = name;
         this.type = type;
      }

      public String name() {
         return this.name;
      }

      public Class<T> type() {
         return this.type;
      }

      public T get(FsmContext<?, ?> context) {
         Object value = context.get(this);
         return value != null ? this.type.cast(value) : null;
      }

      public T remove(FsmContext<?, ?> context) {
         Object value = context.remove(this);
         return value != null ? this.type.cast(value) : null;
      }

      public void set(FsmContext<?, ?> context, T value) {
         context.set(this, value);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            FsmContext.Key<?> key = (FsmContext.Key<?>)o;
            return Objects.equals(this.name, key.name) && Objects.equals(this.type, key.type);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.name, this.type);
      }

      @Override
      public String toString() {
         return new StringJoiner(", ", FsmContext.Key.class.getSimpleName() + "[", "]").add("name='" + this.name + "'").add("type=" + this.type).toString();
      }
   }
}
