package com.phidget22;

public class NetServerAddedEvent {
   Net Source;
   PhidgetServer Server;
   long Kv;

   public NetServerAddedEvent(Net var1, PhidgetServer var2, long var3) {
      this.Source = var1;
      this.Server = var2;
      this.Kv = var3;
   }

   public Net getSource() {
      return this.Source;
   }

   public PhidgetServer getServer() {
      return this.Server;
   }

   public long getKv() {
      return this.Kv;
   }

   @Override
   public String toString() {
      return "[NetServerAddedEvent](Source: " + this.Source + ", Server: " + this.Server + ", Kv: " + this.Kv + ")";
   }
}
