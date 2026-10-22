package com.sun.scenario.effect.impl.sw.sse;

import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.impl.EffectPeer;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;

public abstract class SSEEffectPeer<T extends RenderState> extends EffectPeer<T> {
   protected SSEEffectPeer(FilterContext var1, Renderer var2, String var3) {
      super(var1, var2, var3);
   }
}
