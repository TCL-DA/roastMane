package impl.org.controlsfx.skin;

import javafx.beans.property.ObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.StackPane;
import javafx.scene.text.HitInfo;

public abstract class CustomTextFieldSkin extends TextFieldSkin {
   private static final PseudoClass HAS_NO_SIDE_NODE = PseudoClass.getPseudoClass("no-side-nodes");
   private static final PseudoClass HAS_LEFT_NODE = PseudoClass.getPseudoClass("left-node-visible");
   private static final PseudoClass HAS_RIGHT_NODE = PseudoClass.getPseudoClass("right-node-visible");
   private Node left;
   private StackPane leftPane;
   private Node right;
   private StackPane rightPane;
   private final TextField control;

   public CustomTextFieldSkin(TextField control) {
      super(control);
      this.control = control;
      this.updateChildren();
      this.registerChangeListener(this.leftProperty(), e -> this.updateChildren());
      this.registerChangeListener(this.rightProperty(), e -> this.updateChildren());
   }

   public abstract ObjectProperty<Node> leftProperty();

   public abstract ObjectProperty<Node> rightProperty();

   private void updateChildren() {
      Node newLeft = (Node)this.leftProperty().get();
      this.getChildren().remove(this.leftPane);
      if (newLeft != null) {
         this.leftPane = new StackPane(new Node[]{newLeft});
         this.leftPane.setManaged(false);
         this.leftPane.setAlignment(Pos.CENTER_LEFT);
         this.leftPane.getStyleClass().add("left-pane");
         this.getChildren().add(this.leftPane);
         this.left = newLeft;
      } else {
         this.leftPane = null;
         this.left = null;
      }

      Node newRight = (Node)this.rightProperty().get();
      this.getChildren().remove(this.rightPane);
      if (newRight != null) {
         this.rightPane = new StackPane(new Node[]{newRight});
         this.rightPane.setManaged(false);
         this.rightPane.setAlignment(Pos.CENTER_RIGHT);
         this.rightPane.getStyleClass().add("right-pane");
         this.getChildren().add(this.rightPane);
         this.right = newRight;
      } else {
         this.rightPane = null;
         this.right = null;
      }

      this.control.pseudoClassStateChanged(HAS_LEFT_NODE, this.left != null);
      this.control.pseudoClassStateChanged(HAS_RIGHT_NODE, this.right != null);
      this.control.pseudoClassStateChanged(HAS_NO_SIDE_NODE, this.left == null && this.right == null);
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      double fullHeight = h + this.snappedTopInset() + this.snappedBottomInset();
      double leftWidth = this.leftPane == null ? 0.0 : this.snapSize(this.leftPane.prefWidth(fullHeight));
      double rightWidth = this.rightPane == null ? 0.0 : this.snapSize(this.rightPane.prefWidth(fullHeight));
      double textFieldStartX = this.snapPosition(x) + this.snapSize(leftWidth);
      double textFieldWidth = w - this.snapSize(leftWidth) - this.snapSize(rightWidth);
      super.layoutChildren(textFieldStartX, 0.0, textFieldWidth, fullHeight);
      if (this.leftPane != null) {
         double leftStartX = 0.0;
         this.leftPane.resizeRelocate(0.0, 0.0, leftWidth, fullHeight);
      }

      if (this.rightPane != null) {
         double rightStartX = this.rightPane == null ? 0.0 : w - rightWidth + this.snappedLeftInset();
         this.rightPane.resizeRelocate(rightStartX, 0.0, rightWidth, fullHeight);
      }
   }

   public HitInfo getIndex(double x, double y) {
      double leftWidth = this.leftPane == null ? 0.0 : this.snapSize(this.leftPane.prefWidth(((TextField)this.getSkinnable()).getHeight()));
      return super.getIndex(x - leftWidth, y);
   }

   protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
      double pw = super.computePrefWidth(h, topInset, rightInset, bottomInset, leftInset);
      double leftWidth = this.leftPane == null ? 0.0 : this.snapSize(this.leftPane.prefWidth(h));
      double rightWidth = this.rightPane == null ? 0.0 : this.snapSize(this.rightPane.prefWidth(h));
      return pw + leftWidth + rightWidth;
   }

   protected double computePrefHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
      double ph = super.computePrefHeight(w, topInset, rightInset, bottomInset, leftInset);
      double leftHeight = this.leftPane == null ? 0.0 : this.snapSize(this.leftPane.prefHeight(-1.0));
      double rightHeight = this.rightPane == null ? 0.0 : this.snapSize(this.rightPane.prefHeight(-1.0));
      return Math.max(ph, Math.max(leftHeight, rightHeight));
   }

   protected double computeMinWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
      double mw = super.computeMinWidth(h, topInset, rightInset, bottomInset, leftInset);
      double leftWidth = this.leftPane == null ? 0.0 : this.snapSize(this.leftPane.minWidth(h));
      double rightWidth = this.rightPane == null ? 0.0 : this.snapSize(this.rightPane.minWidth(h));
      return mw + leftWidth + rightWidth;
   }

   protected double computeMinHeight(double w, double topInset, double rightInset, double bottomInset, double leftInset) {
      double mh = super.computeMinHeight(w, topInset, rightInset, bottomInset, leftInset);
      double leftHeight = this.leftPane == null ? 0.0 : this.snapSize(this.leftPane.minHeight(-1.0));
      double rightHeight = this.rightPane == null ? 0.0 : this.snapSize(this.rightPane.minHeight(-1.0));
      return Math.max(mh, Math.max(leftHeight, rightHeight));
   }
}
