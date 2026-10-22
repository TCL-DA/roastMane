package com.sun.marlin;

import com.sun.openpisces.AlphaConsumer;

public interface MarlinAlphaConsumer extends AlphaConsumer {
   boolean supportBlockFlags();

   void clearAlphas(int var1);

   void setAndClearRelativeAlphas(int[] var1, int[] var2, int var3, int var4, int var5);
}
