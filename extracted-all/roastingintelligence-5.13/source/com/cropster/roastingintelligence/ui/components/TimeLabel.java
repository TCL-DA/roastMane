package com.cropster.roastingintelligence.ui.components;

import com.cropster.util.model.Unit;
import com.cropster.util.text.DurationFormat;
import java.math.BigDecimal;
import javafx.scene.control.Label;
import org.cropster.roastingintelligence.model.Measure;

public class TimeLabel extends Label {
   private static final String DEFAULT_LABEL = "--:--";
   private final DurationFormat _durationFormat = DurationFormat.getDurationInstance(true);

   public TimeLabel() {
      super("--:--");
   }

   public TimeLabel(Long time) {
      this.setTime(time);
   }

   public TimeLabel(Measure measure) {
      this.setTime(measure);
   }

   public void setTime(Long time) {
      if (time != null) {
         super.setText(this._durationFormat.format(time));
      } else {
         super.setText("--:--");
      }
   }

   public void setTime(Measure time) {
      this.setTime(Unit.MILLISECOND.from(BigDecimal.valueOf(time.getAmount()), time.getUnit()).longValue());
   }
}
