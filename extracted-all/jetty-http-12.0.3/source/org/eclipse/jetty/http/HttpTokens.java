package org.eclipse.jetty.http;

import org.eclipse.jetty.util.TypeUtil;

public class HttpTokens {
   static final byte COLON = 58;
   static final byte TAB = 9;
   static final byte LINE_FEED = 10;
   static final byte CARRIAGE_RETURN = 13;
   static final byte SPACE = 32;
   static final byte[] CRLF = new byte[]{13, 10};
   public static final HttpTokens.Token[] TOKENS = new HttpTokens.Token[256];

   public static HttpTokens.Token getToken(byte b) {
      return TOKENS[255 & b];
   }

   public static HttpTokens.Token getToken(char c) {
      return c <= 255 ? TOKENS[c] : null;
   }

   public static char sanitizeFieldVchar(char c) {
      switch (c) {
         case '\u0000':
         case '\n':
         case '\r':
            return ' ';
         default:
            return isIllegalFieldVchar(c) ? '?' : c;
      }
   }

   public static boolean isIllegalFieldVchar(char c) {
      return c >= 256 || c < ' ';
   }

   static {
      for (int b = 0; b < 256; b++) {
         switch (b) {
            case 9:
               TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.HTAB);
               break;
            case 10:
               TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.LF);
               break;
            case 13:
               TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.CR);
               break;
            case 32:
               TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.SPACE);
               break;
            case 33:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 42:
            case 43:
            case 45:
            case 46:
            case 94:
            case 95:
            case 96:
            case 124:
            case 126:
               TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.TCHAR);
               break;
            case 58:
               TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.COLON);
               break;
            default:
               if (b >= 48 && b <= 57) {
                  TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.DIGIT);
               } else if (b >= 65 && b <= 90) {
                  TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.ALPHA);
               } else if (b >= 97 && b <= 122) {
                  TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.ALPHA);
               } else if (b >= 33 && b <= 126) {
                  TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.VCHAR);
               } else if (b >= 128) {
                  TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.OTEXT);
               } else {
                  TOKENS[b] = new HttpTokens.Token((byte)b, HttpTokens.Type.CNTL);
               }
         }
      }
   }

   public static enum EndOfContent {
      UNKNOWN_CONTENT,
      NO_CONTENT,
      EOF_CONTENT,
      CONTENT_LENGTH,
      CHUNKED_CONTENT;
   }

   public static class Token {
      private final HttpTokens.Type _type;
      private final byte _b;
      private final char _c;
      private final int _x;
      private final boolean _rfc2616Token;
      private final boolean _rfc6265CookieOctet;

      private Token(byte b, HttpTokens.Type type) {
         this._type = type;
         this._b = b;
         this._c = (char)(255 & b);
         char lc = this._c >= 'A' & this._c <= 'Z' ? (char)(this._c - 'A' + 97) : this._c;
         this._x = this._type != HttpTokens.Type.DIGIT && (this._type != HttpTokens.Type.ALPHA || lc < 'a' || lc > 'f') ? -1 : TypeUtil.convertHexDigit(b);
         this._rfc2616Token = b >= 32
            && b < 127
            && b != 40
            && b != 41
            && b != 60
            && b != 62
            && b != 64
            && b != 44
            && b != 59
            && b != 58
            && b != 92
            && b != 34
            && b != 47
            && b != 91
            && b != 93
            && b != 63
            && b != 61
            && b != 123
            && b != 125
            && b != 32;
         this._rfc6265CookieOctet = b == 33 || b >= 35 && b <= 43 || b >= 45 && b <= 58 || b >= 60 && b <= 91 || b >= 93 && b <= 126;
      }

      public HttpTokens.Type getType() {
         return this._type;
      }

      public byte getByte() {
         return this._b;
      }

      public char getChar() {
         return this._c;
      }

      public boolean isHexDigit() {
         return this._x >= 0;
      }

      public boolean isRfc2616Token() {
         return this._rfc2616Token;
      }

      public boolean isRfc6265CookieOctet() {
         return this._rfc6265CookieOctet;
      }

      public int getHexDigit() {
         return this._x;
      }

      @Override
      public String toString() {
         switch (this._type) {
            case LF:
               return "LF=\\n";
            case CR:
               return "CR=\\r";
            case SPACE:
            case COLON:
            case DIGIT:
            case ALPHA:
            case TCHAR:
            case VCHAR:
               return this._type + "='" + this._c + "'";
            default:
               return String.format("%s=0x%x", this._type, this._b);
         }
      }
   }

   public static enum Type {
      CNTL,
      HTAB,
      LF,
      CR,
      SPACE,
      COLON,
      DIGIT,
      ALPHA,
      TCHAR,
      VCHAR,
      OTEXT;
   }
}
