package javafx.event;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public final class EventType<T extends Event> implements Serializable {
   public static final EventType<Event> ROOT = new EventType<>("EVENT", null);
   private WeakHashMap<EventType<? extends T>, Void> subTypes;
   private final EventType<? super T> superType;
   private final String name;

   @Deprecated
   public EventType() {
      this(ROOT, null);
   }

   public EventType(String var1) {
      this(ROOT, var1);
   }

   public EventType(EventType<? super T> var1) {
      this(var1, null);
   }

   public EventType(EventType<? super T> var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("Event super type must not be null!");
      } else {
         this.superType = var1;
         this.name = var2;
         var1.register(this);
      }
   }

   EventType(String var1, EventType<? super T> var2) {
      this.superType = var2;
      this.name = var1;
      if (var2 != null) {
         if (var2.subTypes != null) {
            Iterator var3 = var2.subTypes.keySet().iterator();

            while (var3.hasNext()) {
               EventType var4 = (EventType)var3.next();
               if (var1 == null && var4.name == null || var1 != null && var1.equals(var4.name)) {
                  var3.remove();
               }
            }
         }

         var2.register(this);
      }
   }

   public final EventType<? super T> getSuperType() {
      return this.superType;
   }

   public final String getName() {
      return this.name;
   }

   @Override
   public String toString() {
      return this.name != null ? this.name : super.toString();
   }

   private void register(EventType<? extends T> var1) {
      if (this.subTypes == null) {
         this.subTypes = new WeakHashMap<>();
      }

      for (EventType var3 : this.subTypes.keySet()) {
         if (var3.name == null && var1.name == null || var3.name != null && var3.name.equals(var1.name)) {
            throw new IllegalArgumentException("EventType \"" + var1 + "\"with parent \"" + var1.getSuperType() + "\" already exists");
         }
      }

      this.subTypes.put(var1, null);
   }

   private Object writeReplace() throws ObjectStreamException {
      LinkedList var1 = new LinkedList();

      for (EventType var2 = this; var2 != ROOT; var2 = var2.superType) {
         var1.addFirst(var2.name);
      }

      return new EventType.EventTypeSerialization(new ArrayList<>(var1));
   }

   static class EventTypeSerialization implements Serializable {
      private List<String> path;

      public EventTypeSerialization(List<String> var1) {
         this.path = var1;
      }

      private Object readResolve() throws ObjectStreamException {
         EventType var1 = EventType.ROOT;

         for (int var2 = 0; var2 < this.path.size(); var2++) {
            String var3 = this.path.get(var2);
            if (var1.subTypes == null) {
               throw new InvalidObjectException("Cannot find event type \"" + var3 + "\" (of " + var1 + ")");
            }

            EventType var4 = this.findSubType(var1.subTypes.keySet(), var3);
            if (var4 == null) {
               throw new InvalidObjectException("Cannot find event type \"" + var3 + "\" (of " + var1 + ")");
            }

            var1 = var4;
         }

         return var1;
      }

      private EventType findSubType(Set<EventType> var1, String var2) {
         for (EventType var4 : var1) {
            if (var4.name == null && var2 == null || var4.name != null && var4.name.equals(var2)) {
               return var4;
            }
         }

         return null;
      }
   }
}
