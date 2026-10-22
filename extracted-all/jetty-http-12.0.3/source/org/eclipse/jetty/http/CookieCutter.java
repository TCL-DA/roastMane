package org.eclipse.jetty.http;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class CookieCutter implements CookieParser {
   protected static final Logger LOG = LoggerFactory.getLogger(CookieCutter.class);
   private final CookieParser.Handler _handler;
   private final CookieCompliance _complianceMode;
   private final ComplianceViolation.Listener _complianceListener;

   public CookieCutter(CookieParser.Handler handler, CookieCompliance compliance, ComplianceViolation.Listener complianceListener) {
      this._handler = handler;
      this._complianceMode = compliance;
      this._complianceListener = complianceListener;
   }

   @Override
   public void parseField(String field) {
      this.parseFields(Collections.singletonList(field));
   }

   @Override
   public void parseFields(List<String> rawFields) {
      StringBuilder unquoted = null;

      for (String hdr : rawFields) {
         String name = null;
         String cookieName = null;
         String cookieValue = null;
         String cookiePath = null;
         String cookieDomain = null;
         String cookieComment = null;
         int cookieVersion = 0;
         boolean invalue = false;
         boolean inQuoted = false;
         boolean quoted = false;
         boolean escaped = false;
         boolean reject = false;
         int tokenstart = -1;
         int tokenend = -1;
         int i = 0;

         for (int length = hdr.length(); i <= length; i++) {
            char c = i == length ? 0 : hdr.charAt(i);
            if (inQuoted) {
               if (escaped) {
                  escaped = false;
                  if (c > 0) {
                     unquoted.append(c);
                  } else {
                     unquoted.setLength(0);
                     inQuoted = false;
                     i--;
                  }
               } else {
                  switch (c) {
                     case '\u0000':
                        if (this._complianceMode.allows(CookieCompliance.Violation.BAD_QUOTES)) {
                           this.reportComplianceViolation(CookieCompliance.Violation.BAD_QUOTES, hdr);
                        } else {
                           reject = true;
                        }

                        unquoted.setLength(0);
                        inQuoted = false;
                        i--;
                        break;
                     case '"':
                        inQuoted = false;
                        quoted = true;
                        tokenstart = i;
                        tokenend = -1;
                        break;
                     case '\\':
                        if (this._complianceMode.allows(CookieCompliance.Violation.ESCAPE_IN_QUOTES)) {
                           this.reportComplianceViolation(CookieCompliance.Violation.ESCAPE_IN_QUOTES, hdr);
                        } else {
                           reject = true;
                        }

                        escaped = true;
                        break;
                     default:
                        if (this.isRFC6265RejectedCharacter(c)) {
                           if (this._complianceMode.allows(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES)) {
                              this.reportComplianceViolation(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES, hdr);
                           } else {
                              reject = true;
                           }
                        }

                        unquoted.append(c);
                  }
               }
            } else if (!invalue) {
               switch (c) {
                  case '\u0000':
                  case '\t':
                  case ' ':
                     break;
                  case '"':
                     reject = true;
                     break;
                  case ';':
                     tokenstart = -1;
                     invalue = false;
                     reject = false;
                     break;
                  case '=':
                     if (quoted) {
                        name = unquoted.toString();
                        unquoted.setLength(0);
                        quoted = false;
                     } else if (tokenstart >= 0) {
                        name = tokenend >= tokenstart ? hdr.substring(tokenstart, tokenend + 1) : hdr.substring(tokenstart);
                     }

                     tokenstart = -1;
                     invalue = true;
                     break;
                  default:
                     if (quoted) {
                        if (this._complianceMode.allows(CookieCompliance.Violation.BAD_QUOTES)) {
                           this.reportComplianceViolation(CookieCompliance.Violation.BAD_QUOTES, hdr);
                        } else {
                           reject = true;
                        }

                        unquoted.append(hdr, tokenstart, i--);
                        inQuoted = true;
                        quoted = false;
                     } else {
                        if (this.isRFC6265RejectedCharacter(c)) {
                           if (this._complianceMode.allows(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES)) {
                              this.reportComplianceViolation(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES, hdr);
                           } else {
                              reject = true;
                           }
                        }

                        if (tokenstart < 0) {
                           tokenstart = i;
                        }

                        tokenend = i;
                     }
               }
            } else {
               switch (c) {
                  case '\t':
                  case ' ':
                     break;
                  case '"':
                     if (tokenstart < 0) {
                        tokenstart = i;
                        inQuoted = true;
                        if (unquoted == null) {
                           unquoted = new StringBuilder();
                        }
                        break;
                     }
                  default:
                     if (quoted) {
                        if (this._complianceMode.allows(CookieCompliance.Violation.BAD_QUOTES)) {
                           this.reportComplianceViolation(CookieCompliance.Violation.BAD_QUOTES, hdr);
                        } else {
                           reject = true;
                        }

                        unquoted.append(hdr, tokenstart, i--);
                        inQuoted = true;
                        quoted = false;
                        break;
                     }

                     if (this.isRFC6265RejectedCharacter(c)) {
                        if (c < 128 && this._complianceMode.allows(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES)) {
                           this.reportComplianceViolation(CookieCompliance.Violation.SPECIAL_CHARS_IN_QUOTES, hdr);
                        } else {
                           reject = true;
                        }
                     }

                     if (tokenstart < 0) {
                        tokenstart = i;
                     }

                     tokenend = i;
                     break;
                  case ',':
                     if (!CookieCompliance.Violation.COMMA_NOT_VALID_OCTET.isAllowedBy(this._complianceMode)) {
                        if (quoted) {
                           if (this._complianceMode.allows(CookieCompliance.Violation.BAD_QUOTES)) {
                              this.reportComplianceViolation(CookieCompliance.Violation.BAD_QUOTES, hdr);
                           } else {
                              reject = true;
                           }

                           unquoted.append(hdr, tokenstart, i--);
                           inQuoted = true;
                           quoted = false;
                        } else {
                           if (tokenstart < 0) {
                              tokenstart = i;
                           }

                           tokenend = i;
                        }
                        break;
                     } else {
                        this.reportComplianceViolation(CookieCompliance.Violation.COMMA_NOT_VALID_OCTET, "Cookie " + cookieName);
                     }
                  case '\u0000':
                  case ';':
                     String value;
                     if (quoted) {
                        value = unquoted.toString();
                        unquoted.setLength(0);
                        quoted = false;
                     } else if (tokenstart >= 0) {
                        value = tokenend >= tokenstart ? hdr.substring(tokenstart, tokenend + 1) : hdr.substring(tokenstart);
                     } else {
                        value = "";
                     }

                     try {
                        if (name != null && name.startsWith("$")) {
                           if (CookieCompliance.Violation.RESERVED_NAMES_NOT_DOLLAR_PREFIXED.isAllowedBy(this._complianceMode)) {
                              this.reportComplianceViolation(
                                 CookieCompliance.Violation.RESERVED_NAMES_NOT_DOLLAR_PREFIXED, "Cookie " + cookieName + " field " + name
                              );
                              String lowercaseName = name.toLowerCase(Locale.ENGLISH);
                              switch (lowercaseName) {
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
                              }
                           }
                        } else {
                           if (cookieName != null) {
                              if (reject) {
                                 if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                                    throw new CookieParser.InvalidCookieException("Bad Cookie");
                                 }

                                 this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, hdr);
                              } else {
                                 this._handler.addCookie(cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath, cookieComment);
                              }

                              reject = false;
                              cookieDomain = null;
                              cookiePath = null;
                              cookieComment = null;
                           }

                           cookieName = name;
                           cookieValue = value;
                        }
                     } catch (Exception var26) {
                        LOG.debug("Unable to process Cookie", var26);
                     }

                     name = null;
                     tokenstart = -1;
                     invalue = false;
               }
            }
         }

         if (cookieName != null) {
            if (reject) {
               if (!this._complianceMode.allows(CookieCompliance.Violation.INVALID_COOKIES)) {
                  throw new CookieParser.InvalidCookieException("Bad Cookie");
               }

               this.reportComplianceViolation(CookieCompliance.Violation.INVALID_COOKIES, hdr);
            } else {
               this._handler.addCookie(cookieName, cookieValue, cookieVersion, cookieDomain, cookiePath, cookieComment);
            }
         }
      }
   }

   protected void reportComplianceViolation(CookieCompliance.Violation violation, String reason) {
      if (this._complianceListener != null) {
         this._complianceListener.onComplianceViolation(this._complianceMode, violation, reason);
      }
   }

   protected boolean isRFC6265RejectedCharacter(char c) {
      return Character.isISOControl(c) || c > 127 || c == ' ' || c == '"' || c == ',' || c == ';' || c == '\\';
   }
}
