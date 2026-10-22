package org.eclipse.yasson.internal.model;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class Property {
   private final String name;
   private final JsonbAnnotatedElement<Class<?>> declaringClassElement;
   private JsonbAnnotatedElement<Field> fieldElement;
   private JsonbAnnotatedElement<Method> getterElement;
   private JsonbAnnotatedElement<Method> setterElement;

   public Property(String name, JsonbAnnotatedElement<Class<?>> declaringClassModel) {
      this.name = name;
      this.declaringClassElement = declaringClassModel;
   }

   public String getName() {
      return this.name;
   }

   public Field getField() {
      return this.fieldElement == null ? null : this.fieldElement.getElement();
   }

   public void setField(Field field) {
      this.fieldElement = new JsonbAnnotatedElement<>(field);
   }

   public Method getGetter() {
      return this.getterElement == null ? null : this.getterElement.getElement();
   }

   public void setGetter(Method getter) {
      this.getterElement = new JsonbAnnotatedElement<>(getter);
   }

   public Method getSetter() {
      return this.setterElement == null ? null : this.setterElement.getElement();
   }

   public void setSetter(Method setter) {
      this.setterElement = new JsonbAnnotatedElement<>(setter);
   }

   public JsonbAnnotatedElement<Class<?>> getDeclaringClassElement() {
      return this.declaringClassElement;
   }

   public Type getPropertyType() {
      if (this.getField() != null) {
         return this.getField().getGenericType();
      } else if (this.getGetter() != null) {
         return this.getGetterType();
      } else if (this.getSetter() != null) {
         return this.getSetterType();
      } else {
         throw new JsonbException("Empty property: " + this.name);
      }
   }

   Type getGetterType() {
      return this.getGetter() != null ? this.getGetter().getGenericReturnType() : null;
   }

   Type getSetterType() {
      Type[] genericParameterTypes = this.getSetter().getGenericParameterTypes();
      if (genericParameterTypes.length != 1) {
         throw new JsonbException("Invalid count of arguments for setter: " + this.getSetter());
      } else {
         return genericParameterTypes[0];
      }
   }

   public JsonbAnnotatedElement<Field> getFieldElement() {
      return this.fieldElement;
   }

   public JsonbAnnotatedElement<Method> getGetterElement() {
      return this.getterElement;
   }

   public JsonbAnnotatedElement<Method> getSetterElement() {
      return this.setterElement;
   }
}
