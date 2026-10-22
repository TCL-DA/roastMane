package org.girod.javafx.svgimage.xml.specs;

import javafx.scene.text.FontPosture;

public class ExtendedFontPosture {
   public final FontPosture posture;
   public final boolean isOblique;

   public ExtendedFontPosture(FontPosture posture) {
      this.posture = posture;
      this.isOblique = false;
   }

   public ExtendedFontPosture(boolean isOblique) {
      this.posture = FontPosture.REGULAR;
      this.isOblique = isOblique;
   }
}
