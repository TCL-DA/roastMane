package javafx.scene.control;

import com.sun.javafx.beans.IDProperty;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.stage.PopupWindowHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.DurationConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.skin.TooltipSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import javafx.util.Duration;

@IDProperty("id")
public class Tooltip extends PopupControl {
   private static String TOOLTIP_PROP_KEY = "javafx.scene.control.Tooltip";
   private static int TOOLTIP_XOFFSET = 10;
   private static int TOOLTIP_YOFFSET = 7;
   private static Tooltip.TooltipBehavior BEHAVIOR = new Tooltip.TooltipBehavior(false);
   private final StringProperty text = new SimpleStringProperty(this, "text", "") {
      protected void invalidated() {
         super.invalidated();
         String var1 = this.get();
         if (Tooltip.this.isShowing() && var1 != null && !var1.equals(Tooltip.this.getText())) {
            Tooltip.this.setAnchorX(Tooltip.BEHAVIOR.lastMouseX);
            Tooltip.this.setAnchorY(Tooltip.BEHAVIOR.lastMouseY);
         }
      }
   };
   private final ObjectProperty<TextAlignment> textAlignment;
   private final ObjectProperty<OverrunStyle> textOverrun;
   private final BooleanProperty wrapText;
   private final ObjectProperty<Font> font;
   private final ObjectProperty<Duration> showDelayProperty;
   private final ObjectProperty<Duration> showDurationProperty;
   private final ObjectProperty<Duration> hideDelayProperty;
   private final ObjectProperty<Node> graphic;
   private StyleableStringProperty imageUrl;
   private final ObjectProperty<ContentDisplay> contentDisplay;
   private final DoubleProperty graphicTextGap;
   private final ReadOnlyBooleanWrapper activated;
   private static final CssMetaData<Tooltip.CSSBridge, Font> FONT = new FontCssMetaData<Tooltip.CSSBridge>("-fx-font", Font.getDefault()) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.fontProperty().isBound();
      }

      public StyleableProperty<Font> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<Font>)var1.tooltip.fontProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, TextAlignment> TEXT_ALIGNMENT = new CssMetaData<Tooltip.CSSBridge, TextAlignment>(
      "-fx-text-alignment", new EnumConverter(TextAlignment.class), TextAlignment.LEFT
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.textAlignmentProperty().isBound();
      }

      public StyleableProperty<TextAlignment> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<TextAlignment>)var1.tooltip.textAlignmentProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, OverrunStyle> TEXT_OVERRUN = new CssMetaData<Tooltip.CSSBridge, OverrunStyle>(
      "-fx-text-overrun", new EnumConverter(OverrunStyle.class), OverrunStyle.ELLIPSIS
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.textOverrunProperty().isBound();
      }

      public StyleableProperty<OverrunStyle> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<OverrunStyle>)var1.tooltip.textOverrunProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, Boolean> WRAP_TEXT = new CssMetaData<Tooltip.CSSBridge, Boolean>(
      "-fx-wrap-text", BooleanConverter.getInstance(), Boolean.FALSE
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.wrapTextProperty().isBound();
      }

      public StyleableProperty<Boolean> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<Boolean>)var1.tooltip.wrapTextProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, String> GRAPHIC = new CssMetaData<Tooltip.CSSBridge, String>(
      "-fx-graphic", StringConverter.getInstance()
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.graphicProperty().isBound();
      }

      public StyleableProperty<String> getStyleableProperty(Tooltip.CSSBridge var1) {
         return var1.tooltip.imageUrlProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, ContentDisplay> CONTENT_DISPLAY = new CssMetaData<Tooltip.CSSBridge, ContentDisplay>(
      "-fx-content-display", new EnumConverter(ContentDisplay.class), ContentDisplay.LEFT
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.contentDisplayProperty().isBound();
      }

      public StyleableProperty<ContentDisplay> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<ContentDisplay>)var1.tooltip.contentDisplayProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, Number> GRAPHIC_TEXT_GAP = new CssMetaData<Tooltip.CSSBridge, Number>(
      "-fx-graphic-text-gap", SizeConverter.getInstance(), 4.0
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.graphicTextGapProperty().isBound();
      }

      public StyleableProperty<Number> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<Number>)var1.tooltip.graphicTextGapProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, Duration> SHOW_DELAY = new CssMetaData<Tooltip.CSSBridge, Duration>(
      "-fx-show-delay", DurationConverter.getInstance(), new Duration(1000.0)
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.showDelayProperty().isBound();
      }

      public StyleableProperty<Duration> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<Duration>)var1.tooltip.showDelayProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, Duration> SHOW_DURATION = new CssMetaData<Tooltip.CSSBridge, Duration>(
      "-fx-show-duration", DurationConverter.getInstance(), new Duration(5000.0)
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.showDurationProperty().isBound();
      }

      public StyleableProperty<Duration> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<Duration>)var1.tooltip.showDurationProperty();
      }
   };
   private static final CssMetaData<Tooltip.CSSBridge, Duration> HIDE_DELAY = new CssMetaData<Tooltip.CSSBridge, Duration>(
      "-fx-hide-delay", DurationConverter.getInstance(), new Duration(200.0)
   ) {
      public boolean isSettable(Tooltip.CSSBridge var1) {
         return !var1.tooltip.hideDelayProperty().isBound();
      }

      public StyleableProperty<Duration> getStyleableProperty(Tooltip.CSSBridge var1) {
         return (StyleableProperty<Duration>)var1.tooltip.hideDelayProperty();
      }
   };
   private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

   public static void install(Node var0, Tooltip var1) {
      BEHAVIOR.install(var0, var1);
   }

   public static void uninstall(Node var0, Tooltip var1) {
      BEHAVIOR.uninstall(var0);
   }

   public Tooltip() {
      this(null);
   }

   public Tooltip(String var1) {
      this.textAlignment = new SimpleStyleableObjectProperty(TEXT_ALIGNMENT, this, "textAlignment", TextAlignment.LEFT);
      this.textOverrun = new SimpleStyleableObjectProperty(TEXT_OVERRUN, this, "textOverrun", OverrunStyle.ELLIPSIS);
      this.wrapText = new SimpleStyleableBooleanProperty(WRAP_TEXT, this, "wrapText", false);
      this.font = new StyleableObjectProperty<Font>(Font.getDefault()) {
         private boolean fontSetByCss = false;

         public void applyStyle(StyleOrigin var1, Font var2) {
            try {
               this.fontSetByCss = true;
               super.applyStyle(var1, var2);
            } catch (Exception var7) {
               throw var7;
            } finally {
               this.fontSetByCss = false;
            }
         }

         public void set(Font var1) {
            Font var2 = (Font)this.get();
            StyleOrigin var3 = ((StyleableObjectProperty)Tooltip.this.font).getStyleOrigin();
            if (var3 == null || (var1 != null ? !var1.equals(var2) : var2 != null)) {
               super.set(var1);
            }
         }

         protected void invalidated() {
            if (!this.fontSetByCss) {
               NodeHelper.reapplyCSS(Tooltip.this.bridge);
            }
         }

         public CssMetaData<Tooltip.CSSBridge, Font> getCssMetaData() {
            return Tooltip.FONT;
         }

         public Object getBean() {
            return Tooltip.this;
         }

         public String getName() {
            return "font";
         }
      };
      this.showDelayProperty = new SimpleStyleableObjectProperty(SHOW_DELAY, this, "showDelay", new Duration(1000.0));
      this.showDurationProperty = new SimpleStyleableObjectProperty(SHOW_DURATION, this, "showDuration", new Duration(5000.0));
      this.hideDelayProperty = new SimpleStyleableObjectProperty(HIDE_DELAY, this, "hideDelay", new Duration(200.0));
      this.graphic = new StyleableObjectProperty<Node>() {
         public CssMetaData getCssMetaData() {
            return Tooltip.GRAPHIC;
         }

         public Object getBean() {
            return Tooltip.this;
         }

         public String getName() {
            return "graphic";
         }
      };
      this.imageUrl = null;
      this.contentDisplay = new SimpleStyleableObjectProperty(CONTENT_DISPLAY, this, "contentDisplay", ContentDisplay.LEFT);
      this.graphicTextGap = new SimpleStyleableDoubleProperty(GRAPHIC_TEXT_GAP, this, "graphicTextGap", 4.0);
      this.activated = new ReadOnlyBooleanWrapper(this, "activated");
      if (var1 != null) {
         this.setText(var1);
      }

      this.bridge = new Tooltip.CSSBridge();
      PopupWindowHelper.getContent(this).setAll(new Node[]{this.bridge});
      this.getStyleClass().setAll(new String[]{"tooltip"});
   }

   public final StringProperty textProperty() {
      return this.text;
   }

   public final void setText(String var1) {
      this.textProperty().setValue(var1);
   }

   public final String getText() {
      return this.text.getValue() == null ? "" : this.text.getValue();
   }

   public final ObjectProperty<TextAlignment> textAlignmentProperty() {
      return this.textAlignment;
   }

   public final void setTextAlignment(TextAlignment var1) {
      this.textAlignmentProperty().setValue(var1);
   }

   public final TextAlignment getTextAlignment() {
      return (TextAlignment)this.textAlignmentProperty().getValue();
   }

   public final ObjectProperty<OverrunStyle> textOverrunProperty() {
      return this.textOverrun;
   }

   public final void setTextOverrun(OverrunStyle var1) {
      this.textOverrunProperty().setValue(var1);
   }

   public final OverrunStyle getTextOverrun() {
      return (OverrunStyle)this.textOverrunProperty().getValue();
   }

   public final BooleanProperty wrapTextProperty() {
      return this.wrapText;
   }

   public final void setWrapText(boolean var1) {
      this.wrapTextProperty().setValue(var1);
   }

   public final boolean isWrapText() {
      return this.wrapTextProperty().getValue();
   }

   public final ObjectProperty<Font> fontProperty() {
      return this.font;
   }

   public final void setFont(Font var1) {
      this.fontProperty().setValue(var1);
   }

   public final Font getFont() {
      return (Font)this.fontProperty().getValue();
   }

   public final ObjectProperty<Duration> showDelayProperty() {
      return this.showDelayProperty;
   }

   public final void setShowDelay(Duration var1) {
      this.showDelayProperty.set(var1);
   }

   public final Duration getShowDelay() {
      return (Duration)this.showDelayProperty.get();
   }

   public final ObjectProperty<Duration> showDurationProperty() {
      return this.showDurationProperty;
   }

   public final void setShowDuration(Duration var1) {
      this.showDurationProperty.set(var1);
   }

   public final Duration getShowDuration() {
      return (Duration)this.showDurationProperty.get();
   }

   public final ObjectProperty<Duration> hideDelayProperty() {
      return this.hideDelayProperty;
   }

   public final void setHideDelay(Duration var1) {
      this.hideDelayProperty.set(var1);
   }

   public final Duration getHideDelay() {
      return (Duration)this.hideDelayProperty.get();
   }

   public final ObjectProperty<Node> graphicProperty() {
      return this.graphic;
   }

   public final void setGraphic(Node var1) {
      this.graphicProperty().setValue(var1);
   }

   public final Node getGraphic() {
      return (Node)this.graphicProperty().getValue();
   }

   private StyleableStringProperty imageUrlProperty() {
      if (this.imageUrl == null) {
         this.imageUrl = new StyleableStringProperty() {
            StyleOrigin origin = StyleOrigin.USER;

            public void applyStyle(StyleOrigin var1, String var2) {
               this.origin = var1;
               if (Tooltip.this.graphic == null || !Tooltip.this.graphic.isBound()) {
                  super.applyStyle(var1, var2);
               }

               this.origin = StyleOrigin.USER;
            }

            protected void invalidated() {
               String var1 = super.get();
               if (var1 == null) {
                  ((StyleableProperty)Tooltip.this.graphicProperty()).applyStyle(this.origin, null);
               } else {
                  Node var2 = Tooltip.this.getGraphic();
                  if (var2 instanceof ImageView) {
                     ImageView var3 = (ImageView)var2;
                     Image var4 = var3.getImage();
                     if (var4 != null) {
                        String var5 = var4.getUrl();
                        if (var1.equals(var5)) {
                           return;
                        }
                     }
                  }

                  Image var6 = StyleManager.getInstance().getCachedImage(var1);
                  if (var6 != null) {
                     ((StyleableProperty)Tooltip.this.graphicProperty()).applyStyle(this.origin, new ImageView(var6));
                  }
               }
            }

            public String get() {
               Node var1 = Tooltip.this.getGraphic();
               if (var1 instanceof ImageView) {
                  Image var2 = ((ImageView)var1).getImage();
                  if (var2 != null) {
                     return var2.getUrl();
                  }
               }

               return null;
            }

            public StyleOrigin getStyleOrigin() {
               return Tooltip.this.graphic != null ? ((StyleableProperty)Tooltip.this.graphic).getStyleOrigin() : null;
            }

            public Object getBean() {
               return Tooltip.this;
            }

            public String getName() {
               return "imageUrl";
            }

            public CssMetaData<Tooltip.CSSBridge, String> getCssMetaData() {
               return Tooltip.GRAPHIC;
            }
         };
      }

      return this.imageUrl;
   }

   public final ObjectProperty<ContentDisplay> contentDisplayProperty() {
      return this.contentDisplay;
   }

   public final void setContentDisplay(ContentDisplay var1) {
      this.contentDisplayProperty().setValue(var1);
   }

   public final ContentDisplay getContentDisplay() {
      return (ContentDisplay)this.contentDisplayProperty().getValue();
   }

   public final DoubleProperty graphicTextGapProperty() {
      return this.graphicTextGap;
   }

   public final void setGraphicTextGap(double var1) {
      this.graphicTextGapProperty().setValue(var1);
   }

   public final double getGraphicTextGap() {
      return this.graphicTextGapProperty().getValue();
   }

   final void setActivated(boolean var1) {
      this.activated.set(var1);
   }

   public final boolean isActivated() {
      return this.activated.get();
   }

   public final ReadOnlyBooleanProperty activatedProperty() {
      return this.activated.getReadOnlyProperty();
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TooltipSkin(this);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   public Styleable getStyleableParent() {
      return (Styleable)(BEHAVIOR.hoveredNode == null ? super.getStyleableParent() : BEHAVIOR.hoveredNode);
   }

   static {
      ArrayList var0 = new ArrayList<>(PopupControl.getClassCssMetaData());
      var0.add(FONT);
      var0.add(TEXT_ALIGNMENT);
      var0.add(TEXT_OVERRUN);
      var0.add(WRAP_TEXT);
      var0.add(GRAPHIC);
      var0.add(CONTENT_DISPLAY);
      var0.add(GRAPHIC_TEXT_GAP);
      var0.add(SHOW_DELAY);
      var0.add(SHOW_DURATION);
      var0.add(HIDE_DELAY);
      STYLEABLES = Collections.unmodifiableList(var0);
   }

   private final class CSSBridge extends PopupControl.CSSBridge {
      private Tooltip tooltip = Tooltip.this;

      CSSBridge() {
         this.setAccessibleRole(AccessibleRole.TOOLTIP);
      }
   }

   private static class TooltipBehavior {
      private Timeline activationTimer = new Timeline();
      private Timeline hideTimer = new Timeline();
      private Timeline leftTimer = new Timeline();
      private Node hoveredNode;
      private Tooltip activatedTooltip;
      private Tooltip visibleTooltip;
      private double lastMouseX;
      private double lastMouseY;
      private boolean hideOnExit;
      private boolean cssForced = false;
      private EventHandler<MouseEvent> MOVE_HANDLER = var1x -> {
         this.lastMouseX = var1x.getScreenX();
         this.lastMouseY = var1x.getScreenY();
         if (this.hideTimer.getStatus() != Status.RUNNING) {
            this.hoveredNode = (Node)var1x.getSource();
            Tooltip var2 = (Tooltip)this.hoveredNode.getProperties().get(Tooltip.TOOLTIP_PROP_KEY);
            if (var2 != null) {
               Window var3 = this.getWindow(this.hoveredNode);
               boolean var4 = this.isWindowHierarchyVisible(this.hoveredNode);
               if (var3 != null && var4) {
                  if (this.leftTimer.getStatus() == Status.RUNNING) {
                     if (this.visibleTooltip != null) {
                        this.visibleTooltip.hide();
                     }

                     this.visibleTooltip = var2;
                     var2.show(var3, var1x.getScreenX() + (double)Tooltip.TOOLTIP_XOFFSET, var1x.getScreenY() + (double)Tooltip.TOOLTIP_YOFFSET);
                     this.leftTimer.stop();
                     if (var2.getShowDuration() != null) {
                        this.hideTimer.getKeyFrames().setAll(new KeyFrame[]{new KeyFrame(var2.getShowDuration(), new KeyValue[0])});
                     }

                     this.hideTimer.playFromStart();
                  } else {
                     if (!this.cssForced) {
                        double var5 = var2.getOpacity();
                        var2.setOpacity(0.0);
                        var2.show(var3);
                        var2.hide();
                        var2.setOpacity(var5);
                        this.cssForced = true;
                     }

                     var2.setActivated(true);
                     this.activatedTooltip = var2;
                     this.activationTimer.stop();
                     if (var2.getShowDelay() != null) {
                        this.activationTimer.getKeyFrames().setAll(new KeyFrame[]{new KeyFrame(var2.getShowDelay(), new KeyValue[0])});
                     }

                     this.activationTimer.playFromStart();
                  }
               }
            }
         }
      };
      private EventHandler<MouseEvent> LEAVING_HANDLER = var1x -> {
         if (this.activationTimer.getStatus() == Status.RUNNING) {
            this.activationTimer.stop();
         } else if (this.hideTimer.getStatus() == Status.RUNNING) {
            assert this.visibleTooltip != null;

            this.hideTimer.stop();
            if (this.hideOnExit) {
               this.visibleTooltip.hide();
            }

            Node var2 = (Node)var1x.getSource();
            Tooltip var3 = (Tooltip)var2.getProperties().get(Tooltip.TOOLTIP_PROP_KEY);
            if (var3 != null) {
               if (var3.getHideDelay() != null) {
                  this.leftTimer.getKeyFrames().setAll(new KeyFrame[]{new KeyFrame(var3.getHideDelay(), new KeyValue[0])});
               }

               this.leftTimer.playFromStart();
            }
         }

         this.hoveredNode = null;
         this.activatedTooltip = null;
         if (this.hideOnExit) {
            this.visibleTooltip = null;
         }
      };
      private EventHandler<MouseEvent> KILL_HANDLER = var1x -> {
         this.activationTimer.stop();
         this.hideTimer.stop();
         this.leftTimer.stop();
         if (this.visibleTooltip != null) {
            this.visibleTooltip.hide();
         }

         this.hoveredNode = null;
         this.activatedTooltip = null;
         this.visibleTooltip = null;
      };

      TooltipBehavior(boolean var1) {
         this.hideOnExit = var1;
         this.activationTimer.setOnFinished(var1x -> {
            assert this.activatedTooltip != null;

            Window var2 = this.getWindow(this.hoveredNode);
            boolean var3 = this.isWindowHierarchyVisible(this.hoveredNode);
            if (var2 != null && var2.isShowing() && var3) {
               double var4 = this.lastMouseX;
               double var6 = this.lastMouseY;
               NodeOrientation var8 = this.hoveredNode.getEffectiveNodeOrientation();
               this.activatedTooltip.getScene().setNodeOrientation(var8);
               if (var8 == NodeOrientation.RIGHT_TO_LEFT) {
                  var4 -= this.activatedTooltip.getWidth();
               }

               this.activatedTooltip.show(var2, var4 + (double)Tooltip.TOOLTIP_XOFFSET, var6 + (double)Tooltip.TOOLTIP_YOFFSET);
               if (var6 + (double)Tooltip.TOOLTIP_YOFFSET > this.activatedTooltip.getAnchorY()) {
                  this.activatedTooltip.hide();
                  var6 -= this.activatedTooltip.getHeight();
                  this.activatedTooltip.show(var2, var4 + (double)Tooltip.TOOLTIP_XOFFSET, var6);
               }

               this.visibleTooltip = this.activatedTooltip;
               this.hoveredNode = null;
               if (this.activatedTooltip.getShowDuration() != null) {
                  this.hideTimer.getKeyFrames().setAll(new KeyFrame[]{new KeyFrame(this.activatedTooltip.getShowDuration(), new KeyValue[0])});
               }

               this.hideTimer.playFromStart();
            }

            this.activatedTooltip.setActivated(false);
            this.activatedTooltip = null;
         });
         this.hideTimer.setOnFinished(var1x -> {
            assert this.visibleTooltip != null;

            this.visibleTooltip.hide();
            this.visibleTooltip = null;
            this.hoveredNode = null;
         });
         this.leftTimer.setOnFinished(var2 -> {
            if (!var1) {
               assert this.visibleTooltip != null;

               this.visibleTooltip.hide();
               this.visibleTooltip = null;
               this.hoveredNode = null;
            }
         });
      }

      private void install(Node var1, Tooltip var2) {
         if (var1 != null) {
            var1.addEventHandler(MouseEvent.MOUSE_MOVED, this.MOVE_HANDLER);
            var1.addEventHandler(MouseEvent.MOUSE_EXITED, this.LEAVING_HANDLER);
            var1.addEventHandler(MouseEvent.MOUSE_PRESSED, this.KILL_HANDLER);
            var1.getProperties().put(Tooltip.TOOLTIP_PROP_KEY, var2);
         }
      }

      private void uninstall(Node var1) {
         if (var1 != null) {
            var1.removeEventHandler(MouseEvent.MOUSE_MOVED, this.MOVE_HANDLER);
            var1.removeEventHandler(MouseEvent.MOUSE_EXITED, this.LEAVING_HANDLER);
            var1.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.KILL_HANDLER);
            Tooltip var2 = (Tooltip)var1.getProperties().get(Tooltip.TOOLTIP_PROP_KEY);
            if (var2 != null) {
               var1.getProperties().remove(Tooltip.TOOLTIP_PROP_KEY);
               if (var2.equals(this.visibleTooltip) || var2.equals(this.activatedTooltip)) {
                  this.KILL_HANDLER.handle(null);
               }
            }
         }
      }

      private Window getWindow(Node var1) {
         Scene var2 = var1 == null ? null : var1.getScene();
         return var2 == null ? null : var2.getWindow();
      }

      private boolean isWindowHierarchyVisible(Node var1) {
         boolean var2 = var1 != null;

         for (Parent var3 = var1 == null ? null : var1.getParent(); var3 != null && var2; var3 = var3.getParent()) {
            var2 = var3.isVisible();
         }

         return var2;
      }
   }
}
