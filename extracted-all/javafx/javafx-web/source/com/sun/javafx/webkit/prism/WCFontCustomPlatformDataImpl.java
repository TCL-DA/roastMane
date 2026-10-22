package com.sun.javafx.webkit.prism;

import com.sun.javafx.font.FontFactory;
import com.sun.javafx.font.PGFont;
import com.sun.prism.GraphicsPipeline;
import com.sun.webkit.graphics.WCFont;
import com.sun.webkit.graphics.WCFontCustomPlatformData;
import java.io.IOException;
import java.io.InputStream;

final class WCFontCustomPlatformDataImpl extends WCFontCustomPlatformData {
   private final PGFont font;

   WCFontCustomPlatformDataImpl(InputStream var1) throws IOException {
      FontFactory var2 = GraphicsPipeline.getPipeline().getFontFactory();
      PGFont[] var3 = var2.loadEmbeddedFont(null, var1, 10.0F, false, false);
      if (var3 == null) {
         throw new IOException("Error loading font");
      } else {
         this.font = var3[0];
      }
   }

   @Override
   protected WCFont createFont(int var1, boolean var2, boolean var3) {
      FontFactory var4 = GraphicsPipeline.getPipeline().getFontFactory();
      return new WCFontImpl(var4.deriveFont(this.font, var2, var3, (float)var1));
   }
}
