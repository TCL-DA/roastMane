package org.eclipse.jetty.server;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.MimeTypes.Type;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.io.content.ContentSourceCompletableFuture;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.CharsetStringBuilder;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.Fields.Field;

public class FormFields extends ContentSourceCompletableFuture<Fields> {
   public static final String MAX_FIELDS_ATTRIBUTE = "org.eclipse.jetty.server.Request.maxFormKeys";
   public static final String MAX_LENGTH_ATTRIBUTE = "org.eclipse.jetty.server.Request.maxFormContentSize";
   private static final CompletableFuture<Fields> EMPTY = CompletableFuture.completedFuture(Fields.EMPTY);
   private final Fields _fields;
   private final CharsetStringBuilder _builder;
   private final int _maxFields;
   private final int _maxLength;
   private int _length;
   private String _name;
   private int _percent = 0;
   private byte _percentCode;

   public static Charset getFormEncodedCharset(Request request) {
      HttpConfiguration config = request.getConnectionMetaData().getHttpConfiguration();
      if (!config.getFormEncodedMethods().contains(request.getMethod())) {
         return null;
      } else {
         String contentType = request.getHeaders().get(HttpHeader.CONTENT_TYPE);
         if (request.getLength() != 0L && !StringUtil.isBlank(contentType)) {
            Type type = (Type)MimeTypes.CACHE.get(MimeTypes.getContentTypeWithoutCharset(contentType));
            if (Type.FORM_ENCODED != type) {
               return null;
            } else {
               String cs = MimeTypes.getCharsetFromContentType(contentType);
               return StringUtil.isEmpty(cs) ? StandardCharsets.UTF_8 : Charset.forName(cs);
            }
         } else {
            return null;
         }
      }
   }

   public static void set(Request request, CompletableFuture<Fields> fields) {
      request.setAttribute(FormFields.class.getName(), fields);
   }

   public static CompletableFuture<Fields> get(Request request) {
      Object attr = request.getAttribute(FormFields.class.getName());
      return (CompletableFuture<Fields>)(attr instanceof FormFields ? (FormFields)attr : EMPTY);
   }

   public static CompletableFuture<Fields> from(Request request) {
      int maxFields = getRequestAttribute(request, "org.eclipse.jetty.server.Request.maxFormKeys");
      int maxLength = getRequestAttribute(request, "org.eclipse.jetty.server.Request.maxFormContentSize");
      return from(request, maxFields, maxLength);
   }

   public static CompletableFuture<Fields> from(Request request, Charset charset) {
      int maxFields = getRequestAttribute(request, "org.eclipse.jetty.server.Request.maxFormKeys");
      int maxLength = getRequestAttribute(request, "org.eclipse.jetty.server.Request.maxFormContentSize");
      return from(request, charset, maxFields, maxLength);
   }

   public static CompletableFuture<Fields> from(Request request, int maxFields, int maxLength) {
      return from(request, getFormEncodedCharset(request), maxFields, maxLength);
   }

   public static CompletableFuture<Fields> from(Request request, Charset charset, int maxFields, int maxLength) {
      return from(request, request, charset, maxFields, maxLength);
   }

   static CompletableFuture<Fields> from(Source source, Attributes attributes, Charset charset, int maxFields, int maxLength) {
      Object attr = attributes.getAttribute(FormFields.class.getName());
      if (attr instanceof FormFields) {
         return (FormFields)attr;
      } else if (attr instanceof Fields fields) {
         return CompletableFuture.completedFuture(fields);
      } else if (charset == null) {
         return EMPTY;
      } else {
         FormFields futureFormFields = new FormFields(source, charset, maxFields, maxLength);
         attributes.setAttribute(FormFields.class.getName(), futureFormFields);
         futureFormFields.parse();
         return futureFormFields;
      }
   }

   private static int getRequestAttribute(Request request, String attribute) {
      Object value = request.getAttribute(attribute);
      if (value == null) {
         return -1;
      } else {
         try {
            return Integer.parseInt(value.toString());
         } catch (NumberFormatException var4) {
            return -1;
         }
      }
   }

   private FormFields(Source source, Charset charset, int maxFields, int maxSize) {
      super(source);
      this._maxFields = maxFields;
      this._maxLength = maxSize;
      this._builder = CharsetStringBuilder.forCharset(charset);
      this._fields = new Fields();
   }

   protected Fields parse(Chunk chunk) throws CharacterCodingException {
      String value = null;
      ByteBuffer buffer = chunk.getByteBuffer();

      while (true) {
         if (BufferUtil.hasContent(buffer)) {
            byte b = buffer.get();
            switch (this._percent) {
               case 1:
                  this._percentCode = b;
                  this._percent++;
                  continue;
               case 2:
                  this._builder.append(UrlEncoded.decodeHexByte((char)this._percentCode, (char)b));
                  this._percent = 0;
                  continue;
               default:
                  if (this._name == null) {
                     switch (b) {
                        case 37:
                           this._percent++;
                           continue;
                        case 43:
                           this._builder.append((byte)32);
                           continue;
                        case 61:
                           this._name = this._builder.build();
                           this.checkLength(this._name);
                           continue;
                        default:
                           this._builder.append(b);
                           continue;
                     }
                  }

                  switch (b) {
                     case 37:
                        this._percent++;
                        continue;
                     case 38:
                        value = this._builder.build();
                        this.checkLength(value);
                        break;
                     case 43:
                        this._builder.append((byte)32);
                        continue;
                     default:
                        this._builder.append(b);
                        continue;
                  }
            }
         }

         if (this._name != null) {
            if (value == null && chunk.isLast()) {
               if (this._percent > 0) {
                  this._builder.append((byte)37);
                  this._builder.append(this._percentCode);
               }

               value = this._builder.build();
               this.checkLength(value);
            }

            if (value != null) {
               Field field = new Field(this._name, value);
               this._name = null;
               value = null;
               if (this._maxFields >= 0 && this._fields.getSize() >= this._maxFields) {
                  throw new IllegalStateException("form with too many fields > " + this._maxFields);
               }

               this._fields.add(field);
            }
         }

         if (!BufferUtil.hasContent(buffer)) {
            return chunk.isLast() ? this._fields : null;
         }
      }
   }

   private void checkLength(String nameOrValue) {
      if (this._maxLength >= 0) {
         this._length = this._length + nameOrValue.length();
         if (this._length > this._maxLength) {
            throw new IllegalStateException("form too large");
         }
      }
   }
}
