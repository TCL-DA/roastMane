package org.apache.commons.io.function;

import java.io.IOException;
import java.util.Objects;

@FunctionalInterface
public interface IOConsumer<T> {
   IOConsumer<?> NOOP_IO_CONSUMER = t -> {
   };

   static <T> IOConsumer<T> noop() {
      return (IOConsumer<T>)NOOP_IO_CONSUMER;
   }

   void accept(T var1) throws IOException;

   default IOConsumer<T> andThen(IOConsumer<? super T> after) {
      Objects.requireNonNull(after, "after");
      return t -> {
         this.accept(t);
         after.accept(t);
      };
   }
}
