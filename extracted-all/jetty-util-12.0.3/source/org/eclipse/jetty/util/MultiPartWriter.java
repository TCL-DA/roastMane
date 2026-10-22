package org.eclipse.jetty.util;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class MultiPartWriter extends FilterWriter {
   private static final String __CRLF = "\r\n";
   private static final String __DASHDASH = "--";
   public static final String MULTIPART_MIXED = "multipart/mixed";
   public static final String MULTIPART_X_MIXED_REPLACE = "multipart/x-mixed-replace";
   private String boundary;
   private boolean inPart = false;

   public MultiPartWriter(Writer out) throws IOException {
      super(out);
      this.boundary = "jetty" + System.identityHashCode(this) + Long.toString(System.currentTimeMillis(), 36);
      this.inPart = false;
   }

   @Override
   public void close() throws IOException {
      try {
         if (this.inPart) {
            this.out.write("\r\n");
         }

         this.out.write("--");
         this.out.write(this.boundary);
         this.out.write("--");
         this.out.write("\r\n");
         this.inPart = false;
      } finally {
         super.close();
      }
   }

   public String getBoundary() {
      return this.boundary;
   }

   public void startPart(String contentType) throws IOException {
      if (this.inPart) {
         this.out.write("\r\n");
      }

      this.out.write("--");
      this.out.write(this.boundary);
      this.out.write("\r\n");
      this.out.write("Content-Type: ");
      this.out.write(contentType);
      this.out.write("\r\n");
      this.out.write("\r\n");
      this.inPart = true;
   }

   public void startPart(String contentType, String[] headers) throws IOException {
      if (this.inPart) {
         this.out.write("\r\n");
      }

      this.out.write("--");
      this.out.write(this.boundary);
      this.out.write("\r\n");
      this.out.write("Content-Type: ");
      this.out.write(contentType);
      this.out.write("\r\n");

      for (int i = 0; headers != null && i < headers.length; i++) {
         this.out.write(headers[i]);
         this.out.write("\r\n");
      }

      this.out.write("\r\n");
      this.inPart = true;
   }

   public void endPart() throws IOException {
      if (this.inPart) {
         this.out.write("\r\n");
      }

      this.inPart = false;
   }
}
