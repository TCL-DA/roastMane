package com.digitalpetri.enip.cip.structs;

import com.digitalpetri.enip.cip.epath.DataSegment;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.epath.EPathSegment;
import com.digitalpetri.enip.cip.epath.LogicalSegment;
import com.digitalpetri.enip.cip.epath.PortSegment;
import com.digitalpetri.enip.util.TimeoutCalculator;
import io.netty.buffer.ByteBuf;
import java.time.Duration;

public class ForwardCloseRequest {
   private final Duration connectionTimeout;
   private final int connectionSerialNumber;
   private final int originatorVendorId;
   private final long originatorSerialNumber;
   private final EPath.PaddedEPath connectionPath;

   public ForwardCloseRequest(
      Duration connectionTimeout, int connectionSerialNumber, int originatorVendorId, long originatorSerialNumber, EPath.PaddedEPath connectionPath
   ) {
      this.connectionTimeout = connectionTimeout;
      this.connectionSerialNumber = connectionSerialNumber;
      this.originatorVendorId = originatorVendorId;
      this.originatorSerialNumber = originatorSerialNumber;
      this.connectionPath = connectionPath;
   }

   public Duration getConnectionTimeout() {
      return this.connectionTimeout;
   }

   public int getConnectionSerialNumber() {
      return this.connectionSerialNumber;
   }

   public int getOriginatorVendorId() {
      return this.originatorVendorId;
   }

   public long getOriginatorSerialNumber() {
      return this.originatorSerialNumber;
   }

   public EPath.PaddedEPath getConnectionPath() {
      return this.connectionPath;
   }

   public static ByteBuf encode(ForwardCloseRequest request, ByteBuf buffer) {
      int priorityAndTimeoutBytes = TimeoutCalculator.calculateTimeoutBytes(request.getConnectionTimeout());
      buffer.writeByte(priorityAndTimeoutBytes >> 8 & 0xFF);
      buffer.writeByte(priorityAndTimeoutBytes & 0xFF);
      buffer.writeShort(request.getConnectionSerialNumber());
      buffer.writeShort(request.getOriginatorVendorId());
      buffer.writeInt((int)request.getOriginatorSerialNumber());
      encodeConnectionPath(request.getConnectionPath(), buffer);
      return buffer;
   }

   private static void encodeConnectionPath(EPath.PaddedEPath path, ByteBuf buffer) {
      int lengthStartIndex = buffer.writerIndex();
      buffer.writeByte(0);
      buffer.writeZero(1);
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
   }
}
