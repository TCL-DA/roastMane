package com.digitalpetri.enip.cip.structs;

import com.digitalpetri.enip.cip.epath.EPath;
import com.digitalpetri.enip.util.TimeoutCalculator;
import io.netty.buffer.ByteBuf;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class ForwardOpenRequest {
   private final Duration timeout;
   private final int o2tConnectionId;
   private final int t2oConnectionId;
   private final int connectionSerialNumber;
   private final int vendorId;
   private final long vendorSerialNumber;
   private final int connectionTimeoutMultiplier;
   private final EPath.PaddedEPath connectionPath;
   private final Duration o2tRpi;
   private final NetworkConnectionParameters o2tParameters;
   private final Duration t2oRpi;
   private final NetworkConnectionParameters t2oParameters;
   private final int transportClassAndTrigger;

   public ForwardOpenRequest(
      Duration timeout,
      int o2tConnectionId,
      int t2oConnectionId,
      int connectionSerialNumber,
      int vendorId,
      long vendorSerialNumber,
      int connectionTimeoutMultiplier,
      EPath.PaddedEPath connectionPath,
      Duration o2tRpi,
      NetworkConnectionParameters o2tParameters,
      Duration t2oRpi,
      NetworkConnectionParameters t2oParameters,
      int transportClassAndTrigger
   ) {
      this.timeout = timeout;
      this.o2tConnectionId = o2tConnectionId;
      this.t2oConnectionId = t2oConnectionId;
      this.connectionSerialNumber = connectionSerialNumber;
      this.vendorId = vendorId;
      this.vendorSerialNumber = vendorSerialNumber;
      this.connectionTimeoutMultiplier = connectionTimeoutMultiplier;
      this.connectionPath = connectionPath;
      this.o2tRpi = o2tRpi;
      this.o2tParameters = o2tParameters;
      this.t2oRpi = t2oRpi;
      this.t2oParameters = t2oParameters;
      this.transportClassAndTrigger = transportClassAndTrigger;
   }

   public Duration getTimeout() {
      return this.timeout;
   }

   public int getO2tConnectionId() {
      return this.o2tConnectionId;
   }

   public int getT2oConnectionId() {
      return this.t2oConnectionId;
   }

   public int getConnectionSerialNumber() {
      return this.connectionSerialNumber;
   }

   public int getVendorId() {
      return this.vendorId;
   }

   public long getVendorSerialNumber() {
      return this.vendorSerialNumber;
   }

   public int getConnectionTimeoutMultiplier() {
      return this.connectionTimeoutMultiplier;
   }

   public EPath.PaddedEPath getConnectionPath() {
      return this.connectionPath;
   }

   public Duration getO2tRpi() {
      return this.o2tRpi;
   }

   public NetworkConnectionParameters getO2tParameters() {
      return this.o2tParameters;
   }

   public Duration getT2oRpi() {
      return this.t2oRpi;
   }

   public NetworkConnectionParameters getT2oParameters() {
      return this.t2oParameters;
   }

   public int getTransportClassAndTrigger() {
      return this.transportClassAndTrigger;
   }

   public static ByteBuf encode(ForwardOpenRequest request, ByteBuf buffer) {
      int priorityAndTimeoutBytes = TimeoutCalculator.calculateTimeoutBytes(request.timeout);
      buffer.writeByte(priorityAndTimeoutBytes >> 8 & 0xFF);
      buffer.writeByte(priorityAndTimeoutBytes & 0xFF);
      buffer.writeInt(0);
      buffer.writeInt(request.t2oConnectionId);
      buffer.writeShort(request.connectionSerialNumber);
      buffer.writeShort(request.vendorId);
      buffer.writeInt((int)request.vendorSerialNumber);
      buffer.writeByte(request.connectionTimeoutMultiplier);
      buffer.writeZero(3);
      buffer.writeInt((int)TimeUnit.MICROSECONDS.convert(request.o2tRpi.toMillis(), TimeUnit.MILLISECONDS));
      buffer.writeShort(parametersToInt(request.o2tParameters));
      buffer.writeInt((int)TimeUnit.MICROSECONDS.convert(request.t2oRpi.toMillis(), TimeUnit.MILLISECONDS));
      buffer.writeShort(parametersToInt(request.t2oParameters));
      buffer.writeByte(request.transportClassAndTrigger);
      EPath.PaddedEPath.encode(request.connectionPath, buffer);
      return buffer;
   }

   private static int parametersToInt(NetworkConnectionParameters parameters) {
      int parametersInt = parameters.getConnectionSize() & 511;
      parametersInt |= parameters.getSizeType().ordinal() << 9;
      parametersInt |= parameters.getPriority().ordinal() << 10;
      parametersInt |= parameters.getConnectionType().ordinal() << 13;
      if (parameters.isRedundantOwner()) {
         parametersInt |= 32768;
      }

      return parametersInt;
   }
}
