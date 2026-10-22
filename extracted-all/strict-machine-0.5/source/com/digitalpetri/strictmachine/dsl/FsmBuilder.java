package com.digitalpetri.strictmachine.dsl;

import com.digitalpetri.strictmachine.Fsm;
import com.digitalpetri.strictmachine.StrictMachine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FsmBuilder<S extends Enum<S>, E> {
   private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
   private final List<Transition<S, E>> transitions = new ArrayList<>();
   private final LinkedList<TransitionAction<S, E>> transitionActions = new LinkedList<>();
   private ActionProxy<S, E> actionProxy = null;
   private final Logger logger;
   private final Map<String, String> mdc;
   private final Executor executor;

   public FsmBuilder() {
      this(EXECUTOR_SERVICE, StrictMachine.class.getName());
   }

   public FsmBuilder(Executor executor, String loggerName) {
      this(executor, loggerName, Collections.emptyMap());
   }

   public FsmBuilder(Executor executor, String loggerName, Map<String, String> mdc) {
      this.executor = executor;
      this.logger = LoggerFactory.getLogger(loggerName);
      this.mdc = mdc;
   }

   public TransitionBuilder<S, E> when(S state) {
      return new TransitionBuilder<>(state, this.transitions, this.transitionActions);
   }

   public ViaBuilder<S, E> onInternalTransition(S state) {
      return this.onTransitionFrom(state).to(state);
   }

   public ActionToBuilder<S, E> onTransitionTo(S state) {
      return this.onTransitionTo(s -> Objects.equals(s, state));
   }

   public ActionToBuilder<S, E> onTransitionTo(Predicate<S> filter) {
      return new ActionToBuilder<>(filter, this.transitionActions);
   }

   public ActionFromBuilder<S, E> onTransitionFrom(S state) {
      return this.onTransitionFrom(s -> Objects.equals(s, state));
   }

   public ActionFromBuilder<S, E> onTransitionFrom(Predicate<S> filter) {
      return new ActionFromBuilder<>(filter, this.transitionActions);
   }

   public void addTransition(Transition<S, E> transition) {
      this.transitions.add(transition);
   }

   public void addTransitionAction(TransitionAction<S, E> transitionAction) {
      this.transitionActions.add(transitionAction);
   }

   public void setActionProxy(ActionProxy<S, E> actionProxy) {
      this.actionProxy = actionProxy;
   }

   public Fsm<S, E> build(S initialState) {
      return new StrictMachine<>(
         this.logger, this.mdc, this.executor, this.actionProxy, initialState, new ArrayList<>(this.transitions), new ArrayList<>(this.transitionActions)
      );
   }
}
