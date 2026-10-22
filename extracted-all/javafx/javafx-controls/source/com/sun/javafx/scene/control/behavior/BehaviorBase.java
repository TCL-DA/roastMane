package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.Node;

public abstract class BehaviorBase<N extends Node> {
   private final N node;
   private final List<InputMap.Mapping<?>> installedDefaultMappings;
   private final List<Runnable> childInputMapDisposalHandlers;

   public BehaviorBase(N var1) {
      this.node = (N)var1;
      this.installedDefaultMappings = new ArrayList<>();
      this.childInputMapDisposalHandlers = new ArrayList<>();
   }

   public abstract InputMap<N> getInputMap();

   public final N getNode() {
      return this.node;
   }

   public void dispose() {
      for (InputMap.Mapping var2 : this.installedDefaultMappings) {
         this.getInputMap().getMappings().remove(var2);
      }

      for (Runnable var4 : this.childInputMapDisposalHandlers) {
         var4.run();
      }
   }

   protected void addDefaultMapping(List<InputMap.Mapping<?>> var1) {
      this.addDefaultMapping(this.getInputMap(), var1.toArray(new InputMap.Mapping[var1.size()]));
   }

   protected void addDefaultMapping(InputMap.Mapping<?>... var1) {
      this.addDefaultMapping(this.getInputMap(), var1);
   }

   protected void addDefaultMapping(InputMap<N> var1, InputMap.Mapping<?>... var2) {
      ArrayList var3 = new ArrayList(var1.getMappings());

      for (InputMap.Mapping var7 : var2) {
         if (!var3.contains(var7)) {
            var1.getMappings().add(var7);
            this.installedDefaultMappings.add(var7);
         }
      }
   }

   protected <T extends Node> void addDefaultChildMap(InputMap<T> var1, InputMap<T> var2) {
      var1.getChildInputMaps().add(var2);
      this.childInputMapDisposalHandlers.add(() -> var1.getChildInputMaps().remove(var2));
   }

   protected InputMap<N> createInputMap() {
      return new InputMap<>(this.node);
   }

   protected void removeMapping(Object var1) {
      InputMap var2 = this.getInputMap();
      var2.lookupMapping(var1).ifPresent(var2x -> {
         var2.getMappings().remove(var2x);
         this.installedDefaultMappings.remove(var2x);
      });
   }

   void rtl(Node var1, Runnable var2, Runnable var3) {
      switch (var1.getEffectiveNodeOrientation()) {
         case RIGHT_TO_LEFT:
            var2.run();
            break;
         default:
            var3.run();
      }
   }

   <T> void rtl(Node var1, T var2, Consumer<T> var3, Consumer<T> var4) {
      switch (var1.getEffectiveNodeOrientation()) {
         case RIGHT_TO_LEFT:
            var3.accept(var2);
            break;
         default:
            var4.accept(var2);
      }
   }

   boolean isRTL(Node var1) {
      switch (var1.getEffectiveNodeOrientation()) {
         case RIGHT_TO_LEFT:
            return true;
         default:
            return false;
      }
   }
}
