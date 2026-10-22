package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.Tooltip;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class DetachableSidebar extends BorderPane implements Initializable {
   private final BooleanProperty detached = new SimpleBooleanProperty();
   @FXML
   protected VBox headerBox;
   @FXML
   protected Button attachButton;
   @FXML
   protected Button detachButton;
   private final StringProperty title = new SimpleStringProperty();
   private SlidingPanel parent;
   private Stage detachedWindow;
   private BorderPane detachedRoot;
   private HBox footerBox = null;

   public DetachableSidebar() {
      this.parentProperty().addListener((s, o, n) -> {
         if (n != null && !(n instanceof SlidingPanel) && n != this.detachedRoot) {
            throw new IllegalStateException(String.format("Parent of %s must not be changed!", this.getClass().getSimpleName()));
         }
      });
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.attachButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Attach sidebar")));
      this.detachButton.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Detach sidebar")));
      JavaFxUtil.configureTooltips(this);
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   public HBox getFooterBox() {
      if (this.footerBox == null) {
         this.footerBox = new HBox();
         this.footerBox.getStyleClass().add("sidebar-footer");
         this.footerBox.setAlignment(Pos.CENTER_LEFT);
         this.setBottom(this.footerBox);
      }

      return this.footerBox;
   }

   public StringProperty titleProperty() {
      return this.title;
   }

   public String getTitle() {
      return (String)this.title.get();
   }

   public void setTitle(String title) {
      this.title.set(title);
   }

   public void showWindow() {
      this.parent = (SlidingPanel)this.getParent();
      this.parent.getChildren().remove(this);
      if (this.detachedWindow == null) {
         this.detachedRoot = new BorderPane();
         this.detachedRoot.getStyleClass().add("root--detached-sidebar");
         this.detachedWindow = new Stage();
         this.detachedWindow.titleProperty().bind(this.titleProperty());
         this.detachedWindow.setMinWidth(200.0);
         this.detachedWindow.setMinHeight(400.0);
         this.detachedWindow.setScene(new Scene(this.detachedRoot));
         this.detachedWindow.getScene().getStylesheets().add(JavaFxController.STYLE_SHEET);
         this.detachedWindow.setOnCloseRequest(e -> {
            this.hideWindow();
            this.setVisible(false);
         });
      }

      this.detachedRoot.setCenter(this);
      this.detachButton.setVisible(!this.detachButton.isVisible());
      this.attachButton.setVisible(!this.attachButton.isVisible());
      this.detached.set(true);
      this.detachedWindow.show();
   }

   @FXML
   void detach() {
      this.showWindow();
      this.parent.slideIn();
   }

   public void hideWindow() {
      this.detachedWindow.hide();
      this.detachedRoot.setCenter(null);
      this.attachButton.setVisible(!this.attachButton.isVisible());
      this.detachButton.setVisible(!this.detachButton.isVisible());
      this.setVisible(false);
      this.parent.getChildren().add(this);
      this.detached.set(false);
   }

   @FXML
   void attach() {
      this.hideWindow();
      Platform.runLater(() -> this.parent.toggleSlidingPanel(this));
   }

   public ReadOnlyBooleanProperty detachedProperty() {
      return this.detached;
   }

   public boolean isDetached() {
      return this.detached.get();
   }

   public Stage getDetachedWindow() {
      return this.detachedWindow;
   }

   protected SlidingPanel getSlidingPanel() {
      return this.parent;
   }
}
