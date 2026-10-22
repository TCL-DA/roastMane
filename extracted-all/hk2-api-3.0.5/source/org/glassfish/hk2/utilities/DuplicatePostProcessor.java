package org.glassfish.hk2.utilities;

import java.util.HashSet;
import java.util.Set;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;

@PerLookup
public class DuplicatePostProcessor implements PopulatorPostProcessor {
   private final DuplicatePostProcessorMode mode;
   private final HashSet<DescriptorImpl> strictDupSet = new HashSet<>();
   private final HashSet<DuplicatePostProcessor.ImplOnlyKey> implOnlyDupSet = new HashSet<>();

   public DuplicatePostProcessor() {
      this(DuplicatePostProcessorMode.STRICT);
   }

   public DuplicatePostProcessor(DuplicatePostProcessorMode mode) {
      this.mode = mode;
   }

   public DuplicatePostProcessorMode getMode() {
      return this.mode;
   }

   @Override
   public DescriptorImpl process(ServiceLocator serviceLocator, DescriptorImpl descriptorImpl) {
      switch (this.mode) {
         case STRICT:
            return this.strict(serviceLocator, descriptorImpl);
         case IMPLEMENTATION_ONLY:
            return this.implementationOnly(serviceLocator, descriptorImpl);
         default:
            throw new AssertionError("UnkownMode: " + this.mode);
      }
   }

   private DescriptorImpl implementationOnly(ServiceLocator serviceLocator, final DescriptorImpl descriptorImpl) {
      final String impl = descriptorImpl.getImplementation();
      if (impl == null) {
         return descriptorImpl;
      } else {
         DuplicatePostProcessor.ImplOnlyKey key = new DuplicatePostProcessor.ImplOnlyKey(descriptorImpl);
         if (this.implOnlyDupSet.contains(key)) {
            return null;
         } else {
            this.implOnlyDupSet.add(key);
            return serviceLocator.getBestDescriptor(new Filter() {
               @Override
               public boolean matches(Descriptor d) {
                  return d.getImplementation().equals(impl) && d.getDescriptorType().equals(descriptorImpl.getDescriptorType());
               }
            }) != null ? null : descriptorImpl;
         }
      }
   }

   private DescriptorImpl strict(ServiceLocator serviceLocator, DescriptorImpl descriptorImpl) {
      if (this.strictDupSet.contains(descriptorImpl)) {
         return null;
      } else {
         this.strictDupSet.add(descriptorImpl);
         Set<String> contracts = descriptorImpl.getAdvertisedContracts();
         final String contract = null;

         for (String candidate : contracts) {
            if (candidate.equals(descriptorImpl.getImplementation())) {
               contract = candidate;
               break;
            }

            contract = candidate;
         }

         final String fName = descriptorImpl.getName();
         final DescriptorImpl fDescriptorImpl = descriptorImpl;
         return serviceLocator.getBestDescriptor(new IndexedFilter() {
            @Override
            public boolean matches(Descriptor d) {
               return fDescriptorImpl.equals(d);
            }

            @Override
            public String getAdvertisedContract() {
               return contract;
            }

            @Override
            public String getName() {
               return fName;
            }
         }) != null ? null : descriptorImpl;
      }
   }

   @Override
   public String toString() {
      return "DuplicateCodeProcessor(" + this.mode + "," + System.identityHashCode(this) + ")";
   }

   private static final class ImplOnlyKey {
      private final String impl;
      private final DescriptorType type;
      private final int hash;

      private ImplOnlyKey(Descriptor desc) {
         this.impl = desc.getImplementation();
         this.type = desc.getDescriptorType();
         this.hash = this.impl.hashCode() ^ this.type.hashCode();
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object o) {
         if (o == null) {
            return false;
         } else if (!(o instanceof DuplicatePostProcessor.ImplOnlyKey)) {
            return false;
         } else {
            DuplicatePostProcessor.ImplOnlyKey other = (DuplicatePostProcessor.ImplOnlyKey)o;
            return other.impl.equals(this.impl) && other.type.equals(this.type);
         }
      }

      @Override
      public String toString() {
         return "ImplOnlyKey(" + this.impl + "," + this.type + "," + System.identityHashCode(this) + ")";
      }
   }
}
