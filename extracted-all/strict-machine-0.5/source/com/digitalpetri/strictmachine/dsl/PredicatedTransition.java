package com.digitalpetri.strictmachine.dsl;

import com.digitalpetri.strictmachine.FsmContext;
import java.util.function.Predicate;

class PredicatedTransition<S, E> implements Transition<S, E> {
   private volatile Predicate<FsmContext<S, E>> guard = ctx -> true;
   private final Predicate<S> from;
   private final Predicate<E> via;
   private final S target;

   PredicatedTransition(Predicate<S> from, Predicate<E> via, S target) {
      this.from = from;
      this.via = via;
      this.target = target;
   }

   @Override
   public S target() {
      return this.target;
   }

   @Override
   public boolean matches(FsmContext<S, E> ctx, S state, E event) {
      return this.from.test(state) && this.via.test(event) && this.guard.test(ctx);
   }

   Predicate<FsmContext<S, E>> getGuard() {
      return this.guard;
   }

   Predicate<S> getFrom() {
      return this.from;
   }

   Predicate<E> getVia() {
      return this.via;
   }

   S getTarget() {
      return this.target;
   }

   void setGuard(Predicate<FsmContext<S, E>> guard) {
      this.guard = guard;
   }
}
