package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "lower",
   category = "Lookup"
)
public class LowerLookup extends AbstractLookup {
   @Override
   public String lookup(final LogEvent ignored, final String key) {
      return key != null ? Strings.toRootLowerCase(key) : null;
   }
}
