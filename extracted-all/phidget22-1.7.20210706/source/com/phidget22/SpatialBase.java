package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpatialBase extends Phidget {
   private List<SpatialAlgorithmDataListener> algorithmDataListeners = new ArrayList<>();
   private long nativeAlgorithmDataHandler = 0L;
   private List<SpatialSpatialDataListener> spatialDataListeners = new ArrayList<>();
   private long nativeSpatialDataHandler = 0L;

   SpatialBase(long var1) {
      super(var1);
   }

   public SpatialBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableAlgorithmDataEvents(var1 && this.algorithmDataListeners.size() > 0);
      this.enableSpatialDataEvents(var1 && this.spatialDataListeners.size() > 0);
   }

   public final void addAlgorithmDataListener(SpatialAlgorithmDataListener var1) {
      synchronized (this.algorithmDataListeners) {
         this.algorithmDataListeners.add(var1);
         this.enableAlgorithmDataEvents(true);
      }
   }

   public final void removeAlgorithmDataListener(SpatialAlgorithmDataListener var1) {
      synchronized (this.algorithmDataListeners) {
         this.algorithmDataListeners.remove(var1);
         this.enableAlgorithmDataEvents(this.algorithmDataListeners.size() > 0);
      }
   }

   private void fireAlgorithmData(SpatialAlgorithmDataEvent var1) {
      synchronized (this.algorithmDataListeners) {
         Iterator var3 = this.algorithmDataListeners.iterator();

         while (var3.hasNext()) {
            ((SpatialAlgorithmDataListener)var3.next()).onAlgorithmData(var1);
         }
      }
   }

   private native void enableAlgorithmDataEvents(boolean var1);

   public final void addSpatialDataListener(SpatialSpatialDataListener var1) {
      synchronized (this.spatialDataListeners) {
         this.spatialDataListeners.add(var1);
         this.enableSpatialDataEvents(true);
      }
   }

   public final void removeSpatialDataListener(SpatialSpatialDataListener var1) {
      synchronized (this.spatialDataListeners) {
         this.spatialDataListeners.remove(var1);
         this.enableSpatialDataEvents(this.spatialDataListeners.size() > 0);
      }
   }

   private void fireSpatialData(SpatialSpatialDataEvent var1) {
      synchronized (this.spatialDataListeners) {
         Iterator var3 = this.spatialDataListeners.iterator();

         while (var3.hasNext()) {
            ((SpatialSpatialDataListener)var3.next()).onSpatialData(var1);
         }
      }
   }

   private native void enableSpatialDataEvents(boolean var1);

   public native SpatialAlgorithm getAlgorithm() throws PhidgetException;

   public native void setAlgorithm(SpatialAlgorithm var1) throws PhidgetException;

   public native double getAlgorithmMagnetometerGain() throws PhidgetException;

   public native void setAlgorithmMagnetometerGain(double var1) throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native boolean getHeatingEnabled() throws PhidgetException;

   public native void setHeatingEnabled(boolean var1) throws PhidgetException;

   public native void setMagnetometerCorrectionParameters(
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23,
      double var25
   ) throws PhidgetException;

   public native void resetMagnetometerCorrectionParameters() throws PhidgetException;

   public native void saveMagnetometerCorrectionParameters() throws PhidgetException;

   public native void zeroAlgorithm() throws PhidgetException;

   public native void zeroGyro() throws PhidgetException;
}
