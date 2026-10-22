package org.glassfish.jersey.process.internal;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Function;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.process.Inflector;

public final class Stages {
   private static final ChainableStage IDENTITY = new AbstractChainableStage() {
      @Override
      public Stage.Continuation apply(Object o) {
         return Stage.Continuation.of(o, this.getDefaultNext());
      }
   };

   private Stages() {
   }

   public static <DATA> ChainableStage<DATA> identity() {
      return IDENTITY;
   }

   public static <DATA, RESULT> Stage<DATA> asStage(Inflector<DATA, RESULT> inflector) {
      return new Stages.InflectingStage<>(inflector);
   }

   public static <DATA, RESULT, T extends Inflector<DATA, RESULT>> T extractInflector(Object stage) {
      return (T)(stage instanceof Inflecting ? ((Inflecting)stage).inflector() : null);
   }

   public static <DATA> Stage.Builder<DATA> chain(Function<DATA, DATA> transformation) {
      return new Stages.StageChainBuilder<>(transformation);
   }

   public static <DATA> Stage.Builder<DATA> chain(ChainableStage<DATA> rootStage) {
      return new Stages.StageChainBuilder<>(rootStage);
   }

   public static <DATA> DATA process(DATA data, Stage<DATA> rootStage) {
      Stage.Continuation<DATA> continuation = Stage.Continuation.of(data, rootStage);

      Stage<DATA> currentStage;
      while ((currentStage = continuation.next()) != null) {
         continuation = currentStage.apply(continuation.result());
      }

      return continuation.result();
   }

   public static <DATA, RESULT, T extends Inflector<DATA, RESULT>> DATA process(DATA data, Stage<DATA> rootStage, Ref<T> inflectorRef) {
      Stage<DATA> lastStage = rootStage;

      Stage.Continuation<DATA> continuation;
      for (continuation = Stage.Continuation.of(data, rootStage); continuation.next() != null; continuation = lastStage.apply(continuation.result())) {
         lastStage = continuation.next();
      }

      inflectorRef.set(extractInflector(lastStage));
      return continuation.result();
   }

   private static class InflectingStage<DATA, RESULT> implements Stage<DATA>, Inflecting<DATA, RESULT> {
      private final Inflector<DATA, RESULT> inflector;

      public InflectingStage(Inflector<DATA, RESULT> inflector) {
         this.inflector = inflector;
      }

      @Override
      public Inflector<DATA, RESULT> inflector() {
         return this.inflector;
      }

      @Override
      public Stage.Continuation<DATA> apply(DATA request) {
         return Stage.Continuation.of(request);
      }
   }

   public static class LinkedStage<DATA> implements Stage<DATA> {
      private final Stage<DATA> nextStage;
      private final Function<DATA, DATA> transformation;

      public LinkedStage(Function<DATA, DATA> transformation, Stage<DATA> nextStage) {
         this.nextStage = nextStage;
         this.transformation = transformation;
      }

      public LinkedStage(Function<DATA, DATA> transformation) {
         this(transformation, null);
      }

      @Override
      public Stage.Continuation<DATA> apply(DATA data) {
         return Stage.Continuation.of(this.transformation.apply(data), this.nextStage);
      }
   }

   private static class StageChainBuilder<DATA> implements Stage.Builder<DATA> {
      private final Deque<Function<DATA, DATA>> transformations = new LinkedList<>();
      private Stage<DATA> rootStage;
      private ChainableStage<DATA> lastStage;

      private StageChainBuilder(Function<DATA, DATA> transformation) {
         this.transformations.push(transformation);
      }

      private StageChainBuilder(ChainableStage<DATA> rootStage) {
         this.rootStage = rootStage;
         this.lastStage = rootStage;
      }

      @Override
      public Stage.Builder<DATA> to(Function<DATA, DATA> transformation) {
         this.transformations.push(transformation);
         return this;
      }

      @Override
      public Stage.Builder<DATA> to(ChainableStage<DATA> stage) {
         this.addTailStage(stage);
         this.lastStage = stage;
         return this;
      }

      private void addTailStage(Stage<DATA> lastStage) {
         Stage<DATA> tail = lastStage;
         if (!this.transformations.isEmpty()) {
            tail = this.convertTransformations(lastStage);
         }

         if (this.rootStage != null) {
            this.lastStage.setDefaultNext(tail);
         } else {
            this.rootStage = tail;
         }
      }

      @Override
      public Stage<DATA> build(Stage<DATA> stage) {
         this.addTailStage(stage);
         return this.rootStage;
      }

      @Override
      public Stage<DATA> build() {
         return this.build(null);
      }

      private Stage<DATA> convertTransformations(Stage<DATA> successor) {
         Stage<DATA> stage;
         if (successor == null) {
            stage = new Stages.LinkedStage<>(this.transformations.poll());
         } else {
            stage = new Stages.LinkedStage<>(this.transformations.poll(), successor);
         }

         Function<DATA, DATA> t;
         while ((t = this.transformations.poll()) != null) {
            stage = new Stages.LinkedStage<>(t, stage);
         }

         return stage;
      }
   }
}
