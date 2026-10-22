package org.apache.logging.log4j.core.script;

import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public abstract class AbstractScript {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected static final String DEFAULT_LANGUAGE = "JavaScript";
   private final String language;
   private final String scriptText;
   private final String name;
   private final String id;

   public AbstractScript(final String name, final String language, final String scriptText) {
      this.language = language;
      this.scriptText = scriptText;
      this.name = name;
      this.id = Strings.isBlank(name) ? Integer.toHexString(Objects.hashCode(this)) : name;
   }

   public String getLanguage() {
      return this.language;
   }

   public String getScriptText() {
      return this.scriptText;
   }

   public String getName() {
      return this.name;
   }

   public String getId() {
      return this.id;
   }
}
