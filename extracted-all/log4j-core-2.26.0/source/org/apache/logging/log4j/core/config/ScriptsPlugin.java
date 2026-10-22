package org.apache.logging.log4j.core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Plugin(
   name = "Scripts",
   category = "Core"
)
public final class ScriptsPlugin {
   private ScriptsPlugin() {
   }

   @PluginFactory
   public static AbstractScript[] createScripts(@PluginElement("Scripts") final AbstractScript[] scripts) {
      Objects.requireNonNull(scripts, "Scripts array cannot be null");
      if (scripts.length == 0) {
         return scripts;
      } else {
         List<AbstractScript> validScripts = new ArrayList<>(scripts.length);

         for (AbstractScript script : scripts) {
            if (Strings.isBlank(script.getName())) {
               throw new ConfigurationException("A script defined in <Scripts> lacks an explicit 'name' attribute");
            }

            validScripts.add(script);
         }

         return validScripts.toArray(new AbstractScript[0]);
      }
   }
}
