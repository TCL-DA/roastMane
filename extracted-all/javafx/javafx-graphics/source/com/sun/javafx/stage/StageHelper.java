package com.sun.javafx.stage;

import com.sun.javafx.util.Utils;
import javafx.stage.Stage;
import javafx.stage.Window;

public class StageHelper extends WindowHelper {
   private static final StageHelper theInstance = new StageHelper();
   private static StageHelper.StageAccessor stageAccessor;

   private static WindowHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Stage var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void visibleChangingImpl(Window var1, boolean var2) {
      super.visibleChangingImpl(var1, var2);
      stageAccessor.doVisibleChanging(var1, var2);
   }

   @Override
   protected void visibleChangedImpl(Window var1, boolean var2) {
      super.visibleChangedImpl(var1, var2);
      stageAccessor.doVisibleChanged(var1, var2);
   }

   public static void initSecurityDialog(Stage var0, boolean var1) {
      stageAccessor.initSecurityDialog(var0, var1);
   }

   public static void setPrimary(Stage var0, boolean var1) {
      stageAccessor.setPrimary(var0, var1);
   }

   public static void setImportant(Stage var0, boolean var1) {
      stageAccessor.setImportant(var0, var1);
   }

   public static void setStageAccessor(StageHelper.StageAccessor var0) {
      if (stageAccessor != null) {
         System.out.println("Warning: Stage accessor already set: " + stageAccessor);
         Thread.dumpStack();
      }

      stageAccessor = var0;
   }

   public static StageHelper.StageAccessor getStageAccessor() {
      return stageAccessor;
   }

   static {
      Utils.forceInit(Stage.class);
   }

   public interface StageAccessor {
      void doVisibleChanging(Window var1, boolean var2);

      void doVisibleChanged(Window var1, boolean var2);

      void initSecurityDialog(Stage var1, boolean var2);

      void setPrimary(Stage var1, boolean var2);

      void setImportant(Stage var1, boolean var2);
   }
}
