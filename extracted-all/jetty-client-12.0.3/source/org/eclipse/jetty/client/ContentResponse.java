package org.eclipse.jetty.client;

import org.eclipse.jetty.client.internal.HttpContentResponse;

public interface ContentResponse extends Response {
   static ContentResponse from(Response response, byte[] content, String mediaType, String encoding) {
      return new HttpContentResponse(response, content, mediaType, encoding);
   }

   String getMediaType();

   String getEncoding();

   byte[] getContent();

   String getContentAsString();
}
