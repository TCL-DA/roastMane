package org.apache.logging.log4j.core.util;

import java.util.Map;
import org.apache.logging.log4j.core.impl.JdkMapAdapterStringMap;
import org.apache.logging.log4j.util.StringMap;

public interface ContextDataProvider {
   Map<String, String> supplyContextData();

   default StringMap supplyStringMap() {
      return new JdkMapAdapterStringMap(this.supplyContextData(), true);
   }

   default Object getValue(final String key) {
      return this.supplyContextData().get(key);
   }
}
