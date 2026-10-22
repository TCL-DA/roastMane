package com.fasterxml.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public abstract class MapperConfigBase<CFG extends ConfigFeature, T extends MapperConfigBase<CFG, T>> extends MapperConfig<T> implements Serializable {
   protected static final ConfigOverride EMPTY_OVERRIDE = ConfigOverride.empty();
   private static final long DEFAULT_MAPPER_FEATURES = MapperFeature.collectLongDefaults();
   private static final long AUTO_DETECT_MASK = MapperFeature.AUTO_DETECT_FIELDS.getLongMask()
      | MapperFeature.AUTO_DETECT_GETTERS.getLongMask()
      | MapperFeature.AUTO_DETECT_IS_GETTERS.getLongMask()
      | MapperFeature.AUTO_DETECT_SETTERS.getLongMask()
      | MapperFeature.AUTO_DETECT_CREATORS.getLongMask();
   protected final SimpleMixInResolver _mixIns;
   protected final SubtypeResolver _subtypeResolver;
   protected final PropertyName _rootName;
   protected final Class<?> _view;
   protected final ContextAttributes _attributes;
   protected final RootNameLookup _rootNames;
   protected final ConfigOverrides _configOverrides;
   protected final DatatypeFeatures _datatypeFeatures;

   protected MapperConfigBase(
      BaseSettings base,
      SubtypeResolver str,
      SimpleMixInResolver mixins,
      RootNameLookup rootNames,
      ConfigOverrides configOverrides,
      DatatypeFeatures datatypeFeatures
   ) {
      super(base, DEFAULT_MAPPER_FEATURES);
      this._mixIns = mixins;
      this._subtypeResolver = str;
      this._rootNames = rootNames;
      this._rootName = null;
      this._view = null;
      this._attributes = ContextAttributes.getEmpty();
      this._configOverrides = configOverrides;
      this._datatypeFeatures = datatypeFeatures;
   }

   protected MapperConfigBase(
      MapperConfigBase<CFG, T> src, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides
   ) {
      super(src, src._base.copy());
      this._mixIns = mixins;
      this._subtypeResolver = str;
      this._rootNames = rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, BaseSettings base) {
      super(src, base);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, long mapperFeatures) {
      super(src, mapperFeatures);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, SubtypeResolver str) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = str;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, PropertyName rootName) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, Class<?> view) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, SimpleMixInResolver mixins) {
      super(src);
      this._mixIns = mixins;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, ContextAttributes attr) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = attr;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = src._datatypeFeatures;
   }

   protected MapperConfigBase(MapperConfigBase<CFG, T> src, DatatypeFeatures datatypeFeatures) {
      super(src);
      this._mixIns = src._mixIns;
      this._subtypeResolver = src._subtypeResolver;
      this._rootNames = src._rootNames;
      this._rootName = src._rootName;
      this._view = src._view;
      this._attributes = src._attributes;
      this._configOverrides = src._configOverrides;
      this._datatypeFeatures = datatypeFeatures;
   }

   protected abstract T _withBase(BaseSettings var1);

   protected abstract T _withMapperFeatures(long var1);

   protected abstract T _with(DatatypeFeatures var1);

   protected DatatypeFeatures _datatypeFeatures() {
      return this._datatypeFeatures;
   }

   public final T with(MapperFeature... features) {
      long newMapperFlags = this._mapperFeatures;

      for (MapperFeature f : features) {
         newMapperFlags |= f.getLongMask();
      }

      return (T)(newMapperFlags == this._mapperFeatures ? this : this._withMapperFeatures(newMapperFlags));
   }

   public final T without(MapperFeature... features) {
      long newMapperFlags = this._mapperFeatures;

      for (MapperFeature f : features) {
         newMapperFlags &= ~f.getLongMask();
      }

      return (T)(newMapperFlags == this._mapperFeatures ? this : this._withMapperFeatures(newMapperFlags));
   }

   public final T with(MapperFeature feature, boolean state) {
      long newMapperFlags;
      if (state) {
         newMapperFlags = this._mapperFeatures | feature.getLongMask();
      } else {
         newMapperFlags = this._mapperFeatures & ~feature.getLongMask();
      }

      return (T)(newMapperFlags == this._mapperFeatures ? this : this._withMapperFeatures(newMapperFlags));
   }

   public final T with(DatatypeFeature feature) {
      return this._with(this._datatypeFeatures().with(feature));
   }

   public final T withFeatures(DatatypeFeature... features) {
      return this._with(this._datatypeFeatures().withFeatures(features));
   }

   public final T without(DatatypeFeature feature) {
      return this._with(this._datatypeFeatures().without(feature));
   }

   public final T withoutFeatures(DatatypeFeature... features) {
      return this._with(this._datatypeFeatures().withoutFeatures(features));
   }

   public final T with(DatatypeFeature feature, boolean state) {
      DatatypeFeatures features = this._datatypeFeatures();
      features = state ? features.with(feature) : features.without(feature);
      return this._with(features);
   }

   public final T with(AnnotationIntrospector ai) {
      return this._withBase(this._base.withAnnotationIntrospector(ai));
   }

   public final T withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
      return this._withBase(this._base.withAppendedAnnotationIntrospector(ai));
   }

   public final T withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
      return this._withBase(this._base.withInsertedAnnotationIntrospector(ai));
   }

   public final T with(ClassIntrospector ci) {
      return this._withBase(this._base.withClassIntrospector(ci));
   }

   public abstract T with(ContextAttributes var1);

   public T withAttributes(Map<?, ?> attributes) {
      return this.with(this.getAttributes().withSharedAttributes(attributes));
   }

   public T withAttribute(Object key, Object value) {
      return this.with(this.getAttributes().withSharedAttribute(key, value));
   }

   public T withoutAttribute(Object key) {
      return this.with(this.getAttributes().withoutSharedAttribute(key));
   }

   public final T with(TypeFactory tf) {
      return this._withBase(this._base.withTypeFactory(tf));
   }

   public final T with(TypeResolverBuilder<?> trb) {
      return this._withBase(this._base.withTypeResolverBuilder(trb));
   }

   public final T with(PropertyNamingStrategy pns) {
      return this._withBase(this._base.withPropertyNamingStrategy(pns));
   }

   public final T with(AccessorNamingStrategy.Provider p) {
      return this._withBase(this._base.withAccessorNaming(p));
   }

   public final T with(HandlerInstantiator hi) {
      return this._withBase(this._base.withHandlerInstantiator(hi));
   }

   public final T with(Base64Variant base64) {
      return this._withBase(this._base.with(base64));
   }

   public T with(DateFormat df) {
      return this._withBase(this._base.withDateFormat(df));
   }

   public final T with(Locale l) {
      return this._withBase(this._base.with(l));
   }

   public final T with(TimeZone tz) {
      return this._withBase(this._base.with(tz));
   }

   public abstract T withRootName(PropertyName var1);

   public T withRootName(String rootName) {
      return rootName == null ? this.withRootName((PropertyName)null) : this.withRootName(PropertyName.construct(rootName));
   }

   public abstract T with(SubtypeResolver var1);

   public abstract T withView(Class<?> var1);

   @Override
   public final SubtypeResolver getSubtypeResolver() {
      return this._subtypeResolver;
   }

   @Deprecated
   public final String getRootName() {
      return this._rootName == null ? null : this._rootName.getSimpleName();
   }

   public final PropertyName getFullRootName() {
      return this._rootName;
   }

   @Override
   public final Class<?> getActiveView() {
      return this._view;
   }

   @Override
   public final ContextAttributes getAttributes() {
      return this._attributes;
   }

   @Override
   public final ConfigOverride getConfigOverride(Class<?> type) {
      ConfigOverride override = this._configOverrides.findOverride(type);
      return override == null ? EMPTY_OVERRIDE : override;
   }

   @Override
   public final ConfigOverride findConfigOverride(Class<?> type) {
      return this._configOverrides.findOverride(type);
   }

   @Override
   public final Value getDefaultPropertyInclusion() {
      return this._configOverrides.getDefaultInclusion();
   }

   @Override
   public final Value getDefaultPropertyInclusion(Class<?> baseType) {
      Value v = this.getConfigOverride(baseType).getInclude();
      Value def = this.getDefaultPropertyInclusion();
      return def == null ? v : def.withOverrides(v);
   }

   @Override
   public final Value getDefaultInclusion(Class<?> baseType, Class<?> propertyType) {
      Value v = this.getConfigOverride(propertyType).getIncludeAsProperty();
      Value def = this.getDefaultPropertyInclusion(baseType);
      return def == null ? v : def.withOverrides(v);
   }

   @Override
   public final com.fasterxml.jackson.annotation.JsonFormat.Value getDefaultPropertyFormat(Class<?> type) {
      return this._configOverrides.findFormatDefaults(type);
   }

   @Override
   public final com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> type) {
      ConfigOverride overrides = this._configOverrides.findOverride(type);
      if (overrides != null) {
         com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value v = overrides.getIgnorals();
         if (v != null) {
            return v;
         }
      }

      return null;
   }

   @Override
   public final com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value getDefaultPropertyIgnorals(Class<?> baseType, AnnotatedClass actualClass) {
      AnnotationIntrospector intr = this.getAnnotationIntrospector();
      com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value base = intr == null ? null : intr.findPropertyIgnoralByName(this, actualClass);
      com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value overrides = this.getDefaultPropertyIgnorals(baseType);
      return com.fasterxml.jackson.annotation.JsonIgnoreProperties.Value.merge(base, overrides);
   }

   @Override
   public final com.fasterxml.jackson.annotation.JsonIncludeProperties.Value getDefaultPropertyInclusions(Class<?> baseType, AnnotatedClass actualClass) {
      AnnotationIntrospector intr = this.getAnnotationIntrospector();
      return intr == null ? null : intr.findPropertyInclusionByName(this, actualClass);
   }

   @Override
   public final VisibilityChecker<?> getDefaultVisibilityChecker() {
      VisibilityChecker<?> vchecker = this._configOverrides.getDefaultVisibility();
      if ((this._mapperFeatures & AUTO_DETECT_MASK) != AUTO_DETECT_MASK) {
         if (!this.isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
            vchecker = vchecker.withFieldVisibility(Visibility.NONE);
         }

         if (!this.isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
            vchecker = vchecker.withGetterVisibility(Visibility.NONE);
         }

         if (!this.isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
            vchecker = vchecker.withIsGetterVisibility(Visibility.NONE);
         }

         if (!this.isEnabled(MapperFeature.AUTO_DETECT_SETTERS)) {
            vchecker = vchecker.withSetterVisibility(Visibility.NONE);
         }

         if (!this.isEnabled(MapperFeature.AUTO_DETECT_CREATORS)) {
            vchecker = vchecker.withCreatorVisibility(Visibility.NONE);
         }
      }

      return vchecker;
   }

   @Override
   public final VisibilityChecker<?> getDefaultVisibilityChecker(Class<?> baseType, AnnotatedClass actualClass) {
      VisibilityChecker<?> vc;
      if (ClassUtil.isJDKClass(baseType)) {
         vc = VisibilityChecker.Std.allPublicInstance();
      } else {
         vc = this.getDefaultVisibilityChecker();
      }

      AnnotationIntrospector intr = this.getAnnotationIntrospector();
      if (intr != null) {
         vc = intr.findAutoDetectVisibility(actualClass, vc);
      }

      ConfigOverride overrides = this._configOverrides.findOverride(baseType);
      if (overrides != null) {
         vc = vc.withOverrides(overrides.getVisibility());
      }

      return vc;
   }

   @Override
   public final com.fasterxml.jackson.annotation.JsonSetter.Value getDefaultSetterInfo() {
      return this._configOverrides.getDefaultSetterInfo();
   }

   @Override
   public Boolean getDefaultMergeable() {
      return this._configOverrides.getDefaultMergeable();
   }

   @Override
   public Boolean getDefaultMergeable(Class<?> baseType) {
      ConfigOverride cfg = this._configOverrides.findOverride(baseType);
      if (cfg != null) {
         Boolean b = cfg.getMergeable();
         if (b != null) {
            return b;
         }
      }

      return this._configOverrides.getDefaultMergeable();
   }

   @Override
   public PropertyName findRootName(JavaType rootType) {
      return this._rootName != null ? this._rootName : this._rootNames.findRootName(rootType, this);
   }

   @Override
   public PropertyName findRootName(Class<?> rawRootType) {
      return this._rootName != null ? this._rootName : this._rootNames.findRootName(rawRootType, this);
   }

   @Override
   public final Class<?> findMixInClassFor(Class<?> cls) {
      return this._mixIns.findMixInClassFor(cls);
   }

   @Override
   public ClassIntrospector.MixInResolver copy() {
      throw new UnsupportedOperationException();
   }

   public final int mixInCount() {
      return this._mixIns.localSize();
   }
}
