package com.sun.scenario.effect.impl.sw.java;

import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.impl.sw.RendererDelegate;

public class JSWRendererDelegate implements RendererDelegate {
   @Override
   public Effect.AccelType getAccelType() {
      return Effect.AccelType.NONE;
   }

   @Override
   public String getPlatformPeerName(String var1, int var2) {
      return "com.sun.scenario.effect.impl.sw.java.JSW" + var1 + "Peer";
   }
}
