package javafx.scene.text;

import java.text.BreakIterator;

public class HitInfo {
   private int charIndex;
   private boolean leading;
   private int insertionIndex;
   private String text;
   private static BreakIterator charIterator = BreakIterator.getCharacterInstance();

   HitInfo(int var1, int var2, boolean var3, String var4) {
      this.charIndex = var1;
      this.leading = var3;
      this.insertionIndex = var2;
      this.text = var4;
   }

   public int getCharIndex() {
      return this.charIndex;
   }

   public boolean isLeading() {
      return this.leading;
   }

   public int getInsertionIndex() {
      if (this.insertionIndex == -1) {
         this.insertionIndex = this.charIndex;
         if (!this.leading) {
            if (this.text != null) {
               int var1;
               synchronized (charIterator) {
                  charIterator.setText(this.text);
                  var1 = charIterator.following(this.insertionIndex);
               }

               if (var1 == -1) {
                  this.insertionIndex++;
               } else {
                  this.insertionIndex = var1;
               }
            } else {
               this.insertionIndex++;
            }
         }
      }

      return this.insertionIndex;
   }

   @Override
   public String toString() {
      return "charIndex: " + this.charIndex + ", isLeading: " + this.leading + ", insertionIndex: " + this.getInsertionIndex();
   }
}
