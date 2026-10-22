package com.cropster.roastingintelligence.ui.util;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProgressTask<T> extends Task<T> {
   private final Logger logger = LoggerFactory.getLogger(ProgressTask.class);
   private final Stage stage;
   private final Animation animation;
   private final Alert alert;

   public ProgressTask(Stage stage) {
      this(stage, org.cropster.roastingintelligence.util.Messages.m_("Syncing data ..."), null);
   }

   public ProgressTask(Stage stage, Animation animation) {
      this(stage, org.cropster.roastingintelligence.util.Messages.m_("Syncing data ..."), animation);
   }

   public ProgressTask(Stage stage, String title) {
      this(stage, title, null);
   }

   public ProgressTask(Stage stage, String title, Animation animation) {
      this.stage = stage;
      this.animation = animation;
      this.alert = WindowUtil.createProgressAlert(stage, title, () -> this.cancel(true));
   }

   protected void scheduled() {
      WindowUtil.center(this.stage, this.alert).show();
   }

   protected void running() {
      if (this.animation != null) {
         this.animation.play();
      }
   }

   protected void done() {
      if (this.animation != null) {
         this.animation.pause();
      }

      Platform.runLater(this.alert::close);
   }

   protected void succeeded() {
      this.logger.info("Task completed successfully");
   }

   protected void cancelled() {
      this.logger.info("Task cancelled");
   }

   protected void failed() {
      if (this.logger.isDebugEnabled()) {
         this.logger.debug("Task failed", this.getException());
      } else {
         this.logger.info("Task failed: {}", this.getException().getMessage());
      }
   }
}
