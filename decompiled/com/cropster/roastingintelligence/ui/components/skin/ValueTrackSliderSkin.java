package com.cropster.roastingintelligence.ui.components.skin;

import java.text.MessageFormat;
import java.util.Locale;
import javafx.scene.control.Slider;
import javafx.scene.control.skin.SliderSkin;

public class ValueTrackSliderSkin extends SliderSkin {
   private static final MessageFormat STYLE_FORMAT = new MessageFormat(
      "-value-slider-track-color: linear-gradient(to right, -default-slider-track-color 0%, -default-slider-track-color {0,number,#0}%, -default-slider-background-color {0,number,#0}%, -default-slider-background-color 100%);",
      Locale.US
   );

   public ValueTrackSliderSkin(Slider slider) {
      super(slider);
      slider.valueProperty().addListener((s, o, n) -> {
         double percentage = 100.0 * (n.doubleValue() - slider.getMin()) / (slider.getMax() - slider.getMin());
         synchronized (STYLE_FORMAT) {
            slider.setStyle(STYLE_FORMAT.format(new Object[]{percentage}));
         }
      });
   }
}
