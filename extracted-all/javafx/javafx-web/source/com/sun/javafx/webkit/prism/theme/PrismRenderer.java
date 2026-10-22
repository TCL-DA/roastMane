package com.sun.javafx.webkit.prism.theme;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.webkit.theme.Renderer;
import com.sun.prism.Graphics;
import com.sun.webkit.graphics.WCGraphicsContext;
import javafx.scene.control.Control;

public final class PrismRenderer extends Renderer {
   @Override
   protected void render(Control var1, WCGraphicsContext var2) {
      SceneHelper.setAllowPGAccess(true);
      NGNode var3 = NodeHelper.getPeer(var1);
      SceneHelper.setAllowPGAccess(false);
      var3.render((Graphics)var2.getPlatformGraphics());
   }
}
