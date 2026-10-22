package com.digitalpetri.strictmachine.dsl;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;

public class ViaBuilder<S, E> {
   private final Predicate<S> fromFilter;
   private final Predicate<S> toFilter;
   private final LinkedList<TransitionAction<S, E>> transitionActions;

   ViaBuilder(Predicate<S> fromFilter, Predicate<S> toFilter, LinkedList<TransitionAction<S, E>> transitionActions) {
      this.fromFilter = fromFilter;
      this.toFilter = toFilter;
      this.transitionActions = transitionActions;
   }

   public ActionBuilder<S, E> via(E event) {
      return new ActionBuilder<>(this.fromFilter, this.toFilter, e -> Objects.equals(e, event), this.transitionActions);
   }

   public ActionBuilder<S, E> via(Class<? extends E> eventClass) {
      return new ActionBuilder<>(this.fromFilter, this.toFilter, e -> Objects.equals(e.getClass(), eventClass), this.transitionActions);
   }

   public ActionBuilder<S, E> via(Predicate<E> eventFilter) {
      return new ActionBuilder<>(this.fromFilter, this.toFilter, eventFilter, this.transitionActions);
   }

   public ActionBuilder<S, E> viaAny() {
      return new ActionBuilder<>(this.fromFilter, this.toFilter, e -> true, this.transitionActions);
   }
}
