package org.eclipse.jetty.client;

import java.io.IOException;
import java.nio.file.Path;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.content.PathContentSource;

public class PathRequestContent extends PathContentSource implements Request.Content {
   private final String contentType;

   public PathRequestContent(Path filePath) throws IOException {
      this(filePath, 4096);
   }

   public PathRequestContent(Path filePath, int bufferSize) throws IOException {
      this("application/octet-stream", filePath, bufferSize);
   }

   public PathRequestContent(String contentType, Path filePath) throws IOException {
      this(contentType, filePath, 4096);
   }

   public PathRequestContent(String contentType, Path filePath, int bufferSize) throws IOException {
      this(contentType, filePath, null);
      this.setBufferSize(bufferSize);
   }

   public PathRequestContent(String contentType, Path filePath, ByteBufferPool bufferPool) throws IOException {
      super(filePath, bufferPool);
      this.contentType = contentType;
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }
}
