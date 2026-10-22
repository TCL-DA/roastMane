package com.digitalpetri.enip.cip.epath;

import io.netty.buffer.ByteBuf;
import java.util.List;

public abstract class EPath {
   private final EPathSegment[] segments;

   protected EPath(EPathSegment[] segments) {
      this.segments = segments;
   }

   public EPathSegment[] getSegments() {
      return this.segments;
   }

   public abstract boolean isPadded();

   public static ByteBuf encode(EPath path, ByteBuf buffer) {
      int lengthStartIndex = buffer.writerIndex();
      buffer.writeByte(0);
      int dataStartIndex = buffer.writerIndex();

      for (EPathSegment segment : path.getSegments()) {
         if (segment instanceof LogicalSegment) {
            LogicalSegment.encode((LogicalSegment<?>)segment, path.isPadded(), buffer);
         } else if (segment instanceof PortSegment) {
            PortSegment.encode((PortSegment)segment, path.isPadded(), buffer);
         } else {
            if (!(segment instanceof DataSegment)) {
               throw new RuntimeException("no encoder for " + segment.getClass().getSimpleName());
            }

            DataSegment.encode((DataSegment<?>)segment, path.isPadded(), buffer);
         }
      }

      int bytesWritten = buffer.writerIndex() - dataStartIndex;
      int wordsWritten = bytesWritten / 2;
      buffer.markWriterIndex();
      buffer.writerIndex(lengthStartIndex);
      buffer.writeByte(wordsWritten);
      buffer.resetWriterIndex();
      return buffer;
   }

   public static final class PaddedEPath extends EPath {
      public PaddedEPath(List<EPathSegment> segments) {
         this(segments.toArray(new EPathSegment[segments.size()]));
      }

      public PaddedEPath(EPathSegment... segments) {
         super(segments);
      }

      @Override
      public boolean isPadded() {
         return true;
      }

      public EPath.PaddedEPath append(EPath.PaddedEPath other) {
         int aLen = this.getSegments().length;
         int bLen = other.getSegments().length;
         EPathSegment[] newSegments = new EPathSegment[aLen + bLen];
         System.arraycopy(this.getSegments(), 0, newSegments, 0, aLen);
         System.arraycopy(other.getSegments(), 0, newSegments, aLen, bLen);
         return new EPath.PaddedEPath(newSegments);
      }
   }
}
