package org.cropster.roastingintelligence.ui.chart;

import org.jfree.data.time.MovingAverage;
import org.jfree.data.xy.XYDataset;

@Deprecated
public class MovingAverageTransformer implements DatasetTransformer {
   private final String _name;
   private final int _period;

   public MovingAverageTransformer(String name, int period) {
      this._name = name;
      this._period = period;
   }

   @Override
   public XYDataset transform(XYDataset dataset) {
      return MovingAverage.createMovingAverage(dataset, this._name, (long)this._period, 0L);
   }
}
