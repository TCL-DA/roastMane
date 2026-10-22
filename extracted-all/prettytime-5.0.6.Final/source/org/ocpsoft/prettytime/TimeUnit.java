package org.ocpsoft.prettytime;

public interface TimeUnit {
   long getMillisPerUnit();

   long getMaxQuantity();

   boolean isPrecise();
}
