package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.util.SlidingAnimation;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class SlidingPanel extends StackPane {
   private Parent currentPanel;
   private SlidingAnimation slidingAnimation;
   private Supplier<SlidingAnimation.SlidingMode> slideModeSupplier;

   public void setShrinkingPane(Region shrinkingPane, Supplier<SlidingAnimation.SlidingMode> slideModeSupplier) {
      this.setShrinkingPane(shrinkingPane, SlidingAnimation.SlidingDirection.LTR, slideModeSupplier);
   }

   public void setShrinkingPane(Region shrinkingPane, SlidingAnimation.SlidingDirection direction, Supplier<SlidingAnimation.SlidingMode> slideModeSupplier) {
      this.slidingAnimation = new SlidingAnimation(this, shrinkingPane, direction);
      this.slideModeSupplier = slideModeSupplier;
   }

   public SlidingAnimation getSlidingAnimation() {
      return this.slidingAnimation;
   }

   public SlidingAnimation.SlidingMode getSlidingMode() {
      return this.slidingAnimation.getMode();
   }

   public SlidingAnimation.SlidingStatus getSlidingStatus() {
      return this.slidingAnimation.getStatus();
   }

   public Parent getCurrentPanel() {
      return this.currentPanel;
   }

   public void slideIn() {
      this.slideIn(null);
   }

   public void slideIn(EventHandler<ActionEvent> onFinished) {
      this.slidingAnimation.setOnFinished(onFinished);
      this.slidingAnimation.setMode(this.slideModeSupplier.get());
      this.slidingAnimation.slideIn();
   }

   public void slideOut() {
      this.slideOut(null);
   }

   public void slideOut(EventHandler<ActionEvent> onFinished) {
      this.slidingAnimation.setOnFinished(onFinished);
      this.slidingAnimation.setMode(this.slideModeSupplier.get());
      this.slidingAnimation.slideOut();
   }

   public void switchPanel(Parent switchedPanel) {
      this.getChildren().forEach(c -> c.setVisible(c == switchedPanel));
      this.currentPanel = switchedPanel;
   }

   public void toggleSlidingPanel(Parent newPanel) {
      if (this.currentPanel != newPanel) {
         if (this.slidingAnimation.getStatus() == SlidingAnimation.SlidingStatus.SLIDING_IN) {
            this.slidingAnimation.setOnFinished(e -> this.toggleSlidingPanel(newPanel));
         } else if (this.slidingAnimation.getStatus() == SlidingAnimation.SlidingStatus.OUT) {
            this.slideIn(e -> {
               this.switchPanel(newPanel);
               Platform.runLater(this::slideOut);
            });
         } else {
            this.switchPanel(newPanel);
            this.slideOut();
         }
      } else if (this.slidingAnimation.getStatus() == SlidingAnimation.SlidingStatus.OUT) {
         this.slideIn(e -> newPanel.setVisible(false));
      } else {
         newPanel.setVisible(true);
         this.slideOut();
      }
   }
}
