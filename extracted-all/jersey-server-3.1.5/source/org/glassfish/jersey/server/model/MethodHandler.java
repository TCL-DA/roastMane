package org.glassfish.jersey.server.model;

import jakarta.ws.rs.Encoded;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;

public abstract class MethodHandler implements ResourceModelComponent {
   private final Collection<Parameter> handlerParameters;

   protected MethodHandler() {
      this.handlerParameters = Collections.emptyList();
   }

   protected MethodHandler(Collection<Parameter> parameters) {
      if (parameters != null) {
         this.handlerParameters = Collections.unmodifiableCollection(new ArrayList<>(parameters));
      } else {
         this.handlerParameters = Collections.emptyList();
      }
   }

   public static MethodHandler create(Class<?> handlerClass) {
      return new MethodHandler.ClassBasedMethodHandler(handlerClass, null);
   }

   public static MethodHandler create(Class<?> handlerClass, boolean keepConstructorParamsEncoded) {
      return new MethodHandler.ClassBasedMethodHandler(handlerClass, keepConstructorParamsEncoded, null);
   }

   public static MethodHandler create(Object handlerInstance) {
      return new MethodHandler.InstanceBasedMethodHandler(handlerInstance, null);
   }

   public static MethodHandler create(Object handlerInstance, Class<?> handlerClass) {
      return new MethodHandler.InstanceBasedMethodHandler(handlerInstance, handlerClass, null);
   }

   public static MethodHandler create(Class<?> handlerClass, Collection<Parameter> handlerParameters) {
      return new MethodHandler.ClassBasedMethodHandler(handlerClass, handlerParameters);
   }

   public static MethodHandler create(Class<?> handlerClass, boolean keepConstructorParamsEncoded, Collection<Parameter> handlerParameters) {
      return new MethodHandler.ClassBasedMethodHandler(handlerClass, keepConstructorParamsEncoded, handlerParameters);
   }

   public static MethodHandler create(Object handlerInstance, Collection<Parameter> handlerParameters) {
      return new MethodHandler.InstanceBasedMethodHandler(handlerInstance, handlerParameters);
   }

   public static MethodHandler create(Object handlerInstance, Class<?> handlerClass, Collection<Parameter> handlerParameters) {
      return new MethodHandler.InstanceBasedMethodHandler(handlerInstance, handlerClass, handlerParameters);
   }

   public abstract Class<?> getHandlerClass();

   public List<HandlerConstructor> getConstructors() {
      return Collections.emptyList();
   }

   public abstract Object getInstance(InjectionManager var1);

   public abstract boolean isClassBased();

   public Collection<Parameter> getParameters() {
      return this.handlerParameters;
   }

   @Override
   public List<? extends ResourceModelComponent> getComponents() {
      return null;
   }

   @Override
   public void accept(ResourceModelVisitor visitor) {
      visitor.visitMethodHandler(this);
   }

   protected abstract Object getHandlerInstance();

   private static class ClassBasedMethodHandler extends MethodHandler {
      private final Class<?> handlerClass;
      private final List<HandlerConstructor> handlerConstructors;

      public ClassBasedMethodHandler(Class<?> handlerClass, Collection<Parameter> handlerParameters) {
         this(handlerClass, handlerClass.isAnnotationPresent(Encoded.class), handlerParameters);
      }

      public ClassBasedMethodHandler(Class<?> handlerClass, boolean disableParamDecoding, Collection<Parameter> handlerParameters) {
         super(handlerParameters);
         this.handlerClass = handlerClass;
         List<HandlerConstructor> constructors = new LinkedList<>();

         for (Constructor<?> constructor : handlerClass.getConstructors()) {
            constructors.add(new HandlerConstructor(constructor, Parameter.create(handlerClass, handlerClass, constructor, disableParamDecoding)));
         }

         this.handlerConstructors = Collections.unmodifiableList(constructors);
      }

      @Override
      public Class<?> getHandlerClass() {
         return this.handlerClass;
      }

      @Override
      public List<HandlerConstructor> getConstructors() {
         return this.handlerConstructors;
      }

      @Override
      public Object getInstance(InjectionManager injectionManager) {
         return Injections.getOrCreate(injectionManager, this.handlerClass);
      }

      @Override
      public boolean isClassBased() {
         return true;
      }

      @Override
      protected Object getHandlerInstance() {
         return null;
      }

      @Override
      public List<? extends ResourceModelComponent> getComponents() {
         return this.handlerConstructors;
      }

      @Override
      public String toString() {
         return "ClassBasedMethodHandler{handlerClass=" + this.handlerClass + ", handlerConstructors=" + this.handlerConstructors + '}';
      }
   }

   private static class InstanceBasedMethodHandler extends MethodHandler {
      private final Object handler;
      private final Class<?> handlerClass;

      public InstanceBasedMethodHandler(Object handler, Collection<Parameter> handlerParameters) {
         super(handlerParameters);
         this.handler = handler;
         this.handlerClass = handler.getClass();
      }

      public InstanceBasedMethodHandler(Object handler, Class<?> handlerClass, Collection<Parameter> handlerParameters) {
         super(handlerParameters);
         this.handler = handler;
         this.handlerClass = handlerClass;
      }

      @Override
      public Class<?> getHandlerClass() {
         return this.handlerClass;
      }

      @Override
      protected Object getHandlerInstance() {
         return this.handler;
      }

      @Override
      public Object getInstance(InjectionManager injectionManager) {
         return this.handler;
      }

      @Override
      public boolean isClassBased() {
         return false;
      }

      @Override
      public String toString() {
         return "InstanceBasedMethodHandler{handler=" + this.handler + ", handlerClass=" + this.handlerClass + '}';
      }
   }
}
