package org.apache.commons.math3.filter;

import org.apache.commons.math3.linear.RealMatrix;

public interface MeasurementModel {
   RealMatrix getMeasurementMatrix();

   RealMatrix getMeasurementNoise();
}
