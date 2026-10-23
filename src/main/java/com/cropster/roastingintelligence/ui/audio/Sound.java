package com.cropster.roastingintelligence.ui.audio;

import java.util.Objects;

public enum Sound {
   INFORMATIVE1("informative-01.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Informative 1")),
   INFORMATIVE2("informative-02.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Informative 2")),
   INFORMATIVE3("informative-03.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Informative 3")),
   INFORMATIVE4("informative-04.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Informative 4")),
   INFORMATIVE5("informative-05.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Informative 5")),
   INFORMATIVE6("informative-06.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Informative 6")),
   LEGACY("legacy.mp3", 2, org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Warning 1")),
   WARNING1("warning-01.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Warning 2")),
   WARNING2("warning-02.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Warning 3")),
   WARNING3("warning-03.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Warning 4")),
   WARNING4("warning-04.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Warning 5")),
   WARNING5("warning-05.mp3", org.cropster.roastingintelligence.util.Messages.c_("Name of a sound", "Warning 6"));

   private final int repetitions;
   private final String soundFile;
   private final String soundName;

   private Sound(String soundFileName, String soundName) {
      this(soundFileName, 1, soundName);
   }

   private Sound(String soundFileName, int repetitions, String soundName) {
      Objects.requireNonNull(soundFileName, "Null 'soundFileName' parameter.");
      this.soundFile = Sound.class.getResource("/audio/" + soundFileName).toExternalForm();
      this.repetitions = Math.max(1, repetitions);
      this.soundName = soundName;
   }

   public int getRepetitions() {
      return this.repetitions;
   }

   String getSoundFile() {
      return this.soundFile;
   }

   public String getSoundName() {
      return this.soundName;
   }

   public void play() {
      this.play(null);
   }

   public void play(String identifier) {
      SoundManager.get().play(this, identifier);
   }
}
