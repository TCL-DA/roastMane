package org.glassfish.jersey.internal.guava;

public final class MoreObjects {
   private MoreObjects() {
   }

   public static MoreObjects.ToStringHelper toStringHelper(Object self) {
      return new MoreObjects.ToStringHelper(simpleName(self.getClass()));
   }

   private static String simpleName(Class<?> clazz) {
      String name = clazz.getName();
      name = name.replaceAll("\\$[0-9]+", "\\$");
      int start = name.lastIndexOf(36);
      if (start == -1) {
         start = name.lastIndexOf(46);
      }

      return name.substring(start + 1);
   }

   public static final class ToStringHelper {
      private final String className;
      private final MoreObjects.ToStringHelper.ValueHolder holderHead = new MoreObjects.ToStringHelper.ValueHolder();
      private MoreObjects.ToStringHelper.ValueHolder holderTail = this.holderHead;
      private final boolean omitNullValues = false;

      private ToStringHelper(String className) {
         this.className = Preconditions.checkNotNull(className);
      }

      public MoreObjects.ToStringHelper add(String name, Object value) {
         return this.addHolder(name, value);
      }

      public MoreObjects.ToStringHelper add(String name, int value) {
         return this.addHolder(name, String.valueOf(value));
      }

      public MoreObjects.ToStringHelper add(String name, long value) {
         return this.addHolder(name, String.valueOf(value));
      }

      @Override
      public String toString() {
         String nextSeparator = "";
         StringBuilder builder = new StringBuilder(32).append(this.className).append('{');

         for (MoreObjects.ToStringHelper.ValueHolder valueHolder = this.holderHead.next; valueHolder != null; valueHolder = valueHolder.next) {
            builder.append(nextSeparator);
            nextSeparator = ", ";
            if (valueHolder.name != null) {
               builder.append(valueHolder.name).append('=');
            }

            builder.append(valueHolder.value);
         }

         return builder.append('}').toString();
      }

      private MoreObjects.ToStringHelper.ValueHolder addHolder() {
         MoreObjects.ToStringHelper.ValueHolder valueHolder = new MoreObjects.ToStringHelper.ValueHolder();
         this.holderTail = this.holderTail.next = valueHolder;
         return valueHolder;
      }

      private MoreObjects.ToStringHelper addHolder(String name, Object value) {
         MoreObjects.ToStringHelper.ValueHolder valueHolder = this.addHolder();
         valueHolder.value = value;
         valueHolder.name = Preconditions.checkNotNull(name);
         return this;
      }

      private static final class ValueHolder {
         String name;
         Object value;
         MoreObjects.ToStringHelper.ValueHolder next;

         private ValueHolder() {
         }
      }
   }
}
