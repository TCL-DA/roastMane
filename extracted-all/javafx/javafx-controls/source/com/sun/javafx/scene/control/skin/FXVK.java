package com.sun.javafx.scene.control.skin;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

public class FXVK extends Control {
   private final ObjectProperty<EventHandler<KeyEvent>> onAction = new SimpleObjectProperty(this, "onAction");
   static final String[] VK_TYPE_NAMES = new String[]{"text", "numeric", "url", "email"};
   public static final String VK_TYPE_PROP_KEY = "vkType";
   String[] chars;
   private ObjectProperty<Node> attachedNode;
   static FXVK vk;
   private static final boolean IS_FXVK_SUPPORTED = Platform.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD);
   private static boolean USE_FXVK = IS_FXVK_SUPPORTED;
   private static final String DEFAULT_STYLE_CLASS = "fxvk";

   public final void setOnAction(EventHandler<KeyEvent> var1) {
      this.onAction.set(var1);
   }

   public final EventHandler<KeyEvent> getOnAction() {
      return (EventHandler<KeyEvent>)this.onAction.get();
   }

   public final ObjectProperty<EventHandler<KeyEvent>> onActionProperty() {
      return this.onAction;
   }

   public FXVK() {
      this.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
      this.getStyleClass().add("fxvk");
   }

   final ObjectProperty<Node> attachedNodeProperty() {
      if (this.attachedNode == null) {
         this.attachedNode = new ObjectPropertyBase<Node>() {
            public Object getBean() {
               return FXVK.this;
            }

            public String getName() {
               return "attachedNode";
            }
         };
      }

      return this.attachedNode;
   }

   final void setAttachedNode(Node var1) {
      this.attachedNodeProperty().setValue(var1);
   }

   final Node getAttachedNode() {
      return this.attachedNode == null ? null : (Node)this.attachedNode.getValue();
   }

   public static void init(Node var0) {
      if (vk == null) {
         vk = new FXVK();
         FXVKSkin var1 = new FXVKSkin(vk);
         vk.setSkin(var1);
         var1.prerender(var0);
      }
   }

   public static void attach(Node var0) {
      if (vk == null) {
         vk = new FXVK();
         vk.setSkin(new FXVKSkin(vk));
      }

      vk.setAttachedNode(var0);
   }

   public static void detach() {
      if (vk != null) {
         vk.setAttachedNode(null);
      }
   }

   public static boolean useFXVK() {
      return USE_FXVK;
   }

   public static void toggleUseVK(TextInputControl var0) {
      Integer var1 = (Integer)var0.getProperties().get("vkType");
      if (var1 == null) {
         var1 = -1;
      }

      var1 = var1 + 1;
      if (var1 < 4) {
         USE_FXVK = true;
         var0.getProperties().put("vkType", var1);
         attach(var0);
      } else {
         detach();
         var0.getProperties().put("vkType", null);
         USE_FXVK = false;
      }
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new FXVKSkin(this);
   }

   public static enum Type {
      TEXT,
      NUMERIC,
      EMAIL;
   }
}
