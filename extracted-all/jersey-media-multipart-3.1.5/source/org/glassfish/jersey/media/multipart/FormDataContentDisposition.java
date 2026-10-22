package org.glassfish.jersey.media.multipart;

import java.text.ParseException;
import java.util.Date;
import org.glassfish.jersey.message.internal.HttpHeaderReader;

public class FormDataContentDisposition extends ContentDisposition {
   private final String name;

   protected FormDataContentDisposition(String type, String name, String fileName, Date creationDate, Date modificationDate, Date readDate, long size) {
      super(type, fileName, creationDate, modificationDate, readDate, size);
      this.name = name;
      if (!"form-data".equalsIgnoreCase(this.getType())) {
         throw new IllegalArgumentException("The content disposition type is not equal to form-data");
      } else if (name == null) {
         throw new IllegalArgumentException("The name parameter is not present");
      }
   }

   public FormDataContentDisposition(String header) throws ParseException {
      this(header, false);
   }

   public FormDataContentDisposition(String header, boolean fileNameFix) throws ParseException {
      this(HttpHeaderReader.newInstance(header), fileNameFix);
   }

   public FormDataContentDisposition(HttpHeaderReader reader, boolean fileNameFix) throws ParseException {
      super(reader, fileNameFix);
      if (!"form-data".equalsIgnoreCase(this.getType())) {
         throw new IllegalArgumentException("The content disposition type is not equal to form-data");
      } else {
         this.name = this.getParameters().get("name");
         if (this.name == null) {
            throw new IllegalArgumentException("The name parameter is not present");
         }
      }
   }

   public String getName() {
      return this.name;
   }

   @Override
   protected StringBuilder toStringBuffer() {
      StringBuilder sb = super.toStringBuffer();
      this.addStringParameter(sb, "name", this.name);
      return sb;
   }

   public static FormDataContentDisposition.FormDataContentDispositionBuilder name(String name) {
      return new FormDataContentDisposition.FormDataContentDispositionBuilder(name);
   }

   public static class FormDataContentDispositionBuilder
      extends ContentDisposition.ContentDispositionBuilder<FormDataContentDisposition.FormDataContentDispositionBuilder, FormDataContentDisposition> {
      private final String name;

      FormDataContentDispositionBuilder(String name) {
         super("form-data");
         this.name = name;
      }

      public FormDataContentDisposition build() {
         return new FormDataContentDisposition(this.type, this.name, this.fileName, this.creationDate, this.modificationDate, this.readDate, this.size);
      }
   }
}
