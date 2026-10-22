package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.Deque;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class VariableTypeInheritanceSearch {
   private final Deque<ParameterizedType> parameterizedSubclasses = new ArrayDeque<>();

   Type searchParametrizedType(Type typeToSearch, TypeVariable<?> typeVar) {
      ParameterizedType parameterizedType = findParameterizedSuperclass(typeToSearch);
      if (parameterizedType == null) {
         return null;
      } else {
         Type matchedGenericType = this.searchRuntimeTypeArgument(parameterizedType, typeVar);
         if (matchedGenericType != null) {
            return matchedGenericType;
         } else {
            this.parameterizedSubclasses.push(parameterizedType);
            return this.searchParametrizedType(((Class)parameterizedType.getRawType()).getGenericSuperclass(), typeVar);
         }
      }
   }

   private Type checkSubclassRuntimeInfo(TypeVariable typeVar) {
      if (this.parameterizedSubclasses.size() == 0) {
         return typeVar;
      } else {
         ParameterizedType parametrizedSubclass = this.parameterizedSubclasses.pop();
         return this.searchRuntimeTypeArgument(parametrizedSubclass, typeVar);
      }
   }

   private Type searchRuntimeTypeArgument(ParameterizedType runtimeType, TypeVariable<?> typeVar) {
      if (ReflectionUtils.getRawType(runtimeType) != typeVar.getGenericDeclaration()) {
         return null;
      } else {
         TypeVariable[] bounds = typeVar.getGenericDeclaration().getTypeParameters();

         for (int i = 0; i < bounds.length; i++) {
            if (bounds[i].equals(typeVar)) {
               Type matchedGenericType = runtimeType.getActualTypeArguments()[i];
               if (matchedGenericType instanceof TypeVariable) {
                  return this.checkSubclassRuntimeInfo((TypeVariable)matchedGenericType);
               }

               return matchedGenericType;
            }
         }

         return null;
      }
   }

   private static ParameterizedType findParameterizedSuperclass(Type type) {
      if (type == null || type instanceof ParameterizedType) {
         return (ParameterizedType)type;
      } else if (!(type instanceof Class)) {
         throw new JsonbException(Messages.getMessage(MessageKeys.RESOLVE_PARAMETRIZED_TYPE, type));
      } else {
         return findParameterizedSuperclass(((Class)type).getGenericSuperclass());
      }
   }
}
