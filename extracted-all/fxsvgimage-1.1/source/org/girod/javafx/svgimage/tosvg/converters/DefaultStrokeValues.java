package org.girod.javafx.svgimage.tosvg.converters;

import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public interface DefaultStrokeValues {
   double DEFAULT_STROKE_MITER_LIMIT = 10.0;
   StrokeLineCap DEFAULT_STROKE_LINE_CAP = StrokeLineCap.SQUARE;
   StrokeLineJoin DEFAULT_STROKE_LINE_JOIN = StrokeLineJoin.MITER;
   double DEFAULT_STROKE_DASH_OFFSET = 0.0;
   double DEFAULT_STROKE_WIDTH = 1.0;
}
