package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.Named;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public interface BeanProperty extends Named {
   Value EMPTY_FORMAT = new Value();
   com.fasterxml.jackson.annotation.JsonInclude.Value EMPTY_INCLUDE = com.fasterxml.jackson.annotation.JsonInclude.Value.empty();

   @Override
   String getName();

   PropertyName getFullName();

   JavaType getType();

   PropertyName getWrapperName();

   PropertyMetadata getMetadata();

   boolean isRequired();

   boolean isVirtual();

   <A extends Annotation> A getAnnotation(Class<A> var1);

   <A extends Annotation> A getContextAnnotation(Class<A> var1);

   AnnotatedMember getMember();

   @Deprecated
   Value findFormatOverrides(AnnotationIntrospector var1);

   Value findPropertyFormat(MapperConfig<?> var1, Class<?> var2);

   com.fasterxml.jackson.annotation.JsonInclude.Value findPropertyInclusion(MapperConfig<?> var1, Class<?> var2);

   List<PropertyName> findAliases(MapperConfig<?> var1);

   void depositSchemaProperty(JsonObjectFormatVisitor var1, SerializerProvider var2) throws JsonMappingException;

   public static class Bogus implements BeanProperty {
      @Override
      public String getName() {
         return "";
      }

      @Override
      public PropertyName getFullName() {
         return PropertyName.NO_NAME;
      }

      @Override
      public JavaType getType() {
         return TypeFactory.unknownType();
      }

      @Override
      public PropertyName getWrapperName() {
         return null;
      }

      @Override
      public PropertyMetadata getMetadata() {
         return PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
      }

      @Override
      public boolean isRequired() {
         return false;
      }

      @Override
      public boolean isVirtual() {
         return false;
      }

      @Override
      public <A extends Annotation> A getAnnotation(Class<A> acls) {
         return null;
      }

      @Override
      public <A extends Annotation> A getContextAnnotation(Class<A> acls) {
         return null;
      }

      @Override
      public AnnotatedMember getMember() {
         return null;
      }

      @Deprecated
      @Override
      public Value findFormatOverrides(AnnotationIntrospector intr) {
         return Value.empty();
      }

      @Override
      public Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType) {
         return Value.empty();
      }

      @Override
      public com.fasterxml.jackson.annotation.JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType) {
         return null;
      }

      @Override
      public List<PropertyName> findAliases(MapperConfig<?> config) {
         return Collections.emptyList();
      }

      @Override
      public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
      }
   }

   public static class Std implements BeanProperty, Serializable {
      private static final long serialVersionUID = 1L;
      protected final PropertyName _name;
      protected final JavaType _type;
      protected final PropertyName _wrapperName;
      protected final PropertyMetadata _metadata;
      protected final AnnotatedMember _member;

      public Std(PropertyName name, JavaType type, PropertyName wrapperName, AnnotatedMember member, PropertyMetadata metadata) {
         this._name = name;
         this._type = type;
         this._wrapperName = wrapperName;
         this._metadata = metadata;
         this._member = member;
      }

      @Deprecated
      public Std(PropertyName name, JavaType type, PropertyName wrapperName, Annotations contextAnnotations, AnnotatedMember member, PropertyMetadata metadata) {
         this(name, type, wrapperName, member, metadata);
      }

      public Std(BeanProperty.Std base, JavaType newType) {
         this(base._name, newType, base._wrapperName, base._member, base._metadata);
      }

      public BeanProperty.Std withType(JavaType type) {
         return new BeanProperty.Std(this, type);
      }

      @Override
      public <A extends Annotation> A getAnnotation(Class<A> acls) {
         return this._member == null ? null : this._member.getAnnotation(acls);
      }

      @Override
      public <A extends Annotation> A getContextAnnotation(Class<A> acls) {
         return null;
      }

      @Deprecated
      @Override
      public Value findFormatOverrides(AnnotationIntrospector intr) {
         if (this._member != null && intr != null) {
            Value v = intr.findFormat(this._member);
            if (v != null) {
               return v;
            }
         }

         return EMPTY_FORMAT;
      }

      @Override
      public Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType) {
         Value v0 = config.getDefaultPropertyFormat(baseType);
         AnnotationIntrospector intr = config.getAnnotationIntrospector();
         if (intr != null && this._member != null) {
            Value v = intr.findFormat(this._member);
            return v == null ? v0 : v0.withOverrides(v);
         } else {
            return v0;
         }
      }

      @Override
      public com.fasterxml.jackson.annotation.JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType) {
         com.fasterxml.jackson.annotation.JsonInclude.Value v0 = config.getDefaultInclusion(baseType, this._type.getRawClass());
         AnnotationIntrospector intr = config.getAnnotationIntrospector();
         if (intr != null && this._member != null) {
            com.fasterxml.jackson.annotation.JsonInclude.Value v = intr.findPropertyInclusion(this._member);
            return v == null ? v0 : v0.withOverrides(v);
         } else {
            return v0;
         }
      }

      @Override
      public List<PropertyName> findAliases(MapperConfig<?> config) {
         return Collections.emptyList();
      }

      @Override
      public String getName() {
         return this._name.getSimpleName();
      }

      @Override
      public PropertyName getFullName() {
         return this._name;
      }

      @Override
      public JavaType getType() {
         return this._type;
      }

      @Override
      public PropertyName getWrapperName() {
         return this._wrapperName;
      }

      @Override
      public boolean isRequired() {
         return this._metadata.isRequired();
      }

      @Override
      public PropertyMetadata getMetadata() {
         return this._metadata;
      }

      @Override
      public AnnotatedMember getMember() {
         return this._member;
      }

      @Override
      public boolean isVirtual() {
         return false;
      }

      @Override
      public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) {
         throw new UnsupportedOperationException("Instances of " + this.getClass().getName() + " should not get visited");
      }
   }
}
