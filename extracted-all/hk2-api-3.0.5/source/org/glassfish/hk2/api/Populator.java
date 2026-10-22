package org.glassfish.hk2.api;

import java.io.IOException;
import java.util.List;

public interface Populator {
   List<ActiveDescriptor<?>> populate(DescriptorFileFinder var1, PopulatorPostProcessor... var2) throws IOException, MultiException;

   List<ActiveDescriptor<?>> populate() throws IOException, MultiException;
}
