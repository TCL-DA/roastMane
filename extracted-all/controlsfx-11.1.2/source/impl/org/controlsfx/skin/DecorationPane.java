package impl.org.controlsfx.skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.control.decoration.Decorator;

public class DecorationPane extends StackPane {
   private final Map<Node, List<Node>> nodeDecorationMap = new WeakHashMap<>();
   ChangeListener<Boolean> visibilityListener = new ChangeListener<Boolean>() {
      public void changed(ObservableValue<? extends Boolean> o, Boolean wasVisible, Boolean isVisible) {
         BooleanProperty p = (BooleanProperty)o;
         Node n = (Node)p.getBean();
         DecorationPane.this.removeAllDecorationsOnNode(n, Decorator.getDecorations(n));
         Decorator.removeAllDecorations(n);
      }
   };

   public DecorationPane() {
      this.setBackground(null);
   }

   public void setRoot(Node root) {
      this.getChildren().setAll(new Node[]{root});
   }

   public void updateDecorationsOnNode(Node targetNode, List<Decoration> added, List<Decoration> removed) {
      this.removeAllDecorationsOnNode(targetNode, removed);
      this.addAllDecorationsOnNode(targetNode, added);
   }

   private void showDecoration(Node targetNode, Decoration decoration) {
      Node decorationNode = decoration.applyDecoration(targetNode);
      if (decorationNode != null) {
         List<Node> decorationNodes = this.nodeDecorationMap.get(targetNode);
         if (decorationNodes == null) {
            decorationNodes = new ArrayList<>();
            this.nodeDecorationMap.put(targetNode, decorationNodes);
         }

         decorationNodes.add(decorationNode);
         if (!this.getChildren().contains(decorationNode)) {
            this.getChildren().add(decorationNode);
            StackPane.setAlignment(decorationNode, Pos.TOP_LEFT);
         }
      }

      targetNode.visibleProperty().addListener(this.visibilityListener);
   }

   private void removeAllDecorationsOnNode(Node targetNode, List<Decoration> decorations) {
      if (decorations != null && targetNode != null) {
         List<Node> decorationNodes = this.nodeDecorationMap.remove(targetNode);
         if (decorationNodes != null) {
            for (Node decorationNode : decorationNodes) {
               boolean success = this.getChildren().remove(decorationNode);
               if (!success) {
                  throw new IllegalStateException(
                     "Could not remove decoration " + decorationNode + " from decoration pane children list: " + this.getChildren()
                  );
               }
            }
         }

         for (Decoration decoration : decorations) {
            decoration.removeDecoration(targetNode);
         }
      }
   }

   private void addAllDecorationsOnNode(Node targetNode, List<Decoration> decorations) {
      if (decorations != null) {
         for (Decoration decoration : decorations) {
            this.showDecoration(targetNode, decoration);
         }
      }
   }
}
