package com.sun.javafx.scene;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Window;

public class TreeShowingExpression extends BooleanExpression {
   private final ChangeListener<Boolean> windowShowingChangedListener = (var1x, var2, var3) -> this.updateTreeShowing();
   private final ChangeListener<Window> sceneWindowChangedListener = (var1x, var2, var3) -> this.windowChanged(var2, var3);
   private final ChangeListener<Scene> nodeSceneChangedListener = (var1x, var2, var3) -> this.sceneChanged(var2, var3);
   private final Node node;
   private ExpressionHelper<Boolean> helper;
   private boolean valid;
   private boolean treeShowing;

   public TreeShowingExpression(Node var1) {
      this.node = var1;
      this.node.sceneProperty().addListener(this.nodeSceneChangedListener);
      NodeHelper.treeVisibleProperty(var1).addListener(this.windowShowingChangedListener);
      this.sceneChanged(null, var1.getScene());
   }

   public void dispose() {
      this.node.sceneProperty().removeListener(this.nodeSceneChangedListener);
      NodeHelper.treeVisibleProperty(this.node).removeListener(this.windowShowingChangedListener);
      this.valid = false;
      this.sceneChanged(this.node.getScene(), null);
   }

   public void addListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   public void removeListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   public void addListener(ChangeListener<? super Boolean> var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, var1);
   }

   public void removeListener(ChangeListener<? super Boolean> var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   protected void invalidate() {
      if (this.valid) {
         this.valid = false;
         ExpressionHelper.fireValueChangedEvent(this.helper);
      }
   }

   public boolean get() {
      if (!this.valid) {
         this.updateTreeShowing();
         this.valid = true;
      }

      return this.treeShowing;
   }

   private void sceneChanged(Scene var1, Scene var2) {
      if (var1 != null) {
         var1.windowProperty().removeListener(this.sceneWindowChangedListener);
      }

      if (var2 != null) {
         var2.windowProperty().addListener(this.sceneWindowChangedListener);
      }

      this.windowChanged(var1 == null ? null : var1.getWindow(), var2 == null ? null : var2.getWindow());
   }

   private void windowChanged(Window var1, Window var2) {
      if (var1 != null) {
         var1.showingProperty().removeListener(this.windowShowingChangedListener);
      }

      if (var2 != null) {
         var2.showingProperty().addListener(this.windowShowingChangedListener);
      }

      this.updateTreeShowing();
   }

   private void updateTreeShowing() {
      boolean var1 = NodeHelper.isTreeShowing(this.node);
      if (var1 != this.treeShowing) {
         this.treeShowing = var1;
         this.invalidate();
      }
   }
}
