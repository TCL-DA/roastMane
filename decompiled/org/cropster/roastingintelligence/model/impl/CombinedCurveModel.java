package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;

public class CombinedCurveModel extends AbstractCurveModel implements CurveModelListener {
   private final CurveModel[] _sources;
   private volatile TreeMap<Long, Double> _values = new TreeMap<>();

   public CombinedCurveModel(CurveModel... sources) {
      if (sources != null && sources.length > 1) {
         this._sources = sources;

         for (CurveModel source : this._sources) {
            this.notifyCurveChanged(source);
         }
      } else {
         throw new IllegalArgumentException("Specify at least two models");
      }
   }

   @Override
   public CurveModel getRootModel() {
      return this._sources[0].getRootModel();
   }

   @Override
   public <T extends CurveModel> T getModel(Class<T> type) {
      if (type.isInstance(this)) {
         return (T)this;
      } else {
         for (CurveModel source : this._sources) {
            T result = source.getModel(type);
            if (result != null) {
               return result;
            }
         }

         return null;
      }
   }

   @Override
   public String getName() {
      String name = "";

      for (CurveModel source : this._sources) {
         if (!name.isEmpty()) {
            name = name + "/";
         }

         name = name + source.getName();
      }

      return String.format("[%s]", name);
   }

   @Override
   public Date getZeroPoint() {
      return this._sources[0].getZeroPoint();
   }

   @Override
   public Unit getUnit() {
      return this._sources[0].getUnit();
   }

   @Override
   public Double getValue(long time) {
      return this._values.get(time);
   }

   @Override
   public SortedMap<Long, Double> getValues() {
      return new TreeMap<>(this._values);
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source, long time) {
      for (CurveModel s : this._sources) {
         if (s.getValues().containsKey(Long.valueOf(time))) {
            Double value = s.getValue(time);
            if (value != null) {
               Double oldValue = this._values.put(time, value);
               if (!value.equals(oldValue)) {
                  this.fireCurveChanged(time);
               }

               return;
            }
         }
      }

      if (this._values.remove(time) != null) {
         this.fireCurveChanged(time);
      }
   }

   @Override
   public synchronized void notifyCurveChanged(CurveModel source) {
      TreeMap<Long, Double> values = new TreeMap<>();

      for (int i = this._sources.length - 1; i >= 0; i--) {
         values.putAll(this._sources[i].getValues());
      }

      this._values = values;
      this.fireCurveChanged();
   }
}
