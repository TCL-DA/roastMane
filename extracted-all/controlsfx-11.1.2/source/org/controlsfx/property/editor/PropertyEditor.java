package org.controlsfx.property.editor;

import javafx.scene.Node;

public interface PropertyEditor<T> {
   Node getEditor();

   T getValue();

   void setValue(T var1);
}
