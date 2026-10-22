package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DictionaryBase extends Phidget {
   private List<DictionaryAddListener> addListeners = new ArrayList<>();
   private long nativeAddHandler = 0L;
   private List<DictionaryRemoveListener> removeListeners = new ArrayList<>();
   private long nativeRemoveHandler = 0L;
   private List<DictionaryUpdateListener> updateListeners = new ArrayList<>();
   private long nativeUpdateHandler = 0L;

   DictionaryBase(long var1) {
      super(var1);
   }

   public DictionaryBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableAddEvents(var1 && this.addListeners.size() > 0);
      this.enableRemoveEvents(var1 && this.removeListeners.size() > 0);
      this.enableUpdateEvents(var1 && this.updateListeners.size() > 0);
   }

   public final void addAddListener(DictionaryAddListener var1) {
      synchronized (this.addListeners) {
         this.addListeners.add(var1);
         this.enableAddEvents(true);
      }
   }

   public final void removeAddListener(DictionaryAddListener var1) {
      synchronized (this.addListeners) {
         this.addListeners.remove(var1);
         this.enableAddEvents(this.addListeners.size() > 0);
      }
   }

   private void fireAdd(DictionaryAddEvent var1) {
      synchronized (this.addListeners) {
         Iterator var3 = this.addListeners.iterator();

         while (var3.hasNext()) {
            ((DictionaryAddListener)var3.next()).onAdd(var1);
         }
      }
   }

   private native void enableAddEvents(boolean var1);

   public final void addRemoveListener(DictionaryRemoveListener var1) {
      synchronized (this.removeListeners) {
         this.removeListeners.add(var1);
         this.enableRemoveEvents(true);
      }
   }

   public final void removeRemoveListener(DictionaryRemoveListener var1) {
      synchronized (this.removeListeners) {
         this.removeListeners.remove(var1);
         this.enableRemoveEvents(this.removeListeners.size() > 0);
      }
   }

   private void fireRemove(DictionaryRemoveEvent var1) {
      synchronized (this.removeListeners) {
         Iterator var3 = this.removeListeners.iterator();

         while (var3.hasNext()) {
            ((DictionaryRemoveListener)var3.next()).onRemove(var1);
         }
      }
   }

   private native void enableRemoveEvents(boolean var1);

   public final void addUpdateListener(DictionaryUpdateListener var1) {
      synchronized (this.updateListeners) {
         this.updateListeners.add(var1);
         this.enableUpdateEvents(true);
      }
   }

   public final void removeUpdateListener(DictionaryUpdateListener var1) {
      synchronized (this.updateListeners) {
         this.updateListeners.remove(var1);
         this.enableUpdateEvents(this.updateListeners.size() > 0);
      }
   }

   private void fireUpdate(DictionaryUpdateEvent var1) {
      synchronized (this.updateListeners) {
         Iterator var3 = this.updateListeners.iterator();

         while (var3.hasNext()) {
            ((DictionaryUpdateListener)var3.next()).onUpdate(var1);
         }
      }
   }

   private native void enableUpdateEvents(boolean var1);

   public native void add(String var1, String var2) throws PhidgetException;

   public native void removeAll() throws PhidgetException;

   public native String get(String var1) throws PhidgetException;

   public native void remove(String var1) throws PhidgetException;

   public native void set(String var1, String var2) throws PhidgetException;

   public native String sscan(String var1) throws PhidgetException;

   public native void update(String var1, String var2) throws PhidgetException;
}
