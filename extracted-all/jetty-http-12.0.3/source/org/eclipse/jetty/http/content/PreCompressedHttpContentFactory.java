package org.eclipse.jetty.http.content;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jetty.http.CompressedContentFormat;

public class PreCompressedHttpContentFactory implements HttpContent.Factory {
   private final HttpContent.Factory _factory;
   private final List<CompressedContentFormat> _preCompressedFormats;

   public PreCompressedHttpContentFactory(HttpContent.Factory factory, CompressedContentFormat[] preCompressedFormats) {
      this(factory, Arrays.asList(preCompressedFormats));
   }

   public PreCompressedHttpContentFactory(HttpContent.Factory factory, List<CompressedContentFormat> preCompressedFormats) {
      this._factory = factory;
      this._preCompressedFormats = preCompressedFormats;
   }

   @Override
   public HttpContent getContent(String pathInContext) throws IOException {
      HttpContent content = this._factory.getContent(pathInContext);
      if (content == null) {
         return null;
      } else {
         Set<CompressedContentFormat> compressedFormats = new HashSet<>();

         for (CompressedContentFormat contentFormat : this._preCompressedFormats) {
            HttpContent preCompressedContent = this._factory.getContent(pathInContext + contentFormat.getExtension());
            if (preCompressedContent != null) {
               compressedFormats.add(contentFormat);
            }
         }

         return new PreCompressedHttpContentFactory.CompressedFormatsHttpContent(content, compressedFormats);
      }
   }

   @Override
   public String toString() {
      return "%s@%x[%s,%s]".formatted(this.getClass().getSimpleName(), this.hashCode(), this._factory, this._preCompressedFormats);
   }

   private static class CompressedFormatsHttpContent extends HttpContent.Wrapper {
      private final Set<CompressedContentFormat> compressedFormats;

      public CompressedFormatsHttpContent(HttpContent content, Set<CompressedContentFormat> compressedFormats) {
         super(content);
         this.compressedFormats = compressedFormats;
      }

      @Override
      public Set<CompressedContentFormat> getPreCompressedContentFormats() {
         return this.compressedFormats;
      }
   }
}
