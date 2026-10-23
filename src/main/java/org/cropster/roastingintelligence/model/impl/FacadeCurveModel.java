package org.cropster.roastingintelligence.model.impl;

import org.cropster.roastingintelligence.model.CurveModel;

public abstract class FacadeCurveModel extends AbstractCurveModel {
   protected final CurveModel _source;

   public FacadeCurveModel(CurveModel source) {
      if (source.getModel(this.getClass()) != null) {
         throw new IllegalStateException("Duplicate model in hierachy");
      } else {
         this._source = source;
      }
   }

   @Override
   public <T extends CurveModel> T getModel(Class<T> type) {
      return (T)(type.isInstance(this) ? this : this._source.getModel(type));
   }
}
