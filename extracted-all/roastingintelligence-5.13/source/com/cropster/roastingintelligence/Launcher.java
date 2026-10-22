package com.cropster.roastingintelligence;

import org.cropster.roastingintelligence.RoastingIntelligenceApp;

public final class Launcher {
   public static void main(String[] args) {
      RoastingIntelligenceApp.computeAndSetMaxVram();
      if (args != null && args.length > 0 && args[0].equals("--console")) {
         RoastingIntelligenceApp application = new RoastingIntelligenceApp();
         application.run("console");
      } else {
         RoastingIntelligenceApp.launch(RoastingIntelligenceApp.class, args);
      }
   }

   private Launcher() {
   }
}
