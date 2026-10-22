package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.utilities.InjecteeImpl;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class IterableProviderImpl<T> implements IterableProvider<T> {
   private final ServiceLocatorImpl locator;
   private final Type requiredType;
   private final Set<Annotation> requiredQualifiers;
   private final Unqualified unqualified;
   private final Injectee originalInjectee;
   private final boolean isIterable;

   IterableProviderImpl(
      ServiceLocatorImpl locator, Type requiredType, Set<Annotation> requiredQualifiers, Unqualified unqualified, Injectee originalInjectee, boolean isIterable
   ) {
      this.locator = locator;
      this.requiredType = requiredType;
      this.requiredQualifiers = Collections.unmodifiableSet(requiredQualifiers);
      this.unqualified = unqualified;
      this.originalInjectee = originalInjectee;
      this.isIterable = isIterable;
   }

   private void justInTime() {
      InjecteeImpl injectee = new InjecteeImpl(this.originalInjectee);
      injectee.setRequiredType(this.requiredType);
      injectee.setRequiredQualifiers(this.requiredQualifiers);
      if (this.unqualified != null) {
         injectee.setUnqualified(this.unqualified);
      }

      this.locator.getInjecteeDescriptor(injectee);
   }

   public T get() {
      this.justInTime();
      return this.locator
         .getUnqualifiedService(
            this.requiredType, this.unqualified, this.isIterable, this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()])
         );
   }

   public ServiceHandle<T> getHandle() {
      this.justInTime();
      return this.locator
         .getUnqualifiedServiceHandle(
            this.requiredType, this.unqualified, this.isIterable, this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()])
         );
   }

   public Iterator<T> iterator() {
      this.justInTime();
      List<ServiceHandle<T>> handles = (List<ServiceHandle<T>>)ReflectionHelper.cast(
         this.locator
            .getAllUnqualifiedServiceHandles(
               this.requiredType, this.unqualified, this.isIterable, this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()])
            )
      );
      return new IterableProviderImpl.MyIterator<>(handles);
   }

   public int getSize() {
      this.justInTime();
      return this.locator
         .getAllUnqualifiedServiceHandles(
            this.requiredType, this.unqualified, this.isIterable, this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()])
         )
         .size();
   }

   public IterableProvider<T> named(String name) {
      return this.qualifiedWith(new NamedImpl(name));
   }

   public <U> IterableProvider<U> ofType(Type type) {
      return new IterableProviderImpl(this.locator, type, this.requiredQualifiers, this.unqualified, this.originalInjectee, this.isIterable);
   }

   public IterableProvider<T> qualifiedWith(Annotation... qualifiers) {
      HashSet<Annotation> moreAnnotations = new HashSet<>(this.requiredQualifiers);

      for (Annotation qualifier : qualifiers) {
         moreAnnotations.add(qualifier);
      }

      return new IterableProviderImpl<>(this.locator, this.requiredType, moreAnnotations, this.unqualified, this.originalInjectee, this.isIterable);
   }

   public Iterable<ServiceHandle<T>> handleIterator() {
      this.justInTime();
      List<ServiceHandle<T>> handles = (List<ServiceHandle<T>>)ReflectionHelper.cast(
         this.locator.getAllServiceHandles(this.requiredType, this.requiredQualifiers.toArray(new Annotation[this.requiredQualifiers.size()]))
      );
      return new IterableProviderImpl.HandleIterable<>(handles);
   }

   @Override
   public String toString() {
      return "IterableProviderImpl("
         + Pretty.type(this.requiredType)
         + ","
         + Pretty.collection(this.requiredQualifiers)
         + ","
         + System.identityHashCode(this)
         + ")";
   }

   private static class HandleIterable<U> implements Iterable<ServiceHandle<U>> {
      private final List<ServiceHandle<U>> handles;

      private HandleIterable(List<ServiceHandle<U>> handles) {
         this.handles = new LinkedList<>(handles);
      }

      @Override
      public Iterator<ServiceHandle<U>> iterator() {
         return new IterableProviderImpl.MyHandleIterator<>(this.handles);
      }
   }

   private static class MyHandleIterator<U> implements Iterator<ServiceHandle<U>> {
      private final LinkedList<ServiceHandle<U>> handles;

      private MyHandleIterator(List<ServiceHandle<U>> handles) {
         this.handles = new LinkedList<>(handles);
      }

      @Override
      public boolean hasNext() {
         return !this.handles.isEmpty();
      }

      public ServiceHandle<U> next() {
         return this.handles.removeFirst();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static class MyIterator<U> implements Iterator<U> {
      private final LinkedList<ServiceHandle<U>> handles;

      private MyIterator(List<ServiceHandle<U>> handles) {
         this.handles = new LinkedList<>(handles);
      }

      @Override
      public boolean hasNext() {
         return !this.handles.isEmpty();
      }

      @Override
      public U next() {
         if (this.handles.isEmpty()) {
            throw new NoSuchElementException();
         } else {
            ServiceHandle<U> nextHandle = this.handles.removeFirst();
            return (U)nextHandle.getService();
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
