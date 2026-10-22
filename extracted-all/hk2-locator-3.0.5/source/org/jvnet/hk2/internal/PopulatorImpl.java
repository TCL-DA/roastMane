package org.jvnet.hk2.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorFileFinder;
import org.glassfish.hk2.api.DescriptorFileFinderInformation;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.DescriptorImpl;

public class PopulatorImpl implements Populator {
   private final ServiceLocator serviceLocator;
   private final DynamicConfigurationService dcs;

   PopulatorImpl(ServiceLocator serviceLocator, DynamicConfigurationService dcs) {
      this.serviceLocator = serviceLocator;
      this.dcs = dcs;
   }

   public List<ActiveDescriptor<?>> populate(DescriptorFileFinder fileFinder, PopulatorPostProcessor... postProcessors) throws IOException {
      List<ActiveDescriptor<?>> descriptors = new LinkedList<>();
      if (fileFinder == null) {
         fileFinder = (DescriptorFileFinder)this.serviceLocator.getService(DescriptorFileFinder.class, new Annotation[0]);
         if (fileFinder == null) {
            return descriptors;
         }
      }

      if (postProcessors == null) {
         postProcessors = new PopulatorPostProcessor[0];
      }

      List<String> descriptorInformation = null;

      List<InputStream> descriptorFileInputStreams;
      try {
         descriptorFileInputStreams = fileFinder.findDescriptorFiles();
         if (fileFinder instanceof DescriptorFileFinderInformation) {
            DescriptorFileFinderInformation dffi = (DescriptorFileFinderInformation)fileFinder;
            descriptorInformation = dffi.getDescriptorFileInformation();
            if (descriptorInformation != null && descriptorInformation.size() != descriptorFileInputStreams.size()) {
               throw new IOException(
                  "The DescriptorFileFinder implementation "
                     + fileFinder.getClass().getName()
                     + " also implements DescriptorFileFinderInformation, however the cardinality of the list returned from getDescriptorFileInformation ("
                     + descriptorInformation.size()
                     + ") does not equal the cardinality of the list returned from findDescriptorFiles ("
                     + descriptorFileInputStreams.size()
                     + ")"
               );
            }
         }
      } catch (IOException var24) {
         throw var24;
      } catch (Throwable var25) {
         throw new MultiException(var25);
      }

      Collector collector = new Collector();
      DynamicConfiguration config = this.dcs.createDynamicConfiguration();
      int lcv = 0;

      for (InputStream is : descriptorFileInputStreams) {
         String identifier = descriptorInformation == null ? null : descriptorInformation.get(lcv);
         lcv++;

         try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            boolean readOne = false;

            while (true) {
               DescriptorImpl descriptorImpl = new DescriptorImpl();

               try {
                  readOne = descriptorImpl.readObject(br);
               } catch (IOException var22) {
                  if (identifier != null) {
                     collector.addThrowable(new IOException("InputStream with identifier \"" + identifier + "\" failed", var22));
                  } else {
                     collector.addThrowable(var22);
                  }
               }

               if (readOne) {
                  for (PopulatorPostProcessor pp : postProcessors) {
                     try {
                        descriptorImpl = pp.process(this.serviceLocator, descriptorImpl);
                     } catch (Throwable var21) {
                        if (identifier != null) {
                           collector.addThrowable(new IOException("InputStream with identifier \"" + identifier + "\" failed", var21));
                        } else {
                           collector.addThrowable(var21);
                        }

                        descriptorImpl = null;
                     }

                     if (descriptorImpl == null) {
                        break;
                     }
                  }

                  if (descriptorImpl != null) {
                     descriptors.add(config.bind(descriptorImpl, false));
                  }
               }

               if (!readOne) {
                  break;
               }
            }
         }
      }

      collector.throwIfErrors();
      config.commit();
      return descriptors;
   }

   public List<ActiveDescriptor<?>> populate() throws IOException {
      return this.populate(new ClasspathDescriptorFileFinder());
   }
}
