package org.eclipse.jetty.http;

import java.util.List;

public interface CookieParser {
   static CookieParser newParser(CookieParser.Handler handler, CookieCompliance compliance, ComplianceViolation.Listener complianceListener) {
      return (CookieParser)(compliance.allows(CookieCompliance.Violation.BAD_QUOTES)
         ? new CookieCutter(handler, compliance, complianceListener)
         : new RFC6265CookieParser(handler, compliance, complianceListener));
   }

   void parseField(String var1) throws CookieParser.InvalidCookieException;

   default void parseFields(List<String> rawFields) throws CookieParser.InvalidCookieException {
      for (String field : rawFields) {
         this.parseField(field);
      }
   }

   public interface Handler {
      void addCookie(String var1, String var2, int var3, String var4, String var5, String var6);
   }

   public static class InvalidCookieException extends IllegalArgumentException {
      public InvalidCookieException() {
      }

      public InvalidCookieException(String s) {
         super(s);
      }

      public InvalidCookieException(String message, Throwable cause) {
         super(message, cause);
      }

      public InvalidCookieException(Throwable cause) {
         super(cause);
      }
   }
}
