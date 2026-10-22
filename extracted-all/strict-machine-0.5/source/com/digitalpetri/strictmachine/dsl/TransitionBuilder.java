package com.digitalpetri.strictmachine.dsl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class TransitionBuilder<S extends Enum<S>, E> {
   private final S from;
   private final List<Transition<S, E>> transitions;
   private final LinkedList<TransitionAction<S, E>> transitionActions;

   TransitionBuilder(S from, List<Transition<S, E>> transitions, LinkedList<TransitionAction<S, E>> transitionActions) {
      this.from = from;
      this.transitions = transitions;
      this.transitionActions = transitionActions;
   }

   public TransitionBuilder.TransitionTo<S, E> on(E event) {
      return to -> {
         PredicatedTransition<S, E> transition = Transitions.fromInstanceViaInstance(this.from, event, to);
         this.transitions.add(transition);
         return new GuardBuilder<>(transition, this.transitionActions);
      };
   }

   public TransitionBuilder.TransitionTo<S, E> on(Class<? extends E> eventClass) {
      return to -> {
         PredicatedTransition<S, E> transition = Transitions.fromInstanceViaClass(this.from, eventClass, to);
         this.transitions.add(transition);
         return new GuardBuilder<>(transition, this.transitionActions);
      };
   }

   public TransitionBuilder.TransitionTo<S, E> on(Predicate<E> eventFilter) {
      return to -> {
         PredicatedTransition<S, E> transition = Transitions.fromInstanceViaDynamic(this.from, eventFilter, to);
         this.transitions.add(transition);
         return new GuardBuilder<>(transition, this.transitionActions);
      };
   }

   public TransitionBuilder.TransitionTo<S, E> onAny() {
      return this.on(e -> true);
   }

   public interface TransitionTo<S, E> {
      GuardBuilder<S, E> transitionTo(S var1);
   }
}
