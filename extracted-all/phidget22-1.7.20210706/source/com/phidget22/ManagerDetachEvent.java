package com.phidget22;

public class ManagerDetachEvent {
   Manager Source;
   Phidget Channel;

   public ManagerDetachEvent(Manager var1, Phidget var2) {
      this.Source = var1;
      this.Channel = var2;
   }

   public Manager getSource() {
      return this.Source;
   }

   public Phidget getChannel() {
      return this.Channel;
   }

   @Override
   public String toString() {
      return "[ManagerDetachEvent](Source: " + this.Source + ", Channel: " + this.Channel + ")";
   }
}
