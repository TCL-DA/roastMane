package com.ghgande.j2mod.modbus.util;

import java.util.Vector;

public class Observable {
   private final Vector<Observer> observers = new Vector<>(10);

   public synchronized int getObserverCount() {
      return this.observers.size();
   }

   public synchronized void addObserver(Observer o) {
      if (!this.observers.contains(o)) {
         this.observers.addElement(o);
      }
   }

   public synchronized void removeObserver(Observer o) {
      this.observers.removeElement(o);
   }

   public synchronized void removeObservers() {
      this.observers.removeAllElements();
   }

   public synchronized void notifyObservers(Object arg) {
      for (int i = 0; i < this.observers.size(); i++) {
         this.observers.elementAt(i).update(this, arg);
      }
   }
}
