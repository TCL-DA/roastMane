package org.eclipse.jetty.http;

public interface SetCookieParser {
   HttpCookie parse(String var1);

   static SetCookieParser newInstance() {
      return new RFC6265SetCookieParser();
   }
}
