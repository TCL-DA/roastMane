package org.glassfish.jersey.process.internal;

import java.util.function.Function;

public interface Stage<DATA> {
   Stage.Continuation<DATA> apply(DATA var1);

   public interface Builder<DATA> {
      Stage.Builder<DATA> to(Function<DATA, DATA> var1);

      Stage.Builder<DATA> to(ChainableStage<DATA> var1);

      Stage<DATA> build();

      Stage<DATA> build(Stage<DATA> var1);
   }

   public static final class Continuation<DATA> {
      private final DATA result;
      private final Stage<DATA> next;

      Continuation(DATA result, Stage<DATA> next) {
         this.result = result;
         this.next = next;
      }

      public static <DATA> Stage.Continuation<DATA> of(DATA result, Stage<DATA> next) {
         return new Stage.Continuation<>(result, next);
      }

      public static <DATA> Stage.Continuation<DATA> of(DATA result) {
         return new Stage.Continuation<>(result, null);
      }

      public DATA result() {
         return this.result;
      }

      public Stage<DATA> next() {
         return this.next;
      }

      public boolean hasNext() {
         return this.next != null;
      }
   }
}
