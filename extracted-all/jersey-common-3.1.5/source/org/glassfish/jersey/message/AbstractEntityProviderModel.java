package org.glassfish.jersey.message;

import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public abstract class AbstractEntityProviderModel<T> {
   private final T provider;
   private final List<MediaType> declaredTypes;
   private final boolean custom;
   private final Class<?> providedType;

   AbstractEntityProviderModel(T provider, List<MediaType> declaredTypes, boolean custom, Class<T> providerType) {
      this.provider = provider;
      this.declaredTypes = declaredTypes;
      this.custom = custom;
      this.providedType = getProviderClassParam(provider, providerType);
   }

   public T provider() {
      return this.provider;
   }

   public List<MediaType> declaredTypes() {
      return this.declaredTypes;
   }

   public boolean isCustom() {
      return this.custom;
   }

   public Class<?> providedType() {
      return this.providedType;
   }

   private static Class<?> getProviderClassParam(Object provider, Class<?> providerType) {
      ReflectionHelper.DeclaringClassInterfacePair pair = ReflectionHelper.getClass(provider.getClass(), providerType);
      Class[] classArgs = ReflectionHelper.getParameterizedClassArguments(pair);
      return classArgs != null ? classArgs[0] : Object.class;
   }
}
