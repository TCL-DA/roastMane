package com.phidget22;

public class PhidgetServer {
   public String name;
   public String stype;
   public ServerType type;
   public int flags;
   public String addr;
   public String host;
   public int port;

   @Override
   public String toString() {
      return "[PhidgetServer](name: "
         + this.name
         + ", stype: "
         + this.stype
         + ", type: "
         + this.type
         + ", flags: "
         + this.flags
         + ", addr: "
         + this.addr
         + ", host: "
         + this.host
         + ", port: "
         + this.port
         + ")";
   }
}
