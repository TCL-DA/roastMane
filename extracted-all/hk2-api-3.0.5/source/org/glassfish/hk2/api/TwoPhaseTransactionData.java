package org.glassfish.hk2.api;

import java.util.List;

public interface TwoPhaseTransactionData {
   List<ActiveDescriptor<?>> getAllAddedDescriptors();

   List<ActiveDescriptor<?>> getAllRemovedDescriptors();
}
