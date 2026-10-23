package org.cropster.roastingintelligence.model.impl;

import com.cropster.util.model.Unit;
import java.util.Date;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;

public class StandardCurveModel extends AbstractCurveModel {
   private final String _name;
   private final Date _startDate;
   private Curve _curve;

   public StandardCurveModel(String name, Date startDate, Unit unit) {
      if (name == null) {
         throw new IllegalArgumentException("name must not be null");
      } else {
         this._name = name;
         this._startDate = startDate;
         this._curve = new Curve(unit);
      }
   }

   public StandardCurveModel(String name, Unit unit) {
      this(name, new Date(0L), unit);
   }

   @Override
   public CurveModel getRootModel() {
      return this;
   }

   @Override
   public <T extends CurveModel> T getModel(Class<T> type) {
      return (T)(type.isInstance(this) ? this : null);
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public Date getZeroPoint() {
      return this._startDate;
   }

   @Override
   public Unit getUnit() {
      return this._curve.getUnit();
   }

   @Override
   public Double getValue(long time) {
      return this._curve.get(Long.valueOf(time));
   }

   public Curve getValues() {
      return this._curve;
   }

   public void setValues(Curve curve) {
      this._curve = curve;
   }
}
