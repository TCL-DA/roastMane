package org.glassfish.hk2.api.messaging;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface TopicDistributionService {
   String HK2_DEFAULT_TOPIC_DISTRIBUTOR = "HK2TopicDistributionService";

   void distributeMessage(Topic<?> var1, Object var2);
}
