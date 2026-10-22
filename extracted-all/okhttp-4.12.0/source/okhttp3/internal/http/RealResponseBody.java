package okhttp3.internal.http

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource

class RealResponseBody(contentTypeString: String?, contentLength: Long, source: BufferedSource) : ResponseBody {
   private final val contentLength: Long
   private final val contentTypeString: String?
   private final val source: BufferedSource

   init {
      this.contentTypeString = contentTypeString;
      this.contentLength = contentLength;
      this.source = source;
   }

   public override fun contentLength(): Long {
      return this.contentLength;
   }

   public override fun contentType(): MediaType? {
      return if (this.contentTypeString != null) MediaType.Companion.parse(this.contentTypeString) else null;
   }

   public override fun source(): BufferedSource {
      return this.source;
   }
}
