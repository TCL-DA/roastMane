package com.digitalpetri.enip.cip.services;

import com.digitalpetri.enip.cip.CipResponseException;
import com.digitalpetri.enip.cip.epath.DataSegment;
import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.cip.epath.EPathSegment;
import com.digitalpetri.enip.cip.epath.LogicalSegment;
import com.digitalpetri.enip.cip.epath.PortSegment;
import com.digitalpetri.enip.cip.structs.MessageRouterRequest;
import com.digitalpetri.enip.util.TimeoutCalculator;
import io.netty.buffer.ByteBuf;
import java.time.Duration;

public class UnconnectedSendService<T> implements CipService<T> {
   public static final int SERVICE_CODE = 82;
   private static final EPath.PaddedEPath CONNECTION_MANAGER_PATH = new EPath.PaddedEPath(new LogicalSegment.ClassId(6), new LogicalSegment.InstanceId(1));
   private final CipService<T> service;
   private final EPath.PaddedEPath connectionPath;
   private final Duration timeout;

   public UnconnectedSendService(CipService<T> service, EPath.PaddedEPath connectionPath, Duration timeout) {
      this.service = service;
      this.connectionPath = connectionPath;
      this.timeout = timeout;
   }

   @Override
   public void encodeRequest(ByteBuf buffer) {
      MessageRouterRequest request = new MessageRouterRequest(82, CONNECTION_MANAGER_PATH, this::encode);
      MessageRouterRequest.encode(request, buffer);
   }

   private ByteBuf encode(ByteBuf buffer) {
      int priorityAndTimeoutBytes = TimeoutCalculator.calculateTimeoutBytes(this.timeout);
      buffer.writeByte(priorityAndTimeoutBytes >> 8 & 0xFF);
      buffer.writeByte(priorityAndTimeoutBytes & 0xFF);
      int bytesWritten = this.encodeEmbeddedService(buffer);
      if (bytesWritten % 2 != 0) {
         buffer.writeByte(0);
      }

      this.encodeConnectionPath(buffer);
      return buffer;
   }

   private int encodeEmbeddedService(ByteBuf buffer) {
      int lengthStartIndex = buffer.writerIndex();
      buffer.writeShort(0);
      int messageStartIndex = buffer.writerIndex();
      this.service.encodeRequest(buffer);
      int bytesWritten = buffer.writerIndex() - messageStartIndex;
      buffer.markWriterIndex();
      buffer.writerIndex(lengthStartIndex);
      buffer.writeShort(bytesWritten);
      buffer.resetWriterIndex();
      return bytesWritten;
   }

   private void encodeConnectionPath(ByteBuf buffer) {
      int pathLengthStartIndex = buffer.writerIndex();
      buffer.writeByte(0);
      buffer.writeByte(0);
      int pathDataStartIndex = buffer.writerIndex();

      for (EPathSegment segment : this.connectionPath.getSegments()) {
         if (segment instanceof LogicalSegment) {
            LogicalSegment.encode((LogicalSegment<?>)segment, this.connectionPath.isPadded(), buffer);
         } else if (segment instanceof PortSegment) {
            PortSegment.encode((PortSegment)segment, this.connectionPath.isPadded(), buffer);
         } else {
            if (!(segment instanceof DataSegment)) {
               throw new RuntimeException("no encoder for " + segment.getClass().getSimpleName());
            }

            DataSegment.encode((DataSegment<?>)segment, this.connectionPath.isPadded(), buffer);
         }
      }

      int pathBytesWritten = buffer.writerIndex() - pathDataStartIndex;
      int wordsWritten = pathBytesWritten / 2;
      buffer.markWriterIndex();
      buffer.writerIndex(pathLengthStartIndex);
      buffer.writeByte(wordsWritten);
      buffer.resetWriterIndex();
   }

   @Override
   public T decodeResponse(ByteBuf buffer) throws CipResponseException, CipService.PartialResponseException {
      return this.service.decodeResponse(buffer);
   }
}
