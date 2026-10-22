package com.ghgande.j2mod.modbus.procimg;

import com.ghgande.j2mod.modbus.util.Observable;

public class ObservableDigitalOut extends Observable implements DigitalOut {
   protected boolean set;

   @Override
   public boolean isSet() {
      return this.set;
   }

   @Override
   public void set(boolean b) {
      this.set = b;
      this.notifyObservers("value");
   }
}
