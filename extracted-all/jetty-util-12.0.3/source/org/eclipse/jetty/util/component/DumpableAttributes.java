package org.eclipse.jetty.util.component;

import java.io.IOException;
import java.util.Map.Entry;
import org.eclipse.jetty.util.Attributes;

public class DumpableAttributes implements Dumpable {
   private final String _name;
   private final Attributes _attributes;

   public DumpableAttributes(String name, Attributes attributes) {
      this._name = name;
      this._attributes = attributes;
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      Object[] array = this._attributes
         .asAttributeMap()
         .entrySet()
         .stream()
         .sorted(Entry.comparingByKey())
         .map(e -> Dumpable.named(e.getKey(), e.getValue()))
         .toArray(Object[]::new);
      Dumpable.dumpObjects(out, indent, this._name + " size=" + array.length, array);
   }
}
