package gnu.gettext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class GettextResource extends ResourceBundle {
   public static boolean verbose = false;
   private static final String CONTEXT_GLUE = "\u0004";

   private static String gettextnull(ResourceBundle var0, String var1) {
      try {
         return (String)var0.getObject(var1);
      } catch (MissingResourceException var3) {
         return null;
      }
   }

   public static String gettext(ResourceBundle var0, String var1) {
      String var2 = gettextnull(var0, var1);
      return var2 != null ? var2 : var1;
   }

   private static String ngettextnull(ResourceBundle var0, String var1, long var2) {
      do {
         if (verbose) {
            System.out.println("ngettext on " + var0);
         }

         Method var5 = null;
         Method var6 = null;

         try {
            var5 = var0.getClass()
               .getMethod("handleGetObject", class$java$lang$String == null ? (class$java$lang$String = class$("java.lang.String")) : class$java$lang$String);
            var6 = var0.getClass().getMethod("getParent");
         } catch (NoSuchMethodException var15) {
         } catch (SecurityException var16) {
         }

         if (verbose) {
            System.out.println("handleGetObject = " + (var5 != null) + ", getParent = " + (var6 != null));
         }

         if (var5 == null || !Modifier.isPublic(var5.getModifiers()) || var6 == null) {
            break;
         }

         Method var7 = null;
         Method var8 = null;

         try {
            var7 = var0.getClass()
               .getMethod("lookup", class$java$lang$String == null ? (class$java$lang$String = class$("java.lang.String")) : class$java$lang$String);
            var8 = var0.getClass().getMethod("pluralEval", long.class);
         } catch (NoSuchMethodException var23) {
         } catch (SecurityException var24) {
         }

         if (verbose) {
            System.out.println("lookup = " + (var7 != null) + ", pluralEval = " + (var8 != null));
         }

         if (var7 != null && var8 != null) {
            Object var28 = null;

            try {
               var28 = var7.invoke(var0, var1);
            } catch (IllegalAccessException var21) {
               var21.printStackTrace();
            } catch (InvocationTargetException var22) {
               var22.getTargetException().printStackTrace();
            }

            if (var28 != null) {
               if (verbose) {
                  System.out.println("localValue = " + var28);
               }

               if (var28 instanceof String) {
                  return (String)var28;
               }

               String[] var10 = (String[])var28;
               long var11 = 0L;

               try {
                  var11 = (Long)var8.invoke(var0, new Long(var2));
                  if (var11 < 0L || var11 >= (long)var10.length) {
                     var11 = 0L;
                  }
               } catch (IllegalAccessException var25) {
                  var25.printStackTrace();
               } catch (InvocationTargetException var26) {
                  var26.getTargetException().printStackTrace();
               }

               return var10[(int)var11];
            }
         } else {
            Object var9 = null;

            try {
               var9 = var5.invoke(var0, var1);
            } catch (IllegalAccessException var19) {
               var19.printStackTrace();
            } catch (InvocationTargetException var20) {
               var20.getTargetException().printStackTrace();
            }

            if (var9 != null) {
               if (verbose) {
                  System.out.println("localValue = " + var9);
               }

               return (String)var9;
            }
         }

         Object var29 = var0;

         try {
            var29 = var6.invoke(var0);
         } catch (IllegalAccessException var17) {
            var17.printStackTrace();
         } catch (InvocationTargetException var18) {
            var18.getTargetException().printStackTrace();
         }

         if (var29 == var0) {
            break;
         }

         var0 = (ResourceBundle)var29;
      } while (var0 != null);

      if (var0 != null) {
         Object var27;
         try {
            var27 = var0.getObject(var1);
         } catch (MissingResourceException var14) {
            var27 = null;
         }

         if (var27 != null) {
            return (String)var27;
         }
      }

      return null;
   }

   public static String ngettext(ResourceBundle var0, String var1, String var2, long var3) {
      String var5 = ngettextnull(var0, var1, var3);
      if (var5 != null) {
         return var5;
      } else {
         return var3 != 1L ? var2 : var1;
      }
   }

   public static String pgettext(ResourceBundle var0, String var1, String var2) {
      String var3 = gettextnull(var0, var1 + "\u0004" + var2);
      return var3 != null ? var3 : var2;
   }

   public static String npgettext(ResourceBundle var0, String var1, String var2, String var3, long var4) {
      String var6 = ngettextnull(var0, var1 + "\u0004" + var2, var4);
      if (var6 != null) {
         return var6;
      } else {
         return var4 != 1L ? var3 : var2;
      }
   }
}
