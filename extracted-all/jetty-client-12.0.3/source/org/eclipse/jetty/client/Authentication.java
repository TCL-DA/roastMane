package org.eclipse.jetty.client;

import java.net.URI;
import java.util.Map;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.StringUtil;

public interface Authentication {
   String ANY_REALM = "<<ANY_REALM>>";

   boolean matches(String var1, URI var2, String var3);

   Authentication.Result authenticate(Request var1, ContentResponse var2, Authentication.HeaderInfo var3, Attributes var4);

   public static class HeaderInfo {
      private final HttpHeader header;
      private final String type;
      private final Map<String, String> params;

      public HeaderInfo(HttpHeader header, String type, Map<String, String> params) throws IllegalArgumentException {
         this.header = header;
         this.type = type;
         this.params = params;
      }

      public String getType() {
         return this.type;
      }

      public String getRealm() {
         return this.params.get("realm");
      }

      public String getBase64() {
         return this.params.get("base64");
      }

      public Map<String, String> getParameters() {
         return this.params;
      }

      public String getParameter(String paramName) {
         return this.params.get(StringUtil.asciiToLowerCase(paramName));
      }

      public HttpHeader getHeader() {
         return this.header;
      }
   }

   public interface Result {
      URI getURI();

      void apply(Request var1);
   }
}
