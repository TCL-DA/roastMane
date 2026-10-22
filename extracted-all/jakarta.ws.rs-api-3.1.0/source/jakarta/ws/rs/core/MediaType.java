package jakarta.ws.rs.core;

import jakarta.ws.rs.ext.RuntimeDelegate;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Map.Entry;

public class MediaType {
   private final String type;
   private final String subtype;
   private final Map<String, String> parameters;
   private final int hash;
   public static final String CHARSET_PARAMETER = "charset";
   public static final String MEDIA_TYPE_WILDCARD = "*";
   public static final String WILDCARD = "*/*";
   public static final MediaType WILDCARD_TYPE = new MediaType();
   public static final String APPLICATION_XML = "application/xml";
   public static final MediaType APPLICATION_XML_TYPE = new MediaType("application", "xml");
   public static final String APPLICATION_ATOM_XML = "application/atom+xml";
   public static final MediaType APPLICATION_ATOM_XML_TYPE = new MediaType("application", "atom+xml");
   public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
   public static final MediaType APPLICATION_XHTML_XML_TYPE = new MediaType("application", "xhtml+xml");
   public static final String APPLICATION_SVG_XML = "application/svg+xml";
   public static final MediaType APPLICATION_SVG_XML_TYPE = new MediaType("application", "svg+xml");
   public static final String APPLICATION_JSON = "application/json";
   public static final MediaType APPLICATION_JSON_TYPE = new MediaType("application", "json");
   public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
   public static final MediaType APPLICATION_FORM_URLENCODED_TYPE = new MediaType("application", "x-www-form-urlencoded");
   public static final String MULTIPART_FORM_DATA = "multipart/form-data";
   public static final MediaType MULTIPART_FORM_DATA_TYPE = new MediaType("multipart", "form-data");
   public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
   public static final MediaType APPLICATION_OCTET_STREAM_TYPE = new MediaType("application", "octet-stream");
   public static final String TEXT_PLAIN = "text/plain";
   public static final MediaType TEXT_PLAIN_TYPE = new MediaType("text", "plain");
   public static final String TEXT_XML = "text/xml";
   public static final MediaType TEXT_XML_TYPE = new MediaType("text", "xml");
   public static final String TEXT_HTML = "text/html";
   public static final MediaType TEXT_HTML_TYPE = new MediaType("text", "html");
   public static final String SERVER_SENT_EVENTS = "text/event-stream";
   public static final MediaType SERVER_SENT_EVENTS_TYPE = new MediaType("text", "event-stream");
   public static final String APPLICATION_JSON_PATCH_JSON = "application/json-patch+json";
   public static final MediaType APPLICATION_JSON_PATCH_JSON_TYPE = new MediaType("application", "json-patch+json");

   public static MediaType valueOf(String type) {
      return RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class).fromString(type);
   }

   private static TreeMap<String, String> createParametersMap(Map<String, String> initialValues) {
      TreeMap<String, String> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      if (initialValues != null) {
         for (Entry<String, String> e : initialValues.entrySet()) {
            map.put(e.getKey().toLowerCase(), e.getValue());
         }
      }

      return map;
   }

   public MediaType(String type, String subtype, Map<String, String> parameters) {
      this(type, subtype, null, createParametersMap(parameters));
   }

   public MediaType(String type, String subtype) {
      this(type, subtype, null, null);
   }

   public MediaType(String type, String subtype, String charset) {
      this(type, subtype, charset, null);
   }

   public MediaType() {
      this("*", "*", null, null);
   }

   private MediaType(String type, String subtype, String charset, Map<String, String> parameterMap) {
      this.type = type == null ? "*" : type;
      this.subtype = subtype == null ? "*" : subtype;
      Map<String, String> map = parameterMap;
      if (parameterMap == null) {
         map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      }

      if (charset != null && !charset.isEmpty()) {
         map.put("charset", charset);
      }

      this.parameters = Collections.unmodifiableMap(map);
      this.hash = Objects.hash(this.type.toLowerCase(), this.subtype.toLowerCase(), this.parameters);
   }

   public String getType() {
      return this.type;
   }

   public boolean isWildcardType() {
      return this.getType().equals("*");
   }

   public String getSubtype() {
      return this.subtype;
   }

   public boolean isWildcardSubtype() {
      return this.getSubtype().equals("*");
   }

   public Map<String, String> getParameters() {
      return this.parameters;
   }

   public MediaType withCharset(String charset) {
      return new MediaType(this.type, this.subtype, charset, createParametersMap(this.parameters));
   }

   public boolean isCompatible(MediaType other) {
      return other == null
         ? false
         : (this.type.equalsIgnoreCase(other.type) || this.isWildcardType() || other.isWildcardType())
            && (this.subtype.equalsIgnoreCase(other.subtype) || this.isWildcardSubtype() || other.isWildcardSubtype());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof MediaType)) {
         return false;
      } else {
         MediaType other = (MediaType)obj;
         return this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype) && this.parameters.equals(other.parameters);
      }
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   @Override
   public String toString() {
      return RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class).toString(this);
   }
}
