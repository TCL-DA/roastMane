package com.digitalpetri.strictmachine.dsl;

import com.digitalpetri.strictmachine.FsmContext;

public interface Transition<S, E> {
   S target();

   boolean matches(FsmContext<S, E> var1, S var2, E var3);
}
