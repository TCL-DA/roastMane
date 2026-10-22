package org.ocpsoft.prettytime;

public interface Duration {
   long getQuantity();

   long getQuantityRounded(int var1);

   TimeUnit getUnit();

   long getDelta();

   boolean isInPast();

   boolean isInFuture();
}
