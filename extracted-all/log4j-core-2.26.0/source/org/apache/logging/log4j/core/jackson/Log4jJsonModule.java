package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;

class Log4jJsonModule extends SimpleModule {
   private static final long serialVersionUID = 1L;
   private final boolean encodeThreadContextAsList;
   private final boolean includeStacktrace;
   private final boolean stacktraceAsString;

   Log4jJsonModule(
      final boolean encodeThreadContextAsList, final boolean includeStacktrace, final boolean stacktraceAsString, final boolean objectMessageAsJsonObject
   ) {
      super(Log4jJsonModule.class.getName(), new Version(2, 0, 0, null, null, null));
      this.encodeThreadContextAsList = encodeThreadContextAsList;
      this.includeStacktrace = includeStacktrace;
      this.stacktraceAsString = stacktraceAsString;
      new Initializers.SimpleModuleInitializer().initialize(this, objectMessageAsJsonObject);
   }

   public void setupModule(final SetupContext context) {
      super.setupModule(context);
      if (this.encodeThreadContextAsList) {
         new Initializers.SetupContextAsEntryListInitializer().setupModule(context, this.includeStacktrace, this.stacktraceAsString);
      } else {
         new Initializers.SetupContextInitializer().setupModule(context, this.includeStacktrace, this.stacktraceAsString);
      }
   }
}
