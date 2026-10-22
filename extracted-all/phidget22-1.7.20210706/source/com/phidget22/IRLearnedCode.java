package com.phidget22;

public class IRLearnedCode {
   public String code;
   public IRCodeInfo codeInfo;

   @Override
   public String toString() {
      return "[IRLearnedCode](code: " + this.code + ", codeInfo: " + this.codeInfo + ")";
   }
}
