package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;

public class ClassLoaderPostProcessor implements PopulatorPostProcessor {
   private final HK2Loader loader;
   private final boolean force;

   public ClassLoaderPostProcessor(ClassLoader classloader, boolean force) {
      this.loader = new HK2LoaderImpl(classloader);
      this.force = force;
   }

   public ClassLoaderPostProcessor(ClassLoader classloader) {
      this(classloader, false);
   }

   @Override
   public DescriptorImpl process(ServiceLocator serviceLocator, DescriptorImpl descriptorImpl) {
      if (this.force) {
         descriptorImpl.setLoader(this.loader);
         return descriptorImpl;
      } else if (descriptorImpl.getLoader() != null) {
         return descriptorImpl;
      } else {
         descriptorImpl.setLoader(this.loader);
         return descriptorImpl;
      }
   }
}
