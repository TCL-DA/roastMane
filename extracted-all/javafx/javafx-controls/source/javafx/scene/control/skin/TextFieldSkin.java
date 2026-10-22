package javafx.scene.control.skin;

import com.sun.javafx.scene.control.behavior.PasswordFieldBehavior;
import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.javafx.scene.control.behavior.TextInputControlBehavior;
import com.sun.javafx.tk.FontMetrics;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;

public class TextFieldSkin extends TextInputControlSkin<TextField> {
   private final TextFieldBehavior behavior;
   private Pane textGroup = new Pane();
   private Group handleGroup;
   private Rectangle clip = new Rectangle();
   private Text textNode = new Text();
   private Text promptNode;
   private Path selectionHighlightPath = new Path();
   private Path characterBoundingPath = new Path();
   private ObservableBooleanValue usePromptText;
   private DoubleProperty textTranslateX = new SimpleDoubleProperty(this, "textTranslateX");
   private double caretWidth;
   private ObservableDoubleValue textRight;
   private double pressX;
   private double pressY;
   static final char BULLET = '●';

   public TextFieldSkin(final TextField var1) {
      super(var1);
      this.behavior = (TextFieldBehavior)(var1 instanceof PasswordField ? new PasswordFieldBehavior((PasswordField)var1) : new TextFieldBehavior(var1));
      this.behavior.setTextFieldSkin(this);
      var1.caretPositionProperty().addListener((var2x, var3, var4) -> {
         if (var1.getWidth() > 0.0) {
            this.updateTextNodeCaretPos(var1.getCaretPosition());
            if (!this.isForwardBias()) {
               this.setForwardBias(true);
            }

            this.updateCaretOff();
         }
      });
      this.forwardBiasProperty().addListener(var2x -> {
         if (var1.getWidth() > 0.0) {
            this.updateTextNodeCaretPos(var1.getCaretPosition());
            this.updateCaretOff();
         }
      });
      this.textRight = new DoubleBinding() {
         {
            this.bind(new Observable[]{TextFieldSkin.this.textGroup.widthProperty()});
         }

         protected double computeValue() {
            return TextFieldSkin.this.textGroup.getWidth();
         }
      };
      this.clip.setSmooth(false);
      this.clip.setX(0.0);
      this.clip.widthProperty().bind(this.textGroup.widthProperty());
      this.clip.heightProperty().bind(this.textGroup.heightProperty());
      this.textGroup.setClip(this.clip);
      this.textGroup.getChildren().addAll(new Node[]{this.selectionHighlightPath, this.textNode, new Group(new Node[]{this.caretPath})});
      this.getChildren().add(this.textGroup);
      if (SHOW_HANDLES) {
         this.handleGroup = new Group();
         this.handleGroup.setManaged(false);
         this.handleGroup.getChildren().addAll(new Node[]{this.caretHandle, this.selectionHandle1, this.selectionHandle2});
         this.getChildren().add(this.handleGroup);
      }

      this.textNode.setManaged(false);
      this.textNode.getStyleClass().add("text");
      this.textNode.fontProperty().bind(var1.fontProperty());
      this.textNode.layoutXProperty().bind(this.textTranslateX);
      this.textNode.textProperty().bind(new StringBinding() {
         {
            this.bind(new Observable[]{var1.textProperty()});
         }

         protected String computeValue() {
            return TextFieldSkin.this.maskText(var1.textProperty().getValueSafe());
         }
      });
      this.textNode.fillProperty().bind(this.textFillProperty());
      this.textNode.selectionFillProperty().bind(new ObjectBinding<Paint>() {
         {
            this.bind(new Observable[]{TextFieldSkin.this.highlightTextFillProperty(), TextFieldSkin.this.textFillProperty(), var1.focusedProperty()});
         }

         protected Paint computeValue() {
            return var1.isFocused() ? (Paint)TextFieldSkin.this.highlightTextFillProperty().get() : (Paint)TextFieldSkin.this.textFillProperty().get();
         }
      });
      this.updateTextNodeCaretPos(var1.getCaretPosition());
      var1.selectionProperty().addListener(var1x -> this.updateSelection());
      this.selectionHighlightPath.setManaged(false);
      this.selectionHighlightPath.setStroke(null);
      this.selectionHighlightPath.layoutXProperty().bind(this.textTranslateX);
      this.selectionHighlightPath.visibleProperty().bind(var1.anchorProperty().isNotEqualTo(var1.caretPositionProperty()).and(var1.focusedProperty()));
      this.selectionHighlightPath.fillProperty().bind(this.highlightFillProperty());
      this.textNode.selectionShapeProperty().addListener(var1x -> this.updateSelection());
      this.caretPath.setManaged(false);
      this.caretPath.setStrokeWidth(1.0);
      this.caretPath.fillProperty().bind(this.textFillProperty());
      this.caretPath.strokeProperty().bind(this.textFillProperty());
      this.caretPath.opacityProperty().bind(new DoubleBinding() {
         {
            this.bind(new Observable[]{TextFieldSkin.this.caretVisibleProperty()});
         }

         protected double computeValue() {
            return TextFieldSkin.this.caretVisibleProperty().get() ? 1.0 : 0.0;
         }
      });
      this.caretPath.layoutXProperty().bind(this.textTranslateX);
      this.textNode.caretShapeProperty().addListener(var2x -> {
         this.caretPath.getElements().setAll((PathElement[])this.textNode.caretShapeProperty().get());
         if (this.caretPath.getElements().size() == 0) {
            this.updateTextNodeCaretPos(var1.getCaretPosition());
         } else if (this.caretPath.getElements().size() != 4) {
            this.caretWidth = (double)Math.round(this.caretPath.getLayoutBounds().getWidth());
         }
      });
      var1.fontProperty().addListener(var2x -> {
         var1.requestLayout();
         this.getSkinnable().requestLayout();
      });
      this.registerChangeListener(var1.prefColumnCountProperty(), var1x -> this.getSkinnable().requestLayout());
      if (var1.isFocused()) {
         this.setCaretAnimating(true);
      }

      var1.alignmentProperty().addListener(var2x -> {
         if (var1.getWidth() > 0.0) {
            this.updateTextPos();
            this.updateCaretOff();
            var1.requestLayout();
         }
      });
      this.usePromptText = new BooleanBinding() {
         {
            this.bind(new Observable[]{var1.textProperty(), var1.promptTextProperty(), TextFieldSkin.this.promptTextFillProperty()});
         }

         protected boolean computeValue() {
            String var1x = var1.getText();
            String var2 = var1.getPromptText();
            return (var1x == null || var1x.isEmpty()) && var2 != null && !var2.isEmpty() && !TextFieldSkin.this.getPromptTextFill().equals(Color.TRANSPARENT);
         }
      };
      this.promptTextFillProperty().addListener(var1x -> this.updateTextPos());
      var1.textProperty().addListener(var1x -> {
         if (!this.behavior.isEditing()) {
            this.updateTextPos();
         }
      });
      if (this.usePromptText.get()) {
         this.createPromptNode();
      }

      this.usePromptText.addListener(var2x -> {
         this.createPromptNode();
         var1.requestLayout();
      });
      if (SHOW_HANDLES) {
         this.selectionHandle1.setRotate(180.0);
         EventHandler var2 = var1x -> {
            this.pressX = var1x.getX();
            this.pressY = var1x.getY();
            var1x.consume();
         };
         this.caretHandle.setOnMousePressed(var2);
         this.selectionHandle1.setOnMousePressed(var2);
         this.selectionHandle2.setOnMousePressed(var2);
         this.caretHandle
            .setOnMouseDragged(
               var1x -> {
                  Point2D var2x = new Point2D(
                     this.caretHandle.getLayoutX() + var1x.getX() + this.pressX - this.textNode.getLayoutX(),
                     this.caretHandle.getLayoutY() + var1x.getY() - this.pressY - 6.0
                  );
                  HitInfo var3 = this.textNode.hitTest(var2x);
                  this.positionCaret(var3, false);
                  var1x.consume();
               }
            );
         this.selectionHandle1
            .setOnMouseDragged(
               new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent var1) {
                     TextField var2 = TextFieldSkin.this.getSkinnable();
                     Point2D var3 = TextFieldSkin.this.textNode.localToScene(0.0, 0.0);
                     Point2D var4 = new Point2D(
                        var1.getSceneX() - var3.getX() + 10.0 - TextFieldSkin.this.pressX + TextFieldSkin.this.selectionHandle1.getWidth() / 2.0,
                        var1.getSceneY() - var3.getY() - TextFieldSkin.this.pressY - 6.0
                     );
                     HitInfo var5 = TextFieldSkin.this.textNode.hitTest(var4);
                     if (var2.getAnchor() < var2.getCaretPosition()) {
                        var2.selectRange(var2.getCaretPosition(), var2.getAnchor());
                     }

                     int var6 = var5.getInsertionIndex();
                     if (var6 >= 0) {
                        if (var6 >= var2.getAnchor() - 1) {
                           var6 = Math.max(0, var2.getAnchor() - 1);
                        }

                        TextFieldSkin.this.positionCaret(var6, var5.isLeading(), true);
                     }

                     var1.consume();
                  }
               }
            );
         this.selectionHandle2
            .setOnMouseDragged(
               new EventHandler<MouseEvent>() {
                  public void handle(MouseEvent var1) {
                     TextField var2 = TextFieldSkin.this.getSkinnable();
                     Point2D var3 = TextFieldSkin.this.textNode.localToScene(0.0, 0.0);
                     Point2D var4 = new Point2D(
                        var1.getSceneX() - var3.getX() + 10.0 - TextFieldSkin.this.pressX + TextFieldSkin.this.selectionHandle2.getWidth() / 2.0,
                        var1.getSceneY() - var3.getY() - TextFieldSkin.this.pressY - 6.0
                     );
                     HitInfo var5 = TextFieldSkin.this.textNode.hitTest(var4);
                     if (var2.getAnchor() > var2.getCaretPosition()) {
                        var2.selectRange(var2.getCaretPosition(), var2.getAnchor());
                     }

                     int var6 = var5.getInsertionIndex();
                     if (var6 > 0) {
                        if (var6 <= var2.getAnchor()) {
                           var6 = Math.min(var2.getAnchor() + 1, var2.getLength());
                        }

                        TextFieldSkin.this.positionCaret(var6, var5.isLeading(), true);
                     }

                     var1.consume();
                  }
               }
            );
      }
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      TextField var11 = this.getSkinnable();
      double var12 = (double)((FontMetrics)this.fontMetrics.get()).getCharWidth('W');
      int var14 = var11.getPrefColumnCount();
      return (double)var14 * var12 + var9 + var5;
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.computePrefHeight(var1, var3, var5, var7, var9);
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      return var3 + this.textNode.getLayoutBounds().getHeight() + var7;
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().prefHeight(var1);
   }

   @Override
   public double computeBaselineOffset(double var1, double var3, double var5, double var7) {
      return var1 + this.textNode.getBaselineOffset();
   }

   public void replaceText(int var1, int var2, String var3) {
      double var4 = this.textNode.getBoundsInParent().getMaxX();
      double var6 = this.caretPath.getLayoutBounds().getMaxX() + this.textTranslateX.get();
      this.getSkinnable().replaceText(var1, var2, var3);
      this.scrollAfterDelete(var4, var6);
   }

   public void deleteChar(boolean var1) {
      double var2 = this.textNode.getBoundsInParent().getMaxX();
      double var4 = this.caretPath.getLayoutBounds().getMaxX() + this.textTranslateX.get();
      if (var1 ? this.getSkinnable().deletePreviousChar() : this.getSkinnable().deleteNextChar()) {
         this.scrollAfterDelete(var2, var4);
      }
   }

   public HitInfo getIndex(double var1, double var3) {
      Point2D var5 = new Point2D(var1 - this.textTranslateX.get() - this.snappedLeftInset(), var3 - this.snappedTopInset());
      return this.textNode.hitTest(var5);
   }

   public void positionCaret(HitInfo var1, boolean var2) {
      this.positionCaret(var1.getInsertionIndex(), var1.isLeading(), var2);
   }

   private void positionCaret(int var1, boolean var2, boolean var3) {
      TextField var4 = this.getSkinnable();
      if (var3) {
         var4.selectPositionCaret(var1);
      } else {
         var4.positionCaret(var1);
      }

      this.setForwardBias(var2);
   }

   @Override
   public Rectangle2D getCharacterBounds(int var1) {
      double var2;
      double var4;
      double var6;
      double var8;
      if (var1 == this.textNode.getText().length()) {
         Bounds var10 = this.textNode.getBoundsInLocal();
         var2 = var10.getMaxX();
         var4 = 0.0;
         var6 = 0.0;
         var8 = var10.getMaxY();
      } else {
         this.characterBoundingPath.getElements().clear();
         this.characterBoundingPath.getElements().addAll(this.textNode.rangeShape(var1, var1 + 1));
         this.characterBoundingPath.setLayoutX(this.textNode.getLayoutX());
         this.characterBoundingPath.setLayoutY(this.textNode.getLayoutY());
         Bounds var11 = this.characterBoundingPath.getBoundsInLocal();
         var2 = var11.getMinX();
         var4 = var11.getMinY();
         var6 = var11.isEmpty() ? 0.0 : var11.getWidth();
         var8 = var11.isEmpty() ? 0.0 : var11.getHeight();
      }

      Bounds var12 = this.textGroup.getBoundsInParent();
      return new Rectangle2D(var2 + var12.getMinX() + this.textTranslateX.get(), var4 + var12.getMinY(), var6, var8);
   }

   @Override
   protected PathElement[] getUnderlineShape(int var1, int var2) {
      return this.textNode.underlineShape(var1, var2);
   }

   @Override
   protected PathElement[] getRangeShape(int var1, int var2) {
      return this.textNode.rangeShape(var1, var2);
   }

   @Override
   protected void addHighlight(List<? extends Node> var1, int var2) {
      this.textGroup.getChildren().addAll(var1);
   }

   @Override
   protected void removeHighlight(List<? extends Node> var1) {
      this.textGroup.getChildren().removeAll(var1);
   }

   @Override
   public void moveCaret(TextInputControlSkin.TextUnit var1, TextInputControlSkin.Direction var2, boolean var3) {
      switch (var1) {
         case CHARACTER:
            switch (var2) {
               case LEFT:
               case RIGHT:
                  this.nextCharacterVisually(var2 == TextInputControlSkin.Direction.RIGHT);
                  return;
               default:
                  throw new IllegalArgumentException(var2 + "");
            }
         default:
            throw new IllegalArgumentException(var1 + "");
      }
   }

   private void nextCharacterVisually(boolean var1) {
      if (this.isRTL()) {
         var1 = !var1;
      }

      Bounds var2 = this.caretPath.getLayoutBounds();
      if (this.caretPath.getElements().size() == 4) {
         var2 = new Path(new PathElement[]{(PathElement)this.caretPath.getElements().get(0), (PathElement)this.caretPath.getElements().get(1)})
            .getLayoutBounds();
      }

      double var3 = var1 ? var2.getMaxX() : var2.getMinX();
      double var5 = (var2.getMinY() + var2.getMaxY()) / 2.0;
      HitInfo var7 = this.textNode.hitTest(new Point2D(var3, var5));
      boolean var8 = var7.isLeading();
      Path var9 = new Path(this.textNode.rangeShape(var7.getCharIndex(), var7.getCharIndex() + 1));
      if (var1 && var9.getLayoutBounds().getMaxX() > var2.getMaxX() || !var1 && var9.getLayoutBounds().getMinX() < var2.getMinX()) {
         var8 = !var8;
      }

      this.positionCaret(var7.getInsertionIndex(), var8, false);
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      super.layoutChildren(var1, var3, var5, var7);
      if (this.textNode != null) {
         Bounds var11 = this.textNode.getLayoutBounds();
         double var12 = this.textNode.getBaselineOffset();
         double var14 = var11.getHeight() - var12;
         double var9;
         switch (this.getSkinnable().getAlignment().getVpos()) {
            case TOP:
               var9 = var12;
               break;
            case CENTER:
               var9 = (var12 + this.textGroup.getHeight() - var14) / 2.0;
               break;
            case BOTTOM:
            default:
               var9 = this.textGroup.getHeight() - var14;
         }

         this.textNode.setY(var9);
         if (this.promptNode != null) {
            this.promptNode.setY(var9);
         }

         if (this.getSkinnable().getWidth() > 0.0) {
            this.updateTextPos();
            this.updateCaretOff();
         }
      }

      if (SHOW_HANDLES) {
         this.handleGroup.setLayoutX(var1 + this.caretWidth / 2.0);
         this.handleGroup.setLayoutY(var3);
         this.selectionHandle1.resize(this.selectionHandle1.prefWidth(-1.0), this.selectionHandle1.prefHeight(-1.0));
         this.selectionHandle2.resize(this.selectionHandle2.prefWidth(-1.0), this.selectionHandle2.prefHeight(-1.0));
         this.caretHandle.resize(this.caretHandle.prefWidth(-1.0), this.caretHandle.prefHeight(-1.0));
         Bounds var16 = this.caretPath.getBoundsInParent();
         this.caretHandle.setLayoutY(var16.getMaxY() - 1.0);
         this.selectionHandle1.setLayoutY(var16.getMinY() - this.selectionHandle1.getHeight() + 1.0);
         this.selectionHandle2.setLayoutY(var16.getMaxY() - 1.0);
      }
   }

   private HPos getHAlignment() {
      return this.getSkinnable().getAlignment().getHpos();
   }

   @Override
   public Point2D getMenuPosition() {
      Point2D var1 = super.getMenuPosition();
      if (var1 != null) {
         var1 = new Point2D(
            Math.max(0.0, var1.getX() - this.textNode.getLayoutX() - this.snappedLeftInset() + this.textTranslateX.get()),
            Math.max(0.0, var1.getY() - this.textNode.getLayoutY() - this.snappedTopInset())
         );
      }

      return var1;
   }

   @Override
   protected String maskText(String var1) {
      if (!(this.getSkinnable() instanceof PasswordField)) {
         return var1;
      } else {
         int var2 = var1.length();
         StringBuilder var3 = new StringBuilder(var2);

         for (int var4 = 0; var4 < var2; var4++) {
            var3.append('●');
         }

         return var3.toString();
      }
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case BOUNDS_FOR_RANGE:
         case OFFSET_AT_POINT:
            return this.textNode.queryAccessibleAttribute(var1, var2);
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   TextInputControlBehavior getBehavior() {
      return this.behavior;
   }

   private void updateTextNodeCaretPos(int var1) {
      if (var1 != 0 && !this.isForwardBias()) {
         this.textNode.setCaretPosition(var1 - 1);
      } else {
         this.textNode.setCaretPosition(var1);
      }

      this.textNode.caretBiasProperty().set(this.isForwardBias());
   }

   private void createPromptNode() {
      if (this.promptNode == null && this.usePromptText.get()) {
         this.promptNode = new Text();
         this.textGroup.getChildren().add(0, this.promptNode);
         this.promptNode.setManaged(false);
         this.promptNode.getStyleClass().add("text");
         this.promptNode.visibleProperty().bind(this.usePromptText);
         this.promptNode.fontProperty().bind(this.getSkinnable().fontProperty());
         this.promptNode.textProperty().bind(this.getSkinnable().promptTextProperty());
         this.promptNode.fillProperty().bind(this.promptTextFillProperty());
         this.updateSelection();
      }
   }

   private void updateSelection() {
      TextField var1 = this.getSkinnable();
      IndexRange var2 = var1.getSelection();
      if (var2 != null && var2.getLength() != 0) {
         this.textNode.selectionStartProperty().set(var2.getStart());
         this.textNode.selectionEndProperty().set(var2.getStart());
         this.textNode.selectionEndProperty().set(var2.getEnd());
      } else {
         this.textNode.selectionStartProperty().set(-1);
         this.textNode.selectionEndProperty().set(-1);
      }

      PathElement[] var3 = (PathElement[])this.textNode.selectionShapeProperty().get();
      if (var3 == null) {
         this.selectionHighlightPath.getElements().clear();
      } else {
         this.selectionHighlightPath.getElements().setAll(var3);
      }

      if (SHOW_HANDLES && var2 != null && var2.getLength() > 0) {
         int var4 = var1.getCaretPosition();
         int var5 = var1.getAnchor();
         this.updateTextNodeCaretPos(var5);
         Bounds var6 = this.caretPath.getBoundsInParent();
         if (var4 < var5) {
            this.selectionHandle2.setLayoutX(var6.getMinX() - this.selectionHandle2.getWidth() / 2.0);
         } else {
            this.selectionHandle1.setLayoutX(var6.getMinX() - this.selectionHandle1.getWidth() / 2.0);
         }

         this.updateTextNodeCaretPos(var4);
         var6 = this.caretPath.getBoundsInParent();
         if (var4 < var5) {
            this.selectionHandle1.setLayoutX(var6.getMinX() - this.selectionHandle1.getWidth() / 2.0);
         } else {
            this.selectionHandle2.setLayoutX(var6.getMinX() - this.selectionHandle2.getWidth() / 2.0);
         }
      }
   }

   private void updateTextPos() {
      double var1 = this.textTranslateX.get();
      double var5 = this.textNode.getLayoutBounds().getWidth();
      switch (this.getHAlignment()) {
         case CENTER:
            double var7 = this.textRight.get() / 2.0;
            double var10;
            if (this.usePromptText.get()) {
               var10 = var7 - this.promptNode.getLayoutBounds().getWidth() / 2.0;
               this.promptNode.setLayoutX(var10);
            } else {
               var10 = var7 - var5 / 2.0;
            }

            if (var10 + var5 <= this.textRight.get()) {
               this.textTranslateX.set(var10);
            }
            break;
         case RIGHT:
            double var9 = this.textRight.get() - var5 - this.caretWidth / 2.0;
            if (var9 > var1 || var9 > 0.0) {
               this.textTranslateX.set(var9);
            }

            if (this.usePromptText.get()) {
               this.promptNode.setLayoutX(this.textRight.get() - this.promptNode.getLayoutBounds().getWidth() - this.caretWidth / 2.0);
            }
            break;
         case LEFT:
         default:
            double var3 = this.caretWidth / 2.0;
            if (var3 < var1 || var3 + var5 <= this.textRight.get()) {
               this.textTranslateX.set(var3);
            }

            if (this.usePromptText.get()) {
               this.promptNode.layoutXProperty().set(var3);
            }
      }
   }

   private void updateCaretOff() {
      double var1 = 0.0;
      double var3 = this.caretPath.getLayoutBounds().getMinX() + this.textTranslateX.get();
      if (var3 < 0.0) {
         var1 = var3;
      } else if (var3 > this.textRight.get() - this.caretWidth) {
         var1 = var3 - (this.textRight.get() - this.caretWidth);
      }

      switch (this.getHAlignment()) {
         case CENTER:
            this.textTranslateX.set(this.textTranslateX.get() - var1);
            break;
         case RIGHT:
            this.textTranslateX
               .set(Math.max(this.textTranslateX.get() - var1, this.textRight.get() - this.textNode.getLayoutBounds().getWidth() - this.caretWidth / 2.0));
            break;
         case LEFT:
         default:
            this.textTranslateX.set(Math.min(this.textTranslateX.get() - var1, this.caretWidth / 2.0));
      }

      if (SHOW_HANDLES) {
         this.caretHandle.setLayoutX(var3 - this.caretHandle.getWidth() / 2.0);
      }
   }

   private void scrollAfterDelete(double var1, double var3) {
      Bounds var5 = this.textNode.getLayoutBounds();
      Bounds var6 = this.textNode.localToParent(var5);
      Bounds var7 = this.clip.getBoundsInParent();
      Bounds var8 = this.caretPath.getLayoutBounds();
      switch (this.getHAlignment()) {
         case CENTER:
         case LEFT:
         default:
            if (var6.getMinX() < var7.getMinX() + this.caretWidth / 2.0 && var6.getMaxX() <= var7.getMaxX()) {
               double var11 = var3 - var8.getMaxX() - this.textTranslateX.get();
               if (var6.getMaxX() + var11 < var7.getMaxX()) {
                  if (var1 <= var7.getMaxX()) {
                     var11 = var1 - var6.getMaxX();
                  } else {
                     var11 = var7.getMaxX() - var6.getMaxX();
                  }
               }

               this.textTranslateX.set(this.textTranslateX.get() + var11);
            }
            break;
         case RIGHT:
            if (var6.getMaxX() > var7.getMaxX()) {
               double var9 = var3 - var8.getMaxX() - this.textTranslateX.get();
               if (var6.getMaxX() + var9 < var7.getMaxX()) {
                  if (var1 <= var7.getMaxX()) {
                     var9 = var1 - var6.getMaxX();
                  } else {
                     var9 = var7.getMaxX() - var6.getMaxX();
                  }
               }

               this.textTranslateX.set(this.textTranslateX.get() + var9);
            } else {
               this.updateTextPos();
            }
      }

      this.updateCaretOff();
   }
}
