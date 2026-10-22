package com.phidget22;

import java.util.Comparator;

public class Phidget extends PhidgetBase implements Comparator<Phidget> {
   private String _phidgetidstr;

   Phidget(long var1) {
      super(var1);
   }

   @Override
   public String toString() {
      try {
         return this.getDescription();
      } catch (PhidgetException var2) {
         return "[" + this.getClass().getName() + "]";
      }
   }

   public String getPhidgetIDString() {
      if (this._phidgetidstr != null) {
         return this._phidgetidstr;
      } else {
         try {
            StringBuffer var1 = new StringBuffer();
            if (this.getIsLocal()) {
               var1.append('L');
            } else {
               var1.append('N');
            }

            if (this.getIsChannel()) {
               var1.append('_').append('C').append(this.getChannel());
               var1.append('_').append(this.getChannelClass());
            } else {
               var1.append('_').append('D').append('_').append(this.getDeviceClass());
               var1.append('_').append(this.getDeviceID());
               if (this.getIsHubPortDevice()) {
                  var1.append('_').append('H');
               }
            }

            var1.append('_').append(this.getHubPort());
            var1.append('_').append(this.getDeviceSerialNumber());
            this._phidgetidstr = var1.toString();
            return this._phidgetidstr;
         } catch (PhidgetException var3) {
            return "_invalid_a_phidget_";
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         return !(var1 instanceof Phidget) ? false : this.hashCode() == var1.hashCode();
      }
   }

   @Override
   public int hashCode() {
      return this.getPhidgetIDString().hashCode();
   }

   public int compare(Phidget var1, Phidget var2) {
      try {
         if (var1.getIsLocal()) {
            if (!var2.getIsLocal()) {
               return 1;
            }
         } else if (var2.getIsLocal()) {
            return -1;
         }

         int var3 = var1.getDeviceSerialNumber() - var2.getDeviceSerialNumber();
         if (var3 != 0) {
            return var3;
         } else {
            int var4 = var1.getHubPort() - var2.getHubPort();
            if (var4 != 0) {
               return var4;
            } else {
               if (var1.getIsChannel()) {
                  if (!var2.getIsChannel()) {
                     return -1;
                  }

                  int var5 = var1.getChannel() - var2.getChannel();
                  if (var5 != 0) {
                     return var5;
                  }

                  int var6 = var1.getChannelClassName().compareTo(var2.getChannelClassName());
                  if (var6 != 0) {
                     return var6;
                  }
               } else {
                  if (var2.getIsChannel()) {
                     return 1;
                  }

                  int var8 = var1.getDeviceClassName().compareTo(var2.getDeviceClassName());
                  if (var8 != 0) {
                     return var8;
                  }

                  int var9 = var1.getDeviceID().ordinal() - var2.getDeviceID().ordinal();
                  if (var9 != 0) {
                     return var9;
                  }

                  if (var1.getIsHubPortDevice()) {
                     if (!var2.getIsHubPortDevice()) {
                        return -1;
                     }
                  } else if (var2.getIsHubPortDevice()) {
                     return 1;
                  }
               }

               return 0;
            }
         }
      } catch (PhidgetException var7) {
         return -1;
      }
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         this.deleteHandle();
         super.handle = 0L;
      } finally {
         super.finalize();
      }
   }

   private native String getDescription() throws PhidgetException;

   private native void deleteHandle() throws PhidgetException;
}
