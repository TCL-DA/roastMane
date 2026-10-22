package org.cropster.roastingintelligence.util;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;

public class EmptyResourceBundle extends ResourceBundle {
   @Override
   protected Object handleGetObject(String key) {
      return null;
   }

   @Override
   public Enumeration<String> getKeys() {
      return new Vector<String>().elements();
   }
}
