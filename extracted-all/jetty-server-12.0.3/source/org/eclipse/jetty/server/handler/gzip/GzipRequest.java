package org.eclipse.jetty.server.handler.gzip;

import java.nio.ByteBuffer;
import java.util.ListIterator;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.GZIPContentDecoder;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.io.content.ContentSourceTransformer;
import org.eclipse.jetty.server.Components;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.compression.InflaterPool;

public class GzipRequest extends Request.Wrapper {
   private static final HttpField X_CE_GZIP = new PreEncodedHttpField("X-Content-Encoding", "gzip");
   private static final InflaterPool __inflaterPool = new InflaterPool(-1, true);
   private final HttpFields _fields;
   private GzipRequest.Decoder _decoder;
   private GzipRequest.GzipTransformer _gzipTransformer;

   public GzipRequest(Request request, int inflateBufferSize) {
      super(request);
      this._fields = this.updateRequestFields(request, inflateBufferSize > 0);
      if (inflateBufferSize > 0) {
         Components components = this.getComponents();
         this._decoder = new GzipRequest.Decoder(__inflaterPool, components.getByteBufferPool(), inflateBufferSize);
         this._gzipTransformer = new GzipRequest.GzipTransformer(this.getWrapped());
      }
   }

   private HttpFields updateRequestFields(Request request, boolean inflatable) {
      HttpFields fields = request.getHeaders();
      Mutable newFields = HttpFields.build(fields);
      boolean contentEncodingSeen = false;
      ListIterator<HttpField> i = newFields.listIterator(newFields.size());

      while (i.hasPrevious()) {
         HttpField field = i.previous();
         HttpHeader header = field.getHeader();
         if (header != null) {
            switch (header) {
               case CONTENT_ENCODING:
                  if (inflatable && !contentEncodingSeen) {
                     contentEncodingSeen = true;
                     if (field.getValue().equalsIgnoreCase("gzip")) {
                        i.set(X_CE_GZIP);
                     } else if (field.containsLast("gzip")) {
                        String v = field.getValue();
                        v = v.substring(0, v.lastIndexOf(44));
                        i.set(new HttpField(HttpHeader.CONTENT_ENCODING, v));
                        i.add(X_CE_GZIP);
                     }
                  }
                  break;
               case IF_MATCH:
               case IF_NONE_MATCH:
                  String etags = field.getValue();
                  String etagsNoSuffix = CompressedContentFormat.GZIP.stripSuffixes(etags);
                  if (!etagsNoSuffix.equals(etags)) {
                     i.set(new HttpField(field.getHeader(), etagsNoSuffix));
                     request.setAttribute("o.e.j.s.h.gzip.GzipHandler.etag", etags);
                  }
                  break;
               case CONTENT_LENGTH:
                  if (inflatable) {
                     i.set(new HttpField("X-Content-Length", field.getValue()));
                  }
            }
         }
      }

      return newFields.asImmutable();
   }

   @Override
   public HttpFields getHeaders() {
      return this._fields == null ? super.getHeaders() : this._fields;
   }

   @Override
   public Chunk read() {
      return this._gzipTransformer != null ? this._gzipTransformer.read() : super.read();
   }

   @Override
   public void demand(Runnable demandCallback) {
      if (this._gzipTransformer != null) {
         this._gzipTransformer.demand(demandCallback);
      } else {
         super.demand(demandCallback);
      }
   }

   void destroy() {
      if (this._decoder != null) {
         this._decoder.destroy();
      }
   }

   private static class Decoder extends GZIPContentDecoder {
      private RetainableByteBuffer _decoded;

      private Decoder(InflaterPool inflaterPool, ByteBufferPool bufferPool, int bufferSize) {
         super(inflaterPool, bufferPool, bufferSize);
      }

      public RetainableByteBuffer decode(Chunk chunk) {
         this.decodeChunks(chunk.getByteBuffer());
         RetainableByteBuffer decoded = this._decoded;
         this._decoded = null;
         return decoded;
      }

      protected boolean decodedChunk(RetainableByteBuffer decoded) {
         decoded.retain();
         this._decoded = decoded;
         return true;
      }

      public void decodeChunks(ByteBuffer compressed) {
         this._decoded = null;
         super.decodeChunks(compressed);
      }
   }

   private class GzipTransformer extends ContentSourceTransformer {
      private Chunk _chunk;

      public GzipTransformer(Source source) {
         super(source);
      }

      protected Chunk transform(Chunk inputChunk) {
         boolean retain = this._chunk == null;
         if (this._chunk == null) {
            this._chunk = inputChunk;
         }

         if (this._chunk == null) {
            return null;
         } else if (Chunk.isFailure(this._chunk)) {
            return this._chunk;
         } else if (this._chunk.isLast() && !this._chunk.hasRemaining()) {
            return Chunk.EOF;
         } else {
            if (retain) {
               this._chunk.retain();
            }

            RetainableByteBuffer decodedBuffer = GzipRequest.this._decoder.decode(this._chunk);
            if (decodedBuffer != null && decodedBuffer.hasRemaining()) {
               return Chunk.from(decodedBuffer.getByteBuffer(), this._chunk.isLast() && !this._chunk.hasRemaining(), decodedBuffer::release);
            } else {
               if (decodedBuffer != null) {
                  decodedBuffer.release();
               }

               Chunk result = this._chunk.isLast() ? Chunk.EOF : null;
               this._chunk.release();
               this._chunk = null;
               return result;
            }
         }
      }
   }
}
