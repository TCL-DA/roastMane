package com.sun.javafx.fxml.builder.web;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.util.Builder;
import javafx.util.Callback;

@Deprecated
public final class WebEngineBuilder implements Builder<WebEngine> {
   private Callback<String, Boolean> confirmHandler;
   private boolean confirmHandlerSet;
   private Callback<PopupFeatures, WebEngine> createPopupHandler;
   private boolean createPopupHandlerSet;
   private EventHandler<WebEvent<String>> onAlert;
   private boolean onAlertSet;
   private EventHandler<WebEvent<Rectangle2D>> onResized;
   private boolean onResizedSet;
   private EventHandler<WebEvent<String>> onStatusChanged;
   private boolean onStatusChangedSet;
   private EventHandler<WebEvent<Boolean>> onVisibilityChanged;
   private boolean onVisibilityChangedSet;
   private Callback<PromptData, String> promptHandler;
   private boolean promptHandlerSet;
   private String location;
   private boolean locationSet;

   public static WebEngineBuilder create() {
      return new WebEngineBuilder();
   }

   public WebEngine build() {
      WebEngine var1 = new WebEngine();
      this.applyTo(var1);
      return var1;
   }

   public void applyTo(WebEngine var1) {
      if (this.confirmHandlerSet) {
         var1.setConfirmHandler(this.confirmHandler);
      }

      if (this.createPopupHandlerSet) {
         var1.setCreatePopupHandler(this.createPopupHandler);
      }

      if (this.onAlertSet) {
         var1.setOnAlert(this.onAlert);
      }

      if (this.onResizedSet) {
         var1.setOnResized(this.onResized);
      }

      if (this.onStatusChangedSet) {
         var1.setOnStatusChanged(this.onStatusChanged);
      }

      if (this.onVisibilityChangedSet) {
         var1.setOnVisibilityChanged(this.onVisibilityChanged);
      }

      if (this.promptHandlerSet) {
         var1.setPromptHandler(this.promptHandler);
      }

      if (this.locationSet) {
         var1.load(this.location);
      }
   }

   public WebEngineBuilder confirmHandler(Callback<String, Boolean> var1) {
      this.confirmHandler = var1;
      this.confirmHandlerSet = true;
      return this;
   }

   public WebEngineBuilder createPopupHandler(Callback<PopupFeatures, WebEngine> var1) {
      this.createPopupHandler = var1;
      this.createPopupHandlerSet = true;
      return this;
   }

   public WebEngineBuilder onAlert(EventHandler<WebEvent<String>> var1) {
      this.onAlert = var1;
      this.onAlertSet = true;
      return this;
   }

   public WebEngineBuilder onResized(EventHandler<WebEvent<Rectangle2D>> var1) {
      this.onResized = var1;
      this.onResizedSet = true;
      return this;
   }

   public WebEngineBuilder onStatusChanged(EventHandler<WebEvent<String>> var1) {
      this.onStatusChanged = var1;
      this.onStatusChangedSet = true;
      return this;
   }

   public WebEngineBuilder onVisibilityChanged(EventHandler<WebEvent<Boolean>> var1) {
      this.onVisibilityChanged = var1;
      this.onVisibilityChangedSet = true;
      return this;
   }

   public WebEngineBuilder promptHandler(Callback<PromptData, String> var1) {
      this.promptHandler = var1;
      this.promptHandlerSet = true;
      return this;
   }

   public WebEngineBuilder location(String var1) {
      this.location = var1;
      this.locationSet = true;
      return this;
   }
}
