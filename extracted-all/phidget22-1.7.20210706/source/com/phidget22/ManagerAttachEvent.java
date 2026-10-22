package com.phidget22;

public class ManagerAttachEvent {
   Manager Source;
   Phidget Channel;

   public ManagerAttachEvent(Manager var1, Phidget var2) {
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
      return "[ManagerAttachEvent](Source: " + this.Source + ", Channel: " + this.Channel + ")";
   }
}
