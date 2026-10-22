package com.cropster.roastingintelligence.task.control;

import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;

public class WriteCommand extends Command<Double> {
   public WriteCommand(MeasurementReaderWriter writableSource, String field, Double value) {
      super(writableSource, field, value);
   }

   @Override
   public void run() {
      this.source.setValue(this.field, this.value);
   }
}
