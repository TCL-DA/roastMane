package org.glassfish.hk2.api;

import org.glassfish.hk2.utilities.DescriptorImpl;

public interface PopulatorPostProcessor {
   DescriptorImpl process(ServiceLocator var1, DescriptorImpl var2);
}
