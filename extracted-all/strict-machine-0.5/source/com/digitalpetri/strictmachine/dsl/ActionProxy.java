package com.digitalpetri.strictmachine.dsl;

@FunctionalInterface
public interface ActionProxy<S, E> {
   void execute(ActionContext<S, E> var1, Action<S, E> var2);
}
