package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.components.skin.ProgressDotSkin;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;

public class ProgressDot extends ProgressBar {
   public ProgressDot() {
   }

   public ProgressDot(double progress) {
      super(progress);
   }

   protected Skin<?> createDefaultSkin() {
      return new ProgressDotSkin(this);
   }
}
