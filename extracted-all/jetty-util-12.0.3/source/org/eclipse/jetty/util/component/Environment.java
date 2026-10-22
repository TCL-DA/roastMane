package org.eclipse.jetty.util.component;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.TypeUtil;

public interface Environment extends Attributes {
   Environment CORE = ensure("core");

   static Collection<Environment> getAll() {
      return Collections.unmodifiableCollection(Environment.Named.__environments.values());
   }

   static Environment get(String name) {
      return Environment.Named.__environments.get(name);
   }

   static Environment ensure(String name) {
      return Environment.Named.__environments.computeIfAbsent(name, Environment.Named::new);
   }

   static Environment set(Environment environment) {
      return Environment.Named.__environments.put(environment.getName(), environment);
   }

   String getName();

   ClassLoader getClassLoader();

   default void run(Runnable runnable) {
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(this.getClassLoader());

      try {
         runnable.run();
      } finally {
         Thread.currentThread().setContextClassLoader(old);
      }
   }

   public static class Named extends Attributes.Mapped implements Environment, Dumpable {
      private static final Map<String, Environment> __environments = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      private final String _name;
      private final ClassLoader _classLoader;

      public Named(String name) {
         this(name, null);
      }

      public Named(String name, ClassLoader classLoader) {
         this._name = name;
         this._classLoader = classLoader == null ? this.getClass().getClassLoader() : classLoader;
      }

      @Override
      public String getName() {
         return this._name;
      }

      @Override
      public ClassLoader getClassLoader() {
         return this._classLoader;
      }

      @Override
      public String dump() {
         return Dumpable.dump(this);
      }

      @Override
      public void dump(Appendable out, String indent) throws IOException {
         Dumpable.dumpObjects(
            out, indent, this, new ClassLoaderDump(this.getClassLoader()), new DumpableCollection("Attributes " + this._name, this.asAttributeMap().entrySet())
         );
      }

      @Override
      public String toString() {
         return "%s@%x{%s}".formatted(TypeUtil.toShortName(this.getClass()), this.hashCode(), this._name);
      }
   }
}
