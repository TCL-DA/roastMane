package com.digitalpetri.strictmachine;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Fsm<S, E> {
   S getState();

   void fireEvent(E var1);

   void fireEvent(E var1, Consumer<S> var2);

   S fireEventBlocking(E var1) throws InterruptedException;

   <T> T getFromContext(Function<FsmContext<S, E>, T> var1);
}
