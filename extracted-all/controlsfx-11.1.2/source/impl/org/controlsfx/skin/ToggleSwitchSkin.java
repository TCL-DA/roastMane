package impl.org.controlsfx.skin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.animation.Animation.Status;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;

public class ToggleSwitchSkin extends SkinBase<ToggleSwitch> {
   private final StackPane thumb = new StackPane();
   private final StackPane thumbArea = new StackPane();
   private final Label label = new Label();
   private final StackPane labelContainer = new StackPane();
   private final TranslateTransition transition = new TranslateTransition(Duration.millis(this.getThumbMoveAnimationTime()), this.thumb);
   private DoubleProperty thumbMoveAnimationTime;
   private static final CssMetaData<ToggleSwitch, Number> THUMB_MOVE_ANIMATION_TIME = new CssMetaData<ToggleSwitch, Number>(
      "-thumb-move-animation-time", SizeConverter.getInstance(), 200
   ) {
      public boolean isSettable(ToggleSwitch toggleSwitch) {
         ToggleSwitchSkin skin = (ToggleSwitchSkin)toggleSwitch.getSkin();
         return skin.thumbMoveAnimationTime == null || !skin.thumbMoveAnimationTime.isBound();
      }

      public StyleableProperty<Number> getStyleableProperty(ToggleSwitch toggleSwitch) {
         ToggleSwitchSkin skin = (ToggleSwitchSkin)toggleSwitch.getSkin();
         return (StyleableProperty<Number>)skin.thumbMoveAnimationTimeProperty();
      }
   };
   private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

   public ToggleSwitchSkin(ToggleSwitch control) {
      super(control);
      this.transition.setFromX(0.0);
      this.label.textProperty().bind(control.textProperty());
      this.getChildren().addAll(new Node[]{this.labelContainer, this.thumbArea, this.thumb});
      this.labelContainer.getChildren().addAll(new Node[]{this.label});
      StackPane.setAlignment(this.label, Pos.CENTER_LEFT);
      this.thumb.getStyleClass().setAll(new String[]{"thumb"});
      this.thumbArea.getStyleClass().setAll(new String[]{"thumb-area"});
      control.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
         if (event.getCode() == KeyCode.SPACE) {
            this.toggle(control);
         }
      });
      this.thumbArea.setOnMouseReleased(event -> this.toggle(control));
      this.thumb.setOnMouseReleased(event -> this.toggle(control));
      control.selectedProperty().addListener((observable, oldValue, newValue) -> {
         if (newValue != oldValue) {
            this.selectedStateChanged();
         }
      });
   }

   private void selectedStateChanged() {
      this.transition.stop();
      if (((ToggleSwitch)this.getSkinnable()).isSelected()) {
         this.transition.setRate(1.0);
         this.transition.jumpTo(Duration.ZERO);
      } else {
         this.transition.setRate(-1.0);
         this.transition.jumpTo(this.transition.getDuration());
      }

      this.transition.play();
   }

   private void toggle(ToggleSwitch toggleSwitch) {
      toggleSwitch.setSelected(!toggleSwitch.isSelected());
   }

   private DoubleProperty thumbMoveAnimationTimeProperty() {
      if (this.thumbMoveAnimationTime == null) {
         this.thumbMoveAnimationTime = new StyleableDoubleProperty(200.0) {
            protected void invalidated() {
               double newValue = this.get();
               if (newValue <= 0.0) {
                  newValue = 1.0;
               }

               ToggleSwitchSkin.this.transition.setDuration(Duration.millis(newValue));
            }

            public Object getBean() {
               return ToggleSwitchSkin.this;
            }

            public String getName() {
               return "thumbMoveAnimationTime";
            }

            public CssMetaData<ToggleSwitch, Number> getCssMetaData() {
               return ToggleSwitchSkin.THUMB_MOVE_ANIMATION_TIME;
            }
         };
      }

      return this.thumbMoveAnimationTime;
   }

   private double getThumbMoveAnimationTime() {
      return this.thumbMoveAnimationTime == null ? 200.0 : this.thumbMoveAnimationTime.get();
   }

   protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
      ToggleSwitch toggleSwitch = (ToggleSwitch)this.getSkinnable();
      double thumbWidth = this.snapSizeX(this.thumb.prefWidth(-1.0));
      double thumbHeight = this.snapSizeY(this.thumb.prefHeight(-1.0));
      this.thumb.resize(thumbWidth, thumbHeight);
      double thumbAreaWidth = this.snapSizeX(this.thumbArea.prefWidth(-1.0));
      double thumbAreaHeight = this.snapSizeY(this.thumbArea.prefHeight(-1.0));
      double thumbAreaY = this.snapPositionY(contentY + contentHeight / 2.0 - thumbAreaHeight / 2.0);
      this.thumbArea.resize(thumbAreaWidth, thumbAreaHeight);
      this.thumbArea.setLayoutX(contentWidth - thumbAreaWidth);
      this.thumbArea.setLayoutY(thumbAreaY);
      this.labelContainer.resize(contentWidth - thumbAreaWidth, thumbAreaHeight);
      this.labelContainer.setLayoutY(thumbAreaY);
      this.thumb.setLayoutX(this.thumbArea.getLayoutX());
      this.thumb.setLayoutY(thumbAreaY + (thumbAreaHeight - thumbHeight) / 2.0);
      double thumbTarget = thumbAreaWidth - thumbWidth;
      this.transition.setToX(thumbTarget);
      if (this.transition.getStatus() == Status.RUNNING) {
         Duration currentTime = this.transition.getCurrentTime();
         this.transition.stop();
         this.transition.playFrom(currentTime);
      } else {
         this.thumb.setTranslateX(toggleSwitch.isSelected() ? thumbTarget : 0.0);
      }
   }

   protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return leftInset + this.label.prefWidth(-1.0) + this.thumbArea.prefWidth(-1.0) + rightInset;
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return topInset + Math.max(this.thumb.prefHeight(-1.0), this.label.prefHeight(-1.0)) + bottomInset;
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return leftInset + this.label.prefWidth(-1.0) + 20.0 + this.thumbArea.prefWidth(-1.0) + rightInset;
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return topInset + Math.max(this.thumb.prefHeight(-1.0), this.label.prefHeight(-1.0)) + bottomInset;
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((ToggleSwitch)this.getSkinnable()).prefWidth(height);
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((ToggleSwitch)this.getSkinnable()).prefHeight(width);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return STYLEABLES;
   }

   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   static {
      List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(SkinBase.getClassCssMetaData());
      styleables.add(THUMB_MOVE_ANIMATION_TIME);
      STYLEABLES = Collections.unmodifiableList(styleables);
   }
}
