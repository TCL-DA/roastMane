package com.digitalpetri.strictmachine.dsl;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionBuilder<S, E> {
   private Predicate<S> from;
   private Predicate<S> to;
   private Predicate<E> via;
   private final LinkedList<TransitionAction<S, E>> transitionActions;

   ActionBuilder(Predicate<S> from, Predicate<S> to, Predicate<E> via, LinkedList<TransitionAction<S, E>> transitionActions) {
      this.from = from;
      this.to = to;
      this.via = via;
      this.transitionActions = transitionActions;
   }

   public ActionBuilder<S, E> execute(Action<S, E> action) {
      return this.executeLast(action);
   }

   public ActionBuilder<S, E> executeLast(Action<S, E> action) {
      TransitionAction<S, E> transitionAction = new ActionBuilder.PredicatedTransitionAction<>(this.from, this.to, this.via, action::execute);
      this.transitionActions.addLast(transitionAction);
      return this;
   }

   public ActionBuilder<S, E> executeFirst(Action<S, E> action) {
      TransitionAction<S, E> transitionAction = new ActionBuilder.PredicatedTransitionAction<>(this.from, this.to, this.via, action::execute);
      this.transitionActions.addFirst(transitionAction);
      return this;
   }

   private static class PredicatedTransitionAction<S, E> implements TransitionAction<S, E> {
      private final Predicate<S> from;
      private final Predicate<S> to;
      private final Predicate<E> via;
      private final Consumer<ActionContext<S, E>> action;

      PredicatedTransitionAction(Predicate<S> from, Predicate<S> to, Predicate<E> via, Consumer<ActionContext<S, E>> action) {
         this.from = from;
         this.to = to;
         this.via = via;
         this.action = action;
      }

      @Override
      public void execute(ActionContext<S, E> context) {
         this.action.accept(context);
      }

      @Override
      public boolean matches(S from, S to, E event) {
         return this.from.test(from) && this.to.test(to) && this.via.test(event);
      }
   }
}
