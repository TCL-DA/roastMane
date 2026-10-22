package org.eclipse.jetty.http;

import java.util.Locale;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RFC6265CookieParser implements CookieParser {
   protected static final Logger LOG = LoggerFactory.getLogger(RFC6265CookieParser.class);
   private final CookieParser.Handler _handler;
   private final CookieCompliance _complianceMode;
   private final ComplianceViolation.Listener _complianceListener;

   protected RFC6265CookieParser(CookieParser.Handler handler, CookieCompliance compliance, ComplianceViolation.Listener complianceListener) {
      this._handler = handler;
      this._complianceMode = compliance;
      this._complianceListener = complianceListener;
   }

   @Override
   public void parseField(String field) {
      RFC6265CookieParser.State state = RFC6265CookieParser.State.START;
      String attributeName = null;
      String value = null;
      String cookieName = null;
      String cookieValue = null;
      String cookiePath = null;
      String cookieDomain = null;
      String cookieComment = null;
      int cookieVersion = 0;
      boolean cookieInvalid = false;
      int spaces = 0;
      int length = field.length();
      StringBuilder string = new StringBuilder();

      for (int i = 0; i <= length; i++) {
         char c = i == length ? 59 : field.charAt(i);
         HttpTokens.Token token = HttpTokens.getToken(c);
         if (token == null) {
            if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
               throw new CookieParser.InvalidCookieException("Invalid Cookie character");
            }

            state = RFC6265CookieParser.State.INVALID_COOKIE;
         } else {
            switch (state) {
               case START:
                  if (c != ' ' && c != '\t' && c != ';') {
                     string.setLength(0);
                     if (token.isRfc2616Token()) {
                        if (!StringUtil.isBlank(cookieName)
                           && (
                              c != '$'
                                 || !this._complianceMode.allows(CookieCompliance.Violation.ATTRIBUTES)
                                    && !this._complianceMode.allows(CookieCompliance.Violation.ATTRIBUTE_VALUES)
                           )) {
                           this._handler.addCookie(cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath, cookieComment);
                           cookieName = null;
                           cookieValue = null;
                           cookieDomain = null;
                           cookiePath = null;
                           cookieComment = null;
                        }

                        string.append(c);
                        state = RFC6265CookieParser.State.IN_NAME;
                     } else {
                        if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                           throw new CookieParser.InvalidCookieException("Bad Cookie name");
                        }

                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = RFC6265CookieParser.State.INVALID_COOKIE;
                     }
                  }
                  break;
               case IN_NAME:
                  if (c == '=') {
                     if (string.charAt(0) == '$') {
                        attributeName = string.toString();
                     } else {
                        cookieName = string.toString();
                     }

                     state = RFC6265CookieParser.State.VALUE;
                  } else if ((c == ' ' || c == '\t') && this._complianceMode.allows(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE)) {
                     this.reportComplianceViolation(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE, field);
                     if (string.charAt(0) == '$') {
                        attributeName = string.toString();
                     } else {
                        cookieName = string.toString();
                     }

                     state = RFC6265CookieParser.State.AFTER_NAME;
                  } else if (token.isRfc2616Token()) {
                     string.append(c);
                  } else {
                     if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        throw new CookieParser.InvalidCookieException("Bad Cookie name");
                     }

                     this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                     state = c == ';' ? RFC6265CookieParser.State.START : RFC6265CookieParser.State.INVALID_COOKIE;
                  }
                  break;
               case AFTER_NAME:
                  if (c == '=') {
                     state = RFC6265CookieParser.State.VALUE;
                  } else if (c != ';' && c != ',') {
                     if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        throw new CookieParser.InvalidCookieException("Bad Cookie");
                     }

                     this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                     state = RFC6265CookieParser.State.INVALID_COOKIE;
                  } else {
                     state = RFC6265CookieParser.State.START;
                  }
                  break;
               case VALUE:
                  if (c == ' ' && this._complianceMode.allows(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE)) {
                     this.reportComplianceViolation(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE, field);
                  } else {
                     string.setLength(0);
                     if (c == '"') {
                        state = RFC6265CookieParser.State.IN_QUOTED_VALUE;
                     } else if (c == ';') {
                        value = "";
                        i--;
                        state = RFC6265CookieParser.State.END;
                     } else if (token.isRfc6265CookieOctet()) {
                        string.append(c);
                        state = RFC6265CookieParser.State.IN_VALUE;
                     } else {
                        if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                           throw new CookieParser.InvalidCookieException("Bad Cookie value");
                        }

                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = RFC6265CookieParser.State.INVALID_COOKIE;
                     }
                  }
                  break;
               case IN_VALUE:
                  if (c == ' ' && this._complianceMode.allows(CookieCompliance.Violation.SPACE_IN_VALUES)) {
                     this.reportComplianceViolation(CookieCompliance.Violation.SPACE_IN_VALUES, field);
                     spaces = 1;
                     state = RFC6265CookieParser.State.SPACE_IN_VALUE;
                  } else if (c == ' ' || c == ';' || c == ',' || c == '\t') {
                     value = string.toString();
                     i--;
                     state = RFC6265CookieParser.State.END;
                  } else if (token.isRfc6265CookieOctet()) {
                     string.append(c);
                  } else {
                     if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        throw new CookieParser.InvalidCookieException("Bad Cookie value");
                     }

                     this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                     state = RFC6265CookieParser.State.INVALID_COOKIE;
                  }
                  break;
               case SPACE_IN_VALUE:
                  if (c == ' ') {
                     spaces++;
                  } else if (c == ';' || c == ',' || c == '\t') {
                     value = string.toString();
                     i--;
                     state = RFC6265CookieParser.State.END;
                  } else if (token.isRfc6265CookieOctet()) {
                     string.append(" ".repeat(spaces)).append(c);
                     state = RFC6265CookieParser.State.IN_VALUE;
                  } else {
                     if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        throw new CookieParser.InvalidCookieException("Bad Cookie value");
                     }

                     this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                     state = RFC6265CookieParser.State.INVALID_COOKIE;
                  }
                  break;
               case IN_QUOTED_VALUE:
                  if (c == '"') {
                     value = string.toString();
                     state = RFC6265CookieParser.State.AFTER_QUOTED_VALUE;
                  } else if (c == '\\' && this._complianceMode.allows(CookieCompliance.Violation.ESCAPE_IN_QUOTES)) {
                     state = RFC6265CookieParser.State.ESCAPED_VALUE;
                  } else if (token.isRfc6265CookieOctet()) {
                     string.append(c);
                  } else if (this._complianceMode.allows(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES)) {
                     this.reportComplianceViolation(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES, field);
                     string.append(c);
                  } else if (c == ',' && this._complianceMode.allows(CookieCompliance.Violation.COMMA_NOT_VALID_OCTET)) {
                     this.reportComplianceViolation(CookieCompliance.Violation.COMMA_NOT_VALID_OCTET, field);
                     string.append(c);
                  } else if (c == ' ' && this._complianceMode.allows(CookieCompliance.Violation.SPACE_IN_VALUES)) {
                     this.reportComplianceViolation(CookieCompliance.Violation.SPACE_IN_VALUES, field);
                     string.append(c);
                  } else {
                     if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        throw new CookieParser.InvalidCookieException("Bad Cookie quoted value");
                     }

                     string.append(c);
                     if (!cookieInvalid) {
                        cookieInvalid = true;
                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                     }
                  }
                  break;
               case ESCAPED_VALUE:
                  string.append(c);
                  state = RFC6265CookieParser.State.IN_QUOTED_VALUE;
                  break;
               case AFTER_QUOTED_VALUE:
                  if (c != ';' && c != ',' && c != ' ' && c != '\t') {
                     if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                        throw new CookieParser.InvalidCookieException("Bad Cookie quoted value");
                     }

                     this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                     state = RFC6265CookieParser.State.INVALID_COOKIE;
                  } else {
                     i--;
                     state = cookieInvalid ? RFC6265CookieParser.State.INVALID_COOKIE : RFC6265CookieParser.State.END;
                  }
                  break;
               case END:
                  if (c == ';') {
                     state = RFC6265CookieParser.State.START;
                  } else if (c == ',') {
                     if (!this._complianceMode.allows(CookieCompliance.Violation.COMMA_SEPARATOR)) {
                        if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                           throw new CookieParser.InvalidCookieException("Comma cookie separator");
                        }

                        this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                        state = RFC6265CookieParser.State.INVALID_COOKIE;
                        break;
                     }

                     this.reportComplianceViolation(CookieCompliance.Violation.COMMA_SEPARATOR, field);
                     state = RFC6265CookieParser.State.START;
                  } else if ((c == ' ' || c == '\t') && this._complianceMode.allows(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE)) {
                     this.reportComplianceViolation(CookieCompliance.Violation.OPTIONAL_WHITE_SPACE, field);
                     break;
                  }

                  if (StringUtil.isBlank(attributeName)) {
                     cookieValue = value;
                  } else {
                     if (this._complianceMode.allows(CookieCompliance.Violation.ATTRIBUTE_VALUES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.ATTRIBUTES, field);
                        String var18 = attributeName.toLowerCase(Locale.ENGLISH);
                        switch (var18) {
                           case "$path":
                              cookiePath = value;
                              break;
                           case "$domain":
                              cookieDomain = value;
                              break;
                           case "$port":
                              cookieComment = "$port=" + value;
                              break;
                           case "$version":
                              cookieVersion = Integer.parseInt(value);
                              break;
                           default:
                              if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                                 throw new IllegalArgumentException("Invalid Cookie attribute");
                              }

                              this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, field);
                              state = RFC6265CookieParser.State.INVALID_COOKIE;
                        }
                     } else if (this._complianceMode.allows(CookieCompliance.Violation.ATTRIBUTES)) {
                        this.reportComplianceViolation(CookieCompliance.Violation.ATTRIBUTES, field);
                     } else {
                        cookieName = attributeName;
                        cookieValue = value;
                     }

                     attributeName = null;
                  }

                  value = null;
                  if (state == RFC6265CookieParser.State.END) {
                     throw new CookieParser.InvalidCookieException("Invalid cookie");
                  }
                  break;
               case INVALID_COOKIE:
                  attributeName = null;
                  value = null;
                  cookieName = null;
                  cookieValue = null;
                  cookiePath = null;
                  cookieDomain = null;
                  cookieComment = null;
                  cookieInvalid = false;
                  if (c == ';') {
                     state = RFC6265CookieParser.State.START;
                  }
            }
         }
      }

      if (!cookieInvalid && !StringUtil.isBlank(cookieName)) {
         this._handler.addCookie(cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath, cookieComment);
      }
   }

   protected void reportComplianceViolation(CookieCompliance.Violation violation, String reason) {
      if (this._complianceListener != null) {
         this._complianceListener.onComplianceViolation(this._complianceMode, violation, reason);
      }
   }

   private static enum State {
      START,
      IN_NAME,
      AFTER_NAME,
      VALUE,
      IN_VALUE,
      SPACE_IN_VALUE,
      IN_QUOTED_VALUE,
      ESCAPED_VALUE,
      AFTER_QUOTED_VALUE,
      END,
      INVALID_COOKIE;
   }
}
