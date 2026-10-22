package com.phidget22;

public class Dictionary extends DictionaryBase {
   public Dictionary() throws PhidgetException {
   }

   Dictionary(long var1) throws PhidgetException {
      super(var1);
   }

   public String get(String var1, String var2) throws PhidgetException {
      String var3 = this.get(var1);
      return var3 == null ? var2 : var3;
   }

   public String[] scan(String var1) throws PhidgetException {
      if (var1 == null) {
         var1 = "";
      }

      String[] var2 = this.sscan(var1).split("\\n");
      return var2.length == 1 && var2[0].length() == 0 ? new String[0] : var2;
   }
}
