package com.phidget22;

public class NetServerRemovedEvent {
   Net Source;
   PhidgetServer Server;

   public NetServerRemovedEvent(Net var1, PhidgetServer var2) {
      this.Source = var1;
      this.Server = var2;
   }

   public Net getSource() {
      return this.Source;
   }

   public PhidgetServer getServer() {
      return this.Server;
   }

   @Override
   public String toString() {
      return "[NetServerRemovedEvent](Source: " + this.Source + ", Server: " + this.Server + ")";
   }
}
