package javafx.scene.media;

import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.util.Pair;

public class MediaMarkerEvent extends ActionEvent {
   private static final long serialVersionUID = 20121107L;
   private Pair<String, Duration> marker;

   MediaMarkerEvent(Pair<String, Duration> var1) {
      this.marker = var1;
   }

   public Pair<String, Duration> getMarker() {
      return this.marker;
   }
}
