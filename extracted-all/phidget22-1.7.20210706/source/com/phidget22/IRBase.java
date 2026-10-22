package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IRBase extends Phidget {
   private List<IRCodeListener> codeListeners = new ArrayList<>();
   private long nativeCodeHandler = 0L;
   private List<IRLearnListener> learnListeners = new ArrayList<>();
   private long nativeLearnHandler = 0L;
   private List<IRRawDataListener> rawDataListeners = new ArrayList<>();
   private long nativeRawDataHandler = 0L;
   public static final int RAW_DATA_LONG_SPACE = -1;
   public static final int MAX_CODE_BIT_COUNT = 128;
   public static final int MAX_CODE_STRING_LENGTH = 33;

   IRBase(long var1) {
      super(var1);
   }

   public IRBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableCodeEvents(var1 && this.codeListeners.size() > 0);
      this.enableLearnEvents(var1 && this.learnListeners.size() > 0);
      this.enableRawDataEvents(var1 && this.rawDataListeners.size() > 0);
   }

   public final void addCodeListener(IRCodeListener var1) {
      synchronized (this.codeListeners) {
         this.codeListeners.add(var1);
         this.enableCodeEvents(true);
      }
   }

   public final void removeCodeListener(IRCodeListener var1) {
      synchronized (this.codeListeners) {
         this.codeListeners.remove(var1);
         this.enableCodeEvents(this.codeListeners.size() > 0);
      }
   }

   private void fireCode(IRCodeEvent var1) {
      synchronized (this.codeListeners) {
         Iterator var3 = this.codeListeners.iterator();

         while (var3.hasNext()) {
            ((IRCodeListener)var3.next()).onCode(var1);
         }
      }
   }

   private native void enableCodeEvents(boolean var1);

   public final void addLearnListener(IRLearnListener var1) {
      synchronized (this.learnListeners) {
         this.learnListeners.add(var1);
         this.enableLearnEvents(true);
      }
   }

   public final void removeLearnListener(IRLearnListener var1) {
      synchronized (this.learnListeners) {
         this.learnListeners.remove(var1);
         this.enableLearnEvents(this.learnListeners.size() > 0);
      }
   }

   private void fireLearn(IRLearnEvent var1) {
      synchronized (this.learnListeners) {
         Iterator var3 = this.learnListeners.iterator();

         while (var3.hasNext()) {
            ((IRLearnListener)var3.next()).onLearn(var1);
         }
      }
   }

   private native void enableLearnEvents(boolean var1);

   public final void addRawDataListener(IRRawDataListener var1) {
      synchronized (this.rawDataListeners) {
         this.rawDataListeners.add(var1);
         this.enableRawDataEvents(true);
      }
   }

   public final void removeRawDataListener(IRRawDataListener var1) {
      synchronized (this.rawDataListeners) {
         this.rawDataListeners.remove(var1);
         this.enableRawDataEvents(this.rawDataListeners.size() > 0);
      }
   }

   private void fireRawData(IRRawDataEvent var1) {
      synchronized (this.rawDataListeners) {
         Iterator var3 = this.rawDataListeners.iterator();

         while (var3.hasNext()) {
            ((IRRawDataListener)var3.next()).onRawData(var1);
         }
      }
   }

   private native void enableRawDataEvents(boolean var1);

   public native IRCode getLastCode() throws PhidgetException;

   public native IRLearnedCode getLastLearnedCode() throws PhidgetException;

   public native void transmit(String var1, IRCodeInfo var2) throws PhidgetException;

   public native void transmitRaw(int[] var1, int var2, double var3, int var5) throws PhidgetException;

   public native void transmitRepeat() throws PhidgetException;
}
