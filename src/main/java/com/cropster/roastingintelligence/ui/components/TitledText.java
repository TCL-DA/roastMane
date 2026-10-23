package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.text.TextFlow;

public class TitledText extends AbstractTitled {
   private final Property<TextFlow> textFlow = new SimpleObjectProperty(new TextFlow());

   public TitledText() {
      this.getStyleClass().add("titled-text");
      ((TextFlow)this.textFlow.getValue()).getStyleClass().add("text");
      this.getChildren().add((Node)this.textFlow.getValue());
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   public TitledText(String title) {
      this();
      this.setTitle(title);
   }

   public ReadOnlyProperty<TextFlow> textFlowProperty() {
      return this.textFlow;
   }

   public TextFlow getTextFlow() {
      return (TextFlow)this.textFlow.getValue();
   }
}
