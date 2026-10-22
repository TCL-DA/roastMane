package org.eclipse.jetty.util.component;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;

@ManagedObject("Dumpable Object")
public interface Dumpable {
   String KEY = "key: +- bean, += managed, +~ unmanaged, +? auto, +: iterable, +] array, +@ map, +> undefined";

   @ManagedOperation(
      value = "Dump the nested Object state as a String",
      impact = "INFO"
   )
   default String dump() {
      return dump(this);
   }

   void dump(Appendable var1, String var2) throws IOException;

   static String dump(Dumpable dumpable) {
      StringBuilder b = new StringBuilder();

      try {
         dumpable.dump(b, "");
      } catch (IOException var3) {
         b.append(var3.toString());
      }

      b.append("key: +- bean, += managed, +~ unmanaged, +? auto, +: iterable, +] array, +@ map, +> undefined");
      return b.toString();
   }

   default String dumpSelf() {
      return this.toString();
   }

   static void dumpObject(Appendable out, Object o) throws IOException {
      try {
         String s;
         if (o == null) {
            s = "null";
         } else if (o instanceof Dumpable) {
            s = ((Dumpable)o).dumpSelf();
            s = StringUtil.replace(s, "\r\n", "|");
            s = StringUtil.replace(s, '\n', '|');
         } else if (o instanceof Collection collection) {
            s = String.format("%s@%x(size=%d)", TypeUtil.toShortName(o.getClass()), o.hashCode(), collection.size());
         } else if (o.getClass().isArray()) {
            s = String.format("%s@%x[size=%d]", o.getClass().getComponentType(), o.hashCode(), Array.getLength(o));
         } else if (o instanceof Map map) {
            s = String.format("%s@%x{size=%d}", TypeUtil.toShortName(o.getClass()), o.hashCode(), map.size());
         } else if (o instanceof Entry<?, ?> entry) {
            s = String.format("%s=%s", entry.getKey(), entry.getValue());
         } else {
            s = String.valueOf(o);
            s = StringUtil.replace(s, "\r\n", "|");
            s = StringUtil.replace(s, '\n', '|');
         }

         if (o instanceof LifeCycle) {
            out.append(s).append(" - ").append(AbstractLifeCycle.getState((LifeCycle)o)).append("\n");
         } else {
            out.append(s).append("\n");
         }
      } catch (Throwable var6) {
         out.append("=> ").append(var6.toString()).append("\n");
      }
   }

   static void dumpObjects(Appendable out, String indent, Object object, Object... extraChildren) throws IOException {
      dumpObject(out, object);
      int extras = extraChildren == null ? 0 : extraChildren.length;
      if (object instanceof Stream) {
         object = ((Stream)object).toArray();
      }

      if (object instanceof Array) {
         object = Arrays.asList((Object[])object);
      }

      if (object instanceof Container) {
         dumpContainer(out, indent, (Container)object, extras == 0);
      }

      if (object instanceof Iterable && !(object instanceof Path)) {
         dumpIterable(out, indent, (Iterable<?>)object, extras == 0);
      } else if (object instanceof Map) {
         dumpMapEntries(out, indent, (Map<?, ?>)object, extras == 0);
      }

      if (extras != 0) {
         int i = 0;

         for (Object item : extraChildren) {
            i++;
            String nextIndent = indent + (i < extras ? "|  " : "   ");
            out.append(indent).append("+> ");
            if (item instanceof Dumpable) {
               ((Dumpable)item).dump(out, nextIndent);
            } else {
               dumpObjects(out, nextIndent, item);
            }
         }
      }
   }

   static void dumpContainer(Appendable out, String indent, Container object, boolean last) throws IOException {
      Container container = object;
      ContainerLifeCycle containerLifeCycle = object instanceof ContainerLifeCycle ? (ContainerLifeCycle)object : null;
      Iterator<Object> i = object.getBeans().iterator();

      while (i.hasNext()) {
         Object bean = i.next();
         if (!(container instanceof Dumpable.DumpableContainer) || ((Dumpable.DumpableContainer)container).isDumpable(bean)) {
            String nextIndent = indent + (!i.hasNext() && last ? "   " : "|  ");
            if (bean instanceof LifeCycle) {
               if (container.isManaged(bean)) {
                  out.append(indent).append("+= ");
                  if (bean instanceof Dumpable) {
                     ((Dumpable)bean).dump(out, nextIndent);
                  } else {
                     dumpObjects(out, nextIndent, bean);
                  }
               } else if (containerLifeCycle != null && containerLifeCycle.isAuto(bean)) {
                  out.append(indent).append("+? ");
                  if (bean instanceof Dumpable) {
                     ((Dumpable)bean).dump(out, nextIndent);
                  } else {
                     dumpObjects(out, nextIndent, bean);
                  }
               } else {
                  out.append(indent).append("+~ ");
                  dumpObject(out, bean);
               }
            } else if (containerLifeCycle != null && containerLifeCycle.isUnmanaged(bean)) {
               out.append(indent).append("+~ ");
               dumpObject(out, bean);
            } else {
               out.append(indent).append("+- ");
               if (bean instanceof Dumpable) {
                  ((Dumpable)bean).dump(out, nextIndent);
               } else {
                  dumpObjects(out, nextIndent, bean);
               }
            }
         }
      }
   }

   static void dumpIterable(Appendable out, String indent, Iterable<?> iterable, boolean last) throws IOException {
      Iterator i = iterable.iterator();

      while (i.hasNext()) {
         Object item = i.next();
         if (Objects.equals(item, iterable)) {
            return;
         }

         String nextIndent = indent + (!i.hasNext() && last ? "   " : "|  ");
         out.append(indent).append("+: ");
         if (item instanceof Dumpable) {
            ((Dumpable)item).dump(out, nextIndent);
         } else {
            dumpObjects(out, nextIndent, item);
         }
      }
   }

   static void dumpMapEntries(Appendable out, String indent, Map<?, ?> map, boolean last) throws IOException {
      Iterator<? extends Entry<?, ?>> i = map.entrySet().iterator();

      while (i.hasNext()) {
         Entry entry = i.next();
         String nextIndent = indent + (!i.hasNext() && last ? "   " : "|  ");
         out.append(indent).append("+@ ").append(String.valueOf(entry.getKey())).append(" = ");
         Object item = entry.getValue();
         if (item instanceof Dumpable) {
            ((Dumpable)item).dump(out, nextIndent);
         } else {
            dumpObjects(out, nextIndent, item);
         }
      }
   }

   static Dumpable named(final String name, Object object) {
      return object instanceof Dumpable dumpable ? new Dumpable() {
         @Override
         public String dumpSelf() {
            return name + ": " + dumpable.dumpSelf();
         }

         @Override
         public void dump(Appendable out, String indent) throws IOException {
            out.append(name).append(": ");
            dumpable.dump(out, indent);
         }
      } : (out, indent) -> {
         out.append(name).append(": ");
         dumpObjects(out, indent, object);
      };
   }

   public interface DumpableContainer extends Dumpable {
      default boolean isDumpable(Object o) {
         return true;
      }
   }
}
