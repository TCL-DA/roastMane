package com.phidget22;

public class DictionaryUpdateEvent {
   Dictionary Source;
   String Key;
   String Value;

   public DictionaryUpdateEvent(Dictionary var1, String var2, String var3) {
      this.Source = var1;
      this.Key = var2;
      this.Value = var3;
   }

   public Dictionary getSource() {
      return this.Source;
   }

   public String getKey() {
      return this.Key;
   }

   public String getValue() {
      return this.Value;
   }

   @Override
   public String toString() {
      return "[DictionaryUpdateEvent](Source: " + this.Source + ", Key: " + this.Key + ", Value: " + this.Value + ")";
   }
}
