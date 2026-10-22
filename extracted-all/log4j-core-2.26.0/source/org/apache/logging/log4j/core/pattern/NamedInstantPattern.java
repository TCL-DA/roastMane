package org.apache.logging.log4j.core.pattern;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum NamedInstantPattern {
   ABSOLUTE("HH:mm:ss,SSS"),
   ABSOLUTE_MICROS("HH:mm:ss,SSSSSS", "HH:mm:ss,nnnnnn"),
   ABSOLUTE_NANOS("HH:mm:ss,SSSSSSSSS", "HH:mm:ss,nnnnnnnnn"),
   ABSOLUTE_PERIOD("HH:mm:ss.SSS"),
   COMPACT("yyyyMMddHHmmssSSS"),
   DATE("dd MMM yyyy HH:mm:ss,SSS"),
   DATE_PERIOD("dd MMM yyyy HH:mm:ss.SSS"),
   DEFAULT("yyyy-MM-dd HH:mm:ss,SSS"),
   DEFAULT_MICROS("yyyy-MM-dd HH:mm:ss,SSSSSS", "yyyy-MM-dd HH:mm:ss,nnnnnn"),
   DEFAULT_NANOS("yyyy-MM-dd HH:mm:ss,SSSSSSSSS", "yyyy-MM-dd HH:mm:ss,nnnnnnnnn"),
   DEFAULT_PERIOD("yyyy-MM-dd HH:mm:ss.SSS"),
   ISO8601_BASIC("yyyyMMdd'T'HHmmss,SSS"),
   ISO8601_BASIC_PERIOD("yyyyMMdd'T'HHmmss.SSS"),
   ISO8601("yyyy-MM-dd'T'HH:mm:ss,SSS"),
   ISO8601_OFFSET_DATE_TIME_HH("yyyy-MM-dd'T'HH:mm:ss,SSSx", "yyyy-MM-dd'T'HH:mm:ss,SSSX"),
   ISO8601_OFFSET_DATE_TIME_HHMM("yyyy-MM-dd'T'HH:mm:ss,SSSxx", "yyyy-MM-dd'T'HH:mm:ss,SSSXX"),
   ISO8601_OFFSET_DATE_TIME_HHCMM("yyyy-MM-dd'T'HH:mm:ss,SSSxxx", "yyyy-MM-dd'T'HH:mm:ss,SSSXXX"),
   ISO8601_PERIOD("yyyy-MM-dd'T'HH:mm:ss.SSS"),
   ISO8601_PERIOD_MICROS("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", "yyyy-MM-dd'T'HH:mm:ss.nnnnnn"),
   US_MONTH_DAY_YEAR2_TIME("dd/MM/yy HH:mm:ss.SSS"),
   US_MONTH_DAY_YEAR4_TIME("dd/MM/yyyy HH:mm:ss.SSS");

   private final String pattern;
   private final String legacyPattern;

   private NamedInstantPattern(String pattern) {
      this(pattern, pattern);
   }

   private NamedInstantPattern(String pattern, String legacyPattern) {
      this.pattern = pattern;
      this.legacyPattern = legacyPattern;
   }

   public String getPattern() {
      return this.pattern;
   }

   String getLegacyPattern() {
      return this.legacyPattern;
   }
}
