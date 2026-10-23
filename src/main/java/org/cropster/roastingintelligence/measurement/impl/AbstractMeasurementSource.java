package org.cropster.roastingintelligence.measurement.impl;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import org.cropster.roastingintelligence.measurement.MeasurementListener;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;

public abstract class AbstractMeasurementSource implements MeasurementSource {
   protected static final int READ_TIMEOUT = 300;
   private final MeasurementSourceFactory _factory;
   private final Collection<MeasurementListener> _listeners = new CopyOnWriteArraySet<>();

   public AbstractMeasurementSource(MeasurementSourceFactory factory) {
      this._factory = factory;
   }

   @Override
   public MeasurementSourceFactory getFactory() {
      return this._factory;
   }

   @Override
   public void addMeasurementListener(MeasurementListener listener) {
      this._listeners.add(listener);
   }

   @Override
   public void removeMeasurementListener(MeasurementListener listener) {
      this._listeners.remove(listener);
   }

   protected void fireStatusChanged() {
      for (MeasurementListener listener : this._listeners) {
         listener.statusChanged(this);
      }
   }

   @Override
   public boolean isValid(String measureName, Double value) {
      return value > -500.0 && value < 3000.0;
   }
}
