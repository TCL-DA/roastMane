package org.eclipse.jetty.http;

import java.util.Objects;
import org.eclipse.jetty.util.StringUtil;

public class CompressedContentFormat {
   public static final CompressedContentFormat GZIP = new CompressedContentFormat("gzip", ".gz");
   public static final CompressedContentFormat BR = new CompressedContentFormat("br", ".br");
   public static final CompressedContentFormat[] NONE = new CompressedContentFormat[0];
   private final String _encoding;
   private final String _extension;
   private final String _etagSuffix;
   private final String _etagSuffixQuote;
   private final PreEncodedHttpField _contentEncoding;

   public CompressedContentFormat(String encoding, String extension) {
      this._encoding = StringUtil.asciiToLowerCase(encoding);
      this._extension = StringUtil.asciiToLowerCase(extension);
      this._etagSuffix = StringUtil.isEmpty(EtagUtils.ETAG_SEPARATOR) ? "" : EtagUtils.ETAG_SEPARATOR + this._encoding;
      this._etagSuffixQuote = this._etagSuffix + "\"";
      this._contentEncoding = new PreEncodedHttpField(HttpHeader.CONTENT_ENCODING, this._encoding);
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof CompressedContentFormat ccf)
         ? false
         : Objects.equals(this._encoding, ccf._encoding) && Objects.equals(this._extension, ccf._extension);
   }

   public String getEncoding() {
      return this._encoding;
   }

   public String getExtension() {
      return this._extension;
   }

   public String getEtagSuffix() {
      return this._etagSuffix;
   }

   public HttpField getContentEncoding() {
      return this._contentEncoding;
   }

   public String etag(String etag) {
      if (StringUtil.isEmpty(EtagUtils.ETAG_SEPARATOR)) {
         return etag;
      } else {
         int end = etag.length() - 1;
         return etag.charAt(end) == '"' ? etag.substring(0, end) + this._etagSuffixQuote : etag + this._etagSuffix;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this._encoding, this._extension);
   }

   public String stripSuffixes(String etagsList) {
      if (StringUtil.isEmpty(EtagUtils.ETAG_SEPARATOR)) {
         return etagsList;
      } else {
         while (true) {
            int i = etagsList.lastIndexOf(this._etagSuffix);
            if (i < 0) {
               return etagsList;
            }

            etagsList = etagsList.substring(0, i) + etagsList.substring(i + this._etagSuffix.length());
         }
      }
   }

   @Override
   public String toString() {
      return this._encoding;
   }
}
