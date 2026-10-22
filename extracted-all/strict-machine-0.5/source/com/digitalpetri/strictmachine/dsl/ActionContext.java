package com.digitalpetri.strictmachine.dsl;

import com.digitalpetri.strictmachine.FsmContext;

public interface ActionContext<S, E> extends FsmContext<S, E> {
   S from();

   S to();

   E event();
}
