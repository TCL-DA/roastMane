package com.cropster.roastingintelligence.ui.components;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.util.MeasureFormat;

public class MeasureLabel extends Label {
   protected final ObjectProperty<Measure> value = new SimpleObjectProperty();
   protected final String _nullString;
   protected final MeasureFormat _format;

   public MeasureLabel() {
      this("-");
   }

   public MeasureLabel(String nullString) {
      super(nullString);
      this._nullString = nullString;
      this._format = MeasureFormat.getInstance();
      this.textProperty().bind(Bindings.createStringBinding(() -> {
         if (this.value.get() != null) {
            String text = this._format.format(this.value.get());
            if (text.length() > 9) {
               text = text.substring(0, 9) + ".";
            }

            return text;
         } else {
            return this._nullString;
         }
      }, new Observable[]{this.value}));
   }

   public MeasureLabel(Measure measure) {
      this();
      this.setValue(measure);
   }

   public MeasureFormat getFormat() {
      return this._format;
   }

   public ObjectProperty<Measure> valueProperty() {
      return this.value;
   }

   public Measure getValue() {
      return (Measure)this.value.get();
   }

   public void setValue(Measure value) {
      this.value.set(value);
   }
}
