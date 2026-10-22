package org.eclipse.jetty.client;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.Attributes;

public class BasicAuthentication extends AbstractAuthentication {
   private final String user;
   private final String password;

   public BasicAuthentication(URI uri, String realm, String user, String password) {
      super(uri, realm);
      this.user = user;
      this.password = password;
   }

   @Override
   public String getType() {
      return "Basic";
   }

   @Override
   public Authentication.Result authenticate(Request request, ContentResponse response, Authentication.HeaderInfo headerInfo, Attributes context) {
      String charsetParam = headerInfo.getParameter("charset");
      Charset charset = charsetParam == null ? null : Charset.forName(charsetParam);
      return new BasicAuthentication.BasicResult(this.getURI(), headerInfo.getHeader(), this.user, this.password, charset);
   }

   public static class BasicResult implements Authentication.Result {
      private final URI uri;
      private final HttpHeader header;
      private final String value;

      public BasicResult(URI uri, String user, String password) {
         this(uri, HttpHeader.AUTHORIZATION, user, password);
      }

      public BasicResult(URI uri, HttpHeader header, String user, String password) {
         this(uri, header, user, password, StandardCharsets.ISO_8859_1);
      }

      public BasicResult(URI uri, HttpHeader header, String user, String password, Charset charset) {
         this.uri = uri;
         this.header = header;
         if (charset == null) {
            charset = StandardCharsets.ISO_8859_1;
         }

         byte[] authBytes = (user + ":" + password).getBytes(charset);
         this.value = "Basic " + Base64.getEncoder().encodeToString(authBytes);
      }

      @Override
      public URI getURI() {
         return this.uri;
      }

      @Override
      public void apply(Request request) {
         if (!request.getHeaders().contains(this.header, this.value)) {
            request.headers(headers -> headers.add(this.header, this.value));
         }
      }

      @Override
      public String toString() {
         return String.format("Basic authentication result for %s", this.getURI());
      }
   }
}
