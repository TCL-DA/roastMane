package org.jvnet.hk2.internal;

import java.net.URL;
import org.glassfish.hk2.utilities.reflection.Pretty;

class DelegatingClassLoader extends ClassLoader {
   private final ClassLoader[] delegates;

   DelegatingClassLoader(ClassLoader parent, ClassLoader... classLoaderDelegates) {
      super(parent);
      this.delegates = classLoaderDelegates;
   }

   @Override
   public Class<?> loadClass(String clazz) throws ClassNotFoundException {
      if (this.getParent() != null) {
         try {
            return this.getParent().loadClass(clazz);
         } catch (ClassNotFoundException var9) {
         }
      }

      ClassNotFoundException firstFail = null;

      for (ClassLoader delegate : this.delegates) {
         try {
            return delegate.loadClass(clazz);
         } catch (ClassNotFoundException var8) {
            if (firstFail == null) {
               firstFail = var8;
            }
         }
      }

      if (firstFail != null) {
         throw firstFail;
      } else {
         throw new ClassNotFoundException("Could not find " + clazz);
      }
   }

   @Override
   public URL getResource(String resource) {
      if (this.getParent() != null) {
         URL u = this.getParent().getResource(resource);
         if (u != null) {
            return u;
         }
      }

      for (ClassLoader delegate : this.delegates) {
         URL u = delegate.getResource(resource);
         if (u != null) {
            return u;
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return "DelegatingClassLoader(" + this.getParent() + "," + Pretty.array(this.delegates) + "," + System.identityHashCode(this) + ")";
   }
}
