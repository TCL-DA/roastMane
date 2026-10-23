package com.cropster.roastingintelligence.ui.components;

import java.net.URI;
import javafx.scene.control.Label;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;

public abstract class AbstractLinkLabel<T> extends Label {
   private URI baseUri;
   protected String path;

   public AbstractLinkLabel() {
      this.getStyleClass().add("label--link");
      this.baseUri = Defaults.getBaseUri();
      this.setOnMouseClicked(e -> RoastingIntelligenceApp.showDocument(this.baseUri.resolve(this.path).toString()));
   }

   public abstract void init(T var1);
}
