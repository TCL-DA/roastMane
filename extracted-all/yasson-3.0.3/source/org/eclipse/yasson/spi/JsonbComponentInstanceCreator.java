package org.eclipse.yasson.spi;

import java.io.Closeable;

public interface JsonbComponentInstanceCreator extends Closeable {
   int DEFAULT_PRIORITY = 0;

   <T> T getOrCreateComponent(Class<T> var1);

   default int getPriority() {
      return 0;
   }
}
