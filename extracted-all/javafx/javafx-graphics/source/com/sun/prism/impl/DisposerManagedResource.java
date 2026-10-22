package com.sun.prism.impl;

public abstract class DisposerManagedResource<T> extends ManagedResource<T> {
   Object referent = new Object();

   public DisposerManagedResource(T var1, ResourcePool var2, Disposer.Record var3) {
      super((T)var1, var2);
      Disposer.addRecord(this.referent, var3);
   }
}
