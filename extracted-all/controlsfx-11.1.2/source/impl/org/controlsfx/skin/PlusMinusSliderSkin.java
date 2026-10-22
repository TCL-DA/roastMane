package impl.org.controlsfx.skin;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.controlsfx.control.PlusMinusSlider;

public class PlusMinusSliderSkin extends SkinBase<PlusMinusSlider> {
   private PlusMinusSliderSkin.SliderReader reader;
   private Slider slider;
   private Region plusRegion;
   private Region minusRegion;
   private BorderPane borderPane;

   public PlusMinusSliderSkin(PlusMinusSlider adjuster) {
      super(adjuster);
      adjuster.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
         public void handle(KeyEvent event) {
            event.consume();
         }
      });
      this.slider = new Slider(-1.0, 1.0, 0.0);
      this.slider.valueProperty().addListener(new ChangeListener<Number>() {
         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            ((PlusMinusSlider)PlusMinusSliderSkin.this.getSkinnable()).getProperties().put("plusminusslidervalue", newValue.doubleValue());
         }
      });
      this.slider.orientationProperty().bind(adjuster.orientationProperty());
      this.slider.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         public void handle(MouseEvent evt) {
            PlusMinusSliderSkin.this.reader = PlusMinusSliderSkin.this.new SliderReader();
            PlusMinusSliderSkin.this.reader.start();
         }
      });
      this.slider.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
         public void handle(MouseEvent evt) {
            if (PlusMinusSliderSkin.this.reader != null) {
               PlusMinusSliderSkin.this.reader.stop();
            }

            KeyValue keyValue = new KeyValue(PlusMinusSliderSkin.this.slider.valueProperty(), 0);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(100.0), new KeyValue[]{keyValue});
            Timeline timeline = new Timeline(new KeyFrame[]{keyFrame});
            timeline.play();
         }
      });
      this.plusRegion = new Region();
      this.plusRegion.getStyleClass().add("adjust-plus");
      this.minusRegion = new Region();
      this.minusRegion.getStyleClass().add("adjust-minus");
      this.borderPane = new BorderPane();
      this.updateLayout(adjuster.getOrientation());
      this.getChildren().add(this.borderPane);
      adjuster.orientationProperty().addListener((observable, oldValue, newValue) -> this.updateLayout(newValue));
   }

   private void updateLayout(Orientation orientation) {
      this.borderPane.getChildren().clear();
      switch (orientation) {
         case HORIZONTAL:
            this.borderPane.setLeft(this.minusRegion);
            this.borderPane.setCenter(this.slider);
            this.borderPane.setRight(this.plusRegion);
            break;
         case VERTICAL:
            this.borderPane.setTop(this.plusRegion);
            this.borderPane.setCenter(this.slider);
            this.borderPane.setBottom(this.minusRegion);
      }
   }

   class SliderReader extends AnimationTimer {
      private long lastTime = System.currentTimeMillis();

      public void handle(long now) {
         if (now - this.lastTime > 10000000L) {
            this.lastTime = now;
            PlusMinusSliderSkin.this.slider
               .fireEvent(
                  new PlusMinusSlider.PlusMinusEvent(
                     PlusMinusSliderSkin.this.slider,
                     PlusMinusSliderSkin.this.slider,
                     PlusMinusSlider.PlusMinusEvent.VALUE_CHANGED,
                     PlusMinusSliderSkin.this.slider.getValue()
                  )
               );
         }
      }
   }
}
