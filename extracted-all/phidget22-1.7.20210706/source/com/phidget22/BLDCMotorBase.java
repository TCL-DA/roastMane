package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BLDCMotorBase extends Phidget {
   private List<BLDCMotorBrakingStrengthChangeListener> brakingStrengthChangeListeners = new ArrayList<>();
   private long nativeBrakingStrengthChangeHandler = 0L;
   private List<BLDCMotorPositionChangeListener> positionChangeListeners = new ArrayList<>();
   private long nativePositionChangeHandler = 0L;
   private List<BLDCMotorVelocityUpdateListener> velocityUpdateListeners = new ArrayList<>();
   private long nativeVelocityUpdateHandler = 0L;

   BLDCMotorBase(long var1) {
      super(var1);
   }

   public BLDCMotorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableBrakingStrengthChangeEvents(var1 && this.brakingStrengthChangeListeners.size() > 0);
      this.enablePositionChangeEvents(var1 && this.positionChangeListeners.size() > 0);
      this.enableVelocityUpdateEvents(var1 && this.velocityUpdateListeners.size() > 0);
   }

   public final void addBrakingStrengthChangeListener(BLDCMotorBrakingStrengthChangeListener var1) {
      synchronized (this.brakingStrengthChangeListeners) {
         this.brakingStrengthChangeListeners.add(var1);
         this.enableBrakingStrengthChangeEvents(true);
      }
   }

   public final void removeBrakingStrengthChangeListener(BLDCMotorBrakingStrengthChangeListener var1) {
      synchronized (this.brakingStrengthChangeListeners) {
         this.brakingStrengthChangeListeners.remove(var1);
         this.enableBrakingStrengthChangeEvents(this.brakingStrengthChangeListeners.size() > 0);
      }
   }

   private void fireBrakingStrengthChange(BLDCMotorBrakingStrengthChangeEvent var1) {
      synchronized (this.brakingStrengthChangeListeners) {
         Iterator var3 = this.brakingStrengthChangeListeners.iterator();

         while (var3.hasNext()) {
            ((BLDCMotorBrakingStrengthChangeListener)var3.next()).onBrakingStrengthChange(var1);
         }
      }
   }

   private native void enableBrakingStrengthChangeEvents(boolean var1);

   public final void addPositionChangeListener(BLDCMotorPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.add(var1);
         this.enablePositionChangeEvents(true);
      }
   }

   public final void removePositionChangeListener(BLDCMotorPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.remove(var1);
         this.enablePositionChangeEvents(this.positionChangeListeners.size() > 0);
      }
   }

   private void firePositionChange(BLDCMotorPositionChangeEvent var1) {
      synchronized (this.positionChangeListeners) {
         Iterator var3 = this.positionChangeListeners.iterator();

         while (var3.hasNext()) {
            ((BLDCMotorPositionChangeListener)var3.next()).onPositionChange(var1);
         }
      }
   }

   private native void enablePositionChangeEvents(boolean var1);

   public final void addVelocityUpdateListener(BLDCMotorVelocityUpdateListener var1) {
      synchronized (this.velocityUpdateListeners) {
         this.velocityUpdateListeners.add(var1);
         this.enableVelocityUpdateEvents(true);
      }
   }

   public final void removeVelocityUpdateListener(BLDCMotorVelocityUpdateListener var1) {
      synchronized (this.velocityUpdateListeners) {
         this.velocityUpdateListeners.remove(var1);
         this.enableVelocityUpdateEvents(this.velocityUpdateListeners.size() > 0);
      }
   }

   private void fireVelocityUpdate(BLDCMotorVelocityUpdateEvent var1) {
      synchronized (this.velocityUpdateListeners) {
         Iterator var3 = this.velocityUpdateListeners.iterator();

         while (var3.hasNext()) {
            ((BLDCMotorVelocityUpdateListener)var3.next()).onVelocityUpdate(var1);
         }
      }
   }

   private native void enableVelocityUpdateEvents(boolean var1);

   public native double getAcceleration() throws PhidgetException;

   public native void setAcceleration(double var1) throws PhidgetException;

   public native double getMinAcceleration() throws PhidgetException;

   public native double getMaxAcceleration() throws PhidgetException;

   public native double getBrakingStrength() throws PhidgetException;

   public native double getMinBrakingStrength() throws PhidgetException;

   public native double getMaxBrakingStrength() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native void enableFailsafe(int var1) throws PhidgetException;

   public native int getMinFailsafeTime() throws PhidgetException;

   public native int getMaxFailsafeTime() throws PhidgetException;

   public native double getPosition() throws PhidgetException;

   public native double getMinPosition() throws PhidgetException;

   public native double getMaxPosition() throws PhidgetException;

   public native void addPositionOffset(double var1) throws PhidgetException;

   public native double getRescaleFactor() throws PhidgetException;

   public native void setRescaleFactor(double var1) throws PhidgetException;

   public native void resetFailsafe() throws PhidgetException;

   public native double getStallVelocity() throws PhidgetException;

   public native void setStallVelocity(double var1) throws PhidgetException;

   public native double getMinStallVelocity() throws PhidgetException;

   public native double getMaxStallVelocity() throws PhidgetException;

   public native double getTargetBrakingStrength() throws PhidgetException;

   public native void setTargetBrakingStrength(double var1) throws PhidgetException;

   public native double getTargetVelocity() throws PhidgetException;

   public native void setTargetVelocity(double var1, AsyncListener var3);

   public native void setTargetVelocity(double var1) throws PhidgetException;

   public native double getVelocity() throws PhidgetException;

   public native double getMinVelocity() throws PhidgetException;

   public native double getMaxVelocity() throws PhidgetException;
}
