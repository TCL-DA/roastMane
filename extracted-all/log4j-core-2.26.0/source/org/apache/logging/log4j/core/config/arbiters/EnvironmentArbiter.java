package org.apache.logging.log4j.core.config.arbiters;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(
   name = "EnvironmentArbiter",
   category = "Core",
   elementType = "Arbiter",
   deferChildren = true,
   printObject = true
)
public class EnvironmentArbiter implements Arbiter {
   private final String propertyName;
   private final String propertyValue;

   private EnvironmentArbiter(final String propertyName, final String propertyValue) {
      this.propertyName = propertyName;
      this.propertyValue = propertyValue;
   }

   @Override
   public boolean isCondition() {
      String value = System.getenv(this.propertyName);
      return value != null && (this.propertyValue == null || value.equals(this.propertyValue));
   }

   @PluginBuilderFactory
   public static EnvironmentArbiter.Builder newBuilder() {
      return new EnvironmentArbiter.Builder();
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<EnvironmentArbiter> {
      public static final String ATTR_PROPERTY_NAME = "propertyName";
      public static final String ATTR_PROPERTY_VALUE = "propertyValue";
      @PluginBuilderAttribute("propertyName")
      private String propertyName;
      @PluginBuilderAttribute("propertyValue")
      private String propertyValue;

      public EnvironmentArbiter.Builder setPropertyName(final String propertyName) {
         this.propertyName = propertyName;
         return this.asBuilder();
      }

      public EnvironmentArbiter.Builder setPropertyValue(final String propertyValue) {
         this.propertyValue = propertyValue;
         return this.asBuilder();
      }

      public EnvironmentArbiter.Builder asBuilder() {
         return this;
      }

      public EnvironmentArbiter build() {
         return new EnvironmentArbiter(this.propertyName, this.propertyValue);
      }
   }
}
