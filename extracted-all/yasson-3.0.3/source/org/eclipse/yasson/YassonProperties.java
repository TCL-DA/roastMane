package org.eclipse.yasson;

@Deprecated
public class YassonProperties {
   /** @deprecated */
   public static final String FAIL_ON_UNKNOWN_PROPERTIES = "jsonb.fail-on-unknown-properties";
   /** @deprecated */
   public static final String USER_TYPE_MAPPING = "jsonb.user-type-mapping";
   /** @deprecated */
   public static final String ZERO_TIME_PARSE_DEFAULTING = "jsonb.zero-time-defaulting";
   /** @deprecated */
   public static final String NULL_ROOT_SERIALIZER = "yasson.null-root-serializer";

   private YassonProperties() {
      throw new IllegalStateException("Util classes cannot be instantiated.");
   }
}
