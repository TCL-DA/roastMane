package org.glassfish.jersey.media.multipart;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;
import org.glassfish.jersey.message.internal.HttpDateFormat;
import org.glassfish.jersey.message.internal.HttpHeaderReader;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriComponent.Type;

public class ContentDisposition {
   private final CharSequence type;
   private final Map<String, String> parameters;
   private String fileName;
   private Date creationDate;
   private Date modificationDate;
   private Date readDate;
   private long size;
   private boolean encoded;
   private static final String CHARSET_GROUP_NAME = "charset";
   private static final String CHARSET_REGEX = "(?<charset>[^']+)";
   private static final String LANG_GROUP_NAME = "lang";
   private static final String LANG_REGEX = "(?<lang>[a-z]{2,8}(-[a-z0-9-]+)?)?";
   private static final String FILENAME_GROUP_NAME = "filename";
   private static final String FILENAME_REGEX = "(?<filename>.+)";
   private static final Pattern FILENAME_EXT_VALUE_PATTERN = Pattern.compile("(?<charset>[^']+)'(?<lang>[a-z]{2,8}(-[a-z0-9-]+)?)?'(?<filename>.+)", 2);
   private static final Pattern FILENAME_VALUE_CHARS_PATTERN = Pattern.compile("(%[a-f0-9]{2}|[a-z0-9!#$&+.^_`|~-])+", 2);

   protected ContentDisposition(String type, String fileName, Date creationDate, Date modificationDate, Date readDate, long size) {
      this.type = type;
      this.fileName = fileName;
      this.creationDate = creationDate;
      this.modificationDate = modificationDate;
      this.readDate = readDate;
      this.size = size;
      this.parameters = Collections.emptyMap();
      this.encoded = false;
   }

   public ContentDisposition(String header) throws ParseException {
      this(header, false);
   }

   public ContentDisposition(String header, boolean fileNameFix) throws ParseException {
      this(HttpHeaderReader.newInstance(header), fileNameFix);
   }

   public ContentDisposition(HttpHeaderReader reader, boolean fileNameFix) throws ParseException {
      reader.hasNext();
      this.type = reader.nextToken();
      Map<String, String> paramsOrNull = reader.hasNext() ? HttpHeaderReader.readParameters(reader, fileNameFix) : null;
      this.parameters = paramsOrNull == null ? Collections.emptyMap() : Collections.unmodifiableMap(paramsOrNull);
      this.createParameters();
   }

   public String getType() {
      return this.type == null ? null : this.type.toString();
   }

   public Map<String, String> getParameters() {
      return this.parameters;
   }

   public String getFileName() {
      return this.getFileName(true);
   }

   public String getFileName(boolean decodeExtended) {
      return this.encoded && decodeExtended ? decodeFromUriFormat(this.fileName) : this.fileName;
   }

   public Date getCreationDate() {
      return this.creationDate;
   }

   public Date getModificationDate() {
      return this.modificationDate;
   }

   public Date getReadDate() {
      return this.readDate;
   }

   public long getSize() {
      return this.size;
   }

   @Override
   public String toString() {
      return this.toStringBuffer().toString();
   }

   protected StringBuilder toStringBuffer() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.type);
      this.addStringParameter(sb, "filename", this.fileName);
      this.addDateParameter(sb, "creation-date", this.creationDate);
      this.addDateParameter(sb, "modification-date", this.modificationDate);
      this.addDateParameter(sb, "read-date", this.readDate);
      this.addLongParameter(sb, "size", this.size);
      return sb;
   }

   protected void addStringParameter(StringBuilder sb, String name, String p) {
      if (p != null) {
         sb.append("; ").append(name).append("=\"").append(p).append("\"");
      }
   }

   protected void addDateParameter(StringBuilder sb, String name, Date p) {
      if (p != null) {
         sb.append("; ").append(name).append("=\"").append(HttpDateFormat.getPreferredDateFormat().format(p)).append("\"");
      }
   }

   protected void addLongParameter(StringBuilder sb, String name, Long p) {
      if (p != -1L) {
         sb.append("; ").append(name).append('=').append(Long.toString(p));
      }
   }

   private void createParameters() throws ParseException {
      this.defineFileName();
      this.creationDate = this.createDate("creation-date");
      this.modificationDate = this.createDate("modification-date");
      this.readDate = this.createDate("read-date");
      this.size = this.createLong("size");
   }

   private void defineFileName() throws ParseException {
      this.encoded = false;
      String fileName = this.parameters.get("filename");
      String fileNameExt = this.parameters.get("filename*");
      if (fileNameExt == null) {
         this.fileName = fileName;
      } else {
         Matcher matcher = FILENAME_EXT_VALUE_PATTERN.matcher(fileNameExt);
         if (matcher.matches()) {
            this.encoded = true;
            String fileNameValueChars = matcher.group("filename");
            if (isFilenameValueCharsEncoded(fileNameValueChars)) {
               this.fileName = fileNameExt;
            } else {
               String charset = matcher.group("charset");
               if (!charset.equalsIgnoreCase("UTF-8")) {
                  throw new ParseException(LocalizationMessages.ERROR_CHARSET_UNSUPPORTED(charset), 0);
               }

               String language = matcher.group("lang");
               this.fileName = charset + "'" + (language == null ? "" : language) + "'" + encodeToUriFormat(fileNameValueChars);
            }
         } else {
            throw new ParseException(LocalizationMessages.ERROR_FILENAME_UNSUPPORTED(fileNameExt), 0);
         }
      }
   }

   private static String decodeFromUriFormat(String parameter) {
      Matcher matcher = FILENAME_EXT_VALUE_PATTERN.matcher(parameter);
      if (matcher.matches()) {
         String fileNameValueChars = matcher.group("filename");
         return UriComponent.decode(fileNameValueChars, Type.UNRESERVED);
      } else {
         return parameter;
      }
   }

   private static String encodeToUriFormat(String parameter) {
      return UriComponent.contextualEncode(parameter, Type.UNRESERVED);
   }

   private static boolean isFilenameValueCharsEncoded(String parameter) {
      return FILENAME_VALUE_CHARS_PATTERN.matcher(parameter).matches();
   }

   private Date createDate(String name) throws ParseException {
      String value = this.parameters.get(name);
      return value == null ? null : HttpDateFormat.getPreferredDateFormat().parse(value);
   }

   private long createLong(String name) throws ParseException {
      String value = this.parameters.get(name);
      if (value == null) {
         return -1L;
      } else {
         try {
            return Long.parseLong(value);
         } catch (NumberFormatException var4) {
            throw new ParseException("Error parsing size parameter of value, " + value, 0);
         }
      }
   }

   public static ContentDisposition.ContentDispositionBuilder type(String type) {
      return new ContentDisposition.ContentDispositionBuilder(type);
   }

   public static class ContentDispositionBuilder<T extends ContentDisposition.ContentDispositionBuilder, V extends ContentDisposition> {
      protected String type;
      protected String fileName;
      protected Date creationDate;
      protected Date modificationDate;
      protected Date readDate;
      protected long size = -1L;

      ContentDispositionBuilder(String type) {
         this.type = type;
      }

      public T fileName(String fileName) {
         this.fileName = fileName;
         return (T)this;
      }

      public T creationDate(Date creationDate) {
         this.creationDate = creationDate;
         return (T)this;
      }

      public T modificationDate(Date modificationDate) {
         this.modificationDate = modificationDate;
         return (T)this;
      }

      public T readDate(Date readDate) {
         this.readDate = readDate;
         return (T)this;
      }

      public T size(long size) {
         this.size = size;
         return (T)this;
      }

      public V build() {
         return (V)(new ContentDisposition(this.type, this.fileName, this.creationDate, this.modificationDate, this.readDate, this.size));
      }
   }
}
