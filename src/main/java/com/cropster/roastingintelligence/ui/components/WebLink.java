package com.cropster.roastingintelligence.ui.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Hyperlink;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;

public class WebLink extends Hyperlink {
   private StringProperty url = new SimpleStringProperty();

   public WebLink() {
      this.url.addListener((s, o, n) -> this.setOnAction(e -> RoastingIntelligenceApp.showDocument(n)));
   }

   public WebLink(String text, String url) {
      this.url.addListener((s, o, n) -> this.setOnAction(e -> RoastingIntelligenceApp.showDocument(n)));
      this.setText(text);
      this.setUrl(url);
   }

   public StringProperty urlProperty() {
      return this.url;
   }

   public String getUrl() {
      return (String)this.url.get();
   }

   public void setUrl(String url) {
      this.url.set(url);
   }
}
