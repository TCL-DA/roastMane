package org.eclipse.yasson.internal.deserializer;

import jakarta.json.stream.JsonParser;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.InstanceCreator;
import org.eclipse.yasson.internal.ReflectionUtils;

class CollectionInstanceCreator implements ModelDeserializer<JsonParser> {
   private final CollectionDeserializer delegate;
   private final Type type;
   private final Class<?> clazz;
   private final boolean isEnumSet;

   CollectionInstanceCreator(CollectionDeserializer delegate, Type type) {
      this.delegate = delegate;
      this.clazz = this.implementationClass(ReflectionUtils.getRawType(type));
      this.isEnumSet = EnumSet.class.isAssignableFrom(this.clazz);
      this.type = this.isEnumSet ? ((ParameterizedType)type).getActualTypeArguments()[0] : type;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      Object instance;
      if (this.isEnumSet) {
         instance = EnumSet.noneOf((Class)this.type);
      } else {
         instance = InstanceCreator.createInstance(this.clazz);
      }

      context.setInstance(instance);
      return this.delegate.deserialize(value, context);
   }

   private Class<?> implementationClass(Class<?> type) {
      return type.isInterface() ? this.createInterfaceInstance(type) : type;
   }

   private Class<?> createInterfaceInstance(Class<?> ifcType) {
      if (List.class.isAssignableFrom(ifcType)) {
         return ArrayList.class;
      } else if (Set.class.isAssignableFrom(ifcType)) {
         return SortedSet.class.isAssignableFrom(ifcType) ? TreeSet.class : HashSet.class;
      } else if (Queue.class.isAssignableFrom(ifcType)) {
         return ArrayDeque.class;
      } else {
         return Collection.class == ifcType ? ArrayList.class : ifcType;
      }
   }
}
