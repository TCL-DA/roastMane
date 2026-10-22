package org.glassfish.jersey.server.internal.scanning;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.glassfish.jersey.server.ResourceFinder;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;

final class VfsSchemeResourceFinderFactory implements UriSchemeResourceFinderFactory {
   private static final Set<String> SCHEMES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("vfsfile", "vfszip", "vfs")));

   @Override
   public Set<String> getSchemes() {
      return SCHEMES;
   }

   @Override
   public ResourceFinder create(URI uri, boolean recursive) {
      return new VfsSchemeResourceFinderFactory.VfsResourceFinder(uri, recursive);
   }

   private static class VfsResourceFinder extends AbstractResourceFinderAdapter {
      private Object current;
      private Object next;
      private final Method openStream;
      private final Method getName;
      private final Method isLeaf;
      private final Iterator<?> iterator;

      public VfsResourceFinder(URI uri, boolean recursive) {
         Object directory = this.bindDirectory(uri);
         this.openStream = this.bindMethod(directory, "openStream");
         this.getName = this.bindMethod(directory, "getName");
         this.isLeaf = this.bindMethod(directory, "isLeaf");
         this.iterator = this.getChildren(directory, recursive);
      }

      private Iterator<?> getChildren(Object directory, boolean recursive) {
         Method getChildren = this.bindMethod(directory, recursive ? "getChildrenRecursively" : "getChildren");
         List<?> list = this.invoke(directory, getChildren, List.class);
         if (list == null) {
            throw new ResourceFinderException("VFS object returned null when accessing children");
         } else {
            return list.iterator();
         }
      }

      private Method bindMethod(final Object object, final String name) {
         if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(new PrivilegedAction<Method>() {
               public Method run() {
                  return VfsResourceFinder.this.bindMethod0(object, name);
               }
            });
         }

         return this.bindMethod0(object, name);
      }

      private <T> T invoke(Object instance, Method method, Class<T> type) {
         try {
            return type.cast(method.invoke(instance));
         } catch (Exception var5) {
            throw new ResourceFinderException("VFS object could not be invoked upon");
         }
      }

      private Method bindMethod0(Object object, String name) {
         Class<?> clazz = object.getClass();

         try {
            return clazz.getMethod(name);
         } catch (NoSuchMethodException var5) {
            throw new ResourceFinderException("VFS object did not have a valid signature");
         }
      }

      private Object bindDirectory(URI uri) {
         Object directory = null;

         try {
            directory = uri.toURL().getContent();
         } catch (IOException var4) {
         }

         if (directory != null && directory.getClass().getSimpleName().equals("VirtualFile")) {
            return directory;
         } else {
            throw new ResourceFinderException("VFS URL did not map to a valid VFS object");
         }
      }

      @Override
      public InputStream open() {
         Object current = this.current;
         if (current == null) {
            throw new IllegalStateException("next() must be called before open()");
         } else {
            return this.invoke(current, this.openStream, InputStream.class);
         }
      }

      @Override
      public void reset() {
         throw new UnsupportedOperationException();
      }

      public boolean advance() {
         while (this.iterator.hasNext()) {
            Object next = this.iterator.next();
            if (this.invoke(next, this.isLeaf, Boolean.class)) {
               this.next = next;
               return true;
            }
         }

         return false;
      }

      @Override
      public boolean hasNext() {
         return this.next != null || this.advance();
      }

      public String next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.current = this.next;
            this.next = null;
            return this.invoke(this.current, this.getName, String.class);
         }
      }
   }
}
