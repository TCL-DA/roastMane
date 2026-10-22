package com.cropster.roastingintelligence.task.control;

import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;

public abstract class Command<T> implements Runnable {
   protected final MeasurementReaderWriter source;
   protected final String field;
   protected final T value;

   public Command(MeasurementReaderWriter source, String field, T value) {
      this.source = source;
      this.field = field;
      this.value = value;
   }
}
