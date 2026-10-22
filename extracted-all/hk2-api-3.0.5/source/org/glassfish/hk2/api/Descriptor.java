package org.glassfish.hk2.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Descriptor {
   String getImplementation();

   Set<String> getAdvertisedContracts();

   String getScope();

   String getName();

   Set<String> getQualifiers();

   DescriptorType getDescriptorType();

   DescriptorVisibility getDescriptorVisibility();

   Map<String, List<String>> getMetadata();

   HK2Loader getLoader();

   int getRanking();

   int setRanking(int var1);

   Boolean isProxiable();

   Boolean isProxyForSameScope();

   String getClassAnalysisName();

   Long getServiceId();

   Long getLocatorId();
}
