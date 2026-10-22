package com.sun.javafx.event;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;

public class CompositeEventTargetImpl implements CompositeEventTarget {
   private final Set<EventTarget> eventTargets;

   public CompositeEventTargetImpl(EventTarget... var1) {
      HashSet var2 = new HashSet(var1.length);
      var2.addAll(Arrays.asList(var1));
      this.eventTargets = Collections.unmodifiableSet(var2);
   }

   @Override
   public Set<EventTarget> getTargets() {
      return this.eventTargets;
   }

   @Override
   public boolean containsTarget(EventTarget var1) {
      return this.eventTargets.contains(var1);
   }

   @Override
   public EventDispatchChain buildEventDispatchChain(EventDispatchChain var1) {
      EventDispatchTree var2 = (EventDispatchTree)var1;

      for (EventTarget var4 : this.eventTargets) {
         EventDispatchTree var5 = var2.createTree();
         var2 = var2.mergeTree((EventDispatchTree)var4.buildEventDispatchChain(var5));
      }

      return var2;
   }
}
