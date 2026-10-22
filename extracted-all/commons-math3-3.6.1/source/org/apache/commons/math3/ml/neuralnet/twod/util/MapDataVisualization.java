package org.apache.commons.math3.ml.neuralnet.twod.util;

import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;

public interface MapDataVisualization {
   double[][] computeImage(NeuronSquareMesh2D var1, Iterable<double[]> var2);
}
