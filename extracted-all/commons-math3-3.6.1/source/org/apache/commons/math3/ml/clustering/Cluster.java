package org.apache.commons.math3.ml.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cluster<T extends Clusterable> implements Serializable {
   private static final long serialVersionUID = -3442297081515880464L;
   private final List<T> points = new ArrayList<>();

   public void addPoint(T point) {
      this.points.add(point);
   }

   public List<T> getPoints() {
      return this.points;
   }
}
