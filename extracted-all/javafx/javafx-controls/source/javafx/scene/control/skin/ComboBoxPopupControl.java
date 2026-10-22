package javafx.scene.control.skin;

import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.FakeFocusTextField;
import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import com.sun.javafx.util.Utils;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

public abstract class ComboBoxPopupControl<T> extends ComboBoxBaseSkin<T> {
   PopupControl popup;
   private boolean popupNeedsReconfiguring = true;
   private final ComboBoxBase<T> comboBoxBase;
   private TextField textField;
   private String initialTextFieldValue = null;
   private EventHandler<MouseEvent> textFieldMouseEventHandler = var1x -> {
      ComboBoxBase var2 = this.getSkinnable();
      if (!var1x.getTarget().equals(var2)) {
         var2.fireEvent(var1x.copyFor(var2, var2));
         var1x.consume();
      }
   };
   private EventHandler<DragEvent> textFieldDragEventHandler = var1x -> {
      ComboBoxBase var2 = this.getSkinnable();
      if (!var1x.getTarget().equals(var2)) {
         var2.fireEvent(var1x.copyFor(var2, var2));
         var1x.consume();
      }
   };

   public ComboBoxPopupControl(ComboBoxBase<T> var1) {
      super(var1);
      this.comboBoxBase = var1;
      this.textField = this.getEditor() != null ? this.getEditableInputNode() : null;
      if (this.textField != null) {
         this.getChildren().add(this.textField);
      }

      this.comboBoxBase.focusedProperty().addListener((var1x, var2, var3) -> {
         if (this.getEditor() != null) {
            ((FakeFocusTextField)this.textField).setFakeFocus(var3);
         }
      });
      this.comboBoxBase.addEventFilter(KeyEvent.ANY, var1x -> {
         if (this.textField != null && this.getEditor() != null) {
            if (var1x.getTarget().equals(this.textField)) {
               return;
            }

            switch (var1x.getCode()) {
               case ESCAPE:
               case F10:
                  break;
               case ENTER:
                  this.handleKeyEvent(var1x, true);
                  break;
               default:
                  this.textField.fireEvent(var1x.copyFor(this.textField, this.textField));
                  var1x.consume();
            }
         } else {
            this.handleKeyEvent(var1x, false);
         }
      });
      if (this.comboBoxBase.getOnInputMethodTextChanged() == null) {
         this.comboBoxBase
            .setOnInputMethodTextChanged(
               var1x -> {
                  if (this.textField != null
                     && this.getEditor() != null
                     && this.comboBoxBase.getScene().getFocusOwner() == this.comboBoxBase
                     && this.textField.getOnInputMethodTextChanged() != null) {
                     this.textField.getOnInputMethodTextChanged().handle(var1x);
                  }
               }
            );
      }

      ParentHelper.setTraversalEngine(this.comboBoxBase, new ParentTraversalEngine(this.comboBoxBase, new Algorithm() {
         public Node select(Node var1, Direction var2, TraversalContext var3) {
            return null;
         }

         public Node selectFirst(TraversalContext var1) {
            return null;
         }

         public Node selectLast(TraversalContext var1) {
            return null;
         }
      }));
      this.updateEditable();
   }

   protected abstract Node getPopupContent();

   protected abstract TextField getEditor();

   protected abstract StringConverter<T> getConverter();

   @Override
   public void show() {
      if (this.getSkinnable() == null) {
         throw new IllegalStateException("ComboBox is null");
      } else {
         Node var1 = this.getPopupContent();
         if (var1 == null) {
            throw new IllegalStateException("Popup node is null");
         } else if (!this.getPopup().isShowing()) {
            this.positionAndShowPopup();
         }
      }
   }

   @Override
   public void hide() {
      if (this.popup != null && this.popup.isShowing()) {
         this.popup.hide();
      }
   }

   PopupControl getPopup() {
      if (this.popup == null) {
         this.createPopup();
      }

      return this.popup;
   }

   TextField getEditableInputNode() {
      if (this.textField == null && this.getEditor() != null) {
         this.textField = this.getEditor();
         this.textField.setFocusTraversable(false);
         this.textField.promptTextProperty().bind(this.comboBoxBase.promptTextProperty());
         this.textField.tooltipProperty().bind(this.comboBoxBase.tooltipProperty());
         this.textField.getProperties().put("TextInputControlBehavior.disableForwardToParent", true);
         this.initialTextFieldValue = this.textField.getText();
      }

      return this.textField;
   }

   void setTextFromTextFieldIntoComboBoxValue() {
      if (this.getEditor() != null) {
         StringConverter var1 = this.getConverter();
         if (var1 != null) {
            Object var2 = this.comboBoxBase.getValue();
            Object var3 = var2;
            String var4 = this.textField.getText();
            if (var2 != null || var4 != null && !var4.isEmpty()) {
               try {
                  var3 = var1.fromString(var4);
               } catch (Exception var6) {
               }
            } else {
               var3 = null;
            }

            if ((var3 != null || var2 != null) && (var3 == null || !var3.equals(var2))) {
               this.comboBoxBase.setValue((T)var3);
            }

            this.updateDisplayNode();
         }
      }
   }

   void updateDisplayNode() {
      if (this.textField != null && this.getEditor() != null) {
         Object var1 = this.comboBoxBase.getValue();
         StringConverter var2 = this.getConverter();
         if (this.initialTextFieldValue != null && !this.initialTextFieldValue.isEmpty()) {
            this.textField.setText(this.initialTextFieldValue);
            this.initialTextFieldValue = null;
         } else {
            String var3 = var2.toString(var1);
            if (var1 == null || var3 == null) {
               this.textField.setText("");
            } else if (!var3.equals(this.textField.getText())) {
               this.textField.setText(var3);
            }
         }
      }
   }

   void updateEditable() {
      final TextField var1 = this.getEditor();
      if (this.getEditor() == null) {
         if (this.textField != null) {
            this.textField.removeEventFilter(MouseEvent.DRAG_DETECTED, this.textFieldMouseEventHandler);
            this.textField.removeEventFilter(DragEvent.ANY, this.textFieldDragEventHandler);
            this.comboBoxBase.setInputMethodRequests(null);
         }
      } else if (var1 != null) {
         var1.addEventFilter(MouseEvent.DRAG_DETECTED, this.textFieldMouseEventHandler);
         var1.addEventFilter(DragEvent.ANY, this.textFieldDragEventHandler);
         this.comboBoxBase.setInputMethodRequests(new ExtendedInputMethodRequests() {
            public Point2D getTextLocation(int var1x) {
               return var1.getInputMethodRequests().getTextLocation(var1x);
            }

            public int getLocationOffset(int var1x, int var2) {
               return var1.getInputMethodRequests().getLocationOffset(var1x, var2);
            }

            public void cancelLatestCommittedText() {
               var1.getInputMethodRequests().cancelLatestCommittedText();
            }

            public String getSelectedText() {
               return var1.getInputMethodRequests().getSelectedText();
            }

            public int getInsertPositionOffset() {
               return ((ExtendedInputMethodRequests)var1.getInputMethodRequests()).getInsertPositionOffset();
            }

            public String getCommittedText(int var1x, int var2) {
               return ((ExtendedInputMethodRequests)var1.getInputMethodRequests()).getCommittedText(var1x, var2);
            }

            public int getCommittedTextLength() {
               return ((ExtendedInputMethodRequests)var1.getInputMethodRequests()).getCommittedTextLength();
            }
         });
      }

      this.textField = var1;
   }

   private Point2D getPrefPopupPosition() {
      return Utils.pointRelativeTo(this.getSkinnable(), this.getPopupContent(), HPos.CENTER, VPos.BOTTOM, 0.0, 0.0, true);
   }

   private void positionAndShowPopup() {
      ComboBoxBase var1 = this.getSkinnable();
      if (var1.getScene() != null) {
         PopupControl var2 = this.getPopup();
         var2.getScene().setNodeOrientation(this.getSkinnable().getEffectiveNodeOrientation());
         Node var3 = this.getPopupContent();
         this.sizePopup();
         Point2D var4 = this.getPrefPopupPosition();
         this.popupNeedsReconfiguring = true;
         this.reconfigurePopup();
         var2.show(var1.getScene().getWindow(), this.snapPositionX(var4.getX()), this.snapPositionY(var4.getY()));
         var3.requestFocus();
         this.sizePopup();
      }
   }

   private void sizePopup() {
      Node var1 = this.getPopupContent();
      if (var1 instanceof Region) {
         Region var2 = (Region)var1;
         double var3 = this.snapSizeY(var2.prefHeight(0.0));
         double var5 = this.snapSizeY(var2.minHeight(0.0));
         double var7 = this.snapSizeY(var2.maxHeight(0.0));
         double var9 = this.snapSizeY(Math.min(Math.max(var3, var5), Math.max(var5, var7)));
         double var11 = this.snapSizeX(var2.prefWidth(var9));
         double var13 = this.snapSizeX(var2.minWidth(var9));
         double var15 = this.snapSizeX(var2.maxWidth(var9));
         double var17 = this.snapSizeX(Math.min(Math.max(var11, var13), Math.max(var13, var15)));
         var1.resize(var17, var9);
      } else {
         var1.autosize();
      }
   }

   private void createPopup() {
      this.popup = new PopupControl() {
         {
            this.setSkin(new Skin<Skinnable>() {
               @Override
               public Skinnable getSkinnable() {
                  return ComboBoxPopupControl.this.getSkinnable();
               }

               @Override
               public Node getNode() {
                  return ComboBoxPopupControl.this.getPopupContent();
               }

               @Override
               public void dispose() {
               }
            });
         }

         @Override
         public Styleable getStyleableParent() {
            return ComboBoxPopupControl.this.getSkinnable();
         }
      };
      this.popup.getStyleClass().add("combo-box-popup");
      this.popup.setConsumeAutoHidingEvents(false);
      this.popup.setAutoHide(true);
      this.popup.setAutoFix(true);
      this.popup.setHideOnEscape(true);
      this.popup.setOnAutoHide(var1x -> this.getBehavior().onAutoHide(this.popup));
      this.popup.addEventHandler(MouseEvent.MOUSE_CLICKED, var1x -> this.getBehavior().onAutoHide(this.popup));
      this.popup.addEventHandler(WindowEvent.WINDOW_HIDDEN, var1x -> this.getSkinnable().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_NODE));
      InvalidationListener var1 = var1x -> {
         this.popupNeedsReconfiguring = true;
         this.reconfigurePopup();
      };
      this.getSkinnable().layoutXProperty().addListener(var1);
      this.getSkinnable().layoutYProperty().addListener(var1);
      this.getSkinnable().widthProperty().addListener(var1);
      this.getSkinnable().heightProperty().addListener(var1);
      this.getSkinnable().sceneProperty().addListener(var1x -> {
         if (((ObservableValue)var1x).getValue() == null) {
            this.hide();
         } else if (this.getSkinnable().isShowing()) {
            this.show();
         }
      });
   }

   void reconfigurePopup() {
      if (this.popup != null) {
         boolean var1 = this.popup.isShowing();
         if (var1) {
            if (this.popupNeedsReconfiguring) {
               this.popupNeedsReconfiguring = false;
               Point2D var2 = this.getPrefPopupPosition();
               Node var3 = this.getPopupContent();
               double var4 = var3.prefWidth(-1.0);
               double var6 = var3.prefHeight(-1.0);
               if (var2.getX() > -1.0) {
                  this.popup.setAnchorX(var2.getX());
               }

               if (var2.getY() > -1.0) {
                  this.popup.setAnchorY(var2.getY());
               }

               if (var4 > -1.0) {
                  this.popup.setMinWidth(var4);
               }

               if (var6 > -1.0) {
                  this.popup.setMinHeight(var6);
               }

               Bounds var8 = var3.getLayoutBounds();
               double var9 = var8.getWidth();
               double var11 = var8.getHeight();
               double var13 = var9 < var4 ? var4 : var9;
               double var15 = var11 < var6 ? var6 : var11;
               if (var13 != var9 || var15 != var11) {
                  var3.resize(var13, var15);
                  if (var3 instanceof Region) {
                     ((Region)var3).setMinSize(var13, var15);
                     ((Region)var3).setPrefSize(var13, var15);
                  }
               }
            }
         }
      }
   }

   private void handleKeyEvent(KeyEvent var1, boolean var2) {
      if (var1.getCode() == KeyCode.ENTER) {
         if (var1.isConsumed() || var1.getEventType() != KeyEvent.KEY_RELEASED) {
            return;
         }

         this.setTextFromTextFieldIntoComboBoxValue();
         if (var2 && this.comboBoxBase.getOnAction() != null) {
            var1.consume();
         } else if (this.textField != null) {
            this.textField.fireEvent(var1);
         }
      } else if ((var1.getCode() == KeyCode.F10 || var1.getCode() == KeyCode.ESCAPE) && var2) {
         var1.consume();
      }
   }
}
