package retrofit2;

import android.os.Build.VERSION;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

final class Platform {
   @Nullable
   static final Executor callbackExecutor;
   static final Reflection reflection;
   static final BuiltInFactories builtInFactories;

   private Platform() {
   }

   static {
      String var0 = System.getProperty("java.vm.name");
      switch (var0) {
         case "Dalvik":
            callbackExecutor = new AndroidMainExecutor();
            if (VERSION.SDK_INT >= 24) {
               reflection = new Reflection.Android24();
               builtInFactories = new BuiltInFactories.Java8();
            } else {
               reflection = new Reflection();
               builtInFactories = new BuiltInFactories();
            }
            break;
         case "RoboVM":
            callbackExecutor = null;
            reflection = new Reflection();
            builtInFactories = new BuiltInFactories();
            break;
         default:
            callbackExecutor = null;
            reflection = new Reflection.Java8();
            builtInFactories = new BuiltInFactories.Java8();
      }
   }
}
