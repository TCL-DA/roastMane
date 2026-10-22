package org.eclipse.jetty.client;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MultiPart;
import org.eclipse.jetty.http.MultiPart.Part;
import org.eclipse.jetty.http.MultiPartFormData.ContentSource;

public class MultiPartRequestContent extends ContentSource implements Request.Content {
   private final String contentType;

   public MultiPartRequestContent() {
      this(MultiPart.generateBoundary("JettyHttpClient-", 24));
   }

   public MultiPartRequestContent(String boundary) {
      super(boundary);
      this.contentType = "multipart/form-data; boundary=\"%s\"".formatted(boundary);
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }

   protected HttpFields customizePartHeaders(Part part) {
      HttpFields headers = super.customizePartHeaders(part);
      if (headers.contains(HttpHeader.CONTENT_TYPE)) {
         return headers;
      } else {
         if (part.getContentSource() instanceof Request.Content requestContent) {
            String contentType = requestContent.getContentType();
            if (contentType != null) {
               return HttpFields.build(headers).put(HttpHeader.CONTENT_TYPE, contentType);
            }
         }

         return headers;
      }
   }
}
