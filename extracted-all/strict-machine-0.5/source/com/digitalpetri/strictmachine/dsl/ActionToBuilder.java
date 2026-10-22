package com.digitalpetri.strictmachine.dsl;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;

public class ActionToBuilder<S extends Enum<S>, E> {
   private final Predicate<S> toFilter;
   private final LinkedList<TransitionAction<S, E>> transitionActions;

   ActionToBuilder(Predicate<S> toFilter, LinkedList<TransitionAction<S, E>> transitionActions) {
      this.toFilter = toFilter;
      this.transitionActions = transitionActions;
   }

   public ViaBuilder<S, E> from(S from) {
      return this.from(s -> Objects.equals(s, from));
   }

   public ViaBuilder<S, E> from(Predicate<S> fromFilter) {
      return new ViaBuilder<>(fromFilter, this.toFilter, this.transitionActions);
   }

   public ViaBuilder<S, E> fromAny() {
      return this.from(s -> true);
   }
}
