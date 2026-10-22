package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "upper",
   category = "Lookup"
)
public class UpperLookup implements StrLookup {
   @Override
   public String lookup(final String key) {
      return key != null ? Strings.toRootUpperCase(key) : null;
   }

   @Override
   public String lookup(final LogEvent event, final String key) {
      return this.lookup(key);
   }
}
