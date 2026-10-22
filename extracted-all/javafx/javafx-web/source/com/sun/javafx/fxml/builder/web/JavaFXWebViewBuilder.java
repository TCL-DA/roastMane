package com.sun.javafx.fxml.builder.web;

import java.util.AbstractMap;
import java.util.Set;
import java.util.Map.Entry;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Builder;

public class JavaFXWebViewBuilder extends AbstractMap<String, Object> implements Builder<WebView> {
   private final WebView view = new WebView();
   private String location = "";

   public WebView build() {
      WebEngine var1 = this.view.getEngine();
      if (this.location != null) {
         var1.load(this.location);
      }

      return this.view;
   }

   public Object put(String var1, Object var2) {
      if (var2 != null) {
         String var3 = var2.toString();
         if ("location".equals(var1)) {
            this.location = var3;
         } else if ("onAlert".equals(var1)) {
         }
      }

      return null;
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      throw new UnsupportedOperationException("Not yet implemented");
   }
}
