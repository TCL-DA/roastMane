package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AnnotationIntrospector implements Versioned, Serializable {
   public static AnnotationIntrospector nopInstance() {
      return NopAnnotationIntrospector.instance;
   }

   public static AnnotationIntrospector pair(AnnotationIntrospector a1, AnnotationIntrospector a2) {
      return new AnnotationIntrospectorPair(a1, a2);
   }

   public Collection<AnnotationIntrospector> allIntrospectors() {
      return Collections.singletonList(this);
   }

   public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
      result.add(this);
      return result;
   }

   public abstract Version version();

   public boolean isAnnotationBundle(Annotation ann) {
      return false;
   }

   public ObjectIdInfo findObjectIdInfo(Annotated ann) {
      return null;
   }

   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
      return objectIdInfo;
   }

   public PropertyName findRootName(AnnotatedClass ac) {
      return null;
   }

   public Boolean isIgnorableType(AnnotatedClass ac) {
      return null;
   }

   public Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated ann) {
      return this.findPropertyIgnorals(ann);
   }

   public com.fasterxml.jackson.annotation.JsonIncludeProperties.Value findPropertyInclusionByName(MapperConfig<?> config, Annotated ann) {
      return com.fasterxml.jackson.annotation.JsonIncludeProperties.Value.all();
   }

   public Object findFilterId(Annotated ann) {
      return null;
   }

   public Object findNamingStrategy(AnnotatedClass ac) {
      return null;
   }

   public String findClassDescription(AnnotatedClass ac) {
      return null;
   }

   @Deprecated
   public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
      return null;
   }

   @Deprecated
   public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
      return null;
   }

   @Deprecated
   public Value findPropertyIgnorals(Annotated ac) {
      return Value.empty();
   }

   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
      return checker;
   }

   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
      return null;
   }

   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
      return null;
   }

   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
      return null;
   }

   public List<NamedType> findSubtypes(Annotated a) {
      return null;
   }

   public String findTypeName(AnnotatedClass ac) {
      return null;
   }

   public Boolean isTypeId(AnnotatedMember am) {
      return null;
   }

   public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
      return null;
   }

   public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
      return null;
   }

   public boolean hasIgnoreMarker(AnnotatedMember m) {
      return false;
   }

   public com.fasterxml.jackson.annotation.JacksonInject.Value findInjectableValue(AnnotatedMember m) {
      Object id = this.findInjectableValueId(m);
      return id != null ? com.fasterxml.jackson.annotation.JacksonInject.Value.forId(id) : null;
   }

   public Boolean hasRequiredMarker(AnnotatedMember m) {
      return null;
   }

   public Class<?>[] findViews(Annotated a) {
      return null;
   }

   public com.fasterxml.jackson.annotation.JsonFormat.Value findFormat(Annotated memberOrClass) {
      return com.fasterxml.jackson.annotation.JsonFormat.Value.empty();
   }

   public PropertyName findWrapperName(Annotated ann) {
      return null;
   }

   public String findPropertyDefaultValue(Annotated ann) {
      return null;
   }

   public String findPropertyDescription(Annotated ann) {
      return null;
   }

   public Integer findPropertyIndex(Annotated ann) {
      return null;
   }

   public String findImplicitPropertyName(AnnotatedMember member) {
      return null;
   }

   public List<PropertyName> findPropertyAliases(Annotated ann) {
      return null;
   }

   public Access findPropertyAccess(Annotated ann) {
      return null;
   }

   public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
      return null;
   }

   public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
      return null;
   }

   @Deprecated
   public Object findInjectableValueId(AnnotatedMember m) {
      return null;
   }

   public Object findSerializer(Annotated am) {
      return null;
   }

   public Object findKeySerializer(Annotated am) {
      return null;
   }

   public Object findContentSerializer(Annotated am) {
      return null;
   }

   public Object findNullSerializer(Annotated am) {
      return null;
   }

   public JsonSerialize.Typing findSerializationTyping(Annotated a) {
      return null;
   }

   public Object findSerializationConverter(Annotated a) {
      return null;
   }

   public Object findSerializationContentConverter(AnnotatedMember a) {
      return null;
   }

   public com.fasterxml.jackson.annotation.JsonInclude.Value findPropertyInclusion(Annotated a) {
      return com.fasterxml.jackson.annotation.JsonInclude.Value.empty();
   }

   @Deprecated
   public Include findSerializationInclusion(Annotated a, Include defValue) {
      return defValue;
   }

   @Deprecated
   public Include findSerializationInclusionForContent(Annotated a, Include defValue) {
      return defValue;
   }

   public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      return baseType;
   }

   @Deprecated
   public Class<?> findSerializationType(Annotated a) {
      return null;
   }

   @Deprecated
   public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
      return null;
   }

   @Deprecated
   public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
      return null;
   }

   public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
      return null;
   }

   public Boolean findSerializationSortAlphabetically(Annotated ann) {
      return null;
   }

   public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
   }

   public PropertyName findNameForSerialization(Annotated a) {
      return null;
   }

   public Boolean hasAsKey(MapperConfig<?> config, Annotated a) {
      return null;
   }

   public Boolean hasAsValue(Annotated a) {
      return a instanceof AnnotatedMethod && this.hasAsValueAnnotation((AnnotatedMethod)a) ? true : null;
   }

   public Boolean hasAnyGetter(Annotated ann) {
      return ann instanceof AnnotatedMethod && this.hasAnyGetterAnnotation((AnnotatedMethod)ann) ? true : null;
   }

   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
      return names;
   }

   public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliases) {
   }

   public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
      return null;
   }

   @Deprecated
   public String findEnumValue(Enum<?> value) {
      return value.name();
   }

   @Deprecated
   public boolean hasAsValueAnnotation(AnnotatedMethod am) {
      return false;
   }

   @Deprecated
   public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
      return false;
   }

   public Object findDeserializer(Annotated am) {
      return null;
   }

   public Object findKeyDeserializer(Annotated am) {
      return null;
   }

   public Object findContentDeserializer(Annotated am) {
      return null;
   }

   public Object findDeserializationConverter(Annotated a) {
      return null;
   }

   public Object findDeserializationContentConverter(AnnotatedMember a) {
      return null;
   }

   public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      return baseType;
   }

   @Deprecated
   public Class<?> findDeserializationType(Annotated ann, JavaType baseType) {
      return null;
   }

   @Deprecated
   public Class<?> findDeserializationKeyType(Annotated ann, JavaType baseKeyType) {
      return null;
   }

   @Deprecated
   public Class<?> findDeserializationContentType(Annotated ann, JavaType baseContentType) {
      return null;
   }

   public Object findValueInstantiator(AnnotatedClass ac) {
      return null;
   }

   public Class<?> findPOJOBuilder(AnnotatedClass ac) {
      return null;
   }

   public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
      return null;
   }

   public PropertyName findNameForDeserialization(Annotated ann) {
      return null;
   }

   public Boolean hasAnySetter(Annotated ann) {
      return null;
   }

   public com.fasterxml.jackson.annotation.JsonSetter.Value findSetterInfo(Annotated ann) {
      return com.fasterxml.jackson.annotation.JsonSetter.Value.empty();
   }

   public Boolean findMergeInfo(Annotated ann) {
      return null;
   }

   public Mode findCreatorAnnotation(MapperConfig<?> config, Annotated ann) {
      if (this.hasCreatorAnnotation(ann)) {
         Mode mode = this.findCreatorBinding(ann);
         if (mode == null) {
            mode = Mode.DEFAULT;
         }

         return mode;
      } else {
         return null;
      }
   }

   @Deprecated
   public boolean hasCreatorAnnotation(Annotated ann) {
      return false;
   }

   @Deprecated
   public Mode findCreatorBinding(Annotated ann) {
      return null;
   }

   @Deprecated
   public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
      return false;
   }

   protected <A extends Annotation> A _findAnnotation(Annotated ann, Class<A> annoClass) {
      return ann.getAnnotation(annoClass);
   }

   protected boolean _hasAnnotation(Annotated ann, Class<? extends Annotation> annoClass) {
      return ann.hasAnnotation(annoClass);
   }

   protected boolean _hasOneOf(Annotated ann, Class<? extends Annotation>[] annoClasses) {
      return ann.hasOneOf(annoClasses);
   }

   public static class ReferenceProperty {
      private final AnnotationIntrospector.ReferenceProperty.Type _type;
      private final String _name;

      public ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type t, String n) {
         this._type = t;
         this._name = n;
      }

      public static AnnotationIntrospector.ReferenceProperty managed(String name) {
         return new AnnotationIntrospector.ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.MANAGED_REFERENCE, name);
      }

      public static AnnotationIntrospector.ReferenceProperty back(String name) {
         return new AnnotationIntrospector.ReferenceProperty(AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE, name);
      }

      public AnnotationIntrospector.ReferenceProperty.Type getType() {
         return this._type;
      }

      public String getName() {
         return this._name;
      }

      public boolean isManagedReference() {
         return this._type == AnnotationIntrospector.ReferenceProperty.Type.MANAGED_REFERENCE;
      }

      public boolean isBackReference() {
         return this._type == AnnotationIntrospector.ReferenceProperty.Type.BACK_REFERENCE;
      }

      public static enum Type {
         MANAGED_REFERENCE,
         BACK_REFERENCE;
      }
   }

   public interface XmlExtensions {
      String findNamespace(MapperConfig<?> var1, Annotated var2);

      Boolean isOutputAsAttribute(MapperConfig<?> var1, Annotated var2);

      Boolean isOutputAsText(MapperConfig<?> var1, Annotated var2);

      Boolean isOutputAsCData(MapperConfig<?> var1, Annotated var2);
   }
}
