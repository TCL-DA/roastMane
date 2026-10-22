package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Collections;
import java.util.Iterator;

public class SimpleBeanPropertyDefinition extends BeanPropertyDefinition {
   protected final AnnotationIntrospector _annotationIntrospector;
   protected final AnnotatedMember _member;
   protected final PropertyMetadata _metadata;
   protected final PropertyName _fullName;
   protected final Value _inclusion;

   protected SimpleBeanPropertyDefinition(
      AnnotationIntrospector intr, AnnotatedMember member, PropertyName fullName, PropertyMetadata metadata, Value inclusion
   ) {
      this._annotationIntrospector = intr;
      this._member = member;
      this._fullName = fullName;
      this._metadata = metadata == null ? PropertyMetadata.STD_OPTIONAL : metadata;
      this._inclusion = inclusion;
   }

   public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member) {
      return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, PropertyName.construct(member.getName()), null, EMPTY_INCLUDE);
   }

   public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name) {
      return construct(config, member, name, null, EMPTY_INCLUDE);
   }

   public static SimpleBeanPropertyDefinition construct(
      MapperConfig<?> config, AnnotatedMember member, PropertyName name, PropertyMetadata metadata, Include inclusion
   ) {
      Value inclValue = inclusion != null && inclusion != Include.USE_DEFAULTS ? Value.construct(inclusion, null) : EMPTY_INCLUDE;
      return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, name, metadata, inclValue);
   }

   public static SimpleBeanPropertyDefinition construct(
      MapperConfig<?> config, AnnotatedMember member, PropertyName name, PropertyMetadata metadata, Value inclusion
   ) {
      return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, name, metadata, inclusion);
   }

   @Override
   public BeanPropertyDefinition withSimpleName(String newName) {
      return this._fullName.hasSimpleName(newName) && !this._fullName.hasNamespace()
         ? this
         : new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, new PropertyName(newName), this._metadata, this._inclusion);
   }

   @Override
   public BeanPropertyDefinition withName(PropertyName newName) {
      return this._fullName.equals(newName)
         ? this
         : new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, newName, this._metadata, this._inclusion);
   }

   public BeanPropertyDefinition withMetadata(PropertyMetadata metadata) {
      return metadata.equals(this._metadata)
         ? this
         : new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, this._fullName, metadata, this._inclusion);
   }

   public BeanPropertyDefinition withInclusion(Value inclusion) {
      return this._inclusion == inclusion
         ? this
         : new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, this._fullName, this._metadata, inclusion);
   }

   @Override
   public String getName() {
      return this._fullName.getSimpleName();
   }

   @Override
   public PropertyName getFullName() {
      return this._fullName;
   }

   @Override
   public boolean hasName(PropertyName name) {
      return this._fullName.equals(name);
   }

   @Override
   public String getInternalName() {
      return this.getName();
   }

   @Override
   public PropertyName getWrapperName() {
      return this._annotationIntrospector != null && this._member != null ? this._annotationIntrospector.findWrapperName(this._member) : null;
   }

   @Override
   public boolean isExplicitlyIncluded() {
      return false;
   }

   @Override
   public boolean isExplicitlyNamed() {
      return false;
   }

   @Override
   public PropertyMetadata getMetadata() {
      return this._metadata;
   }

   @Override
   public JavaType getPrimaryType() {
      return this._member == null ? TypeFactory.unknownType() : this._member.getType();
   }

   @Override
   public Class<?> getRawPrimaryType() {
      return this._member == null ? Object.class : this._member.getRawType();
   }

   @Override
   public Value findInclusion() {
      return this._inclusion;
   }

   @Override
   public boolean hasGetter() {
      return this.getGetter() != null;
   }

   @Override
   public boolean hasSetter() {
      return this.getSetter() != null;
   }

   @Override
   public boolean hasField() {
      return this._member instanceof AnnotatedField;
   }

   @Override
   public boolean hasConstructorParameter() {
      return this._member instanceof AnnotatedParameter;
   }

   @Override
   public AnnotatedMethod getGetter() {
      return this._member instanceof AnnotatedMethod && ((AnnotatedMethod)this._member).getParameterCount() == 0 ? (AnnotatedMethod)this._member : null;
   }

   @Override
   public AnnotatedMethod getSetter() {
      return this._member instanceof AnnotatedMethod && ((AnnotatedMethod)this._member).getParameterCount() == 1 ? (AnnotatedMethod)this._member : null;
   }

   @Override
   public AnnotatedField getField() {
      return this._member instanceof AnnotatedField ? (AnnotatedField)this._member : null;
   }

   @Override
   public AnnotatedParameter getConstructorParameter() {
      return this._member instanceof AnnotatedParameter ? (AnnotatedParameter)this._member : null;
   }

   @Override
   public Iterator<AnnotatedParameter> getConstructorParameters() {
      AnnotatedParameter param = this.getConstructorParameter();
      return param == null ? ClassUtil.emptyIterator() : Collections.singleton(param).iterator();
   }

   @Override
   public AnnotatedMember getPrimaryMember() {
      return this._member;
   }
}
