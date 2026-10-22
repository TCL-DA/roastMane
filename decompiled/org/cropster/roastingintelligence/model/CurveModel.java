package org.cropster.roastingintelligence.model;

import java.util.Date;
import java.util.SortedMap;
import java.util.SortedSet;
import org.cropster.roastingintelligence.listener.CurveModelListener;

public interface CurveModel extends CurveData {
   CurveModel getRootModel();

   <T extends CurveModel> T getModel(Class<T> var1);

   String getName();

   Date getZeroPoint();

   Double getValue(long var1);

   SortedMap<Long, Double> tailCurve(Long var1);

   void addListener(CurveModelListener var1);

   void removeListener(CurveModelListener var1);

   void fireCurveChanged();

   void fireCurveChanged(long var1);

   void fireCurveChanged(SortedSet<Long> var1);
}
