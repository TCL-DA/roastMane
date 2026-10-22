package org.glassfish.jersey.server.model;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Request;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.process.Inflector;

public final class Invocable implements Parameterized, ResourceModelComponent {
   static final Method APPLY_INFLECTOR_METHOD = initApplyMethod();
   private final MethodHandler handler;
   private final Method definitionMethod;
   private final Method handlingMethod;
   private final List<Parameter> parameters;
   private final Class<?> rawResponseType;
   private final Type responseType;
   private final Type routingResponseType;
   private final Class<?> rawRoutingResponseType;

   private static Method initApplyMethod() {
      try {
         return Inflector.class.getMethod("apply", Object.class);
      } catch (NoSuchMethodException var2) {
         IncompatibleClassChangeError error = new IncompatibleClassChangeError("Inflector.apply(Object) method not found");
         error.initCause(var2);
         throw error;
      }
   }

   public static <T> Invocable create(Inflector<Request, T> inflector) {
      return create(MethodHandler.create(inflector), APPLY_INFLECTOR_METHOD, false);
   }

   public static Invocable create(Class<? extends Inflector> inflectorClass) {
      return create(MethodHandler.create(inflectorClass), APPLY_INFLECTOR_METHOD, false);
   }

   public static Invocable create(MethodHandler handler, Method handlingMethod) {
      return create(handler, handlingMethod, false);
   }

   public static Invocable create(MethodHandler handler, Method definitionMethod, boolean encodedParameters) {
      return create(handler, definitionMethod, null, encodedParameters);
   }

   public static Invocable create(MethodHandler handler, Method definitionMethod, Method handlingMethod, boolean encodedParameters) {
      return new Invocable(handler, definitionMethod, handlingMethod, encodedParameters, null);
   }

   public static Invocable create(MethodHandler handler, Method definitionMethod, Method handlingMethod, boolean encodedParameters, Type routingResponseType) {
      return new Invocable(handler, definitionMethod, handlingMethod, encodedParameters, routingResponseType);
   }

   private Invocable(MethodHandler handler, Method definitionMethod, Method handlingMethod, boolean encodedParameters, Type routingResponseType) {
      this.handler = handler;
      this.definitionMethod = definitionMethod;
      this.handlingMethod = handlingMethod == null ? ReflectionHelper.findOverridingMethodOnClass(handler.getHandlerClass(), definitionMethod) : handlingMethod;
      Class<?> handlerClass = handler.getHandlerClass();
      Class<?> definitionClass = definitionMethod.getDeclaringClass();
      ClassTypePair handlingCtPair = ReflectionHelper.resolveGenericType(
         handlerClass, this.handlingMethod.getDeclaringClass(), this.handlingMethod.getReturnType(), this.handlingMethod.getGenericReturnType()
      );
      ClassTypePair definitionCtPair = ReflectionHelper.resolveGenericType(
         definitionClass, this.definitionMethod.getDeclaringClass(), this.definitionMethod.getReturnType(), this.definitionMethod.getGenericReturnType()
      );
      this.rawResponseType = handlingCtPair.rawClass();
      boolean handlerReturnTypeIsParameterized = handlingCtPair.type() instanceof ParameterizedType;
      boolean definitionReturnTypeIsParameterized = definitionCtPair.type() instanceof ParameterizedType;
      this.responseType = handlingCtPair.rawClass() == definitionCtPair.rawClass() && definitionReturnTypeIsParameterized && !handlerReturnTypeIsParameterized
         ? definitionCtPair.type()
         : handlingCtPair.type();
      if (routingResponseType == null) {
         this.routingResponseType = this.responseType;
         this.rawRoutingResponseType = this.rawResponseType;
      } else {
         GenericType routingResponseGenericType = new GenericType(routingResponseType);
         this.routingResponseType = routingResponseGenericType.getType();
         this.rawRoutingResponseType = routingResponseGenericType.getRawType();
      }

      this.parameters = Collections.unmodifiableList(Parameter.create(handlerClass, definitionMethod.getDeclaringClass(), definitionMethod, encodedParameters));
   }

   public MethodHandler getHandler() {
      return this.handler;
   }

   public Method getHandlingMethod() {
      return this.handlingMethod;
   }

   public Method getDefinitionMethod() {
      return this.definitionMethod;
   }

   public Type getResponseType() {
      return this.responseType;
   }

   public Class<?> getRawResponseType() {
      return this.rawResponseType;
   }

   public boolean isInflector() {
      return APPLY_INFLECTOR_METHOD == this.definitionMethod || APPLY_INFLECTOR_METHOD.equals(this.definitionMethod);
   }

   @Override
   public boolean requiresEntity() {
      for (Parameter p : this.getParameters()) {
         if (Source.ENTITY == p.getSource()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public List<Parameter> getParameters() {
      return this.parameters;
   }

   @Override
   public void accept(ResourceModelVisitor visitor) {
      visitor.visitInvocable(this);
   }

   @Override
   public List<? extends ResourceModelComponent> getComponents() {
      return Arrays.asList(this.handler);
   }

   @Override
   public String toString() {
      return "Invocable{handler="
         + this.handler
         + ", definitionMethod="
         + this.definitionMethod
         + ", parameters="
         + this.parameters
         + ", responseType="
         + this.responseType
         + '}';
   }

   public Type getRoutingResponseType() {
      return this.routingResponseType;
   }

   public Class<?> getRawRoutingResponseType() {
      return this.rawRoutingResponseType;
   }
}
