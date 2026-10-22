package jakarta.activation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class CommandInfo {
   private String verb;
   private String className;

   public CommandInfo(String verb, String className) {
      this.verb = verb;
      this.className = className;
   }

   public String getCommandName() {
      return this.verb;
   }

   public String getCommandClass() {
      return this.className;
   }

   public Object getCommandObject(DataHandler dh, ClassLoader loader) throws IOException, ClassNotFoundException {
      Object new_bean = null;
      new_bean = CommandInfo.Beans.instantiate(loader, this.className);
      if (new_bean != null) {
         if (new_bean instanceof CommandObject) {
            ((CommandObject)new_bean).setCommandContext(this.verb, dh);
         } else if (new_bean instanceof Externalizable && dh != null) {
            InputStream is = dh.getInputStream();
            if (is != null) {
               ((Externalizable)new_bean).readExternal(new ObjectInputStream(is));
            }
         }
      }

      return new_bean;
   }

   private static final class Beans {
      static final Method instantiateMethod;

      static Object instantiate(ClassLoader loader, String cn) throws IOException, ClassNotFoundException {
         if (instantiateMethod != null) {
            try {
               return instantiateMethod.invoke(null, loader, cn);
            } catch (IllegalAccessException | InvocationTargetException var6) {
               return null;
            }
         } else {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
               String cname = cn.replace('/', '.');
               if (cname.startsWith("[")) {
                  int b = cname.lastIndexOf(91) + 2;
                  if (b > 1 && b < cname.length()) {
                     cname = cname.substring(b);
                  }
               }

               int i = cname.lastIndexOf(46);
               if (i != -1) {
                  security.checkPackageAccess(cname.substring(0, i));
               }
            }

            if (loader == null) {
               loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                  public ClassLoader run() {
                     ClassLoader cl = null;

                     try {
                        cl = ClassLoader.getSystemClassLoader();
                     } catch (SecurityException var3) {
                     }

                     return cl;
                  }
               });
            }

            Class<?> beanClass = Class.forName(cn, true, loader);

            try {
               return beanClass.getDeclaredConstructor().newInstance();
            } catch (Exception var7) {
               throw new ClassNotFoundException(beanClass + ": " + var7, var7);
            }
         }
      }

      static {
         Method m;
         try {
            Class<?> c = Class.forName("java.beans.Beans");
            m = c.getDeclaredMethod("instantiate", ClassLoader.class, String.class);
         } catch (NoSuchMethodException | ClassNotFoundException var2) {
            m = null;
         }

         instantiateMethod = m;
      }
   }
}
