package com.digitalpetri.strictmachine.dsl;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;

public class ActionFromBuilder<S extends Enum<S>, E> {
   private final Predicate<S> fromFilter;
   private final LinkedList<TransitionAction<S, E>> transitionActions;

   ActionFromBuilder(Predicate<S> fromFilter, LinkedList<TransitionAction<S, E>> transitionActions) {
      this.fromFilter = fromFilter;
      this.transitionActions = transitionActions;
   }

   public ViaBuilder<S, E> to(S state) {
      return this.to(s -> Objects.equals(s, state));
   }

   public ViaBuilder<S, E> to(Predicate<S> toFilter) {
      return new ViaBuilder<>(this.fromFilter, toFilter, this.transitionActions);
   }

   public ViaBuilder<S, E> toAny() {
      return this.to(s -> true);
   }
}
