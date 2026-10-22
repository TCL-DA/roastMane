package org.jfree.fx;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.text.CharacterIterator;
import javafx.geometry.Bounds;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class FXFontMetrics extends FontMetrics {
   private Graphics2D g2;

   public FXFontMetrics(Font font, Graphics2D g2) {
      super(font);
      this.g2 = g2;
   }

   @Override
   public Rectangle2D getStringBounds(CharacterIterator ci, int beginIndex, int limit, Graphics context) {
      char[] arr = new char[limit - beginIndex];
      ci.setIndex(beginIndex);

      for (int idx = 0; idx < arr.length; idx++) {
         arr[idx] = ci.current();
         ci.next();
      }

      return this.getStringBounds(arr, beginIndex, limit, context);
   }

   @Override
   public Rectangle2D getStringBounds(char[] chars, int beginIndex, int limit, Graphics context) {
      String str = new String(chars, beginIndex, limit - beginIndex);
      return this.getStringBounds(str, context);
   }

   @Override
   public Rectangle2D getStringBounds(String str, int beginIndex, int limit, Graphics context) {
      String substr = str.substring(beginIndex, limit);
      return super.getStringBounds(substr, context);
   }

   @Override
   public Rectangle2D getStringBounds(String str, Graphics context) {
      Text text = new Text(str);
      FontWeight weight = this.font.isBold() ? FontWeight.BOLD : FontWeight.NORMAL;
      FontPosture posture = this.font.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR;
      javafx.scene.text.Font jfxfont = javafx.scene.text.Font.font(this.font.getFamily(), weight, posture, (double)this.font.getSize());
      text.setFont(jfxfont);
      Bounds b = text.getLayoutBounds();
      return new Double(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
   }

   @Override
   public int stringWidth(String str) {
      return (int)this.getStringBounds(str, this.g2).getWidth();
   }
}
