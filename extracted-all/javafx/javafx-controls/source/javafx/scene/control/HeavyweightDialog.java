package javafx.scene.control;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

class HeavyweightDialog extends FXDialog {
   final Stage stage = new Stage() {
      public void centerOnScreen() {
         Window var1 = HeavyweightDialog.this.getOwner();
         if (var1 != null) {
            HeavyweightDialog.this.positionStage();
         } else if (this.getWidth() > 0.0 && this.getHeight() > 0.0) {
            super.centerOnScreen();
         }
      }
   };
   private Scene scene;
   private final Parent DUMMY_ROOT = new Region();
   private final Dialog<?> dialog;
   private DialogPane dialogPane;
   private double prefX = Double.NaN;
   private double prefY = Double.NaN;

   HeavyweightDialog(Dialog<?> var1) {
      this.dialog = var1;
      this.stage.setResizable(false);
      this.stage.setOnCloseRequest(var2 -> {
         if (this.requestPermissionToClose(var1)) {
            var1.close();
         } else {
            var2.consume();
         }
      });
      this.stage.addEventHandler(KeyEvent.KEY_PRESSED, var2 -> {
         if (var2.getCode() == KeyCode.ESCAPE && !var2.isConsumed() && this.requestPermissionToClose(var1)) {
            var1.close();
            var2.consume();
         }
      });
   }

   @Override
   void initStyle(StageStyle var1) {
      this.stage.initStyle(var1);
   }

   @Override
   StageStyle getStyle() {
      return this.stage.getStyle();
   }

   @Override
   public void initOwner(Window var1) {
      this.updateStageBindings(this.stage.getOwner(), var1);
      this.stage.initOwner(var1);
   }

   @Override
   public Window getOwner() {
      return this.stage.getOwner();
   }

   @Override
   public void initModality(Modality var1) {
      this.stage.initModality(var1 == null ? Modality.APPLICATION_MODAL : var1);
   }

   @Override
   public Modality getModality() {
      return this.stage.getModality();
   }

   @Override
   public void setDialogPane(DialogPane var1) {
      this.dialogPane = var1;
      if (this.scene == null) {
         this.scene = new Scene(var1);
         this.stage.setScene(this.scene);
      } else {
         this.scene.setRoot(var1);
      }

      var1.autosize();
      this.stage.sizeToScene();
   }

   @Override
   public void show() {
      this.scene.setRoot(this.dialogPane);
      this.stage.centerOnScreen();
      this.stage.show();
   }

   @Override
   public void showAndWait() {
      this.scene.setRoot(this.dialogPane);
      this.stage.centerOnScreen();
      this.stage.showAndWait();
   }

   @Override
   public void close() {
      if (this.stage.isShowing()) {
         this.stage.hide();
      }

      if (this.scene != null) {
         this.scene.setRoot(this.DUMMY_ROOT);
      }
   }

   @Override
   public ReadOnlyBooleanProperty showingProperty() {
      return this.stage.showingProperty();
   }

   @Override
   public Window getWindow() {
      return this.stage;
   }

   @Override
   public Node getRoot() {
      return this.stage.getScene().getRoot();
   }

   @Override
   public double getX() {
      return this.stage.getX();
   }

   @Override
   public void setX(double var1) {
      this.stage.setX(var1);
   }

   @Override
   public ReadOnlyDoubleProperty xProperty() {
      return this.stage.xProperty();
   }

   @Override
   public double getY() {
      return this.stage.getY();
   }

   @Override
   public void setY(double var1) {
      this.stage.setY(var1);
   }

   @Override
   public ReadOnlyDoubleProperty yProperty() {
      return this.stage.yProperty();
   }

   @Override
   ReadOnlyDoubleProperty heightProperty() {
      return this.stage.heightProperty();
   }

   @Override
   void setHeight(double var1) {
      this.stage.setHeight(var1);
   }

   @Override
   double getSceneHeight() {
      return this.scene == null ? 0.0 : this.scene.getHeight();
   }

   @Override
   ReadOnlyDoubleProperty widthProperty() {
      return this.stage.widthProperty();
   }

   @Override
   void setWidth(double var1) {
      this.stage.setWidth(var1);
   }

   @Override
   BooleanProperty resizableProperty() {
      return this.stage.resizableProperty();
   }

   @Override
   StringProperty titleProperty() {
      return this.stage.titleProperty();
   }

   @Override
   ReadOnlyBooleanProperty focusedProperty() {
      return this.stage.focusedProperty();
   }

   @Override
   public void sizeToScene() {
      this.stage.sizeToScene();
   }

   private void positionStage() {
      double var1 = this.getX();
      double var3 = this.getY();
      if (!Double.isNaN(var1) && !Double.isNaN(var3) && Double.compare(var1, this.prefX) != 0 && Double.compare(var3, this.prefY) != 0) {
         this.setX(var1);
         this.setY(var3);
      } else {
         this.dialogPane.applyCss();
         this.dialogPane.layout();
         Window var5 = this.getOwner();
         Scene var6 = var5.getScene();
         double var7 = var6.getY();
         double var9 = this.dialogPane.prefWidth(-1.0);
         double var11 = this.dialogPane.prefHeight(var9);
         var1 = var5.getX() + var6.getWidth() / 2.0 - var9 / 2.0;
         var3 = var5.getY() + var7 / 2.0 + var6.getHeight() / 2.0 - var11 / 2.0;
         this.prefX = var1;
         this.prefY = var3;
         this.setX(var1);
         this.setY(var3);
      }
   }

   private void updateStageBindings(Window var1, Window var2) {
      Scene var3 = this.stage.getScene();
      if (var1 != null && var1 instanceof Stage) {
         Stage var4 = (Stage)var1;
         Bindings.unbindContent(this.stage.getIcons(), var4.getIcons());
         this.stage.renderScaleXProperty().unbind();
         this.stage.renderScaleYProperty().unbind();
         Scene var5 = var4.getScene();
         if (this.scene != null && var3 != null) {
            Bindings.unbindContent(var3.getStylesheets(), var5.getStylesheets());
         }
      }

      if (var2 instanceof Stage) {
         Stage var6 = (Stage)var2;
         Bindings.bindContent(this.stage.getIcons(), var6.getIcons());
         this.stage.renderScaleXProperty().bind(var6.renderScaleXProperty());
         this.stage.renderScaleYProperty().bind(var6.renderScaleYProperty());
         Scene var7 = var6.getScene();
         if (this.scene != null && var3 != null) {
            Bindings.bindContent(var3.getStylesheets(), var7.getStylesheets());
         }
      }
   }
}
