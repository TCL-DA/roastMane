package com.digitalpetri.strictmachine.dsl;

import com.digitalpetri.strictmachine.FsmContext;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Predicate;

public class GuardBuilder<S, E> extends ActionBuilder<S, E> {
   private final PredicatedTransition<S, E> transition;

   GuardBuilder(PredicatedTransition<S, E> transition, LinkedList<TransitionAction<S, E>> transitionActions) {
      super(transition.getFrom(), s -> Objects.equals(s, transition.getTarget()), transition.getVia(), transitionActions);
      this.transition = transition;
   }

   public ActionBuilder<S, E> guardedBy(Predicate<FsmContext<S, E>> guard) {
      this.transition.setGuard(guard);
      return this;
   }
}
