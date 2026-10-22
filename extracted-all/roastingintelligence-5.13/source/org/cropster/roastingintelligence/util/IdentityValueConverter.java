package org.cropster.roastingintelligence.util;

public class IdentityValueConverter implements ValueConverter {
   @Override
   public double convertValueToPercent(double value) {
      return value;
   }

   @Override
   public double convertPercentToValue(double percent) {
      return percent;
   }
}
