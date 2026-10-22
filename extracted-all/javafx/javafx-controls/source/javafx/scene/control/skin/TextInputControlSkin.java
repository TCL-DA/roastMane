package javafx.scene.control.skin;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.TextInputControlBehavior;
import com.sun.javafx.scene.control.skin.FXVK;
import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.PaintConverter;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.AccessibleAction;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.IndexRange;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import javafx.stage.Window;
import javafx.util.Duration;

public abstract class TextInputControlSkin<T extends TextInputControl> extends SkinBase<T> {
   static boolean preload = false;
   static final boolean SHOW_HANDLES = Properties.IS_TOUCH_SUPPORTED;
   private static final boolean IS_FXVK_SUPPORTED = Platform.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD);
   final ObservableObjectValue<FontMetrics> fontMetrics;
   private ObservableBooleanValue caretVisible;
   private TextInputControlSkin.CaretBlinking caretBlinking = new TextInputControlSkin.CaretBlinking(this.blinkProperty());
   final Path caretPath = new Path();
   StackPane caretHandle = null;
   StackPane selectionHandle1 = null;
   StackPane selectionHandle2 = null;
   private int imstart;
   private int imlength;
   private List<Shape> imattrs = new ArrayList<>();
   private BooleanProperty blink;
   private final ObjectProperty<Paint> textFill = new StyleableObjectProperty<Paint>(Color.BLACK) {
      protected void invalidated() {
         TextInputControlSkin.this.updateTextFill();
      }

      public Object getBean() {
         return TextInputControlSkin.this;
      }

      public String getName() {
         return "textFill";
      }

      public CssMetaData<TextInputControl, Paint> getCssMetaData() {
         return TextInputControlSkin.StyleableProperties.TEXT_FILL;
      }
   };
   private final ObjectProperty<Paint> promptTextFill = new StyleableObjectProperty<Paint>(Color.GRAY) {
      public Object getBean() {
         return TextInputControlSkin.this;
      }

      public String getName() {
         return "promptTextFill";
      }

      public CssMetaData<TextInputControl, Paint> getCssMetaData() {
         return TextInputControlSkin.StyleableProperties.PROMPT_TEXT_FILL;
      }
   };
   private final ObjectProperty<Paint> highlightFill = new StyleableObjectProperty<Paint>(Color.DODGERBLUE) {
      protected void invalidated() {
         TextInputControlSkin.this.updateHighlightFill();
      }

      public Object getBean() {
         return TextInputControlSkin.this;
      }

      public String getName() {
         return "highlightFill";
      }

      public CssMetaData<TextInputControl, Paint> getCssMetaData() {
         return TextInputControlSkin.StyleableProperties.HIGHLIGHT_FILL;
      }
   };
   private final ObjectProperty<Paint> highlightTextFill = new StyleableObjectProperty<Paint>(Color.WHITE) {
      protected void invalidated() {
         TextInputControlSkin.this.updateHighlightTextFill();
      }

      public Object getBean() {
         return TextInputControlSkin.this;
      }

      public String getName() {
         return "highlightTextFill";
      }

      public CssMetaData<TextInputControl, Paint> getCssMetaData() {
         return TextInputControlSkin.StyleableProperties.HIGHLIGHT_TEXT_FILL;
      }
   };
   private final BooleanProperty displayCaret = new StyleableBooleanProperty(true) {
      public Object getBean() {
         return TextInputControlSkin.this;
      }

      public String getName() {
         return "displayCaret";
      }

      public CssMetaData<TextInputControl, Boolean> getCssMetaData() {
         return TextInputControlSkin.StyleableProperties.DISPLAY_CARET;
      }
   };
   private BooleanProperty forwardBias = new SimpleBooleanProperty(this, "forwardBias", true);

   public TextInputControlSkin(final T var1) {
      super((T)var1);
      this.fontMetrics = new ObjectBinding<FontMetrics>() {
         {
            this.bind(new Observable[]{var1.fontProperty()});
         }

         protected FontMetrics computeValue() {
            TextInputControlSkin.this.invalidateMetrics();
            return Toolkit.getToolkit().getFontLoader().getFontMetrics(var1.getFont());
         }
      };
      this.caretVisible = new BooleanBinding() {
         {
            this.bind(
               new Observable[]{
                  var1.focusedProperty(),
                  var1.anchorProperty(),
                  var1.caretPositionProperty(),
                  var1.disabledProperty(),
                  var1.editableProperty(),
                  TextInputControlSkin.this.displayCaret,
                  TextInputControlSkin.this.blinkProperty()
               }
            );
         }

         protected boolean computeValue() {
            return !TextInputControlSkin.this.blinkProperty().get()
               && TextInputControlSkin.this.displayCaret.get()
               && var1.isFocused()
               && (PlatformUtil.isWindows() || var1.getCaretPosition() == var1.getAnchor())
               && !var1.isDisabled()
               && var1.isEditable();
         }
      };
      if (SHOW_HANDLES) {
         this.caretHandle = new StackPane();
         this.selectionHandle1 = new StackPane();
         this.selectionHandle2 = new StackPane();
         this.caretHandle.setManaged(false);
         this.selectionHandle1.setManaged(false);
         this.selectionHandle2.setManaged(false);
         if (PlatformUtil.isIOS()) {
            this.caretHandle.setVisible(false);
         } else {
            this.caretHandle
               .visibleProperty()
               .bind(
                  new BooleanBinding() {
                     {
                        this.bind(
                           new Observable[]{
                              var1.focusedProperty(),
                              var1.anchorProperty(),
                              var1.caretPositionProperty(),
                              var1.disabledProperty(),
                              var1.editableProperty(),
                              var1.lengthProperty(),
                              TextInputControlSkin.this.displayCaret
                           }
                        );
                     }

                     protected boolean computeValue() {
                        return TextInputControlSkin.this.displayCaret.get()
                           && var1.isFocused()
                           && var1.getCaretPosition() == var1.getAnchor()
                           && !var1.isDisabled()
                           && var1.isEditable()
                           && var1.getLength() > 0;
                     }
                  }
               );
         }

         this.selectionHandle1
            .visibleProperty()
            .bind(
               new BooleanBinding() {
                  {
                     this.bind(
                        new Observable[]{
                           var1.focusedProperty(),
                           var1.anchorProperty(),
                           var1.caretPositionProperty(),
                           var1.disabledProperty(),
                           TextInputControlSkin.this.displayCaret
                        }
                     );
                  }

                  protected boolean computeValue() {
                     return TextInputControlSkin.this.displayCaret.get()
                        && var1.isFocused()
                        && var1.getCaretPosition() != var1.getAnchor()
                        && !var1.isDisabled();
                  }
               }
            );
         this.selectionHandle2
            .visibleProperty()
            .bind(
               new BooleanBinding() {
                  {
                     this.bind(
                        new Observable[]{
                           var1.focusedProperty(),
                           var1.anchorProperty(),
                           var1.caretPositionProperty(),
                           var1.disabledProperty(),
                           TextInputControlSkin.this.displayCaret
                        }
                     );
                  }

                  protected boolean computeValue() {
                     return TextInputControlSkin.this.displayCaret.get()
                        && var1.isFocused()
                        && var1.getCaretPosition() != var1.getAnchor()
                        && !var1.isDisabled();
                  }
               }
            );
         this.caretHandle.getStyleClass().setAll(new String[]{"caret-handle"});
         this.selectionHandle1.getStyleClass().setAll(new String[]{"selection-handle"});
         this.selectionHandle2.getStyleClass().setAll(new String[]{"selection-handle"});
         this.selectionHandle1.setId("selection-handle-1");
         this.selectionHandle2.setId("selection-handle-2");
      }

      if (IS_FXVK_SUPPORTED) {
         if (preload) {
            Scene var2 = var1.getScene();
            if (var2 != null) {
               Window var3 = var2.getWindow();
               if (var3 != null) {
                  FXVK.init(var1);
               }
            }
         }

         var1.focusedProperty()
            .addListener(
               var2x -> {
                  if (FXVK.useFXVK()) {
                     Scene var3x = this.getSkinnable().getScene();
                     if (var1.isEditable() && var1.isFocused()) {
                        FXVK.attach(var1);
                     } else if (var3x == null
                        || var3x.getWindow() == null
                        || !var3x.getWindow().isFocused()
                        || !(var3x.getFocusOwner() instanceof TextInputControl)
                        || !((TextInputControl)var3x.getFocusOwner()).isEditable()) {
                        FXVK.detach();
                     }
                  }
               }
            );
      }

      if (var1.getOnInputMethodTextChanged() == null) {
         var1.setOnInputMethodTextChanged(var1x -> this.handleInputMethodEvent(var1x));
      }

      var1.setInputMethodRequests(
         new ExtendedInputMethodRequests() {
            public Point2D getTextLocation(int var1x) {
               Scene var2 = ((TextInputControl)TextInputControlSkin.this.getSkinnable()).getScene();
               Window var3 = var2.getWindow();
               Rectangle2D var4 = TextInputControlSkin.this.getCharacterBounds(var1.getSelection().getStart() + var1x);
               Point2D var5 = ((TextInputControl)TextInputControlSkin.this.getSkinnable()).localToScene(var4.getMinX(), var4.getMaxY());
               return new Point2D(var3.getX() + var2.getX() + var5.getX(), var3.getY() + var2.getY() + var5.getY());
            }

            public int getLocationOffset(int var1x, int var2) {
               return TextInputControlSkin.this.getInsertionPoint((double)var1x, (double)var2);
            }

            public void cancelLatestCommittedText() {
            }

            public String getSelectedText() {
               TextInputControl var1x = (TextInputControl)TextInputControlSkin.this.getSkinnable();
               IndexRange var2 = var1x.getSelection();
               return var1x.getText(var2.getStart(), var2.getEnd());
            }

            public int getInsertPositionOffset() {
               int var1x = ((TextInputControl)TextInputControlSkin.this.getSkinnable()).getCaretPosition();
               if (var1x < TextInputControlSkin.this.imstart) {
                  return var1x;
               } else {
                  return var1x < TextInputControlSkin.this.imstart + TextInputControlSkin.this.imlength
                     ? TextInputControlSkin.this.imstart
                     : var1x - TextInputControlSkin.this.imlength;
               }
            }

            public String getCommittedText(int var1x, int var2) {
               TextInputControl var3 = (TextInputControl)TextInputControlSkin.this.getSkinnable();
               if (var1x < TextInputControlSkin.this.imstart) {
                  return var2 <= TextInputControlSkin.this.imstart
                     ? var3.getText(var1x, var2)
                     : var3.getText(var1x, TextInputControlSkin.this.imstart)
                        + var3.getText(TextInputControlSkin.this.imstart + TextInputControlSkin.this.imlength, var2 + TextInputControlSkin.this.imlength);
               } else {
                  return var3.getText(var1x + TextInputControlSkin.this.imlength, var2 + TextInputControlSkin.this.imlength);
               }
            }

            public int getCommittedTextLength() {
               return ((TextInputControl)TextInputControlSkin.this.getSkinnable()).getText().length() - TextInputControlSkin.this.imlength;
            }
         }
      );
   }

   private final void setBlink(boolean var1) {
      this.blinkProperty().set(var1);
   }

   private final boolean isBlink() {
      return this.blinkProperty().get();
   }

   private final BooleanProperty blinkProperty() {
      if (this.blink == null) {
         this.blink = new SimpleBooleanProperty(this, "blink", true);
      }

      return this.blink;
   }

   protected final void setTextFill(Paint var1) {
      this.textFill.set(var1);
   }

   protected final Paint getTextFill() {
      return (Paint)this.textFill.get();
   }

   protected final ObjectProperty<Paint> textFillProperty() {
      return this.textFill;
   }

   protected final void setPromptTextFill(Paint var1) {
      this.promptTextFill.set(var1);
   }

   protected final Paint getPromptTextFill() {
      return (Paint)this.promptTextFill.get();
   }

   protected final ObjectProperty<Paint> promptTextFillProperty() {
      return this.promptTextFill;
   }

   protected final void setHighlightFill(Paint var1) {
      this.highlightFill.set(var1);
   }

   protected final Paint getHighlightFill() {
      return (Paint)this.highlightFill.get();
   }

   protected final ObjectProperty<Paint> highlightFillProperty() {
      return this.highlightFill;
   }

   protected final void setHighlightTextFill(Paint var1) {
      this.highlightTextFill.set(var1);
   }

   protected final Paint getHighlightTextFill() {
      return (Paint)this.highlightTextFill.get();
   }

   protected final ObjectProperty<Paint> highlightTextFillProperty() {
      return this.highlightTextFill;
   }

   private final void setDisplayCaret(boolean var1) {
      this.displayCaret.set(var1);
   }

   private final boolean isDisplayCaret() {
      return this.displayCaret.get();
   }

   private final BooleanProperty displayCaretProperty() {
      return this.displayCaret;
   }

   protected final BooleanProperty forwardBiasProperty() {
      return this.forwardBias;
   }

   public final void setForwardBias(boolean var1) {
      this.forwardBias.set(var1);
   }

   protected final boolean isForwardBias() {
      return this.forwardBias.get();
   }

   protected abstract PathElement[] getUnderlineShape(int var1, int var2);

   protected abstract PathElement[] getRangeShape(int var1, int var2);

   protected abstract void addHighlight(List<? extends Node> var1, int var2);

   protected abstract void removeHighlight(List<? extends Node> var1);

   public abstract void moveCaret(TextInputControlSkin.TextUnit var1, TextInputControlSkin.Direction var2, boolean var3);

   public Point2D getMenuPosition() {
      if (SHOW_HANDLES) {
         if (this.caretHandle.isVisible()) {
            return new Point2D(this.caretHandle.getLayoutX() + this.caretHandle.getWidth() / 2.0, this.caretHandle.getLayoutY());
         } else {
            return this.selectionHandle1.isVisible() && this.selectionHandle2.isVisible()
               ? new Point2D(
                  (
                        this.selectionHandle1.getLayoutX()
                           + this.selectionHandle1.getWidth() / 2.0
                           + this.selectionHandle2.getLayoutX()
                           + this.selectionHandle2.getWidth() / 2.0
                     )
                     / 2.0,
                  this.selectionHandle2.getLayoutY() + this.selectionHandle2.getHeight() / 2.0
               )
               : null;
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   protected String maskText(String var1) {
      return var1;
   }

   protected int getInsertionPoint(double var1, double var3) {
      return 0;
   }

   public Rectangle2D getCharacterBounds(int var1) {
      return null;
   }

   protected void scrollCharacterToVisible(int var1) {
   }

   protected void invalidateMetrics() {
   }

   protected void updateTextFill() {
   }

   protected void updateHighlightFill() {
   }

   protected void updateHighlightTextFill() {
   }

   protected void handleInputMethodEvent(InputMethodEvent var1) {
      TextInputControl var2 = this.getSkinnable();
      if (var2.isEditable() && !var2.textProperty().isBound() && !var2.isDisabled()) {
         if (this.imlength != 0) {
            this.removeHighlight(this.imattrs);
            this.imattrs.clear();
            var2.selectRange(this.imstart, this.imstart + this.imlength);
         }

         if (var1.getCommitted().length() != 0) {
            String var3 = var1.getCommitted();
            var2.replaceText(var2.getSelection(), var3);
         }

         this.imstart = var2.getSelection().getStart();
         StringBuilder var8 = new StringBuilder();

         for (InputMethodTextRun var5 : var1.getComposed()) {
            var8.append(var5.getText());
         }

         var2.replaceText(var2.getSelection(), var8.toString());
         this.imlength = var8.length();
         if (this.imlength != 0) {
            int var9 = this.imstart;

            for (InputMethodTextRun var6 : var1.getComposed()) {
               int var7 = var9 + var6.getText().length();
               this.createInputMethodAttributes(var6.getHighlight(), var9, var7);
               var9 = var7;
            }

            this.addHighlight(this.imattrs, this.imstart);
            int var11 = var1.getCaretPosition();
            if (var11 >= 0 && var11 < this.imlength) {
               var2.selectRange(this.imstart + var11, this.imstart + var11);
            }
         }
      }
   }

   public void setCaretAnimating(boolean var1) {
      if (var1) {
         this.caretBlinking.start();
      } else {
         this.caretBlinking.stop();
         this.blinkProperty().set(true);
      }
   }

   TextInputControlBehavior getBehavior() {
      return null;
   }

   ObservableBooleanValue caretVisibleProperty() {
      return this.caretVisible;
   }

   boolean isRTL() {
      return this.getSkinnable().getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
   }

   private void createInputMethodAttributes(InputMethodHighlight var1, int var2, int var3) {
      double var4 = 0.0;
      double var6 = 0.0;
      double var8 = 0.0;
      double var10 = 0.0;
      PathElement[] var12 = this.getUnderlineShape(var2, var3);

      for (int var13 = 0; var13 < var12.length; var13++) {
         PathElement var14 = var12[var13];
         if (var14 instanceof MoveTo) {
            var4 = var6 = ((MoveTo)var14).getX();
            var8 = var10 = ((MoveTo)var14).getY();
         } else if (var14 instanceof LineTo) {
            var4 = var4 < ((LineTo)var14).getX() ? var4 : ((LineTo)var14).getX();
            var6 = var6 > ((LineTo)var14).getX() ? var6 : ((LineTo)var14).getX();
            var8 = var8 < ((LineTo)var14).getY() ? var8 : ((LineTo)var14).getY();
            var10 = var10 > ((LineTo)var14).getY() ? var10 : ((LineTo)var14).getY();
         } else if (var14 instanceof HLineTo) {
            var4 = var4 < ((HLineTo)var14).getX() ? var4 : ((HLineTo)var14).getX();
            var6 = var6 > ((HLineTo)var14).getX() ? var6 : ((HLineTo)var14).getX();
         } else if (var14 instanceof VLineTo) {
            var8 = var8 < ((VLineTo)var14).getY() ? var8 : ((VLineTo)var14).getY();
            var10 = var10 > ((VLineTo)var14).getY() ? var10 : ((VLineTo)var14).getY();
         }

         if (var14 instanceof ClosePath || var13 == var12.length - 1 || var13 < var12.length - 1 && var12[var13 + 1] instanceof MoveTo) {
            Object var15 = null;
            if (var1 == InputMethodHighlight.SELECTED_RAW) {
               var15 = new Path();
               ((Path)var15).getElements().addAll(this.getRangeShape(var2, var3));
               var15.setFill(Color.BLUE);
               var15.setOpacity(0.3F);
            } else if (var1 == InputMethodHighlight.UNSELECTED_RAW) {
               var15 = new Line(var4 + 2.0, var10 + 1.0, var6 - 2.0, var10 + 1.0);
               var15.setStroke((Paint)this.textFill.get());
               var15.setStrokeWidth(var10 - var8);
               ObservableList var16 = var15.getStrokeDashArray();
               var16.add(2.0);
               var16.add(2.0);
            } else if (var1 == InputMethodHighlight.SELECTED_CONVERTED) {
               var15 = new Line(var4 + 2.0, var10 + 1.0, var6 - 2.0, var10 + 1.0);
               var15.setStroke((Paint)this.textFill.get());
               var15.setStrokeWidth((var10 - var8) * 3.0);
            } else if (var1 == InputMethodHighlight.UNSELECTED_CONVERTED) {
               var15 = new Line(var4 + 2.0, var10 + 1.0, var6 - 2.0, var10 + 1.0);
               var15.setStroke((Paint)this.textFill.get());
               var15.setStrokeWidth(var10 - var8);
            }

            if (var15 != null) {
               var15.setManaged(false);
               this.imattrs.add((Shape)var15);
            }
         }
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TextInputControlSkin.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case SHOW_TEXT_RANGE:
            Integer var3 = (Integer)var2[0];
            Integer var4 = (Integer)var2[1];
            if (var3 != null && var4 != null) {
               this.scrollCharacterToVisible(var4);
               this.scrollCharacterToVisible(var3);
               this.scrollCharacterToVisible(var4);
            }
            break;
         default:
            super.executeAccessibleAction(var1, var2);
      }
   }

   static {
      Void var0 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         String var0x = System.getProperty("com.sun.javafx.virtualKeyboard.preload");
         if (var0x != null && var0x.equalsIgnoreCase("PRERENDER")) {
            preload = true;
         }

         return null;
      }));
   }

   private static final class CaretBlinking {
      private final Timeline caretTimeline;
      private final WeakReference<BooleanProperty> blinkPropertyRef;

      public CaretBlinking(BooleanProperty var1) {
         this.blinkPropertyRef = new WeakReference<>(var1);
         this.caretTimeline = new Timeline();
         this.caretTimeline.setCycleCount(-1);
         this.caretTimeline
            .getKeyFrames()
            .addAll(
               new KeyFrame[]{
                  new KeyFrame(Duration.ZERO, var1x -> this.setBlink(false), new KeyValue[0]),
                  new KeyFrame(Duration.seconds(0.5), var1x -> this.setBlink(true), new KeyValue[0]),
                  new KeyFrame(Duration.seconds(1.0), new KeyValue[0])
               }
            );
      }

      public void start() {
         this.caretTimeline.play();
      }

      public void stop() {
         this.caretTimeline.stop();
      }

      private void setBlink(boolean var1) {
         BooleanProperty var2 = this.blinkPropertyRef.get();
         if (var2 == null) {
            this.caretTimeline.stop();
         } else {
            var2.set(var1);
         }
      }
   }

   public static enum Direction {
      LEFT,
      RIGHT,
      UP,
      DOWN,
      BEGINNING,
      END;
   }

   private static class StyleableProperties {
      private static final CssMetaData<TextInputControl, Paint> TEXT_FILL = new CssMetaData<TextInputControl, Paint>(
         "-fx-text-fill", PaintConverter.getInstance(), Color.BLACK
      ) {
         public boolean isSettable(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return var2.textFill == null || !var2.textFill.isBound();
         }

         public StyleableProperty<Paint> getStyleableProperty(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return (StyleableProperty<Paint>)var2.textFill;
         }
      };
      private static final CssMetaData<TextInputControl, Paint> PROMPT_TEXT_FILL = new CssMetaData<TextInputControl, Paint>(
         "-fx-prompt-text-fill", PaintConverter.getInstance(), Color.GRAY
      ) {
         public boolean isSettable(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return var2.promptTextFill == null || !var2.promptTextFill.isBound();
         }

         public StyleableProperty<Paint> getStyleableProperty(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return (StyleableProperty<Paint>)var2.promptTextFill;
         }
      };
      private static final CssMetaData<TextInputControl, Paint> HIGHLIGHT_FILL = new CssMetaData<TextInputControl, Paint>(
         "-fx-highlight-fill", PaintConverter.getInstance(), Color.DODGERBLUE
      ) {
         public boolean isSettable(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return var2.highlightFill == null || !var2.highlightFill.isBound();
         }

         public StyleableProperty<Paint> getStyleableProperty(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return (StyleableProperty<Paint>)var2.highlightFill;
         }
      };
      private static final CssMetaData<TextInputControl, Paint> HIGHLIGHT_TEXT_FILL = new CssMetaData<TextInputControl, Paint>(
         "-fx-highlight-text-fill", PaintConverter.getInstance(), Color.WHITE
      ) {
         public boolean isSettable(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return var2.highlightTextFill == null || !var2.highlightTextFill.isBound();
         }

         public StyleableProperty<Paint> getStyleableProperty(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return (StyleableProperty<Paint>)var2.highlightTextFill;
         }
      };
      private static final CssMetaData<TextInputControl, Boolean> DISPLAY_CARET = new CssMetaData<TextInputControl, Boolean>(
         "-fx-display-caret", BooleanConverter.getInstance(), Boolean.TRUE
      ) {
         public boolean isSettable(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return var2.displayCaret == null || !var2.displayCaret.isBound();
         }

         public StyleableProperty<Boolean> getStyleableProperty(TextInputControl var1) {
            TextInputControlSkin var2 = (TextInputControlSkin)var1.getSkin();
            return (StyleableProperty<Boolean>)var2.displayCaret;
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(SkinBase.getClassCssMetaData());
         var0.add(TEXT_FILL);
         var0.add(PROMPT_TEXT_FILL);
         var0.add(HIGHLIGHT_FILL);
         var0.add(HIGHLIGHT_TEXT_FILL);
         var0.add(DISPLAY_CARET);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   public static enum TextUnit {
      CHARACTER,
      WORD,
      LINE,
      PARAGRAPH,
      PAGE;
   }
}
