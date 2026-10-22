package javafx.scene.control;

import javafx.scene.Node;

public interface Skin<C extends Skinnable> {
   C getSkinnable();

   Node getNode();

   void dispose();
}
