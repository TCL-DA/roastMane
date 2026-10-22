package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.scene.traversal.Direction;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FocusTraversalInputMap<N extends Node> {
   private static final List<InputMap.Mapping<?>> mappings = new ArrayList<>();

   private FocusTraversalInputMap() {
   }

   public static InputMap.Mapping<?>[] getFocusTraversalMappings() {
      return mappings.toArray(new InputMap.Mapping[mappings.size()]);
   }

   public static <N extends Node> InputMap<N> createInputMap(N var0) {
      InputMap var1 = new InputMap<>(var0);
      var1.getMappings().addAll(getFocusTraversalMappings());
      return var1;
   }

   public static void traverse(Node var0, Direction var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("Attempting to traverse on a null Node. Most probably a KeyEvent has been fired with a null target specified.");
      } else {
         NodeHelper.traverse(var0, var1);
      }
   }

   public static final void traverseUp(KeyEvent var0) {
      traverse(getNode(var0), Direction.UP);
   }

   public static final void traverseDown(KeyEvent var0) {
      traverse(getNode(var0), Direction.DOWN);
   }

   public static final void traverseLeft(KeyEvent var0) {
      traverse(getNode(var0), Direction.LEFT);
   }

   public static final void traverseRight(KeyEvent var0) {
      traverse(getNode(var0), Direction.RIGHT);
   }

   public static final void traverseNext(KeyEvent var0) {
      traverse(getNode(var0), Direction.NEXT);
   }

   public static final void traversePrevious(KeyEvent var0) {
      traverse(getNode(var0), Direction.PREVIOUS);
   }

   private static Node getNode(KeyEvent var0) {
      EventTarget var1 = var0.getTarget();
      return var1 instanceof Node ? (Node)var1 : null;
   }

   static {
      mappings.add(new InputMap.KeyMapping(KeyCode.UP, var0 -> traverseUp(var0)));
      mappings.add(new InputMap.KeyMapping(KeyCode.DOWN, var0 -> traverseDown(var0)));
      mappings.add(new InputMap.KeyMapping(KeyCode.LEFT, var0 -> traverseLeft(var0)));
      mappings.add(new InputMap.KeyMapping(KeyCode.RIGHT, var0 -> traverseRight(var0)));
      mappings.add(new InputMap.KeyMapping(KeyCode.TAB, var0 -> traverseNext(var0)));
      mappings.add(new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).shift(), var0 -> traversePrevious(var0)));
      mappings.add(new InputMap.KeyMapping(new KeyBinding(KeyCode.UP).shift().alt().ctrl(), var0 -> traverseUp(var0)));
      mappings.add(new InputMap.KeyMapping(new KeyBinding(KeyCode.DOWN).shift().alt().ctrl(), var0 -> traverseDown(var0)));
      mappings.add(new InputMap.KeyMapping(new KeyBinding(KeyCode.LEFT).shift().alt().ctrl(), var0 -> traverseLeft(var0)));
      mappings.add(new InputMap.KeyMapping(new KeyBinding(KeyCode.RIGHT).shift().alt().ctrl(), var0 -> traverseRight(var0)));
      mappings.add(new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).shift().alt().ctrl(), var0 -> traverseNext(var0)));
      mappings.add(new InputMap.KeyMapping(new KeyBinding(KeyCode.TAB).alt().ctrl(), var0 -> traversePrevious(var0)));
   }
}
