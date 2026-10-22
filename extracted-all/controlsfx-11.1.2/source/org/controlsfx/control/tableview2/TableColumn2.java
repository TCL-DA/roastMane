package org.controlsfx.control.tableview2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;

public class TableColumn2<S, T> extends TableColumn<S, T> {
   private final ObjectProperty<Node> southNode = new SimpleObjectProperty(this, "southNode", null);

   public TableColumn2() {
   }

   public TableColumn2(String text) {
      this();
      this.setText(text);
   }

   public final void setSouthNode(Node value) {
      this.southNode.set(value);
   }

   public final Node getSouthNode() {
      return (Node)this.southNode.get();
   }

   public final ObjectProperty<Node> southNodeProperty() {
      return this.southNode;
   }
}
