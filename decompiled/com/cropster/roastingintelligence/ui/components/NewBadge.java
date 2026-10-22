package com.cropster.roastingintelligence.ui.components;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.remote.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewBadge extends Label {
   private final Logger logger = LoggerFactory.getLogger(NewBadge.class);
   private AppModel appModel;
   private Version version;

   public NewBadge() {
      this(null, null);
   }

   public NewBadge(Version version) {
      this(null, version);
   }

   public NewBadge(AppModel appModel, Version version) {
      this.appModel = appModel;
      this.version = version;
      this.init();
      this.updateVisibility();
   }

   public AppModel getAppModel() {
      return this.appModel;
   }

   public void setAppModel(AppModel appModel) {
      this.appModel = appModel;
      this.updateVisibility();
   }

   public Version getVersion() {
      return this.version;
   }

   public void setVersion(Version version) {
      this.version = version;
      this.updateVisibility();
   }

   public void setVersion(String version) {
      this.setVersion(new Version(version));
   }

   private void init() {
      this.getStyleClass().addAll(new String[]{"tag", "tag--badge", "tag--info"});
      this.setContentDisplay(ContentDisplay.TEXT_ONLY);
      this.setText("NEW");
      this.managedProperty().bind(this.visibleProperty());
      this.parentProperty().addListener((s, o, n) -> this.updateVisibility());
   }

   private void updateVisibility() {
      boolean shouldBeVisibile = false;
      if (this.appModel != null && this.version != null) {
         shouldBeVisibile = this.version.compareMajorAndMinorVersionTo(this.appModel.getVersion()) == 0;
      }

      this.setVisible(shouldBeVisibile);
      if (!shouldBeVisibile) {
         this.logger.warn("NewBadge for version {} doesn't match application version {}, and should be removed!", this.version, this.appModel.getVersion());
      }
   }
}
