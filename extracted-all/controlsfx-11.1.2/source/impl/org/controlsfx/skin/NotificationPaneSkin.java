package impl.org.controlsfx.skin;

import impl.org.controlsfx.ImplUtils;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;

public class NotificationPaneSkin extends SkinBase<NotificationPane> {
   private NotificationBar notificationBar;
   private Node content;
   private Rectangle clip = new Rectangle();

   public NotificationPaneSkin(final NotificationPane control) {
      super(control);
      this.notificationBar = new NotificationBar() {
         @Override
         public void requestContainerLayout() {
            control.requestLayout();
         }

         @Override
         public String getText() {
            return control.getText();
         }

         @Override
         public Node getGraphic() {
            return control.getGraphic();
         }

         @Override
         public ObservableList<Action> getActions() {
            return control.getActions();
         }

         @Override
         public boolean isShowing() {
            return control.isShowing();
         }

         @Override
         public boolean isShowFromTop() {
            return control.isShowFromTop();
         }

         @Override
         public void hide() {
            control.hide();
         }

         @Override
         public boolean isCloseButtonVisible() {
            return control.isCloseButtonVisible();
         }

         @Override
         public double getContainerHeight() {
            return control.getHeight();
         }

         @Override
         public void relocateInParent(double x, double y) {
            NotificationPaneSkin.this.notificationBar.relocate(x, y);
         }
      };
      control.setClip(this.clip);
      this.updateContent();
      this.registerChangeListener(control.heightProperty(), e -> {
         if (((NotificationPane)this.getSkinnable()).isShowing() && !((NotificationPane)this.getSkinnable()).isShowFromTop()) {
            this.notificationBar.requestLayout();
         }
      });
      this.registerChangeListener(control.contentProperty(), e -> this.updateContent());
      this.registerChangeListener(control.textProperty(), e -> this.notificationBar.label.setText(((NotificationPane)this.getSkinnable()).getText()));
      this.registerChangeListener(control.graphicProperty(), e -> this.notificationBar.label.setGraphic(((NotificationPane)this.getSkinnable()).getGraphic()));
      this.registerChangeListener(control.showingProperty(), e -> {
         if (((NotificationPane)this.getSkinnable()).isShowing()) {
            this.notificationBar.doShow();
         } else {
            this.notificationBar.doHide();
         }
      });
      this.registerChangeListener(control.showFromTopProperty(), e -> {
         if (((NotificationPane)this.getSkinnable()).isShowing()) {
            ((NotificationPane)this.getSkinnable()).requestLayout();
         }
      });
      this.registerChangeListener(control.closeButtonVisibleProperty(), e -> this.notificationBar.updatePane());
      ((NotificationPane)this.getSkinnable()).focusedProperty().addListener((o, ov, hasFocus) -> {
         if (hasFocus && this.getChildren().size() > 0) {
            ((Node)this.getChildren().get(0)).requestFocus();
         }
      });
      EventHandler<KeyEvent> keyPressEventHandler = event -> {
         if (KeyCode.TAB.equals(event.getCode()) && this.content != null && this.content.isFocused() && event.isShiftDown()) {
            ImplUtils.focusPreviousSibling(this.getSkinnable());
         }
      };
      ((NotificationPane)this.getSkinnable()).addEventHandler(KeyEvent.KEY_PRESSED, keyPressEventHandler);
   }

   private void updateContent() {
      this.content = ((NotificationPane)this.getSkinnable()).getContent();
      if (this.content == null) {
         this.getChildren().setAll(new Node[]{this.notificationBar});
      } else {
         this.getChildren().setAll(new Node[]{this.content, this.notificationBar});
      }
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      double notificationBarHeight = this.notificationBar.prefHeight(w);
      this.notificationBar.resize(w, notificationBarHeight);
      if (this.content != null) {
         this.content.resizeRelocate(x, y, w, h);
      }

      this.clip.setX(x);
      this.clip.setY(y);
      this.clip.setWidth(w);
      this.clip.setHeight(h);
   }

   protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.content == null ? 0.0 : this.content.minWidth(height);
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.content == null ? 0.0 : this.content.minHeight(width);
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.content == null ? 0.0 : this.content.prefWidth(height);
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.content == null ? 0.0 : this.content.prefHeight(width);
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.content == null ? 0.0 : this.content.maxWidth(height);
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return this.content == null ? 0.0 : this.content.maxHeight(width);
   }
}
