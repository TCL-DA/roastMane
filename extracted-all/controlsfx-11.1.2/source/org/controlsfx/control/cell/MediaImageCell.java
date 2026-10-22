package org.controlsfx.control.cell;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.controlsfx.control.GridCell;

public class MediaImageCell extends GridCell<Media> {
   private MediaPlayer mediaPlayer;
   private final MediaView mediaView;

   public MediaImageCell() {
      this.getStyleClass().add("media-grid-cell");
      this.mediaView = new MediaView();
      this.mediaView.setMediaPlayer(this.mediaPlayer);
      this.mediaView.fitHeightProperty().bind(this.heightProperty());
      this.mediaView.fitWidthProperty().bind(this.widthProperty());
      this.mediaView.setMediaPlayer(this.mediaPlayer);
   }

   public void pause() {
      if (this.mediaPlayer != null) {
         this.mediaPlayer.pause();
      }
   }

   public void play() {
      if (this.mediaPlayer != null) {
         this.mediaPlayer.play();
      }
   }

   public void stop() {
      if (this.mediaPlayer != null) {
         this.mediaPlayer.stop();
      }
   }

   protected void updateItem(Media item, boolean empty) {
      super.updateItem(item, empty);
      this.getChildren().clear();
      if (this.mediaPlayer != null) {
         this.mediaPlayer.stop();
      }

      if (empty) {
         this.setGraphic(null);
      } else {
         this.mediaPlayer = new MediaPlayer(item);
         this.mediaView.setMediaPlayer(this.mediaPlayer);
         this.setGraphic(this.mediaView);
      }
   }
}
