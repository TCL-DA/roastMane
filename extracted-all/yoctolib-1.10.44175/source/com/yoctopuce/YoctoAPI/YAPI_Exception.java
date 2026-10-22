package com.yoctopuce.YoctoAPI;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class YAPI_Exception extends Exception {
   protected String _subStackTrace = "";
   private Exception _subExeptions = null;
   private static final long serialVersionUID = -7489291044621894115L;
   public int errorType;

   public YAPI_Exception(String message) {
      super(message);
      this.errorType = -8;
   }

   public YAPI_Exception(int code, String message) {
      super(message);
      this.errorType = code;
   }

   public YAPI_Exception(int code, String message, Exception e) {
      super(message);
      this.errorType = code;
      this._subExeptions = e;
   }

   public String getStackTraceToString() {
      Writer writer = new StringWriter();
      PrintWriter printWriter = new PrintWriter(writer);
      if (this._subExeptions != null) {
         this._subExeptions.printStackTrace(printWriter);
      } else {
         this.printStackTrace(printWriter);
      }

      return this._subStackTrace + writer.toString();
   }
}
