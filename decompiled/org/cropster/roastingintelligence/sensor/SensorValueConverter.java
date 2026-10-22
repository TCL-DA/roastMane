package org.cropster.roastingintelligence.sensor;

public interface SensorValueConverter {
   boolean isRatiometric();

   double convertValue(double var1);
}
