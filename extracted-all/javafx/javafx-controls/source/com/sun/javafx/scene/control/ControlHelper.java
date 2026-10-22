package com.sun.javafx.scene.control;

import com.sun.javafx.scene.layout.RegionHelper;
import com.sun.javafx.util.Utils;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;

public class ControlHelper extends RegionHelper {
   private static final ControlHelper theInstance = new ControlHelper();
   private static ControlHelper.ControlAccessor controlAccessor;

   private static ControlHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Control var0) {
      setHelper(var0, getInstance());
   }

   public static void superProcessCSS(Node var0) {
      ((ControlHelper)getHelper(var0)).superProcessCSSImpl(var0);
   }

   public static StringProperty skinClassNameProperty(Control var0) {
      return controlAccessor.skinClassNameProperty(var0);
   }

   void superProcessCSSImpl(Node var1) {
      super.processCSSImpl(var1);
   }

   protected void processCSSImpl(Node var1) {
      controlAccessor.doProcessCSS(var1);
   }

   public static void setControlAccessor(ControlHelper.ControlAccessor var0) {
      if (controlAccessor != null) {
         throw new IllegalStateException();
      } else {
         controlAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Control.class);
   }

   public interface ControlAccessor {
      void doProcessCSS(Node var1);

      StringProperty skinClassNameProperty(Control var1);
   }
}
