package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnnotationIntrospectorPair extends AnnotationIntrospector implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final AnnotationIntrospector _primary;
   protected final AnnotationIntrospector _secondary;

   public AnnotationIntrospectorPair(AnnotationIntrospector p, AnnotationIntrospector s) {
      this._primary = p;
      this._secondary = s;
   }

   @Override
   public Version version() {
      return this._primary.version();
   }

   public static AnnotationIntrospector create(AnnotationIntrospector primary, AnnotationIntrospector secondary) {
      if (primary == null) {
         return secondary;
      } else {
         return (AnnotationIntrospector)(secondary == null ? primary : new AnnotationIntrospectorPair(primary, secondary));
      }
   }

   @Override
   public Collection<AnnotationIntrospector> allIntrospectors() {
      return this.allIntrospectors(new ArrayList<>());
   }

   @Override
   public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
      this._primary.allIntrospectors(result);
      this._secondary.allIntrospectors(result);
      return result;
   }

   @Override
   public boolean isAnnotationBundle(Annotation ann) {
      return this._primary.isAnnotationBundle(ann) || this._secondary.isAnnotationBundle(ann);
   }

   @Override
   public PropertyName findRootName(AnnotatedClass ac) {
      PropertyName name1 = this._primary.findRootName(ac);
      if (name1 == null) {
         return this._secondary.findRootName(ac);
      } else if (name1.hasSimpleName()) {
         return name1;
      } else {
         PropertyName name2 = this._secondary.findRootName(ac);
         return name2 == null ? name1 : name2;
      }
   }

   @Override
   public Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated ann) {
      Value v2 = this._secondary.findPropertyIgnoralByName(config, ann);
      Value v1 = this._primary.findPropertyIgnoralByName(config, ann);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   @Override
   public com.fasterxml.jackson.annotation.JsonIncludeProperties.Value findPropertyInclusionByName(MapperConfig<?> config, Annotated a) {
      com.fasterxml.jackson.annotation.JsonIncludeProperties.Value v2 = this._secondary.findPropertyInclusionByName(config, a);
      com.fasterxml.jackson.annotation.JsonIncludeProperties.Value v1 = this._primary.findPropertyInclusionByName(config, a);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   @Override
   public Boolean isIgnorableType(AnnotatedClass ac) {
      Boolean result = this._primary.isIgnorableType(ac);
      if (result == null) {
         result = this._secondary.isIgnorableType(ac);
      }

      return result;
   }

   @Override
   public Object findFilterId(Annotated ann) {
      Object id = this._primary.findFilterId(ann);
      if (id == null) {
         id = this._secondary.findFilterId(ann);
      }

      return id;
   }

   @Override
   public Object findNamingStrategy(AnnotatedClass ac) {
      Object str = this._primary.findNamingStrategy(ac);
      if (str == null) {
         str = this._secondary.findNamingStrategy(ac);
      }

      return str;
   }

   @Override
   public String findClassDescription(AnnotatedClass ac) {
      String str = this._primary.findClassDescription(ac);
      if (str == null || str.isEmpty()) {
         str = this._secondary.findClassDescription(ac);
      }

      return str;
   }

   @Deprecated
   @Override
   public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
      String[] result = this._primary.findPropertiesToIgnore(ac, forSerialization);
      if (result == null) {
         result = this._secondary.findPropertiesToIgnore(ac, forSerialization);
      }

      return result;
   }

   @Deprecated
   @Override
   public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
      Boolean result = this._primary.findIgnoreUnknownProperties(ac);
      if (result == null) {
         result = this._secondary.findIgnoreUnknownProperties(ac);
      }

      return result;
   }

   @Deprecated
   @Override
   public Value findPropertyIgnorals(Annotated a) {
      Value v2 = this._secondary.findPropertyIgnorals(a);
      Value v1 = this._primary.findPropertyIgnorals(a);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   @Override
   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
      checker = this._secondary.findAutoDetectVisibility(ac, checker);
      return this._primary.findAutoDetectVisibility(ac, checker);
   }

   @Override
   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
      TypeResolverBuilder<?> b = this._primary.findTypeResolver(config, ac, baseType);
      if (b == null) {
         b = this._secondary.findTypeResolver(config, ac, baseType);
      }

      return b;
   }

   @Override
   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
      TypeResolverBuilder<?> b = this._primary.findPropertyTypeResolver(config, am, baseType);
      if (b == null) {
         b = this._secondary.findPropertyTypeResolver(config, am, baseType);
      }

      return b;
   }

   @Override
   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
      TypeResolverBuilder<?> b = this._primary.findPropertyContentTypeResolver(config, am, baseType);
      if (b == null) {
         b = this._secondary.findPropertyContentTypeResolver(config, am, baseType);
      }

      return b;
   }

   @Override
   public List<NamedType> findSubtypes(Annotated a) {
      List<NamedType> types1 = this._primary.findSubtypes(a);
      List<NamedType> types2 = this._secondary.findSubtypes(a);
      if (types1 == null || types1.isEmpty()) {
         return types2;
      } else if (types2 != null && !types2.isEmpty()) {
         ArrayList<NamedType> result = new ArrayList<>(types1.size() + types2.size());
         result.addAll(types1);
         result.addAll(types2);
         return result;
      } else {
         return types1;
      }
   }

   @Override
   public String findTypeName(AnnotatedClass ac) {
      String name = this._primary.findTypeName(ac);
      if (name == null || name.isEmpty()) {
         name = this._secondary.findTypeName(ac);
      }

      return name;
   }

   @Override
   public AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member) {
      AnnotationIntrospector.ReferenceProperty r = this._primary.findReferenceType(member);
      return r == null ? this._secondary.findReferenceType(member) : r;
   }

   @Override
   public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
      NameTransformer r = this._primary.findUnwrappingNameTransformer(member);
      return r == null ? this._secondary.findUnwrappingNameTransformer(member) : r;
   }

   @Override
   public com.fasterxml.jackson.annotation.JacksonInject.Value findInjectableValue(AnnotatedMember m) {
      com.fasterxml.jackson.annotation.JacksonInject.Value r = this._primary.findInjectableValue(m);
      if (r == null || r.getUseInput() == null) {
         com.fasterxml.jackson.annotation.JacksonInject.Value secondary = this._secondary.findInjectableValue(m);
         if (secondary != null) {
            r = r == null ? secondary : r.withUseInput(secondary.getUseInput());
         }
      }

      return r;
   }

   @Override
   public boolean hasIgnoreMarker(AnnotatedMember m) {
      return this._primary.hasIgnoreMarker(m) || this._secondary.hasIgnoreMarker(m);
   }

   @Override
   public Boolean hasRequiredMarker(AnnotatedMember m) {
      Boolean r = this._primary.hasRequiredMarker(m);
      return r == null ? this._secondary.hasRequiredMarker(m) : r;
   }

   @Deprecated
   @Override
   public Object findInjectableValueId(AnnotatedMember m) {
      Object r = this._primary.findInjectableValueId(m);
      return r == null ? this._secondary.findInjectableValueId(m) : r;
   }

   @Override
   public Object findSerializer(Annotated am) {
      Object r = this._primary.findSerializer(am);
      return this._isExplicitClassOrOb(r, JsonSerializer.None.class)
         ? r
         : this._explicitClassOrOb(this._secondary.findSerializer(am), JsonSerializer.None.class);
   }

   @Override
   public Object findKeySerializer(Annotated a) {
      Object r = this._primary.findKeySerializer(a);
      return this._isExplicitClassOrOb(r, JsonSerializer.None.class)
         ? r
         : this._explicitClassOrOb(this._secondary.findKeySerializer(a), JsonSerializer.None.class);
   }

   @Override
   public Object findContentSerializer(Annotated a) {
      Object r = this._primary.findContentSerializer(a);
      return this._isExplicitClassOrOb(r, JsonSerializer.None.class)
         ? r
         : this._explicitClassOrOb(this._secondary.findContentSerializer(a), JsonSerializer.None.class);
   }

   @Override
   public Object findNullSerializer(Annotated a) {
      Object r = this._primary.findNullSerializer(a);
      return this._isExplicitClassOrOb(r, JsonSerializer.None.class)
         ? r
         : this._explicitClassOrOb(this._secondary.findNullSerializer(a), JsonSerializer.None.class);
   }

   @Deprecated
   @Override
   public Include findSerializationInclusion(Annotated a, Include defValue) {
      defValue = this._secondary.findSerializationInclusion(a, defValue);
      return this._primary.findSerializationInclusion(a, defValue);
   }

   @Deprecated
   @Override
   public Include findSerializationInclusionForContent(Annotated a, Include defValue) {
      defValue = this._secondary.findSerializationInclusionForContent(a, defValue);
      return this._primary.findSerializationInclusionForContent(a, defValue);
   }

   @Override
   public com.fasterxml.jackson.annotation.JsonInclude.Value findPropertyInclusion(Annotated a) {
      com.fasterxml.jackson.annotation.JsonInclude.Value v2 = this._secondary.findPropertyInclusion(a);
      com.fasterxml.jackson.annotation.JsonInclude.Value v1 = this._primary.findPropertyInclusion(a);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   @Override
   public JsonSerialize.Typing findSerializationTyping(Annotated a) {
      JsonSerialize.Typing r = this._primary.findSerializationTyping(a);
      return r == null ? this._secondary.findSerializationTyping(a) : r;
   }

   @Override
   public Object findSerializationConverter(Annotated a) {
      Object r = this._primary.findSerializationConverter(a);
      return r == null ? this._secondary.findSerializationConverter(a) : r;
   }

   @Override
   public Object findSerializationContentConverter(AnnotatedMember a) {
      Object r = this._primary.findSerializationContentConverter(a);
      return r == null ? this._secondary.findSerializationContentConverter(a) : r;
   }

   @Override
   public Class<?>[] findViews(Annotated a) {
      Class<?>[] result = this._primary.findViews(a);
      if (result == null) {
         result = this._secondary.findViews(a);
      }

      return result;
   }

   @Override
   public Boolean isTypeId(AnnotatedMember member) {
      Boolean b = this._primary.isTypeId(member);
      return b == null ? this._secondary.isTypeId(member) : b;
   }

   @Override
   public ObjectIdInfo findObjectIdInfo(Annotated ann) {
      ObjectIdInfo r = this._primary.findObjectIdInfo(ann);
      return r == null ? this._secondary.findObjectIdInfo(ann) : r;
   }

   @Override
   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
      objectIdInfo = this._secondary.findObjectReferenceInfo(ann, objectIdInfo);
      return this._primary.findObjectReferenceInfo(ann, objectIdInfo);
   }

   @Override
   public com.fasterxml.jackson.annotation.JsonFormat.Value findFormat(Annotated ann) {
      com.fasterxml.jackson.annotation.JsonFormat.Value v1 = this._primary.findFormat(ann);
      com.fasterxml.jackson.annotation.JsonFormat.Value v2 = this._secondary.findFormat(ann);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   @Override
   public PropertyName findWrapperName(Annotated ann) {
      PropertyName name = this._primary.findWrapperName(ann);
      if (name == null) {
         name = this._secondary.findWrapperName(ann);
      } else if (name == PropertyName.USE_DEFAULT) {
         PropertyName name2 = this._secondary.findWrapperName(ann);
         if (name2 != null) {
            name = name2;
         }
      }

      return name;
   }

   @Override
   public String findPropertyDefaultValue(Annotated ann) {
      String str = this._primary.findPropertyDefaultValue(ann);
      return str != null && !str.isEmpty() ? str : this._secondary.findPropertyDefaultValue(ann);
   }

   @Override
   public String findPropertyDescription(Annotated ann) {
      String r = this._primary.findPropertyDescription(ann);
      return r == null ? this._secondary.findPropertyDescription(ann) : r;
   }

   @Override
   public Integer findPropertyIndex(Annotated ann) {
      Integer r = this._primary.findPropertyIndex(ann);
      return r == null ? this._secondary.findPropertyIndex(ann) : r;
   }

   @Override
   public String findImplicitPropertyName(AnnotatedMember ann) {
      String r = this._primary.findImplicitPropertyName(ann);
      return r == null ? this._secondary.findImplicitPropertyName(ann) : r;
   }

   @Override
   public List<PropertyName> findPropertyAliases(Annotated ann) {
      List<PropertyName> r = this._primary.findPropertyAliases(ann);
      return r == null ? this._secondary.findPropertyAliases(ann) : r;
   }

   @Override
   public Access findPropertyAccess(Annotated ann) {
      Access acc = this._primary.findPropertyAccess(ann);
      if (acc != null && acc != Access.AUTO) {
         return acc;
      } else {
         acc = this._secondary.findPropertyAccess(ann);
         return acc != null ? acc : Access.AUTO;
      }
   }

   @Override
   public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
      AnnotatedMethod res = this._primary.resolveSetterConflict(config, setter1, setter2);
      if (res == null) {
         res = this._secondary.resolveSetterConflict(config, setter1, setter2);
      }

      return res;
   }

   @Override
   public PropertyName findRenameByField(MapperConfig<?> config, AnnotatedField f, PropertyName implName) {
      PropertyName n = this._secondary.findRenameByField(config, f, implName);
      if (n == null) {
         n = this._primary.findRenameByField(config, f, implName);
      }

      return n;
   }

   @Override
   public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      JavaType t = this._secondary.refineSerializationType(config, a, baseType);
      return this._primary.refineSerializationType(config, a, t);
   }

   @Deprecated
   @Override
   public Class<?> findSerializationType(Annotated a) {
      Class<?> r = this._primary.findSerializationType(a);
      return r == null ? this._secondary.findSerializationType(a) : r;
   }

   @Deprecated
   @Override
   public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
      Class<?> r = this._primary.findSerializationKeyType(am, baseType);
      return r == null ? this._secondary.findSerializationKeyType(am, baseType) : r;
   }

   @Deprecated
   @Override
   public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
      Class<?> r = this._primary.findSerializationContentType(am, baseType);
      return r == null ? this._secondary.findSerializationContentType(am, baseType) : r;
   }

   @Override
   public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
      String[] r = this._primary.findSerializationPropertyOrder(ac);
      return r == null ? this._secondary.findSerializationPropertyOrder(ac) : r;
   }

   @Override
   public Boolean findSerializationSortAlphabetically(Annotated ann) {
      Boolean r = this._primary.findSerializationSortAlphabetically(ann);
      return r == null ? this._secondary.findSerializationSortAlphabetically(ann) : r;
   }

   @Override
   public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {
      this._primary.findAndAddVirtualProperties(config, ac, properties);
      this._secondary.findAndAddVirtualProperties(config, ac, properties);
   }

   @Override
   public PropertyName findNameForSerialization(Annotated a) {
      PropertyName n = this._primary.findNameForSerialization(a);
      if (n == null) {
         n = this._secondary.findNameForSerialization(a);
      } else if (n == PropertyName.USE_DEFAULT) {
         PropertyName n2 = this._secondary.findNameForSerialization(a);
         if (n2 != null) {
            n = n2;
         }
      }

      return n;
   }

   @Override
   public Boolean hasAsKey(MapperConfig<?> config, Annotated a) {
      Boolean b = this._primary.hasAsKey(config, a);
      if (b == null) {
         b = this._secondary.hasAsKey(config, a);
      }

      return b;
   }

   @Override
   public Boolean hasAsValue(Annotated a) {
      Boolean b = this._primary.hasAsValue(a);
      if (b == null) {
         b = this._secondary.hasAsValue(a);
      }

      return b;
   }

   @Override
   public Boolean hasAnyGetter(Annotated a) {
      Boolean b = this._primary.hasAnyGetter(a);
      if (b == null) {
         b = this._secondary.hasAnyGetter(a);
      }

      return b;
   }

   @Override
   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
      names = this._secondary.findEnumValues(enumType, enumValues, names);
      return this._primary.findEnumValues(enumType, enumValues, names);
   }

   @Override
   public void findEnumAliases(Class<?> enumType, Enum<?>[] enumValues, String[][] aliases) {
      this._secondary.findEnumAliases(enumType, enumValues, aliases);
      this._primary.findEnumAliases(enumType, enumValues, aliases);
   }

   @Override
   public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
      Enum<?> en = this._primary.findDefaultEnumValue(enumCls);
      return en == null ? this._secondary.findDefaultEnumValue(enumCls) : en;
   }

   @Deprecated
   @Override
   public String findEnumValue(Enum<?> value) {
      String r = this._primary.findEnumValue(value);
      return r == null ? this._secondary.findEnumValue(value) : r;
   }

   @Deprecated
   @Override
   public boolean hasAsValueAnnotation(AnnotatedMethod am) {
      return this._primary.hasAsValueAnnotation(am) || this._secondary.hasAsValueAnnotation(am);
   }

   @Deprecated
   @Override
   public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
      return this._primary.hasAnyGetterAnnotation(am) || this._secondary.hasAnyGetterAnnotation(am);
   }

   @Override
   public Object findDeserializer(Annotated a) {
      Object r = this._primary.findDeserializer(a);
      return this._isExplicitClassOrOb(r, JsonDeserializer.None.class)
         ? r
         : this._explicitClassOrOb(this._secondary.findDeserializer(a), JsonDeserializer.None.class);
   }

   @Override
   public Object findKeyDeserializer(Annotated a) {
      Object r = this._primary.findKeyDeserializer(a);
      return this._isExplicitClassOrOb(r, KeyDeserializer.None.class)
         ? r
         : this._explicitClassOrOb(this._secondary.findKeyDeserializer(a), KeyDeserializer.None.class);
   }

   @Override
   public Object findContentDeserializer(Annotated am) {
      Object r = this._primary.findContentDeserializer(am);
      return this._isExplicitClassOrOb(r, JsonDeserializer.None.class)
         ? r
         : this._explicitClassOrOb(this._secondary.findContentDeserializer(am), JsonDeserializer.None.class);
   }

   @Override
   public Object findDeserializationConverter(Annotated a) {
      Object ob = this._primary.findDeserializationConverter(a);
      return ob == null ? this._secondary.findDeserializationConverter(a) : ob;
   }

   @Override
   public Object findDeserializationContentConverter(AnnotatedMember a) {
      Object ob = this._primary.findDeserializationContentConverter(a);
      return ob == null ? this._secondary.findDeserializationContentConverter(a) : ob;
   }

   @Override
   public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      JavaType t = this._secondary.refineDeserializationType(config, a, baseType);
      return this._primary.refineDeserializationType(config, a, t);
   }

   @Deprecated
   @Override
   public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
      Class<?> r = this._primary.findDeserializationType(am, baseType);
      return r != null ? r : this._secondary.findDeserializationType(am, baseType);
   }

   @Deprecated
   @Override
   public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
      Class<?> result = this._primary.findDeserializationKeyType(am, baseKeyType);
      return result == null ? this._secondary.findDeserializationKeyType(am, baseKeyType) : result;
   }

   @Deprecated
   @Override
   public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
      Class<?> result = this._primary.findDeserializationContentType(am, baseContentType);
      return result == null ? this._secondary.findDeserializationContentType(am, baseContentType) : result;
   }

   @Override
   public Object findValueInstantiator(AnnotatedClass ac) {
      Object result = this._primary.findValueInstantiator(ac);
      return result == null ? this._secondary.findValueInstantiator(ac) : result;
   }

   @Override
   public Class<?> findPOJOBuilder(AnnotatedClass ac) {
      Class<?> result = this._primary.findPOJOBuilder(ac);
      return result == null ? this._secondary.findPOJOBuilder(ac) : result;
   }

   @Override
   public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
      JsonPOJOBuilder.Value result = this._primary.findPOJOBuilderConfig(ac);
      return result == null ? this._secondary.findPOJOBuilderConfig(ac) : result;
   }

   @Override
   public PropertyName findNameForDeserialization(Annotated a) {
      PropertyName n = this._primary.findNameForDeserialization(a);
      if (n == null) {
         n = this._secondary.findNameForDeserialization(a);
      } else if (n == PropertyName.USE_DEFAULT) {
         PropertyName n2 = this._secondary.findNameForDeserialization(a);
         if (n2 != null) {
            n = n2;
         }
      }

      return n;
   }

   @Override
   public Boolean hasAnySetter(Annotated a) {
      Boolean b = this._primary.hasAnySetter(a);
      if (b == null) {
         b = this._secondary.hasAnySetter(a);
      }

      return b;
   }

   @Override
   public com.fasterxml.jackson.annotation.JsonSetter.Value findSetterInfo(Annotated a) {
      com.fasterxml.jackson.annotation.JsonSetter.Value v2 = this._secondary.findSetterInfo(a);
      com.fasterxml.jackson.annotation.JsonSetter.Value v1 = this._primary.findSetterInfo(a);
      return v2 == null ? v1 : v2.withOverrides(v1);
   }

   @Override
   public Boolean findMergeInfo(Annotated a) {
      Boolean b = this._primary.findMergeInfo(a);
      if (b == null) {
         b = this._secondary.findMergeInfo(a);
      }

      return b;
   }

   @Deprecated
   @Override
   public boolean hasCreatorAnnotation(Annotated a) {
      return this._primary.hasCreatorAnnotation(a) || this._secondary.hasCreatorAnnotation(a);
   }

   @Deprecated
   @Override
   public Mode findCreatorBinding(Annotated a) {
      Mode mode = this._primary.findCreatorBinding(a);
      return mode != null ? mode : this._secondary.findCreatorBinding(a);
   }

   @Override
   public Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
      Mode mode = this._primary.findCreatorAnnotation(config, a);
      return mode == null ? this._secondary.findCreatorAnnotation(config, a) : mode;
   }

   @Deprecated
   @Override
   public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
      return this._primary.hasAnySetterAnnotation(am) || this._secondary.hasAnySetterAnnotation(am);
   }

   protected boolean _isExplicitClassOrOb(Object maybeCls, Class<?> implicit) {
      if (maybeCls == null || maybeCls == implicit) {
         return false;
      } else {
         return maybeCls instanceof Class ? !ClassUtil.isBogusClass((Class<?>)maybeCls) : true;
      }
   }

   protected Object _explicitClassOrOb(Object maybeCls, Class<?> implicit) {
      if (maybeCls == null || maybeCls == implicit) {
         return null;
      } else {
         return maybeCls instanceof Class && ClassUtil.isBogusClass((Class<?>)maybeCls) ? null : maybeCls;
      }
   }
}
