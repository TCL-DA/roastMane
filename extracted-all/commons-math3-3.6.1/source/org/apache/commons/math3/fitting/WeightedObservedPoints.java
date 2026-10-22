package org.apache.commons.math3.fitting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WeightedObservedPoints implements Serializable {
   private static final long serialVersionUID = 20130813L;
   private final List<WeightedObservedPoint> observations = new ArrayList<>();

   public void add(double x, double y) {
      this.add(1.0, x, y);
   }

   public void add(double weight, double x, double y) {
      this.observations.add(new WeightedObservedPoint(weight, x, y));
   }

   public void add(WeightedObservedPoint observed) {
      this.observations.add(observed);
   }

   public List<WeightedObservedPoint> toList() {
      return new ArrayList<>(this.observations);
   }

   public void clear() {
      this.observations.clear();
   }
}
