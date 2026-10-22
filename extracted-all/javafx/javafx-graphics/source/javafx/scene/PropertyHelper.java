package javafx.scene;

import java.security.AccessController;
import java.security.PrivilegedAction;

class PropertyHelper {
   static boolean getBooleanProperty(String var0) {
      try {
         return AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
            String var1 = System.getProperty(var0);
            return "true".equals(var1.toLowerCase());
         }));
      } catch (Exception var2) {
         return false;
      }
   }
}
