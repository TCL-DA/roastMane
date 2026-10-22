package org.glassfish.hk2.api;

public interface TwoPhaseResource {
   void prepareDynamicConfiguration(TwoPhaseTransactionData var1) throws MultiException;

   void activateDynamicConfiguration(TwoPhaseTransactionData var1);

   void rollbackDynamicConfiguration(TwoPhaseTransactionData var1);
}
