package com.digitalpetri.strictmachine.dsl;

public interface TransitionAction<S, E> {
   void execute(ActionContext<S, E> var1);

   boolean matches(S var1, S var2, E var3);
}
