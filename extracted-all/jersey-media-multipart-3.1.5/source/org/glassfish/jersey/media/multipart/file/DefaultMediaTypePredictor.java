package org.glassfish.jersey.media.multipart.file;

import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.util.Locale;

public class DefaultMediaTypePredictor implements MediaTypePredictor {
   private static final DefaultMediaTypePredictor MEDIA_TYPE_PREDICTOR = new DefaultMediaTypePredictor();

   @Override
   public MediaType getMediaTypeFromFile(File file) {
      return DefaultMediaTypePredictor.CommonMediaTypes.getMediaTypeFromFile(file);
   }

   @Override
   public MediaType getMediaTypeFromFileName(String fileName) {
      return DefaultMediaTypePredictor.CommonMediaTypes.getMediaTypeFromFileName(fileName);
   }

   public static DefaultMediaTypePredictor getInstance() {
      return MEDIA_TYPE_PREDICTOR;
   }

   public static enum CommonMediaTypes {
      XML(".xml", MediaType.APPLICATION_XML_TYPE),
      JSON(".json", MediaType.APPLICATION_JSON_TYPE),
      TXT(".txt", MediaType.TEXT_PLAIN_TYPE),
      HTM(".htm", MediaType.TEXT_HTML_TYPE),
      HTML(".html", MediaType.TEXT_HTML_TYPE),
      PDF(".pdf", new MediaType("application", "pdf")),
      JPG(".jpg", new MediaType("image", "jpeg")),
      PNG(".png", new MediaType("image", "png")),
      GIF(".gif", new MediaType("image", "gif")),
      BMP(".bmp", new MediaType("image", "pdf")),
      TAR(".tar", new MediaType("application", "x-tar")),
      ZIP(".zip", new MediaType("application", "zip")),
      GZ(".gz", new MediaType("application", "x-gzip")),
      RAR(".rar", new MediaType("application", "x-rar")),
      MP3(".mp3", new MediaType("audio", "mpeg")),
      WAV(".wav", new MediaType("audio", "x-wave")),
      AVI(".avi", new MediaType("video", "x-msvideo")),
      MPEG(".mpeg", new MediaType("video", "mpeg"));

      private final String extension;
      private final MediaType mediaType;

      private CommonMediaTypes(String extension, MediaType mediaType) {
         if (extension != null && extension.startsWith(".") && mediaType != null) {
            this.extension = extension;
            this.mediaType = mediaType;
         } else {
            throw new IllegalArgumentException();
         }
      }

      public String getExtension() {
         return this.extension;
      }

      public MediaType getMediaType() {
         return this.mediaType;
      }

      public static MediaType getMediaTypeFromFile(File file) {
         if (file == null) {
            return null;
         } else {
            String fileName = file.getName();
            return getMediaTypeFromFileName(fileName);
         }
      }

      public static MediaType getMediaTypeFromFileName(String fileName) {
         if (fileName == null) {
            return null;
         } else {
            DefaultMediaTypePredictor.CommonMediaTypes[] types = values();
            if (types != null && types.length > 0) {
               for (DefaultMediaTypePredictor.CommonMediaTypes type : types) {
                  if (fileName.toLowerCase(Locale.ROOT).endsWith(type.getExtension())) {
                     return type.getMediaType();
                  }
               }
            }

            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
         }
      }
   }
}
