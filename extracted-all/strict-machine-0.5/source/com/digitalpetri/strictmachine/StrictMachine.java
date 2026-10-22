package com.digitalpetri.strictmachine;

import com.digitalpetri.strictmachine.dsl.ActionContext;
import com.digitalpetri.strictmachine.dsl.ActionProxy;
import com.digitalpetri.strictmachine.dsl.Transition;
import com.digitalpetri.strictmachine.dsl.TransitionAction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class StrictMachine<S, E> implements Fsm<S, E> {
   private static final AtomicLong INSTANCE_ID = new AtomicLong(0L);
   private final long instanceId = INSTANCE_ID.getAndIncrement();
   private volatile boolean pollExecuted = false;
   private final Object queueLock = new Object();
   private final ArrayDeque<StrictMachine<S, E>.PendingEvent> eventQueue = new ArrayDeque<>();
   private final ArrayDeque<StrictMachine<S, E>.PendingEvent> eventShelf = new ArrayDeque<>();
   private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
   private final Map<FsmContext.Key<?>, Object> contextValues = new ConcurrentHashMap<>();
   private final AtomicReference<S> state = new AtomicReference<>();
   private final Logger logger;
   private final Map<String, String> mdc;
   private final Executor executor;
   private final ActionProxy<S, E> actionProxy;
   private final List<Transition<S, E>> transitions;
   private final List<TransitionAction<S, E>> transitionActions;
   private static final int PADDING = 24;

   public StrictMachine(
      Logger logger,
      Executor executor,
      ActionProxy<S, E> actionProxy,
      S initialState,
      List<Transition<S, E>> transitions,
      List<TransitionAction<S, E>> transitionActions
   ) {
      this(logger, Collections.emptyMap(), executor, actionProxy, initialState, transitions, transitionActions);
   }

   public StrictMachine(
      Logger logger,
      Map<String, String> mdc,
      Executor executor,
      ActionProxy<S, E> actionProxy,
      S initialState,
      List<Transition<S, E>> transitions,
      List<TransitionAction<S, E>> transitionActions
   ) {
      this.logger = logger;
      this.mdc = mdc;
      this.executor = executor;
      this.actionProxy = actionProxy;
      this.transitions = transitions;
      this.transitionActions = transitionActions;
      this.state.set(initialState);
   }

   @Override
   public S getState() {
      Object var1;
      try {
         this.readWriteLock.readLock().lock();
         var1 = this.state.get();
      } finally {
         this.readWriteLock.readLock().unlock();
      }

      return (S)var1;
   }

   @Override
   public void fireEvent(E event) {
      this.fireEvent(event, null);
   }

   @Override
   public void fireEvent(E event, Consumer<S> stateConsumer) {
      synchronized (this.queueLock) {
         this.eventQueue.add(new StrictMachine.PendingEvent(event, stateConsumer));
         this.maybeExecutePoll();
      }
   }

   @Override
   public S fireEventBlocking(E event) throws InterruptedException {
      LinkedTransferQueue<S> transferQueue = new LinkedTransferQueue();
      this.fireEvent(event, transferQueue::put);
      return transferQueue.take();
   }

   @Override
   public <T> T getFromContext(Function<FsmContext<S, E>, T> get) {
      Object var2;
      try {
         this.readWriteLock.writeLock().lock();
         var2 = get.apply(new StrictMachine.FsmContextImpl());
      } finally {
         this.readWriteLock.writeLock().unlock();
      }

      return (T)var2;
   }

   private void maybeExecutePoll() {
      synchronized (this.queueLock) {
         if (!this.pollExecuted && !this.eventQueue.isEmpty()) {
            this.executor.execute(new StrictMachine.PollAndEvaluate());
            this.pollExecuted = true;
         }
      }
   }

   private static String padRight(String s) {
      return String.format("%1$-24s", s);
   }

   private class ActionContextImpl extends StrictMachine<S, E>.FsmContextImpl implements ActionContext<S, E> {
      private final S from;
      private final S to;
      private final E event;

      ActionContextImpl(S from, S to, E event) {
         this.from = from;
         this.to = to;
         this.event = event;
      }

      @Override
      public S from() {
         return this.from;
      }

      @Override
      public S to() {
         return this.to;
      }

      @Override
      public E event() {
         return this.event;
      }
   }

   private class FsmContextImpl implements FsmContext<S, E> {
      private FsmContextImpl() {
      }

      @Override
      public S currentState() {
         return StrictMachine.this.getState();
      }

      @Override
      public void fireEvent(E event) {
         StrictMachine.this.fireEvent(event);
      }

      @Override
      public void shelveEvent(E event) {
         try {
            StrictMachine.this.readWriteLock.writeLock().lock();
            StrictMachine.this.eventShelf.add(StrictMachine.this.new PendingEvent(event, s -> {
            }));
         } finally {
            StrictMachine.this.readWriteLock.writeLock().unlock();
         }
      }

      @Override
      public void processShelvedEvents() {
         try {
            StrictMachine.this.readWriteLock.writeLock().lock();
            synchronized (StrictMachine.this.queueLock) {
               while (!StrictMachine.this.eventShelf.isEmpty()) {
                  StrictMachine.this.eventQueue.addFirst(StrictMachine.this.eventShelf.removeLast());
               }

               StrictMachine.this.maybeExecutePoll();
            }
         } finally {
            StrictMachine.this.readWriteLock.writeLock().unlock();
         }
      }

      @Override
      public Object get(FsmContext.Key<?> key) {
         Object var2;
         try {
            StrictMachine.this.readWriteLock.readLock().lock();
            var2 = StrictMachine.this.contextValues.get(key);
         } finally {
            StrictMachine.this.readWriteLock.readLock().unlock();
         }

         return var2;
      }

      @Override
      public Object remove(FsmContext.Key<?> key) {
         Object var2;
         try {
            StrictMachine.this.readWriteLock.writeLock().lock();
            var2 = StrictMachine.this.contextValues.remove(key);
         } finally {
            StrictMachine.this.readWriteLock.writeLock().unlock();
         }

         return var2;
      }

      @Override
      public void set(FsmContext.Key<?> key, Object value) {
         try {
            StrictMachine.this.readWriteLock.writeLock().lock();
            StrictMachine.this.contextValues.put(key, value);
         } finally {
            StrictMachine.this.readWriteLock.writeLock().unlock();
         }
      }

      @Override
      public long getInstanceId() {
         return StrictMachine.this.instanceId;
      }
   }

   private class PendingEvent {
      final E event;
      final Consumer<S> stateConsumer;

      PendingEvent(E event, Consumer<S> stateConsumer) {
         this.event = event;
         this.stateConsumer = stateConsumer;
      }
   }

   private class PollAndEvaluate implements Runnable {
      private PollAndEvaluate() {
      }

      @Override
      public void run() {
         StrictMachine<S, E>.PendingEvent pending;
         synchronized (StrictMachine.this.queueLock) {
            pending = StrictMachine.this.eventQueue.poll();
            if (pending == null) {
               return;
            }
         }

         E event = pending.event;

         try {
            StrictMachine.this.readWriteLock.writeLock().lock();
            S currState = StrictMachine.this.state.get();
            S nextState = currState;
            StrictMachine<S, E>.FsmContextImpl ctx = StrictMachine.this.new FsmContextImpl();

            for (Transition<S, E> transition : StrictMachine.this.transitions) {
               if (transition.matches(ctx, currState, event)) {
                  nextState = transition.target();
                  break;
               }
            }

            StrictMachine.this.state.set(nextState);
            if (StrictMachine.this.logger.isDebugEnabled()) {
               StrictMachine.this.mdc.forEach(MDC::put);

               try {
                  StrictMachine.this.logger
                     .debug(
                        "[{}] {} x {} = {}",
                        new Object[]{
                           StrictMachine.this.instanceId,
                           StrictMachine.padRight(String.format("S(%s)", currState)),
                           StrictMachine.padRight(String.format("E(%s)", event)),
                           StrictMachine.padRight(String.format("S'(%s)", nextState))
                        }
                     );
               } finally {
                  StrictMachine.this.mdc.keySet().forEach(MDC::remove);
               }
            }

            StrictMachine<S, E>.ActionContextImpl actionContext = StrictMachine.this.new ActionContextImpl(currState, nextState, event);
            List<TransitionAction<S, E>> matchingActions = new ArrayList<>();

            for (TransitionAction<S, E> transitionAction : StrictMachine.this.transitionActions) {
               if (transitionAction.matches(currState, nextState, event)) {
                  matchingActions.add(transitionAction);
               }
            }

            if (StrictMachine.this.logger.isTraceEnabled()) {
               StrictMachine.this.mdc.forEach(MDC::put);

               try {
                  StrictMachine.this.logger.trace("[{}] found {} matching TransitionActions", StrictMachine.this.instanceId, matchingActions.size());
               } finally {
                  StrictMachine.this.mdc.keySet().forEach(MDC::remove);
               }
            }

            matchingActions.forEach(
               transitionActionx -> {
                  try {
                     if (StrictMachine.this.actionProxy == null) {
                        if (StrictMachine.this.logger.isTraceEnabled()) {
                           StrictMachine.this.mdc.forEach(MDC::put);

                           try {
                              StrictMachine.this.logger.trace("[{}] executing TransitionAction: {}", StrictMachine.this.instanceId, transitionActionx);
                           } finally {
                              StrictMachine.this.mdc.keySet().forEach(MDC::remove);
                           }
                        }

                        transitionActionx.execute(actionContext);
                     } else {
                        if (StrictMachine.this.logger.isTraceEnabled()) {
                           StrictMachine.this.mdc.forEach(MDC::put);

                           try {
                              StrictMachine.this.logger
                                 .trace("[{}] executing (via proxy) TransitionAction: {}", StrictMachine.this.instanceId, transitionActionx);
                           } finally {
                              StrictMachine.this.mdc.keySet().forEach(MDC::remove);
                           }
                        }

                        StrictMachine.this.actionProxy.execute(actionContext, transitionActionx::execute);
                     }
                  } catch (Throwable var21) {
                     Throwable ex = var21;
                     StrictMachine.this.mdc.forEach(MDC::put);

                     try {
                        StrictMachine.this.logger
                           .warn("[{}] Uncaught Throwable executing TransitionAction: {}", new Object[]{StrictMachine.this.instanceId, transitionActionx, ex});
                     } finally {
                        StrictMachine.this.mdc.keySet().forEach(MDC::remove);
                     }
                  }
               }
            );
         } finally {
            StrictMachine.this.readWriteLock.writeLock().unlock();
         }

         if (pending.stateConsumer != null) {
            pending.stateConsumer.accept(StrictMachine.this.state.get());
         }

         synchronized (StrictMachine.this.queueLock) {
            if (StrictMachine.this.eventQueue.isEmpty()) {
               StrictMachine.this.pollExecuted = false;
            } else {
               StrictMachine.this.executor.execute(StrictMachine.this.new PollAndEvaluate());
            }
         }
      }
   }
}
