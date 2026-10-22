package org.cropster.roastingintelligence.model;

import com.cropster.util.model.Unit;
import java.util.SortedMap;

public interface CurveData {
   SortedMap<Long, Double> getValues();

   Unit getUnit();
}
