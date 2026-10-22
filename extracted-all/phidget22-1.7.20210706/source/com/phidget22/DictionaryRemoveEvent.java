package com.phidget22;

public class DictionaryRemoveEvent {
   Dictionary Source;
   String Key;

   public DictionaryRemoveEvent(Dictionary var1, String var2) {
      this.Source = var1;
      this.Key = var2;
   }

   public Dictionary getSource() {
      return this.Source;
   }

   public String getKey() {
      return this.Key;
   }

   @Override
   public String toString() {
      return "[DictionaryRemoveEvent](Source: " + this.Source + ", Key: " + this.Key + ")";
   }
}
