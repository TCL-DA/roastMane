package org.eclipse.jetty.client;

import org.eclipse.jetty.io.content.OutputStreamContentSource;

public class OutputStreamRequestContent extends OutputStreamContentSource implements Request.Content {
   private final String contentType;

   public OutputStreamRequestContent() {
      this("application/octet-stream");
   }

   public OutputStreamRequestContent(String contentType) {
      this.contentType = contentType;
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }
}
