package org.jvnet.hk2.internal;

import java.io.Serializable;
import java.util.Comparator;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ServiceHandle;

public class ServiceHandleComparator implements Comparator<ServiceHandle<?>>, Serializable {
   private static final long serialVersionUID = -3475592779302344427L;
   private final DescriptorComparator baseComparator = new DescriptorComparator();

   public int compare(ServiceHandle<?> o1, ServiceHandle<?> o2) {
      return this.baseComparator.compare((Descriptor)o1.getActiveDescriptor(), (Descriptor)o2.getActiveDescriptor());
   }
}
