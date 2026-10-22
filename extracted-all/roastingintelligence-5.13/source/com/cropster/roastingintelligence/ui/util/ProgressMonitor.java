package com.cropster.roastingintelligence.ui.util;

import java.util.concurrent.CompletableFuture;
import javafx.stage.Stage;

public class ProgressMonitor<T> extends ProgressTask<T> {
   private CompletableFuture<T> future;

   public ProgressMonitor(Stage stage, CompletableFuture<T> future) {
      super(stage);
      this.future = future;
   }

   protected T call() throws Exception {
      while (!this.future.isDone()) {
         Thread.sleep(200L);
      }

      return this.future.get();
   }
}
