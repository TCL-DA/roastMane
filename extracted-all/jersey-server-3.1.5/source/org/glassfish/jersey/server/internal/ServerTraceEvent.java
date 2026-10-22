package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.message.internal.TracingLogger.Event;
import org.glassfish.jersey.message.internal.TracingLogger.Level;

public enum ServerTraceEvent implements Event {
   START(Level.SUMMARY, "START", null),
   START_HEADERS(Level.VERBOSE, "START", null),
   PRE_MATCH(Level.TRACE, "PRE-MATCH", "Filter by %s"),
   PRE_MATCH_SUMMARY(Level.SUMMARY, "PRE-MATCH", "PreMatchRequest summary: %s filters"),
   MATCH_PATH_FIND(Level.TRACE, "MATCH", "Matching path [%s]"),
   MATCH_PATH_NOT_MATCHED(Level.VERBOSE, "MATCH", "Pattern [%s] is NOT matched"),
   MATCH_PATH_SELECTED(Level.TRACE, "MATCH", "Pattern [%s] IS selected"),
   MATCH_PATH_SKIPPED(Level.VERBOSE, "MATCH", "Pattern [%s] is skipped"),
   MATCH_LOCATOR(Level.TRACE, "MATCH", "Matched locator : %s"),
   MATCH_RESOURCE_METHOD(Level.TRACE, "MATCH", "Matched method  : %s"),
   MATCH_RUNTIME_RESOURCE(Level.TRACE, "MATCH", "Matched resource: template=[%s] regexp=[%s] matches=[%s] from=[%s]"),
   MATCH_RESOURCE(Level.TRACE, "MATCH", "Resource instance: %s"),
   MATCH_SUMMARY(Level.SUMMARY, "MATCH", "RequestMatching summary"),
   REQUEST_FILTER(Level.TRACE, "REQ-FILTER", "Filter by %s"),
   REQUEST_FILTER_SUMMARY(Level.SUMMARY, "REQ-FILTER", "Request summary: %s filters"),
   METHOD_INVOKE(Level.SUMMARY, "INVOKE", "Resource %s method=[%s]"),
   DISPATCH_RESPONSE(Level.TRACE, "INVOKE", "Response: %s"),
   RESPONSE_FILTER(Level.TRACE, "RESP-FILTER", "Filter by %s"),
   RESPONSE_FILTER_SUMMARY(Level.SUMMARY, "RESP-FILTER", "Response summary: %s filters"),
   FINISHED(Level.SUMMARY, "FINISHED", "Response status: %s"),
   EXCEPTION_MAPPING(Level.SUMMARY, "EXCEPTION", "Exception mapper %s maps %s ('%s') to <%s>");

   private final Level level;
   private final String category;
   private final String messageFormat;

   private ServerTraceEvent(Level level, String category, String messageFormat) {
      this.level = level;
      this.category = category;
      this.messageFormat = messageFormat;
   }

   public String category() {
      return this.category;
   }

   public Level level() {
      return this.level;
   }

   public String messageFormat() {
      return this.messageFormat;
   }
}
