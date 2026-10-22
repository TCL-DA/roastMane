package org.cropster.roastingintelligence.listener;

import org.cropster.roastingintelligence.RoastingModel;

public interface RoastingListener {
   void roastingStarted(RoastingModel var1);

   void roastingForced(RoastingModel var1);

   void roastingAborted(RoastingModel var1);

   void roastingStopped(RoastingModel var1);

   void roastingFinalized(RoastingModel var1);
}
