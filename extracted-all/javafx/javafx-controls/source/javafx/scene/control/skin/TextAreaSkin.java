package javafx.scene.control.skin;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import com.sun.javafx.tk.FontMetrics;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.geometry.VerticalDirection;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TextAreaSkin extends TextInputControlSkin<TextArea> {
   private static final Path tmpCaretPath = new Path();
   private final TextArea textArea;
   private static final boolean USE_MULTIPLE_NODES = false;
   private final TextAreaBehavior behavior;
   private double computedMinWidth = Double.NEGATIVE_INFINITY;
   private double computedMinHeight = Double.NEGATIVE_INFINITY;
   private double computedPrefWidth = Double.NEGATIVE_INFINITY;
   private double computedPrefHeight = Double.NEGATIVE_INFINITY;
   private double widthForComputedPrefHeight = Double.NEGATIVE_INFINITY;
   private double characterWidth;
   private double lineHeight;
   private TextAreaSkin.ContentView contentView = new TextAreaSkin.ContentView();
   private Group paragraphNodes = new Group();
   private Text promptNode;
   private ObservableBooleanValue usePromptText;
   private ObservableIntegerValue caretPosition;
   private Group selectionHighlightGroup = new Group();
   private ScrollPane scrollPane;
   private Bounds oldViewportBounds;
   private VerticalDirection scrollDirection = null;
   private Path characterBoundingPath = new Path();
   private Timeline scrollSelectionTimeline = new Timeline();
   private EventHandler<ActionEvent> scrollSelectionHandler = var1x -> {
      switch (this.scrollDirection) {
         case UP:
         case DOWN:
      }
   };
   private double pressX;
   private double pressY;
   private boolean handlePressed;
   double targetCaretX = -1.0;

   public TextAreaSkin(final TextArea var1) {
      super(var1);
      this.behavior = new TextAreaBehavior(var1);
      this.behavior.setTextAreaSkin(this);
      this.textArea = var1;
      this.caretPosition = new IntegerBinding() {
         {
            this.bind(new Observable[]{var1.caretPositionProperty()});
         }

         protected int computeValue() {
            return var1.getCaretPosition();
         }
      };
      this.caretPosition.addListener((var2x, var3x, var4x) -> {
         this.targetCaretX = -1.0;
         if (var1.getWidth() > 0.0) {
            this.setForwardBias(true);
         }
      });
      this.forwardBiasProperty().addListener(var2x -> {
         if (var1.getWidth() > 0.0) {
            this.updateTextNodeCaretPos(var1.getCaretPosition());
         }
      });
      this.scrollPane = new ScrollPane();
      this.scrollPane.setFitToWidth(var1.isWrapText());
      this.scrollPane.setContent(this.contentView);
      this.getChildren().add(this.scrollPane);
      this.getSkinnable().addEventFilter(ScrollEvent.ANY, var1x -> {
         if (var1x.isDirect() && this.handlePressed) {
            var1x.consume();
         }
      });
      this.selectionHighlightGroup.setManaged(false);
      this.selectionHighlightGroup.setVisible(false);
      this.contentView.getChildren().add(this.selectionHighlightGroup);
      this.paragraphNodes.setManaged(false);
      this.contentView.getChildren().add(this.paragraphNodes);
      this.caretPath.setManaged(false);
      this.caretPath.setStrokeWidth(1.0);
      this.caretPath.fillProperty().bind(this.textFillProperty());
      this.caretPath.strokeProperty().bind(this.textFillProperty());
      this.caretPath.opacityProperty().bind(new DoubleBinding() {
         {
            this.bind(new Observable[]{TextAreaSkin.this.caretVisibleProperty()});
         }

         protected double computeValue() {
            return TextAreaSkin.this.caretVisibleProperty().get() ? 1.0 : 0.0;
         }
      });
      this.contentView.getChildren().add(this.caretPath);
      if (SHOW_HANDLES) {
         this.contentView.getChildren().addAll(new Node[]{this.caretHandle, this.selectionHandle1, this.selectionHandle2});
      }

      this.scrollPane.hvalueProperty().addListener((var1x, var2x, var3x) -> this.getSkinnable().setScrollLeft(var3x.doubleValue() * this.getScrollLeftMax()));
      this.scrollPane.vvalueProperty().addListener((var1x, var2x, var3x) -> this.getSkinnable().setScrollTop(var3x.doubleValue() * this.getScrollTopMax()));
      this.scrollSelectionTimeline.setCycleCount(-1);
      ObservableList var2 = this.scrollSelectionTimeline.getKeyFrames();
      var2.clear();
      var2.add(new KeyFrame(Duration.millis(350.0), this.scrollSelectionHandler, new KeyValue[0]));
      int var3 = 0;

      for (byte var4 = 1; var3 < var4; var3++) {
         Object var5 = var4 == 1 ? var1.textProperty().getValueSafe() : (CharSequence)var1.getParagraphs().get(var3);
         this.addParagraphNode(var3, var5.toString());
      }

      var1.selectionProperty().addListener((var2x, var3x, var4x) -> {
         var1.requestLayout();
         this.contentView.requestLayout();
      });
      var1.wrapTextProperty().addListener((var1x, var2x, var3x) -> {
         this.invalidateMetrics();
         this.scrollPane.setFitToWidth(var3x);
      });
      var1.prefColumnCountProperty().addListener((var1x, var2x, var3x) -> {
         this.invalidateMetrics();
         this.updatePrefViewportWidth();
      });
      var1.prefRowCountProperty().addListener((var1x, var2x, var3x) -> {
         this.invalidateMetrics();
         this.updatePrefViewportHeight();
      });
      this.updateFontMetrics();
      this.fontMetrics.addListener(var1x -> this.updateFontMetrics());
      this.contentView.paddingProperty().addListener(var1x -> {
         this.updatePrefViewportWidth();
         this.updatePrefViewportHeight();
      });
      this.scrollPane
         .viewportBoundsProperty()
         .addListener(
            var1x -> {
               if (this.scrollPane.getViewportBounds() != null) {
                  Bounds var2x = this.scrollPane.getViewportBounds();
                  if (this.oldViewportBounds == null
                     || this.oldViewportBounds.getWidth() != var2x.getWidth()
                     || this.oldViewportBounds.getHeight() != var2x.getHeight()) {
                     this.invalidateMetrics();
                     this.oldViewportBounds = var2x;
                     this.contentView.requestLayout();
                  }
               }
            }
         );
      var1.scrollTopProperty().addListener((var1x, var2x, var3x) -> {
         double var4x = var3x.doubleValue() < this.getScrollTopMax() ? var3x.doubleValue() / this.getScrollTopMax() : 1.0;
         this.scrollPane.setVvalue(var4x);
      });
      var1.scrollLeftProperty().addListener((var1x, var2x, var3x) -> {
         double var4x = var3x.doubleValue() < this.getScrollLeftMax() ? var3x.doubleValue() / this.getScrollLeftMax() : 1.0;
         this.scrollPane.setHvalue(var4x);
      });
      var1.textProperty().addListener(var2x -> {
         this.invalidateMetrics();
         ((Text)this.paragraphNodes.getChildren().get(0)).setText(var1.textProperty().getValueSafe());
         this.contentView.requestLayout();
      });
      this.usePromptText = new BooleanBinding() {
         {
            this.bind(new Observable[]{var1.textProperty(), var1.promptTextProperty()});
         }

         protected boolean computeValue() {
            String var1x = var1.getText();
            String var2 = var1.getPromptText();
            return (var1x == null || var1x.isEmpty()) && var2 != null && !var2.isEmpty();
         }
      };
      if (this.usePromptText.get()) {
         this.createPromptNode();
      }

      this.usePromptText.addListener(var2x -> {
         this.createPromptNode();
         var1.requestLayout();
      });
      this.updateHighlightFill();
      this.updatePrefViewportWidth();
      this.updatePrefViewportHeight();
      if (var1.isFocused()) {
         this.setCaretAnimating(true);
      }

      if (SHOW_HANDLES) {
         this.selectionHandle1.setRotate(180.0);
         EventHandler var6 = var1x -> {
            this.pressX = var1x.getX();
            this.pressY = var1x.getY();
            this.handlePressed = true;
            var1x.consume();
         };
         EventHandler var7 = var1x -> this.handlePressed = false;
         this.caretHandle.setOnMousePressed(var6);
         this.selectionHandle1.setOnMousePressed(var6);
         this.selectionHandle2.setOnMousePressed(var6);
         this.caretHandle.setOnMouseReleased(var7);
         this.selectionHandle1.setOnMouseReleased(var7);
         this.selectionHandle2.setOnMouseReleased(var7);
         this.caretHandle
            .setOnMouseDragged(
               var1x -> {
                  Text var2x = this.getTextNode();
                  Point2D var3x = var2x.localToScene(0.0, 0.0);
                  Point2D var4x = new Point2D(
                     var1x.getSceneX() - var3x.getX() - this.pressX + this.caretHandle.getWidth() / 2.0, var1x.getSceneY() - var3x.getY() - this.pressY - 6.0
                  );
                  HitInfo var5x = var2x.hitTest(this.translateCaretPosition(var4x));
                  this.positionCaret(var5x, false);
                  var1x.consume();
               }
            );
         this.selectionHandle1
            .setOnMouseDragged(
               var1x -> {
                  TextArea var2x = this.getSkinnable();
                  Text var3x = this.getTextNode();
                  Point2D var4x = var3x.localToScene(0.0, 0.0);
                  Point2D var5x = new Point2D(
                     var1x.getSceneX() - var4x.getX() - this.pressX + this.selectionHandle1.getWidth() / 2.0,
                     var1x.getSceneY() - var4x.getY() - this.pressY + this.selectionHandle1.getHeight() + 5.0
                  );
                  HitInfo var6x = var3x.hitTest(this.translateCaretPosition(var5x));
                  if (var2x.getAnchor() < var2x.getCaretPosition()) {
                     var2x.selectRange(var2x.getCaretPosition(), var2x.getAnchor());
                  }

                  int var7x = var6x.getCharIndex();
                  if (var7x > 0 && var7x >= var2x.getAnchor()) {
                     var7x = var2x.getAnchor();
                  }

                  this.positionCaret(var6x, true);
                  var1x.consume();
               }
            );
         this.selectionHandle2
            .setOnMouseDragged(
               var1x -> {
                  TextArea var2x = this.getSkinnable();
                  Text var3x = this.getTextNode();
                  Point2D var4x = var3x.localToScene(0.0, 0.0);
                  Point2D var5x = new Point2D(
                     var1x.getSceneX() - var4x.getX() - this.pressX + this.selectionHandle2.getWidth() / 2.0,
                     var1x.getSceneY() - var4x.getY() - this.pressY - 6.0
                  );
                  HitInfo var6x = var3x.hitTest(this.translateCaretPosition(var5x));
                  if (var2x.getAnchor() > var2x.getCaretPosition()) {
                     var2x.selectRange(var2x.getCaretPosition(), var2x.getAnchor());
                  }

                  int var7x = var6x.getCharIndex();
                  if (var7x > 0) {
                     if (var7x <= var2x.getAnchor() + 1) {
                        var7x = Math.min(var2x.getAnchor() + 2, var2x.getLength());
                     }

                     this.positionCaret(var6x, true);
                  }

                  var1x.consume();
               }
            );
      }
   }

   @Override
   protected void invalidateMetrics() {
      this.computedMinWidth = Double.NEGATIVE_INFINITY;
      this.computedMinHeight = Double.NEGATIVE_INFINITY;
      this.computedPrefWidth = Double.NEGATIVE_INFINITY;
      this.computedPrefHeight = Double.NEGATIVE_INFINITY;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      this.scrollPane.resizeRelocate(var1, var3, var5, var7);
   }

   @Override
   protected void updateHighlightFill() {
      for (Node var2 : this.selectionHighlightGroup.getChildren()) {
         Path var3 = (Path)var2;
         var3.setFill((Paint)this.highlightFillProperty().get());
      }
   }

   public HitInfo getIndex(double var1, double var3) {
      Text var5 = this.getTextNode();
      Point2D var6 = new Point2D(var1 - var5.getLayoutX(), var3 - this.getTextTranslateY());
      return var5.hitTest(this.translateCaretPosition(var6));
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
         case LINE:
            switch (var2) {
               case UP:
                  this.previousLine(var3);
                  return;
               case DOWN:
                  this.nextLine(var3);
                  return;
               case BEGINNING:
                  this.lineStart(var3, var3 && PlatformUtil.isMac());
                  return;
               case END:
                  this.lineEnd(var3, var3 && PlatformUtil.isMac());
                  return;
               default:
                  throw new IllegalArgumentException(var2 + "");
            }
         case PAGE:
            switch (var2) {
               case UP:
                  this.previousPage(var3);
                  return;
               case DOWN:
                  this.nextPage(var3);
                  return;
               default:
                  throw new IllegalArgumentException(var2 + "");
            }
         case PARAGRAPH:
            switch (var2) {
               case UP:
                  this.paragraphStart(true, var3);
                  return;
               case DOWN:
                  this.paragraphEnd(true, var3);
                  return;
               case BEGINNING:
                  this.paragraphStart(false, var3);
                  return;
               case END:
                  this.paragraphEnd(false, var3);
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

      Text var2 = this.getTextNode();
      Bounds var3 = this.caretPath.getLayoutBounds();
      if (this.caretPath.getElements().size() == 4) {
         var3 = new Path(new PathElement[]{(PathElement)this.caretPath.getElements().get(0), (PathElement)this.caretPath.getElements().get(1)})
            .getLayoutBounds();
      }

      double var4 = var1 ? var3.getMaxX() : var3.getMinX();
      double var6 = (var3.getMinY() + var3.getMaxY()) / 2.0;
      HitInfo var8 = var2.hitTest(new Point2D(var4, var6));
      boolean var9 = var8.isLeading();
      Path var10 = new Path(var2.rangeShape(var8.getCharIndex(), var8.getCharIndex() + 1));
      if (var1 && var10.getLayoutBounds().getMaxX() > var3.getMaxX() || !var1 && var10.getLayoutBounds().getMinX() < var3.getMinX()) {
         var9 = !var9;
         this.positionCaret(var8.getInsertionIndex(), var9, false, false);
      } else {
         int var11 = this.textArea.getCaretPosition();
         this.targetCaretX = var1 ? 0.0 : Double.MAX_VALUE;
         this.downLines(var1 ? 1 : -1, false, false);
         this.targetCaretX = -1.0;
         if (var11 == this.textArea.getCaretPosition()) {
            if (var1) {
               this.textArea.forward();
            } else {
               this.textArea.backward();
            }
         }
      }
   }

   private void downLines(int var1, boolean var2, boolean var3) {
      Text var4 = this.getTextNode();
      Bounds var5 = this.caretPath.getLayoutBounds();
      double var6 = (var5.getMinY() + var5.getMaxY()) / 2.0 + (double)var1 * this.lineHeight;
      if (var6 < 0.0) {
         var6 = 0.0;
      }

      double var8 = this.targetCaretX >= 0.0 ? this.targetCaretX : var5.getMaxX();
      HitInfo var10 = var4.hitTest(this.translateCaretPosition(new Point2D(var8, var6)));
      int var11 = var10.getCharIndex();
      int var12 = var4.getCaretPosition();
      boolean var13 = var4.isCaretBias();
      var4.setCaretBias(var10.isLeading());
      var4.setCaretPosition(var11);
      tmpCaretPath.getElements().clear();
      tmpCaretPath.getElements().addAll(var4.getCaretShape());
      tmpCaretPath.setLayoutX(var4.getLayoutX());
      tmpCaretPath.setLayoutY(var4.getLayoutY());
      Bounds var14 = tmpCaretPath.getLayoutBounds();
      double var15 = (var14.getMinY() + var14.getMaxY()) / 2.0;
      var4.setCaretBias(var13);
      var4.setCaretPosition(var12);
      if (var1 == 0 || var1 > 0 && var15 > var5.getMaxY() || var1 < 0 && var15 < var5.getMinY()) {
         this.positionCaret(var10.getInsertionIndex(), var10.isLeading(), var2, var3);
         this.targetCaretX = var8;
      }
   }

   private void previousLine(boolean var1) {
      this.downLines(-1, var1, false);
   }

   private void nextLine(boolean var1) {
      this.downLines(1, var1, false);
   }

   private void previousPage(boolean var1) {
      this.downLines(-((int)(this.scrollPane.getViewportBounds().getHeight() / this.lineHeight)), var1, false);
   }

   private void nextPage(boolean var1) {
      this.downLines((int)(this.scrollPane.getViewportBounds().getHeight() / this.lineHeight), var1, false);
   }

   private void lineStart(boolean var1, boolean var2) {
      this.targetCaretX = 0.0;
      this.downLines(0, var1, var2);
      this.targetCaretX = -1.0;
   }

   private void lineEnd(boolean var1, boolean var2) {
      this.targetCaretX = Double.MAX_VALUE;
      this.downLines(0, var1, var2);
      this.targetCaretX = -1.0;
   }

   private void paragraphStart(boolean var1, boolean var2) {
      TextArea var3 = this.getSkinnable();
      String var4 = var3.textProperty().getValueSafe();
      int var5 = var3.getCaretPosition();
      if (var5 > 0) {
         if (var1 && var4.codePointAt(var5 - 1) == 10) {
            var5--;
         }

         while (var5 > 0 && var4.codePointAt(var5 - 1) != 10) {
            var5--;
         }

         if (var2) {
            var3.selectPositionCaret(var5);
         } else {
            var3.positionCaret(var5);
            this.setForwardBias(true);
         }
      }
   }

   private void paragraphEnd(boolean var1, boolean var2) {
      TextArea var3 = this.getSkinnable();
      String var4 = var3.textProperty().getValueSafe();
      int var5 = var3.getCaretPosition();
      int var6 = var4.length();
      boolean var7 = false;
      boolean var8 = PlatformUtil.isWindows();
      if (var5 < var6) {
         if (var1 && var4.codePointAt(var5) == 10) {
            var5++;
            var7 = true;
         }

         if (!var8 || !var7) {
            while (var5 < var6 && var4.codePointAt(var5) != 10) {
               var5++;
            }

            if (var8 && var5 < var6) {
               var5++;
            }
         }

         if (var2) {
            var3.selectPositionCaret(var5);
         } else {
            var3.positionCaret(var5);
         }
      }
   }

   @Override
   protected PathElement[] getUnderlineShape(int var1, int var2) {
      int var3 = 0;

      for (Node var5 : this.paragraphNodes.getChildren()) {
         Text var6 = (Text)var5;
         int var7 = var3 + var6.textProperty().getValueSafe().length();
         if (var7 >= var1) {
            return var6.underlineShape(var1 - var3, var2 - var3);
         }

         var3 = var7 + 1;
      }

      return null;
   }

   @Override
   protected PathElement[] getRangeShape(int var1, int var2) {
      int var3 = 0;

      for (Node var5 : this.paragraphNodes.getChildren()) {
         Text var6 = (Text)var5;
         int var7 = var3 + var6.textProperty().getValueSafe().length();
         if (var7 >= var1) {
            return var6.rangeShape(var1 - var3, var2 - var3);
         }

         var3 = var7 + 1;
      }

      return null;
   }

   @Override
   protected void addHighlight(List<? extends Node> var1, int var2) {
      int var3 = 0;
      Text var4 = null;

      for (Node var6 : this.paragraphNodes.getChildren()) {
         Text var7 = (Text)var6;
         int var8 = var3 + var7.textProperty().getValueSafe().length();
         if (var8 >= var2) {
            var4 = var7;
            break;
         }

         var3 = var8 + 1;
      }

      if (var4 != null) {
         for (Node var10 : var1) {
            var10.setLayoutX(var4.getLayoutX());
            var10.setLayoutY(var4.getLayoutY());
         }
      }

      this.contentView.getChildren().addAll(var1);
   }

   @Override
   protected void removeHighlight(List<? extends Node> var1) {
      this.contentView.getChildren().removeAll(var1);
   }

   @Override
   public Point2D getMenuPosition() {
      this.contentView.layoutChildren();
      Point2D var1 = super.getMenuPosition();
      if (var1 != null) {
         var1 = new Point2D(
            Math.max(0.0, var1.getX() - this.contentView.snappedLeftInset() - this.getSkinnable().getScrollLeft()),
            Math.max(0.0, var1.getY() - this.contentView.snappedTopInset() - this.getSkinnable().getScrollTop())
         );
      }

      return var1;
   }

   public Bounds getCaretBounds() {
      return this.getSkinnable().sceneToLocal(this.caretPath.localToScene(this.caretPath.getBoundsInLocal()));
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case LINE_FOR_OFFSET:
         case LINE_START:
         case LINE_END:
         case BOUNDS_FOR_RANGE:
         case OFFSET_AT_POINT:
            Text var3 = this.getTextNode();
            return var3.queryAccessibleAttribute(var1, var2);
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }

      throw new UnsupportedOperationException();
   }

   @Override
   public double computeBaselineOffset(double var1, double var3, double var5, double var7) {
      Text var9 = (Text)this.paragraphNodes.getChildren().get(0);
      return Utils.getAscent(this.getSkinnable().getFont(), var9.getBoundsType()) + this.contentView.snappedTopInset() + this.textArea.snappedTopInset();
   }

   private char getCharacter(int var1) {
      int var2 = this.paragraphNodes.getChildren().size();
      int var3 = 0;
      int var4 = var1;

      String var5;
      for (var5 = null; var3 < var2; var3++) {
         Text var6 = (Text)this.paragraphNodes.getChildren().get(var3);
         var5 = var6.getText();
         int var7 = var5.length() + 1;
         if (var4 < var7) {
            break;
         }

         var4 -= var7;
      }

      return var4 == var5.length() ? '\n' : var5.charAt(var4);
   }

   @Override
   protected int getInsertionPoint(double var1, double var3) {
      TextArea var5 = this.getSkinnable();
      int var6 = this.paragraphNodes.getChildren().size();
      int var7 = -1;
      if (var6 > 0) {
         if (var3 < this.contentView.snappedTopInset()) {
            Text var8 = (Text)this.paragraphNodes.getChildren().get(0);
            var7 = this.getNextInsertionPoint(var8, var1, -1, VerticalDirection.DOWN);
         } else if (var3 > this.contentView.snappedTopInset() + this.contentView.getHeight()) {
            int var14 = var6 - 1;
            Text var9 = (Text)this.paragraphNodes.getChildren().get(var14);
            var7 = this.getNextInsertionPoint(var9, var1, -1, VerticalDirection.UP) + (var5.getLength() - var9.getText().length());
         } else {
            int var15 = 0;

            for (int var16 = 0; var16 < var6; var16++) {
               Text var10 = (Text)this.paragraphNodes.getChildren().get(var16);
               Bounds var11 = var10.getBoundsInLocal();
               double var12 = var10.getLayoutY() + var11.getMinY();
               if (var3 >= var12 && var3 < var12 + var10.getBoundsInLocal().getHeight()) {
                  var7 = this.getInsertionPoint(var10, var1 - var10.getLayoutX(), var3 - var10.getLayoutY()) + var15;
                  break;
               }

               var15 += var10.getText().length() + 1;
            }
         }
      }

      return var7;
   }

   public void positionCaret(HitInfo var1, boolean var2) {
      this.positionCaret(var1.getInsertionIndex(), var1.isLeading(), var2, false);
   }

   private void positionCaret(int var1, boolean var2, boolean var3, boolean var4) {
      boolean var5 = var1 > 0 && var1 <= this.getSkinnable().getLength() && this.getSkinnable().getText().codePointAt(var1 - 1) == 10;
      if (!var2 && var5) {
         var2 = true;
         var1--;
      }

      if (var3) {
         if (var4) {
            this.getSkinnable().extendSelection(var1);
         } else {
            this.getSkinnable().selectPositionCaret(var1);
         }
      } else {
         this.getSkinnable().positionCaret(var1);
      }

      this.setForwardBias(var2);
   }

   @Override
   public Rectangle2D getCharacterBounds(int var1) {
      TextArea var2 = this.getSkinnable();
      int var3 = this.paragraphNodes.getChildren().size();
      int var4 = var2.getLength() + 1;
      Text var5 = null;

      do {
         var5 = (Text)this.paragraphNodes.getChildren().get(--var3);
         var4 -= var5.getText().length() + 1;
      } while (var1 < var4);

      int var6 = var1 - var4;
      boolean var7 = false;
      if (var6 == var5.getText().length()) {
         var6--;
         var7 = true;
      }

      this.characterBoundingPath.getElements().clear();
      this.characterBoundingPath.getElements().addAll(var5.rangeShape(var6, var6 + 1));
      this.characterBoundingPath.setLayoutX(var5.getLayoutX());
      this.characterBoundingPath.setLayoutY(var5.getLayoutY());
      Bounds var8 = this.characterBoundingPath.getBoundsInLocal();
      double var9 = var8.getMinX() + var5.getLayoutX() - var2.getScrollLeft();
      double var11 = var8.getMinY() + var5.getLayoutY() - var2.getScrollTop();
      double var13 = var8.isEmpty() ? 0.0 : var8.getWidth();
      double var15 = var8.isEmpty() ? 0.0 : var8.getHeight();
      if (var7) {
         var9 += var13;
         var13 = 0.0;
      }

      return new Rectangle2D(var9, var11, var13, var15);
   }

   @Override
   protected void scrollCharacterToVisible(int var1) {
      Platform.runLater(() -> {
         if (this.getSkinnable().getLength() != 0) {
            Rectangle2D var2 = this.getCharacterBounds(var1);
            this.scrollBoundsToVisible(var2);
         }
      });
   }

   TextAreaBehavior getBehavior() {
      return this.behavior;
   }

   private void createPromptNode() {
      if (this.promptNode == null && this.usePromptText.get()) {
         this.promptNode = new Text();
         this.contentView.getChildren().add(0, this.promptNode);
         this.promptNode.setManaged(false);
         this.promptNode.getStyleClass().add("text");
         this.promptNode.visibleProperty().bind(this.usePromptText);
         this.promptNode.fontProperty().bind(this.getSkinnable().fontProperty());
         this.promptNode.textProperty().bind(this.getSkinnable().promptTextProperty());
         this.promptNode.fillProperty().bind(this.promptTextFillProperty());
      }
   }

   private void addParagraphNode(int var1, String var2) {
      TextArea var3 = this.getSkinnable();
      Text var4 = new Text(var2);
      var4.setTextOrigin(VPos.TOP);
      var4.setManaged(false);
      var4.getStyleClass().add("text");
      var4.boundsTypeProperty().addListener((var1x, var2x, var3x) -> {
         this.invalidateMetrics();
         this.updateFontMetrics();
      });
      this.paragraphNodes.getChildren().add(var1, var4);
      var4.fontProperty().bind(var3.fontProperty());
      var4.fillProperty().bind(this.textFillProperty());
      var4.selectionFillProperty().bind(this.highlightTextFillProperty());
   }

   private double getScrollTopMax() {
      return Math.max(0.0, this.contentView.getHeight() - this.scrollPane.getViewportBounds().getHeight());
   }

   private double getScrollLeftMax() {
      return Math.max(0.0, this.contentView.getWidth() - this.scrollPane.getViewportBounds().getWidth());
   }

   private int getInsertionPoint(Text var1, double var2, double var4) {
      HitInfo var6 = var1.hitTest(new Point2D(var2, var4));
      return var6.getInsertionIndex();
   }

   private int getNextInsertionPoint(Text var1, double var2, int var4, VerticalDirection var5) {
      return 0;
   }

   private void scrollCaretToVisible() {
      TextArea var1 = this.getSkinnable();
      Bounds var2 = this.caretPath.getLayoutBounds();
      double var3 = var2.getMinX() - var1.getScrollLeft();
      double var5 = var2.getMinY() - var1.getScrollTop();
      double var7 = var2.getWidth();
      double var9 = var2.getHeight();
      if (SHOW_HANDLES) {
         if (this.caretHandle.isVisible()) {
            var9 += this.caretHandle.getHeight();
         } else if (this.selectionHandle1.isVisible() && this.selectionHandle2.isVisible()) {
            var3 -= this.selectionHandle1.getWidth() / 2.0;
            var5 -= this.selectionHandle1.getHeight();
            var7 += this.selectionHandle1.getWidth() / 2.0 + this.selectionHandle2.getWidth() / 2.0;
            var9 += this.selectionHandle1.getHeight() + this.selectionHandle2.getHeight();
         }
      }

      if (var7 > 0.0 && var9 > 0.0) {
         this.scrollBoundsToVisible(new Rectangle2D(var3, var5, var7, var9));
      }
   }

   private void scrollBoundsToVisible(Rectangle2D var1) {
      TextArea var2 = this.getSkinnable();
      Bounds var3 = this.scrollPane.getViewportBounds();
      double var4 = var3.getWidth();
      double var6 = var3.getHeight();
      double var8 = var2.getScrollTop();
      double var10 = var2.getScrollLeft();
      double var12 = 6.0;
      if (var1.getMinY() < 0.0) {
         double var14 = var8 + var1.getMinY();
         if (var14 <= this.contentView.snappedTopInset()) {
            var14 = 0.0;
         }

         var2.setScrollTop(var14);
      } else if (this.contentView.snappedTopInset() + var1.getMaxY() > var6) {
         double var16 = var8 + this.contentView.snappedTopInset() + var1.getMaxY() - var6;
         if (var16 >= this.getScrollTopMax() - this.contentView.snappedBottomInset()) {
            var16 = this.getScrollTopMax();
         }

         var2.setScrollTop(var16);
      }

      if (var1.getMinX() < 0.0) {
         double var17 = var10 + var1.getMinX() - var12;
         if (var17 <= this.contentView.snappedLeftInset() + var12) {
            var17 = 0.0;
         }

         var2.setScrollLeft(var17);
      } else if (this.contentView.snappedLeftInset() + var1.getMaxX() > var4) {
         double var18 = var10 + this.contentView.snappedLeftInset() + var1.getMaxX() - var4 + var12;
         if (var18 >= this.getScrollLeftMax() - this.contentView.snappedRightInset() - var12) {
            var18 = this.getScrollLeftMax();
         }

         var2.setScrollLeft(var18);
      }
   }

   private void updatePrefViewportWidth() {
      int var1 = this.getSkinnable().getPrefColumnCount();
      this.scrollPane.setPrefViewportWidth((double)var1 * this.characterWidth + this.contentView.snappedLeftInset() + this.contentView.snappedRightInset());
      this.scrollPane.setMinViewportWidth(this.characterWidth + this.contentView.snappedLeftInset() + this.contentView.snappedRightInset());
   }

   private void updatePrefViewportHeight() {
      int var1 = this.getSkinnable().getPrefRowCount();
      this.scrollPane.setPrefViewportHeight((double)var1 * this.lineHeight + this.contentView.snappedTopInset() + this.contentView.snappedBottomInset());
      this.scrollPane.setMinViewportHeight(this.lineHeight + this.contentView.snappedTopInset() + this.contentView.snappedBottomInset());
   }

   private void updateFontMetrics() {
      Text var1 = (Text)this.paragraphNodes.getChildren().get(0);
      this.lineHeight = Utils.getLineHeight(this.getSkinnable().getFont(), var1.getBoundsType());
      this.characterWidth = (double)((FontMetrics)this.fontMetrics.get()).getCharWidth('W');
   }

   private double getTextTranslateX() {
      return this.contentView.snappedLeftInset();
   }

   private double getTextTranslateY() {
      return this.contentView.snappedTopInset();
   }

   private double getTextLeft() {
      return 0.0;
   }

   private Point2D translateCaretPosition(Point2D var1) {
      return var1;
   }

   private Text getTextNode() {
      return (Text)this.paragraphNodes.getChildren().get(0);
   }

   private void updateTextNodeCaretPos(int var1) {
      Text var2 = this.getTextNode();
      if (this.isForwardBias()) {
         var2.setCaretPosition(var1);
      } else {
         var2.setCaretPosition(var1 - 1);
      }

      var2.caretBiasProperty().set(this.isForwardBias());
   }

   private class ContentView extends Region {
      private ContentView() {
         this.getStyleClass().add("content");
         this.addEventHandler(MouseEvent.MOUSE_PRESSED, var1x -> {
            TextAreaSkin.this.behavior.mousePressed(var1x);
            var1x.consume();
         });
         this.addEventHandler(MouseEvent.MOUSE_RELEASED, var1x -> {
            TextAreaSkin.this.behavior.mouseReleased(var1x);
            var1x.consume();
         });
         this.addEventHandler(MouseEvent.MOUSE_DRAGGED, var1x -> {
            TextAreaSkin.this.behavior.mouseDragged(var1x);
            var1x.consume();
         });
      }

      protected ObservableList<Node> getChildren() {
         return super.getChildren();
      }

      public Orientation getContentBias() {
         return Orientation.HORIZONTAL;
      }

      protected double computePrefWidth(double var1) {
         if (TextAreaSkin.this.computedPrefWidth < 0.0) {
            double var3 = 0.0;

            for (Node var6 : TextAreaSkin.this.paragraphNodes.getChildren()) {
               Text var7 = (Text)var6;
               var3 = Math.max(var3, Utils.computeTextWidth(var7.getFont(), var7.getText(), 0.0));
            }

            var3 += this.snappedLeftInset() + this.snappedRightInset();
            Bounds var9 = TextAreaSkin.this.scrollPane.getViewportBounds();
            TextAreaSkin.this.computedPrefWidth = Math.max(var3, var9 != null ? var9.getWidth() : 0.0);
         }

         return TextAreaSkin.this.computedPrefWidth;
      }

      protected double computePrefHeight(double var1) {
         if (var1 != TextAreaSkin.this.widthForComputedPrefHeight) {
            TextAreaSkin.this.invalidateMetrics();
            TextAreaSkin.this.widthForComputedPrefHeight = var1;
         }

         if (TextAreaSkin.this.computedPrefHeight < 0.0) {
            double var3;
            if (var1 == -1.0) {
               var3 = 0.0;
            } else {
               var3 = Math.max(var1 - (this.snappedLeftInset() + this.snappedRightInset()), 0.0);
            }

            double var5 = 0.0;

            for (Node var8 : TextAreaSkin.this.paragraphNodes.getChildren()) {
               Text var9 = (Text)var8;
               var5 += Utils.computeTextHeight(var9.getFont(), var9.getText(), var3, var9.getBoundsType());
            }

            var5 += this.snappedTopInset() + this.snappedBottomInset();
            Bounds var11 = TextAreaSkin.this.scrollPane.getViewportBounds();
            TextAreaSkin.this.computedPrefHeight = Math.max(var5, var11 != null ? var11.getHeight() : 0.0);
         }

         return TextAreaSkin.this.computedPrefHeight;
      }

      protected double computeMinWidth(double var1) {
         if (TextAreaSkin.this.computedMinWidth < 0.0) {
            double var3 = this.snappedLeftInset() + this.snappedRightInset();
            TextAreaSkin.this.computedMinWidth = Math.min(TextAreaSkin.this.characterWidth + var3, this.computePrefWidth(var1));
         }

         return TextAreaSkin.this.computedMinWidth;
      }

      protected double computeMinHeight(double var1) {
         if (TextAreaSkin.this.computedMinHeight < 0.0) {
            double var3 = this.snappedTopInset() + this.snappedBottomInset();
            TextAreaSkin.this.computedMinHeight = Math.min(TextAreaSkin.this.lineHeight + var3, this.computePrefHeight(var1));
         }

         return TextAreaSkin.this.computedMinHeight;
      }

      public void layoutChildren() {
         TextArea var1 = TextAreaSkin.this.getSkinnable();
         double var2 = this.getWidth();
         double var4 = this.snappedTopInset();
         double var6 = this.snappedLeftInset();
         double var8 = Math.max(var2 - (var6 + this.snappedRightInset()), 0.0);
         double var10 = var4;
         ObservableList var12 = TextAreaSkin.this.paragraphNodes.getChildren();

         for (int var13 = 0; var13 < var12.size(); var13++) {
            Node var14 = (Node)var12.get(var13);
            Text var15 = (Text)var14;
            var15.setWrappingWidth(var8);
            Bounds var16 = var15.getBoundsInLocal();
            var15.setLayoutX(var6);
            var15.setLayoutY(var10);
            var10 += var16.getHeight();
         }

         if (TextAreaSkin.this.promptNode != null) {
            TextAreaSkin.this.promptNode.setLayoutX(var6);
            TextAreaSkin.this.promptNode.setLayoutY(var4 + TextAreaSkin.this.promptNode.getBaselineOffset());
            TextAreaSkin.this.promptNode.setWrappingWidth(var8);
         }

         IndexRange var26 = var1.getSelection();
         Bounds var27 = TextAreaSkin.this.caretPath.getBoundsInParent();
         TextAreaSkin.this.selectionHighlightGroup.getChildren().clear();
         int var28 = var1.getCaretPosition();
         int var29 = var1.getAnchor();
         if (TextInputControlSkin.SHOW_HANDLES) {
            if (var26.getLength() > 0) {
               TextAreaSkin.this.selectionHandle1
                  .resize(TextAreaSkin.this.selectionHandle1.prefWidth(-1.0), TextAreaSkin.this.selectionHandle1.prefHeight(-1.0));
               TextAreaSkin.this.selectionHandle2
                  .resize(TextAreaSkin.this.selectionHandle2.prefWidth(-1.0), TextAreaSkin.this.selectionHandle2.prefHeight(-1.0));
            } else {
               TextAreaSkin.this.caretHandle.resize(TextAreaSkin.this.caretHandle.prefWidth(-1.0), TextAreaSkin.this.caretHandle.prefHeight(-1.0));
            }

            if (var26.getLength() > 0) {
               int var17 = var12.size();
               int var18 = var1.getLength() + 1;
               Object var19 = null;

               do {
                  var19 = (Text)var12.get(--var17);
                  var18 -= var19.getText().length() + 1;
               } while (var29 < var18);

               TextAreaSkin.this.updateTextNodeCaretPos(var29 - var18);
               TextAreaSkin.this.caretPath.getElements().clear();
               TextAreaSkin.this.caretPath.getElements().addAll(var19.getCaretShape());
               TextAreaSkin.this.caretPath.setLayoutX(var19.getLayoutX());
               TextAreaSkin.this.caretPath.setLayoutY(var19.getLayoutY());
               Bounds var20 = TextAreaSkin.this.caretPath.getBoundsInParent();
               if (var28 < var29) {
                  TextAreaSkin.this.selectionHandle2.setLayoutX(var20.getMinX() - TextAreaSkin.this.selectionHandle2.getWidth() / 2.0);
                  TextAreaSkin.this.selectionHandle2.setLayoutY(var20.getMaxY() - 1.0);
               } else {
                  TextAreaSkin.this.selectionHandle1.setLayoutX(var20.getMinX() - TextAreaSkin.this.selectionHandle1.getWidth() / 2.0);
                  TextAreaSkin.this.selectionHandle1.setLayoutY(var20.getMinY() - TextAreaSkin.this.selectionHandle1.getHeight() + 1.0);
               }
            }
         }

         int var30 = var12.size();
         int var32 = var1.getLength() + 1;
         Text var35 = null;

         do {
            var35 = (Text)var12.get(--var30);
            var32 -= var35.getText().length() + 1;
         } while (var28 < var32);

         TextAreaSkin.this.updateTextNodeCaretPos(var28 - var32);
         TextAreaSkin.this.caretPath.getElements().clear();
         TextAreaSkin.this.caretPath.getElements().addAll(var35.getCaretShape());
         TextAreaSkin.this.caretPath.setLayoutX(var35.getLayoutX());
         var35.setLayoutX(2.0 * var35.getLayoutX() - var35.getBoundsInParent().getMinX());
         TextAreaSkin.this.caretPath.setLayoutY(var35.getLayoutY());
         if (var27 == null || !var27.equals(TextAreaSkin.this.caretPath.getBoundsInParent())) {
            TextAreaSkin.this.scrollCaretToVisible();
         }

         var30 = var26.getStart();
         var32 = var26.getEnd();
         int var37 = 0;

         for (int var40 = var12.size(); var37 < var40; var37++) {
            Node var21 = (Node)var12.get(var37);
            Text var22 = (Text)var21;
            int var23 = var22.getText().length() + 1;
            if (var32 > var30 && var30 < var23) {
               var22.setSelectionStart(var30);
               var22.setSelectionEnd(Math.min(var32, var23));
               Path var24 = new Path();
               var24.setManaged(false);
               var24.setStroke(null);
               PathElement[] var25 = var22.getSelectionShape();
               if (var25 != null) {
                  var24.getElements().addAll(var25);
               }

               TextAreaSkin.this.selectionHighlightGroup.getChildren().add(var24);
               TextAreaSkin.this.selectionHighlightGroup.setVisible(true);
               var24.setLayoutX(var22.getLayoutX());
               var24.setLayoutY(var22.getLayoutY());
               TextAreaSkin.this.updateHighlightFill();
            } else {
               var22.setSelectionStart(-1);
               var22.setSelectionEnd(-1);
               TextAreaSkin.this.selectionHighlightGroup.setVisible(false);
            }

            var30 = Math.max(0, var30 - var23);
            var32 = Math.max(0, var32 - var23);
         }

         if (TextInputControlSkin.SHOW_HANDLES) {
            Bounds var38 = TextAreaSkin.this.caretPath.getBoundsInParent();
            if (var26.getLength() > 0) {
               if (var28 < var29) {
                  TextAreaSkin.this.selectionHandle1.setLayoutX(var38.getMinX() - TextAreaSkin.this.selectionHandle1.getWidth() / 2.0);
                  TextAreaSkin.this.selectionHandle1.setLayoutY(var38.getMinY() - TextAreaSkin.this.selectionHandle1.getHeight() + 1.0);
               } else {
                  TextAreaSkin.this.selectionHandle2.setLayoutX(var38.getMinX() - TextAreaSkin.this.selectionHandle2.getWidth() / 2.0);
                  TextAreaSkin.this.selectionHandle2.setLayoutY(var38.getMaxY() - 1.0);
               }
            } else {
               TextAreaSkin.this.caretHandle.setLayoutX(var38.getMinX() - TextAreaSkin.this.caretHandle.getWidth() / 2.0 + 1.0);
               TextAreaSkin.this.caretHandle.setLayoutY(var38.getMaxY());
            }
         }

         if (TextAreaSkin.this.scrollPane.getPrefViewportWidth() == 0.0 || TextAreaSkin.this.scrollPane.getPrefViewportHeight() == 0.0) {
            TextAreaSkin.this.updatePrefViewportWidth();
            TextAreaSkin.this.updatePrefViewportHeight();
            if (this.getParent() != null && TextAreaSkin.this.scrollPane.getPrefViewportWidth() > 0.0
               || TextAreaSkin.this.scrollPane.getPrefViewportHeight() > 0.0) {
               this.getParent().requestLayout();
            }
         }

         Bounds var39 = TextAreaSkin.this.scrollPane.getViewportBounds();
         boolean var41 = TextAreaSkin.this.scrollPane.isFitToWidth();
         boolean var42 = TextAreaSkin.this.scrollPane.isFitToHeight();
         boolean var43 = var1.isWrapText() || this.computePrefWidth(-1.0) <= var39.getWidth();
         boolean var44 = this.computePrefHeight(var2) <= var39.getHeight();
         if (var41 != var43 || var42 != var44) {
            Platform.runLater(() -> {
               TextAreaSkin.this.scrollPane.setFitToWidth(var43);
               TextAreaSkin.this.scrollPane.setFitToHeight(var44);
            });
            this.getParent().requestLayout();
         }
      }
   }
}
