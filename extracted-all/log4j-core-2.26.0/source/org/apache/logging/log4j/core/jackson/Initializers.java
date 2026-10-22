package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import java.util.Collections;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext.ContextStack;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.ExtendedStackTraceElement;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.util.StringMap;

class Initializers {
   private abstract static class AbstractInitializer {
      private AbstractInitializer() {
      }

      void setupModule(final SetupContext context, final boolean includeStacktrace, final boolean stacktraceAsString) {
         context.setMixInAnnotations(StackTraceElement.class, StackTraceElementMixIn.class);
         context.setMixInAnnotations(Marker.class, MarkerMixIn.class);
         context.setMixInAnnotations(Level.class, LevelMixIn.class);
         context.setMixInAnnotations(Instant.class, InstantMixIn.class);
         context.setMixInAnnotations(LogEvent.class, LogEventMixIn.class);
         context.setMixInAnnotations(ExtendedStackTraceElement.class, ExtendedStackTraceElementMixIn.class);
         context.setMixInAnnotations(
            ThrowableProxy.class,
            includeStacktrace
               ? (stacktraceAsString ? ThrowableProxyWithStacktraceAsStringMixIn.class : ThrowableProxyMixIn.class)
               : ThrowableProxyWithoutStacktraceMixIn.class
         );
      }
   }

   static class SetupContextAsEntryListInitializer extends Initializers.AbstractInitializer {
      @Override
      void setupModule(final SetupContext context, final boolean includeStacktrace, final boolean stacktraceAsString) {
         super.setupModule(context, includeStacktrace, stacktraceAsString);
         context.addSerializers(new SimpleSerializers(Collections.singletonList(new ContextDataAsEntryListSerializer())));
         context.addDeserializers(new SimpleDeserializers(Collections.singletonMap(StringMap.class, new ContextDataAsEntryListDeserializer())));
      }
   }

   static class SetupContextInitializer extends Initializers.AbstractInitializer {
      @Override
      void setupModule(final SetupContext context, final boolean includeStacktrace, final boolean stacktraceAsString) {
         super.setupModule(context, includeStacktrace, stacktraceAsString);
         context.addSerializers(new SimpleSerializers(Collections.singletonList(new ContextDataSerializer())));
         context.addDeserializers(new SimpleDeserializers(Collections.singletonMap(StringMap.class, new ContextDataDeserializer())));
      }
   }

   static class SimpleModuleInitializer {
      void initialize(final SimpleModule simpleModule, final boolean objectMessageAsJsonObject) {
         simpleModule.addDeserializer(StackTraceElement.class, new Log4jStackTraceElementDeserializer());
         simpleModule.addDeserializer(ContextStack.class, new MutableThreadContextStackDeserializer());
         if (objectMessageAsJsonObject) {
            simpleModule.addSerializer(ObjectMessage.class, new ObjectMessageSerializer());
         }

         simpleModule.addSerializer(Message.class, new MessageSerializer());
      }
   }
}
