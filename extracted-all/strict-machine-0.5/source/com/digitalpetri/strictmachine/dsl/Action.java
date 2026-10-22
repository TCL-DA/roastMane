package com.digitalpetri.strictmachine.dsl;

@FunctionalInterface
public interface Action<S, E> {
   void execute(ActionContext<S, E> var1);
}
