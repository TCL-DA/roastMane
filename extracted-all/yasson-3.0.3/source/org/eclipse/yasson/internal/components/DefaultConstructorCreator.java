package org.eclipse.yasson.internal.components;

import java.io.IOException;
import org.eclipse.yasson.internal.InstanceCreator;
import org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

public class DefaultConstructorCreator implements JsonbComponentInstanceCreator {
   @Override
   public <T> T getOrCreateComponent(Class<T> componentClass) {
      return InstanceCreator.createInstance(componentClass);
   }

   @Override
   public void close() throws IOException {
   }
}
