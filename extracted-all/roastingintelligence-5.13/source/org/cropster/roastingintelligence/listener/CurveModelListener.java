package org.cropster.roastingintelligence.listener;

import java.util.SortedSet;
import org.cropster.roastingintelligence.model.CurveModel;

public interface CurveModelListener {
   void notifyCurveChanged(CurveModel var1, long var2);

   void notifyCurveChanged(CurveModel var1);

   default void notifyCurveChanged(CurveModel source, SortedSet<Long> times) {
      times.forEach(time -> this.notifyCurveChanged(source, time));
   }
}
