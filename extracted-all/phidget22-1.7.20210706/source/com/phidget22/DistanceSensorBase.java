package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DistanceSensorBase extends Phidget {
   private List<DistanceSensorDistanceChangeListener> distanceChangeListeners = new ArrayList<>();
   private long nativeDistanceChangeHandler = 0L;
   private List<DistanceSensorSonarReflectionsUpdateListener> sonarReflectionsUpdateListeners = new ArrayList<>();
   private long nativeSonarReflectionsUpdateHandler = 0L;

   DistanceSensorBase(long var1) {
      super(var1);
   }

   public DistanceSensorBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableDistanceChangeEvents(var1 && this.distanceChangeListeners.size() > 0);
      this.enableSonarReflectionsUpdateEvents(var1 && this.sonarReflectionsUpdateListeners.size() > 0);
   }

   public final void addDistanceChangeListener(DistanceSensorDistanceChangeListener var1) {
      synchronized (this.distanceChangeListeners) {
         this.distanceChangeListeners.add(var1);
         this.enableDistanceChangeEvents(true);
      }
   }

   public final void removeDistanceChangeListener(DistanceSensorDistanceChangeListener var1) {
      synchronized (this.distanceChangeListeners) {
         this.distanceChangeListeners.remove(var1);
         this.enableDistanceChangeEvents(this.distanceChangeListeners.size() > 0);
      }
   }

   private void fireDistanceChange(DistanceSensorDistanceChangeEvent var1) {
      synchronized (this.distanceChangeListeners) {
         Iterator var3 = this.distanceChangeListeners.iterator();

         while (var3.hasNext()) {
            ((DistanceSensorDistanceChangeListener)var3.next()).onDistanceChange(var1);
         }
      }
   }

   private native void enableDistanceChangeEvents(boolean var1);

   public final void addSonarReflectionsUpdateListener(DistanceSensorSonarReflectionsUpdateListener var1) {
      synchronized (this.sonarReflectionsUpdateListeners) {
         this.sonarReflectionsUpdateListeners.add(var1);
         this.enableSonarReflectionsUpdateEvents(true);
      }
   }

   public final void removeSonarReflectionsUpdateListener(DistanceSensorSonarReflectionsUpdateListener var1) {
      synchronized (this.sonarReflectionsUpdateListeners) {
         this.sonarReflectionsUpdateListeners.remove(var1);
         this.enableSonarReflectionsUpdateEvents(this.sonarReflectionsUpdateListeners.size() > 0);
      }
   }

   private void fireSonarReflectionsUpdate(DistanceSensorSonarReflectionsUpdateEvent var1) {
      synchronized (this.sonarReflectionsUpdateListeners) {
         Iterator var3 = this.sonarReflectionsUpdateListeners.iterator();

         while (var3.hasNext()) {
            ((DistanceSensorSonarReflectionsUpdateListener)var3.next()).onSonarReflectionsUpdate(var1);
         }
      }
   }

   private native void enableSonarReflectionsUpdateEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native int getDistance() throws PhidgetException;

   public native int getMinDistance() throws PhidgetException;

   public native int getMaxDistance() throws PhidgetException;

   public native int getDistanceChangeTrigger() throws PhidgetException;

   public native void setDistanceChangeTrigger(int var1) throws PhidgetException;

   public native int getMinDistanceChangeTrigger() throws PhidgetException;

   public native int getMaxDistanceChangeTrigger() throws PhidgetException;

   public native boolean getSonarQuietMode() throws PhidgetException;

   public native void setSonarQuietMode(boolean var1) throws PhidgetException;

   public native DistanceSensorSonarReflections getSonarReflections() throws PhidgetException;
}
