package org.eclipse.jetty.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RFC6265SetCookieParser implements SetCookieParser {
   private static final Logger LOG = LoggerFactory.getLogger(RFC6265SetCookieParser.class);

   @Override
   public HttpCookie parse(String setCookieValue) {
      RFC6265SetCookieParser.State state = RFC6265SetCookieParser.State.NAME;
      String name = null;
      boolean quoted = false;
      HttpCookie.Builder cookie = null;
      int offset = 0;
      int length = setCookieValue.length();

      for (int i = 0; i < length; i++) {
         char ch = setCookieValue.charAt(i);
         switch (state) {
            case NAME:
               HttpTokens.Token token = HttpTokens.getToken(ch);
               if (token == null) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("invalid character {} at index {} of {}", new Object[]{ch, i, setCookieValue});
                  }

                  return null;
               }

               if (ch == '=') {
                  name = setCookieValue.substring(offset, i).trim();
                  if (name.isEmpty()) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("invalid empty cookie name at index {} of {}", i, setCookieValue);
                     }

                     return null;
                  }

                  offset = i + 1;
                  state = RFC6265SetCookieParser.State.VALUE_START;
               }
               break;
            case VALUE_START:
               if (!isWhitespace(ch)) {
                  if (ch == '"') {
                     quoted = true;
                  } else {
                     i--;
                  }

                  offset = i + 1;
                  state = RFC6265SetCookieParser.State.VALUE;
               }
               break;
            case VALUE:
               if (quoted && ch == '"') {
                  quoted = false;
                  String value = setCookieValue.substring(offset, i).trim();
                  cookie = HttpCookie.build(name, value);
                  offset = i + 1;
                  state = RFC6265SetCookieParser.State.ATTRIBUTE;
               } else if (ch == ';') {
                  String value = setCookieValue.substring(offset, i).trim();
                  cookie = HttpCookie.build(name, value);
                  offset = i + 1;
                  state = RFC6265SetCookieParser.State.ATTRIBUTE_NAME;
               }
               break;
            case ATTRIBUTE:
               if (!isWhitespace(ch)) {
                  if (ch != ';') {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("invalid character {} at index {} of {}", new Object[]{ch, i, setCookieValue});
                     }

                     return null;
                  }

                  offset = i + 1;
                  state = RFC6265SetCookieParser.State.ATTRIBUTE_NAME;
               }
               break;
            case ATTRIBUTE_NAME:
               HttpTokens.Token tokenx = HttpTokens.getToken(ch);
               if (tokenx != null && tokenx.getType() != HttpTokens.Type.CNTL) {
                  if (ch == '=') {
                     name = setCookieValue.substring(offset, i).trim();
                     offset = i + 1;
                     state = RFC6265SetCookieParser.State.ATTRIBUTE_VALUE_START;
                  } else if (ch == ';') {
                     name = setCookieValue.substring(offset, i).trim();
                     if (!this.setAttribute(cookie, name, "")) {
                        return null;
                     }

                     offset = i + 1;
                  }
                  break;
               }

               if (LOG.isDebugEnabled()) {
                  LOG.debug("invalid character {} at index {} of {}", new Object[]{ch, i, setCookieValue});
               }

               return null;
            case ATTRIBUTE_VALUE_START:
               if (!isWhitespace(ch)) {
                  if (ch == '"') {
                     quoted = true;
                  } else {
                     i--;
                  }

                  offset = i + 1;
                  state = RFC6265SetCookieParser.State.ATTRIBUTE_VALUE;
               }
               break;
            case ATTRIBUTE_VALUE:
               if (quoted && ch == '"') {
                  quoted = false;
                  String value = setCookieValue.substring(offset, i).trim();
                  if (!this.setAttribute(cookie, name, value)) {
                     return null;
                  }

                  offset = i + 1;
                  state = RFC6265SetCookieParser.State.ATTRIBUTE;
               } else if (ch == ';') {
                  String value = setCookieValue.substring(offset, i).trim();
                  if (!this.setAttribute(cookie, name, value)) {
                     return null;
                  }

                  offset = i + 1;
                  state = RFC6265SetCookieParser.State.ATTRIBUTE_NAME;
               }
               break;
            default:
               throw new IllegalStateException("invalid state " + state);
         }
      }
      return switch (state) {
         case NAME -> null;
         case VALUE_START -> HttpCookie.from(name, "");
         case VALUE -> HttpCookie.from(name, setCookieValue.substring(offset, length).trim());
         case ATTRIBUTE -> cookie.build();
         case ATTRIBUTE_NAME -> this.setAttribute(cookie, setCookieValue.substring(offset, length).trim(), "") ? cookie.build() : null;
         case ATTRIBUTE_VALUE_START -> this.setAttribute(cookie, name, "") ? cookie.build() : null;
         case ATTRIBUTE_VALUE -> this.setAttribute(cookie, name, setCookieValue.substring(offset, length).trim()) ? cookie.build() : null;
      };
   }

   private static boolean isWhitespace(char ch) {
      return ch == ' ' || ch == '\t';
   }

   private boolean setAttribute(HttpCookie.Builder cookie, String name, String value) {
      try {
         cookie.attribute(name, value);
         return true;
      } catch (Throwable var5) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("could not set attribute {}={}", new Object[]{name, value, var5});
         }

         return false;
      }
   }

   private static enum State {
      NAME,
      VALUE_START,
      VALUE,
      ATTRIBUTE,
      ATTRIBUTE_NAME,
      ATTRIBUTE_VALUE_START,
      ATTRIBUTE_VALUE;
   }
}
