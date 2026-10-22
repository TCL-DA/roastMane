package org.jvnet.hk2.internal;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class OptionalActiveDescriptor<T> extends AbstractActiveDescriptor<Optional<T>> {
   private Injectee injectee;
   private ServiceLocatorImpl locator;

   public OptionalActiveDescriptor() {
   }

   OptionalActiveDescriptor(Injectee injectee, ServiceLocatorImpl locator) {
      super(
         new HashSet(),
         PerLookup.class,
         ReflectionHelper.getNameFromAllQualifiers(injectee.getRequiredQualifiers(), injectee.getParent()),
         injectee.getRequiredQualifiers(),
         DescriptorType.CLASS,
         DescriptorVisibility.NORMAL,
         0,
         null,
         null,
         locator.getPerLocatorUtilities().getAutoAnalyzerName(injectee.getInjecteeClass()),
         null
      );
      this.injectee = injectee;
      this.locator = locator;
   }

   public Class<?> getImplementationClass() {
      return Optional.class;
   }

   public Type getImplementationType() {
      return Optional.class;
   }

   public Optional<T> create(ServiceHandle<?> root) {
      Injectee unwrapped = new SystemInjecteeImpl(
         ReflectionHelper.getFirstTypeArgument(this.injectee.getRequiredType()),
         this.injectee.getRequiredQualifiers(),
         this.injectee.getPosition(),
         this.injectee.getParent(),
         true,
         this.injectee.isSelf(),
         this.injectee.getUnqualified(),
         this
      );
      return Optional.ofNullable(this.locator.getInjecteeDescriptor(unwrapped)).flatMap(d -> Optional.ofNullable((T)d.create(root)));
   }
}
