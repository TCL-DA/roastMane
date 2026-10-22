package org.eclipse.jetty.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.FileID;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Index.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MimeTypes {
   static final Logger LOG = LoggerFactory.getLogger(MimeTypes.class);
   private static final Set<Locale> KNOWN_LOCALES = Set.copyOf(Arrays.asList(Locale.getAvailableLocales()));
   public static final String ISO_8859_1 = StandardCharsets.ISO_8859_1.name().toLowerCase();
   public static final String UTF8 = StandardCharsets.UTF_8.name().toLowerCase();
   public static final String UTF16 = StandardCharsets.UTF_16.name().toLowerCase();
   private static final Index<String> CHARSETS = new Builder()
      .caseSensitive(false)
      .with("utf-8", UTF8)
      .with("utf8", UTF8)
      .with("utf-16", UTF16)
      .with("utf16", UTF16)
      .with("iso-8859-1", ISO_8859_1)
      .with("iso_8859_1", ISO_8859_1)
      .build();
   public static final Index<MimeTypes.Type> CACHE = new Builder().caseSensitive(false).withAll(() -> {
      Map<String, MimeTypes.Type> result = new HashMap<>();

      for (MimeTypes.Type type : MimeTypes.Type.values()) {
         String key1 = type.toString();
         result.put(key1, type);
         if (key1.indexOf(";charset=") > 0) {
            String key2 = StringUtil.replace(key1, ";charset=", "; charset=");
            result.put(key2, type);
         }
      }

      return result;
   }).build();
   protected final Map<String, String> _mimeMap = new HashMap<>();
   protected final Map<String, Charset> _inferredEncodings = new HashMap<>();
   protected final Map<String, Charset> _assumedEncodings = new HashMap<>();
   public static final MimeTypes DEFAULTS = new MimeTypes(null) {
      {
         for (MimeTypes.Type type : MimeTypes.Type.values()) {
            if (type.isCharsetAssumed()) {
               this._assumedEncodings.put(type.asString(), type.getCharset());
            }
         }

         String resourceName = "mime.properties";

         try (InputStream stream = MimeTypes.class.getResourceAsStream(resourceName)) {
            if (stream == null) {
               LOG.warn("Missing mime-type resource: {}", resourceName);
            } else {
               try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                  Properties props = new Properties();
                  props.load(reader);
                  props.stringPropertyNames().stream().filter(Objects::nonNull).forEach(x -> {
                     if (x.contains(".")) {
                        LOG.warn("ignoring invalid extension {} from mime.properties", x);
                     } else {
                        this._mimeMap.put(StringUtil.asciiToLowerCase(x), MimeTypes.normalizeMimeType(props.getProperty(x)));
                     }
                  });
                  if (this._mimeMap.isEmpty()) {
                     LOG.warn("Empty mime types at {}", resourceName);
                  } else if (this._mimeMap.size() < props.keySet().size()) {
                     LOG.warn("Duplicate or null mime-type extension in resource: {}", resourceName);
                  }
               } catch (IOException var16) {
                  if (LOG.isDebugEnabled()) {
                     LOG.warn("Unable to read mime-type resource: {}", resourceName, var16);
                  } else {
                     LOG.warn("Unable to read mime-type resource: {} - {}", resourceName, var16.toString());
                  }
               }
            }
         } catch (IOException var18) {
            if (LOG.isDebugEnabled()) {
               LOG.warn("Unable to load mime-type resource: {}", resourceName, var18);
            } else {
               LOG.warn("Unable to load mime-type resource: {} - {}", resourceName, var18.toString());
            }
         }

         resourceName = "encoding.properties";

         try (InputStream streamx = MimeTypes.class.getResourceAsStream(resourceName)) {
            if (streamx == null) {
               LOG.warn("Missing encoding resource: {}", resourceName);
            } else {
               try (InputStreamReader readerx = new InputStreamReader(streamx, StandardCharsets.UTF_8)) {
                  Properties propsx = new Properties();
                  propsx.load(readerx);
                  propsx.stringPropertyNames().stream().filter(Objects::nonNull).forEach(t -> {
                     String charset = props.getProperty(t);
                     if (charset.startsWith("-")) {
                        this._assumedEncodings.put(t, Charset.forName(charset.substring(1)));
                     } else {
                        this._inferredEncodings.put(t, Charset.forName(props.getProperty(t)));
                     }
                  });
                  if (this._inferredEncodings.isEmpty()) {
                     LOG.warn("Empty encodings at {}", resourceName);
                  } else if (this._inferredEncodings.size() + this._assumedEncodings.size() < propsx.keySet().size()) {
                     LOG.warn("Null or duplicate encodings in resource: {}", resourceName);
                  }
               } catch (IOException var13) {
                  if (LOG.isDebugEnabled()) {
                     LOG.warn("Unable to read encoding resource: {}", resourceName, var13);
                  } else {
                     LOG.warn("Unable to read encoding resource: {} - {}", resourceName, var13.toString());
                  }
               }
            }
         } catch (IOException var15) {
            if (LOG.isDebugEnabled()) {
               LOG.warn("Unable to load encoding resource: {}", resourceName, var15);
            } else {
               LOG.warn("Unable to load encoding resource: {} - {}", resourceName, var15.toString());
            }
         }
      }
   };

   public static MimeTypes.Type getBaseType(String contentType) {
      if (StringUtil.isEmpty(contentType)) {
         return null;
      } else {
         MimeTypes.Type type = (MimeTypes.Type)CACHE.getBest(contentType);
         if (type == null) {
            return null;
         } else if (type.asString().length() == contentType.length()) {
            return type.getBaseType();
         } else if (contentType.charAt(type.asString().length()) == ';') {
            return type.getBaseType();
         } else {
            contentType = contentType.replace(" ", "");
            if (type.asString().length() == contentType.length()) {
               return type.getBaseType();
            } else {
               return contentType.charAt(type.asString().length()) == ';' ? type.getBaseType() : null;
            }
         }
      }
   }

   public static boolean isKnownLocale(Locale locale) {
      return KNOWN_LOCALES.contains(locale);
   }

   public static String normalizeCharset(String charsetName) {
      String n = (String)CHARSETS.get(charsetName);
      return n == null ? charsetName : n;
   }

   public static String normalizeCharset(String charsetName, int offset, int length) {
      String n = (String)CHARSETS.get(charsetName, offset, length);
      return n == null ? charsetName.substring(offset, offset + length) : n;
   }

   public static Charset getKnownCharset(String charsetName) throws UnsupportedEncodingException {
      if (StandardCharsets.UTF_8.name().equalsIgnoreCase(charsetName)) {
         return StandardCharsets.UTF_8;
      } else {
         charsetName = normalizeCharset(charsetName);
         if (StandardCharsets.UTF_8.name().equalsIgnoreCase(charsetName)) {
            return StandardCharsets.UTF_8;
         } else {
            try {
               return Charset.forName(charsetName);
            } catch (UnsupportedCharsetException var2) {
               throw new UnsupportedEncodingException(var2.getMessage());
            }
         }
      }
   }

   private static String nameOf(Charset charset) {
      return charset == null ? null : charset.name();
   }

   public MimeTypes() {
      this(DEFAULTS);
   }

   public MimeTypes(MimeTypes defaults) {
      if (defaults != null) {
         this._mimeMap.putAll(defaults.getMimeMap());
         this._assumedEncodings.putAll(defaults._assumedEncodings);
         this._inferredEncodings.putAll(defaults._inferredEncodings);
      }
   }

   public Charset getCharset(String mimeType) throws IllegalCharsetNameException, UnsupportedCharsetException {
      if (mimeType == null) {
         return null;
      } else {
         MimeTypes.Type mime = (MimeTypes.Type)CACHE.get(mimeType);
         if (mime != null && mime.getCharset() != null) {
            return mime.getCharset();
         } else {
            String charsetName = getCharsetFromContentType(mimeType);
            if (charsetName != null) {
               return Charset.forName(charsetName);
            } else {
               Charset charset = this.getAssumedCharset(mimeType);
               return charset != null ? charset : this.getInferredCharset(mimeType);
            }
         }
      }
   }

   public String getMimeByExtension(String filename) {
      String ext = FileID.getExtension(filename);
      return this.getMimeForExtension(Objects.requireNonNullElse(ext, "*"));
   }

   public String getMimeForExtension(String extension) {
      return this._mimeMap.get(extension);
   }

   public Charset getInferredCharset(String contentType) {
      return this._inferredEncodings.get(contentType);
   }

   public Charset getAssumedCharset(String contentType) {
      return this._assumedEncodings.get(contentType);
   }

   public String getCharsetInferredFromContentType(String contentType) {
      return nameOf(this._inferredEncodings.get(contentType));
   }

   public String getCharsetAssumedFromContentType(String contentType) {
      return nameOf(this._assumedEncodings.get(contentType));
   }

   public Map<String, String> getMimeMap() {
      return Collections.unmodifiableMap(this._mimeMap);
   }

   public Map<String, String> getInferredMap() {
      return this._inferredEncodings.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().name()));
   }

   public Map<String, String> getAssumedMap() {
      return this._assumedEncodings.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().name()));
   }

   private static String normalizeMimeType(String type) {
      MimeTypes.Type t = (MimeTypes.Type)CACHE.get(type);
      return t != null ? t.asString() : StringUtil.asciiToLowerCase(type);
   }

   public static String getCharsetFromContentType(String value) {
      if (value == null) {
         return null;
      } else {
         int end = value.length();
         int state = 0;
         int start = 0;
         boolean quote = false;

         int i;
         for (i = 0; i < end; i++) {
            char b = value.charAt(i);
            if (quote && state != 10) {
               if ('"' == b) {
                  quote = false;
               }
            } else if (';' == b && state <= 8) {
               state = 1;
            } else {
               switch (state) {
                  case 0:
                     if ('"' == b) {
                        quote = true;
                     }
                     break;
                  case 1:
                     if ('c' == b) {
                        state = 2;
                     } else if (' ' != b) {
                        state = 0;
                     }
                     break;
                  case 2:
                     if ('h' == b) {
                        state = 3;
                     } else {
                        state = 0;
                     }
                     break;
                  case 3:
                     if ('a' == b) {
                        state = 4;
                     } else {
                        state = 0;
                     }
                     break;
                  case 4:
                     if ('r' == b) {
                        state = 5;
                     } else {
                        state = 0;
                     }
                     break;
                  case 5:
                     if ('s' == b) {
                        state = 6;
                     } else {
                        state = 0;
                     }
                     break;
                  case 6:
                     if ('e' == b) {
                        state = 7;
                     } else {
                        state = 0;
                     }
                     break;
                  case 7:
                     if ('t' == b) {
                        state = 8;
                     } else {
                        state = 0;
                     }
                     break;
                  case 8:
                     if ('=' == b) {
                        state = 9;
                     } else if (' ' != b) {
                        state = 0;
                     }
                     break;
                  case 9:
                     if (' ' != b) {
                        if ('"' == b) {
                           quote = true;
                           start = i + 1;
                           state = 10;
                        } else {
                           start = i;
                           state = 10;
                        }
                     }
                     break;
                  case 10:
                     if (!quote && (';' == b || ' ' == b) || quote && '"' == b) {
                        return normalizeCharset(value, start, i - start);
                     }
                     break;
                  default:
                     throw new IllegalStateException();
               }
            }
         }

         return state == 10 ? normalizeCharset(value, start, i - start) : null;
      }
   }

   public static String getContentTypeWithoutCharset(String value) {
      int end = value.length();
      int state = 0;
      int start = 0;
      boolean quote = false;
      int i = 0;

      StringBuilder builder;
      for (builder = null; i < end; i++) {
         char b = value.charAt(i);
         if ('"' == b) {
            quote = !quote;
            switch (state) {
               case 9:
                  builder = new StringBuilder();
                  builder.append(value, 0, start + 1);
                  state = 10;
               case 10:
                  break;
               case 11:
                  builder.append(b);
                  break;
               default:
                  start = i;
                  state = 0;
            }
         } else if (quote) {
            if (builder != null && state != 10) {
               builder.append(b);
            }
         } else {
            switch (state) {
               case 0:
                  if (';' == b) {
                     state = 1;
                  } else if (' ' != b) {
                     start = i;
                  }
                  break;
               case 1:
                  if ('c' == b) {
                     state = 2;
                  } else if (' ' != b) {
                     state = 0;
                  }
                  break;
               case 2:
                  if ('h' == b) {
                     state = 3;
                  } else {
                     state = 0;
                  }
                  break;
               case 3:
                  if ('a' == b) {
                     state = 4;
                  } else {
                     state = 0;
                  }
                  break;
               case 4:
                  if ('r' == b) {
                     state = 5;
                  } else {
                     state = 0;
                  }
                  break;
               case 5:
                  if ('s' == b) {
                     state = 6;
                  } else {
                     state = 0;
                  }
                  break;
               case 6:
                  if ('e' == b) {
                     state = 7;
                  } else {
                     state = 0;
                  }
                  break;
               case 7:
                  if ('t' == b) {
                     state = 8;
                  } else {
                     state = 0;
                  }
                  break;
               case 8:
                  if ('=' == b) {
                     state = 9;
                  } else if (' ' != b) {
                     state = 0;
                  }
                  break;
               case 9:
                  if (' ' != b) {
                     builder = new StringBuilder();
                     builder.append(value, 0, start + 1);
                     state = 10;
                  }
                  break;
               case 10:
                  if (';' == b) {
                     builder.append(b);
                     state = 11;
                  }
                  break;
               case 11:
                  if (' ' != b) {
                     builder.append(b);
                  }
                  break;
               default:
                  throw new IllegalStateException();
            }
         }
      }

      return builder == null ? value : builder.toString();
   }

   public static class Mutable extends MimeTypes {
      public Mutable() {
         this(DEFAULTS);
      }

      public Mutable(MimeTypes defaults) {
         super(defaults);
      }

      public String addMimeMapping(String extension, String type) {
         if (extension.contains(".")) {
            throw new IllegalArgumentException("extensions cannot contain '.'");
         } else {
            return this._mimeMap.put(StringUtil.asciiToLowerCase(extension), MimeTypes.normalizeMimeType(type));
         }
      }

      public String addInferred(String contentType, String encoding) {
         return MimeTypes.nameOf(this._inferredEncodings.put(contentType, Charset.forName(encoding)));
      }

      public String addAssumed(String contentType, String encoding) {
         return MimeTypes.nameOf(this._assumedEncodings.put(contentType, Charset.forName(encoding)));
      }
   }

   public static enum Type {
      FORM_ENCODED("application/x-www-form-urlencoded"),
      MESSAGE_HTTP("message/http"),
      MULTIPART_BYTERANGES("multipart/byteranges"),
      MULTIPART_FORM_DATA("multipart/form-data"),
      TEXT_HTML("text/html") {
         @Override
         public HttpField getContentTypeField(Charset charset) {
            if (Objects.equals(charset, StandardCharsets.UTF_8)) {
               return TEXT_HTML_UTF_8.getContentTypeField();
            } else {
               return Objects.equals(charset, StandardCharsets.ISO_8859_1) ? TEXT_HTML_8859_1.getContentTypeField() : super.getContentTypeField(charset);
            }
         }
      },
      TEXT_PLAIN("text/plain") {
         @Override
         public HttpField getContentTypeField(Charset charset) {
            if (Objects.equals(charset, StandardCharsets.UTF_8)) {
               return TEXT_PLAIN_UTF_8.getContentTypeField();
            } else {
               return Objects.equals(charset, StandardCharsets.ISO_8859_1) ? TEXT_PLAIN_8859_1.getContentTypeField() : super.getContentTypeField(charset);
            }
         }
      },
      TEXT_XML("text/xml") {
         @Override
         public HttpField getContentTypeField(Charset charset) {
            if (Objects.equals(charset, StandardCharsets.UTF_8)) {
               return TEXT_XML_UTF_8.getContentTypeField();
            } else {
               return Objects.equals(charset, StandardCharsets.ISO_8859_1) ? TEXT_XML_8859_1.getContentTypeField() : super.getContentTypeField(charset);
            }
         }
      },
      TEXT_JSON("text/json", StandardCharsets.UTF_8),
      APPLICATION_JSON("application/json", StandardCharsets.UTF_8),
      TEXT_HTML_8859_1("text/html;charset=iso-8859-1", TEXT_HTML),
      TEXT_HTML_UTF_8("text/html;charset=utf-8", TEXT_HTML),
      TEXT_PLAIN_8859_1("text/plain;charset=iso-8859-1", TEXT_PLAIN),
      TEXT_PLAIN_UTF_8("text/plain;charset=utf-8", TEXT_PLAIN),
      TEXT_XML_8859_1("text/xml;charset=iso-8859-1", TEXT_XML),
      TEXT_XML_UTF_8("text/xml;charset=utf-8", TEXT_XML),
      TEXT_JSON_8859_1("text/json;charset=iso-8859-1", TEXT_JSON),
      TEXT_JSON_UTF_8("text/json;charset=utf-8", TEXT_JSON),
      APPLICATION_JSON_8859_1("application/json;charset=iso-8859-1", APPLICATION_JSON),
      APPLICATION_JSON_UTF_8("application/json;charset=utf-8", APPLICATION_JSON);

      private final String _string;
      private final MimeTypes.Type _base;
      private final Charset _charset;
      private final String _charsetString;
      private final boolean _assumedCharset;
      private final HttpField _field;

      private Type(String name) {
         this._string = name;
         this._base = this;
         this._charset = null;
         this._charsetString = null;
         this._assumedCharset = false;
         this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
      }

      private Type(String name, MimeTypes.Type base) {
         this._string = name;
         this._base = base;
         int i = name.indexOf(";charset=");
         this._charset = Charset.forName(name.substring(i + 9));
         this._charsetString = this._charset.toString().toLowerCase(Locale.ENGLISH);
         this._assumedCharset = false;
         this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
      }

      private Type(String name, Charset cs) {
         this._string = name;
         this._base = this;
         this._charset = cs;
         this._charsetString = this._charset == null ? null : this._charset.toString().toLowerCase(Locale.ENGLISH);
         this._assumedCharset = true;
         this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
      }

      public Charset getCharset() {
         return this._charset;
      }

      public String getCharsetString() {
         return this._charsetString;
      }

      public boolean is(String type) {
         return this._string.equalsIgnoreCase(type);
      }

      public String asString() {
         return this._string;
      }

      @Override
      public String toString() {
         return this._string;
      }

      public boolean isCharsetAssumed() {
         return this._assumedCharset;
      }

      public HttpField getContentTypeField() {
         return this._field;
      }

      public HttpField getContentTypeField(Charset charset) {
         return Objects.equals(this._charset, charset)
            ? this._field
            : new HttpField(HttpHeader.CONTENT_TYPE, MimeTypes.getContentTypeWithoutCharset(this._string) + ";charset=" + charset.name());
      }

      public MimeTypes.Type getBaseType() {
         return this._base;
      }
   }

   public static class Wrapper extends MimeTypes.Mutable {
      private MimeTypes _wrapped;

      public Wrapper() {
         super(null);
      }

      public MimeTypes getWrapped() {
         return this._wrapped;
      }

      public void setWrapped(MimeTypes wrapped) {
         this._wrapped = wrapped;
      }

      @Override
      public String getMimeForExtension(String extension) {
         String mime = super.getMimeForExtension(extension);
         return mime == null && this._wrapped != null ? this._wrapped.getMimeForExtension(extension) : mime;
      }

      @Override
      public String getCharsetInferredFromContentType(String contentType) {
         String charset = super.getCharsetInferredFromContentType(contentType);
         return charset == null && this._wrapped != null ? this._wrapped.getCharsetInferredFromContentType(contentType) : charset;
      }

      @Override
      public String getCharsetAssumedFromContentType(String contentType) {
         String charset = super.getCharsetAssumedFromContentType(contentType);
         return charset == null && this._wrapped != null ? this._wrapped.getCharsetAssumedFromContentType(contentType) : charset;
      }

      @Override
      public Map<String, String> getMimeMap() {
         Map<String, String> map = super.getMimeMap();
         if (this._wrapped != null && !map.isEmpty()) {
            Map<String, String> var2 = new HashMap<>(map);
            var2.putAll(this._wrapped.getMimeMap());
            return Collections.unmodifiableMap(var2);
         } else {
            return map;
         }
      }

      @Override
      public Map<String, String> getInferredMap() {
         Map<String, String> map = super.getInferredMap();
         if (this._wrapped != null && !map.isEmpty()) {
            Map<String, String> var2 = new HashMap<>(map);
            var2.putAll(this._wrapped.getInferredMap());
            return Collections.unmodifiableMap(var2);
         } else {
            return map;
         }
      }

      @Override
      public Map<String, String> getAssumedMap() {
         Map<String, String> map = super.getAssumedMap();
         if (this._wrapped != null && !map.isEmpty()) {
            Map<String, String> var2 = new HashMap<>(map);
            var2.putAll(this._wrapped.getAssumedMap());
            return Collections.unmodifiableMap(var2);
         } else {
            return map;
         }
      }
   }
}
