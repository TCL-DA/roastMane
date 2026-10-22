package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.util.Assert;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

@Plugin(
   name = "AsyncWaitStrategyFactory",
   category = "Core",
   printObject = true
)
public class AsyncWaitStrategyFactoryConfig {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private final String factoryClassName;

   public AsyncWaitStrategyFactoryConfig(final String factoryClassName) {
      this.factoryClassName = Assert.requireNonEmpty(factoryClassName, "factoryClassName");
   }

   @PluginBuilderFactory
   public static <B extends AsyncWaitStrategyFactoryConfig.Builder<B>> B newBuilder() {
      return new AsyncWaitStrategyFactoryConfig.Builder<B>().asBuilder();
   }

   public AsyncWaitStrategyFactory createWaitStrategyFactory() {
      try {
         return (AsyncWaitStrategyFactory)LoaderUtil.newCheckedInstanceOf(this.factoryClassName, AsyncWaitStrategyFactory.class);
      } catch (ClassCastException var2) {
         LOGGER.error("Ignoring factory '{}': it is not assignable to AsyncWaitStrategyFactory", this.factoryClassName);
         return null;
      } catch (ReflectiveOperationException var3) {
         LOGGER.info("Invalid implementation class name value: error creating AsyncWaitStrategyFactory {}: {}", this.factoryClassName, var3.getMessage(), var3);
         return null;
      }
   }

   public static class Builder<B extends AsyncWaitStrategyFactoryConfig.Builder<B>>
      implements org.apache.logging.log4j.core.util.Builder<AsyncWaitStrategyFactoryConfig> {
      @PluginBuilderAttribute("class")
      @Required(
         message = "AsyncWaitStrategyFactory cannot be configured without a factory class name"
      )
      private String factoryClassName;

      public String getFactoryClassName() {
         return this.factoryClassName;
      }

      public B setFactoryClassName(final String className) {
         this.factoryClassName = Assert.requireNonEmpty(className, "The 'className' argument must not be null or empty.");
         return this.asBuilder();
      }

      @Deprecated
      public B withFactoryClassName(final String className) {
         this.factoryClassName = Assert.requireNonEmpty(className, "The 'className' argument must not be null or empty.");
         return this.asBuilder();
      }

      public AsyncWaitStrategyFactoryConfig build() {
         return !this.isValid() ? null : new AsyncWaitStrategyFactoryConfig(this.factoryClassName);
      }

      public B asBuilder() {
         return (B)this;
      }
   }
}
