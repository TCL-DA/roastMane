package javafx.scene.media;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;

class MediaTimerTask extends TimerTask {
   private Timer mediaTimer = null;
   static final Object timerLock = new Object();
   private WeakReference<MediaPlayer> playerRef;

   MediaTimerTask(MediaPlayer var1) {
      this.playerRef = new WeakReference<>(var1);
   }

   void start() {
      if (this.mediaTimer == null) {
         this.mediaTimer = new Timer(true);
         this.mediaTimer.scheduleAtFixedRate(this, 0L, 100L);
      }
   }

   void stop() {
      if (this.mediaTimer != null) {
         this.mediaTimer.cancel();
         this.mediaTimer = null;
      }
   }

   @Override
   public void run() {
      synchronized (timerLock) {
         MediaPlayer var2 = this.playerRef.get();
         if (var2 != null) {
            Platform.runLater(() -> {
               synchronized (timerLock) {
                  var2.updateTime();
               }
            });
         } else {
            this.cancel();
         }
      }
   }
}
