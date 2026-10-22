package org.apache.commons.math3.filter;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public interface ProcessModel {
   RealMatrix getStateTransitionMatrix();

   RealMatrix getControlMatrix();

   RealMatrix getProcessNoise();

   RealVector getInitialStateEstimate();

   RealMatrix getInitialErrorCovariance();
}
