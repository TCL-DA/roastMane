package com.cropster.roastingintelligence.ui.audio;

import java.util.Objects;
import java.util.WeakHashMap;
import javafx.scene.media.AudioClip;

public final class SoundManager {
   private final WeakHashMap<Sound, AudioClip> cache = new WeakHashMap<>();
   private int repetitions = 1;
   private double volume = 0.7;
   private AudioClip currentlyPlayingClip = null;
   private String currentlyPlayingIdentifier = null;

   public static SoundManager get() {
      return SoundManager.SoundManagerHolder.INSTANCE;
   }

   private SoundManager() {
   }

   public synchronized void play(Sound sound) {
      this.play(sound, null);
   }

   public synchronized void play(Sound sound, String identifier) {
      AudioClip clip = this.cache.get(Objects.requireNonNull(sound, "Null 'sound' parameter."));
      if (clip == null) {
         String soundFile = sound.getSoundFile();
         if (soundFile != null) {
            clip = new AudioClip(soundFile);
            this.cache.put(sound, clip);
         }
      }

      this.stop();
      if (clip == null) {
         throw new IllegalStateException("Could not load sound: " + sound);
      } else {
         this.currentlyPlayingClip = clip;
         this.currentlyPlayingIdentifier = identifier;
         clip.setVolume(this.getVolume());
         clip.setCycleCount(sound.getRepetitions() * this.getRepetitions());
         clip.play();
      }
   }

   public boolean isPlaying() {
      return this.currentlyPlayingClip != null && this.currentlyPlayingClip.isPlaying();
   }

   public boolean isPlaying(String identifier) {
      Objects.requireNonNull(identifier, "Null 'identifier' parameter.");
      return this.isPlaying() && identifier.equals(this.currentlyPlayingIdentifier);
   }

   public synchronized void stop() {
      if (this.currentlyPlayingClip != null) {
         this.currentlyPlayingClip.stop();
         this.currentlyPlayingClip = null;
         this.currentlyPlayingIdentifier = null;
      }
   }

   public String getCurrentlyPlayingIdentifier() {
      return this.currentlyPlayingIdentifier;
   }

   public int getRepetitions() {
      return this.repetitions;
   }

   public void setRepetitions(int repetitions) {
      this.repetitions = repetitions;
   }

   public double getVolume() {
      return this.volume;
   }

   public void setVolume(double volume) {
      this.volume = volume;
   }

   private static final class SoundManagerHolder {
      private static final SoundManager INSTANCE = new SoundManager();
   }
}
