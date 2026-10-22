package org.glassfish.jersey.message.internal;

public enum MsgTraceEvent implements TracingLogger.Event {
   RI_BEFORE(TracingLogger.Level.TRACE, "RI", "%s BEFORE context.proceed()"),
   RI_AFTER(TracingLogger.Level.TRACE, "RI", "%s AFTER context.proceed()"),
   RI_SUMMARY(TracingLogger.Level.SUMMARY, "RI", "ReadFrom summary: %s interceptors"),
   MBR_FIND(TracingLogger.Level.TRACE, "MBR", "Find MBR for type=[%s] genericType=[%s] mediaType=[%s] annotations=%s"),
   MBR_NOT_READABLE(TracingLogger.Level.VERBOSE, "MBR", "%s is NOT readable"),
   MBR_SELECTED(TracingLogger.Level.TRACE, "MBR", "%s IS readable"),
   MBR_SKIPPED(TracingLogger.Level.VERBOSE, "MBR", "%s is skipped"),
   MBR_READ_FROM(TracingLogger.Level.TRACE, "MBR", "ReadFrom by %s"),
   MBW_FIND(TracingLogger.Level.TRACE, "MBW", "Find MBW for type=[%s] genericType=[%s] mediaType=[%s] annotations=%s"),
   MBW_NOT_WRITEABLE(TracingLogger.Level.VERBOSE, "MBW", "%s is NOT writeable"),
   MBW_SELECTED(TracingLogger.Level.TRACE, "MBW", "%s IS writeable"),
   MBW_SKIPPED(TracingLogger.Level.VERBOSE, "MBW", "%s is skipped"),
   MBW_WRITE_TO(TracingLogger.Level.TRACE, "MBW", "WriteTo by %s"),
   WI_BEFORE(TracingLogger.Level.TRACE, "WI", "%s BEFORE context.proceed()"),
   WI_AFTER(TracingLogger.Level.TRACE, "WI", "%s AFTER context.proceed()"),
   WI_SUMMARY(TracingLogger.Level.SUMMARY, "WI", "WriteTo summary: %s interceptors");

   private final TracingLogger.Level level;
   private final String category;
   private final String messageFormat;

   private MsgTraceEvent(TracingLogger.Level level, String category, String messageFormat) {
      this.level = level;
      this.category = category;
      this.messageFormat = messageFormat;
   }

   @Override
   public String category() {
      return this.category;
   }

   @Override
   public TracingLogger.Level level() {
      return this.level;
   }

   @Override
   public String messageFormat() {
      return this.messageFormat;
   }
}
