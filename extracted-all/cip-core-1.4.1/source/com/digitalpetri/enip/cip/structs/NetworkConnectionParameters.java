package com.digitalpetri.enip.cip.structs;

public class NetworkConnectionParameters {
   private final int connectionSize;
   private final NetworkConnectionParameters.SizeType sizeType;
   private final NetworkConnectionParameters.Priority priority;
   private final NetworkConnectionParameters.ConnectionType connectionType;
   private final boolean redundantOwner;

   public NetworkConnectionParameters(
      int connectionSize,
      NetworkConnectionParameters.SizeType sizeType,
      NetworkConnectionParameters.Priority priority,
      NetworkConnectionParameters.ConnectionType connectionType,
      boolean redundantOwner
   ) {
      this.connectionSize = connectionSize;
      this.sizeType = sizeType;
      this.priority = priority;
      this.connectionType = connectionType;
      this.redundantOwner = redundantOwner;
   }

   public int getConnectionSize() {
      return this.connectionSize;
   }

   public NetworkConnectionParameters.SizeType getSizeType() {
      return this.sizeType;
   }

   public NetworkConnectionParameters.Priority getPriority() {
      return this.priority;
   }

   public NetworkConnectionParameters.ConnectionType getConnectionType() {
      return this.connectionType;
   }

   public boolean isRedundantOwner() {
      return this.redundantOwner;
   }

   public static enum ConnectionType {
      Null,
      Multicast,
      PointToPoint,
      Reserved;
   }

   public static enum Priority {
      Low,
      High,
      Scheduled,
      Urgent;
   }

   public static enum SizeType {
      Fixed,
      Variable;
   }
}
