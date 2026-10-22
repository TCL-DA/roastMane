package com.phidget22;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class GPSBase extends Phidget {
   private List<GPSHeadingChangeListener> headingChangeListeners = new ArrayList<>();
   private long nativeHeadingChangeHandler = 0L;
   private List<GPSPositionChangeListener> positionChangeListeners = new ArrayList<>();
   private long nativePositionChangeHandler = 0L;
   private List<GPSPositionFixStateChangeListener> positionFixStateChangeListeners = new ArrayList<>();
   private long nativePositionFixStateChangeHandler = 0L;

   GPSBase(long var1) {
      super(var1);
   }

   public GPSBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableHeadingChangeEvents(var1 && this.headingChangeListeners.size() > 0);
      this.enablePositionChangeEvents(var1 && this.positionChangeListeners.size() > 0);
      this.enablePositionFixStateChangeEvents(var1 && this.positionFixStateChangeListeners.size() > 0);
   }

   public final void addHeadingChangeListener(GPSHeadingChangeListener var1) {
      synchronized (this.headingChangeListeners) {
         this.headingChangeListeners.add(var1);
         this.enableHeadingChangeEvents(true);
      }
   }

   public final void removeHeadingChangeListener(GPSHeadingChangeListener var1) {
      synchronized (this.headingChangeListeners) {
         this.headingChangeListeners.remove(var1);
         this.enableHeadingChangeEvents(this.headingChangeListeners.size() > 0);
      }
   }

   private void fireHeadingChange(GPSHeadingChangeEvent var1) {
      synchronized (this.headingChangeListeners) {
         Iterator var3 = this.headingChangeListeners.iterator();

         while (var3.hasNext()) {
            ((GPSHeadingChangeListener)var3.next()).onHeadingChange(var1);
         }
      }
   }

   private native void enableHeadingChangeEvents(boolean var1);

   public final void addPositionChangeListener(GPSPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.add(var1);
         this.enablePositionChangeEvents(true);
      }
   }

   public final void removePositionChangeListener(GPSPositionChangeListener var1) {
      synchronized (this.positionChangeListeners) {
         this.positionChangeListeners.remove(var1);
         this.enablePositionChangeEvents(this.positionChangeListeners.size() > 0);
      }
   }

   private void firePositionChange(GPSPositionChangeEvent var1) {
      synchronized (this.positionChangeListeners) {
         Iterator var3 = this.positionChangeListeners.iterator();

         while (var3.hasNext()) {
            ((GPSPositionChangeListener)var3.next()).onPositionChange(var1);
         }
      }
   }

   private native void enablePositionChangeEvents(boolean var1);

   public final void addPositionFixStateChangeListener(GPSPositionFixStateChangeListener var1) {
      synchronized (this.positionFixStateChangeListeners) {
         this.positionFixStateChangeListeners.add(var1);
         this.enablePositionFixStateChangeEvents(true);
      }
   }

   public final void removePositionFixStateChangeListener(GPSPositionFixStateChangeListener var1) {
      synchronized (this.positionFixStateChangeListeners) {
         this.positionFixStateChangeListeners.remove(var1);
         this.enablePositionFixStateChangeEvents(this.positionFixStateChangeListeners.size() > 0);
      }
   }

   private void firePositionFixStateChange(GPSPositionFixStateChangeEvent var1) {
      synchronized (this.positionFixStateChangeListeners) {
         Iterator var3 = this.positionFixStateChangeListeners.iterator();

         while (var3.hasNext()) {
            ((GPSPositionFixStateChangeListener)var3.next()).onPositionFixStateChange(var1);
         }
      }
   }

   private native void enablePositionFixStateChangeEvents(boolean var1);

   public native double getAltitude() throws PhidgetException;

   public native GPSDate getDate() throws PhidgetException;

   public native Calendar getDateAndTime() throws PhidgetException;

   public native double getHeading() throws PhidgetException;

   public native double getLatitude() throws PhidgetException;

   public native double getLongitude() throws PhidgetException;

   public native NMEAData getNMEAData() throws PhidgetException;

   public native boolean getPositionFixState() throws PhidgetException;

   public native GPSTime getTime() throws PhidgetException;

   public native double getVelocity() throws PhidgetException;
}
