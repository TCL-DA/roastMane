package local;

import java.security.Permission;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;

public final class Launcher {
   public static void main(String[] var0) {
      System.setProperty("org.cropster.roastingintelligence.instance", "roastmaneoffline");
      System.setProperty("org.cropster.roastingintelligence.version", "5.13");
      System.setProperty("com.cropster.roastingintelligence.segment.writeKey", "");
      System.setSecurityManager(new SecurityManager() {
         @Override
         public void checkPermission(Permission var1) {
         }

         @Override
         public void checkConnect(String var1, int var2) {
            throw new SecurityException("Offline mode blocks network: " + var1 + ":" + var2);
         }

         @Override
         public void checkConnect(String var1, int var2, Object var3) {
            this.checkConnect(var1, var2);
         }
      });
      RoastingIntelligenceApp.main(var0);
   }
}
