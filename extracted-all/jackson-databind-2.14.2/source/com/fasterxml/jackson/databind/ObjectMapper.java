package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.cfg.BaseSettings;
import com.fasterxml.jackson.databind.cfg.CoercionConfigs;
import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.DatatypeFeature;
import com.fasterxml.jackson.databind.cfg.DatatypeFeatures;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.cfg.MutableCoercionConfig;
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.introspect.DefaultAccessorNamingStrategy;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.RootNameLookup;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ObjectMapper extends ObjectCodec implements Versioned, Serializable {
   private static final long serialVersionUID = 2L;
   protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
   protected static final BaseSettings DEFAULT_BASE = new BaseSettings(
      null,
      DEFAULT_ANNOTATION_INTROSPECTOR,
      null,
      TypeFactory.defaultInstance(),
      null,
      StdDateFormat.instance,
      null,
      Locale.getDefault(),
      null,
      Base64Variants.getDefaultVariant(),
      LaissezFaireSubTypeValidator.instance,
      new DefaultAccessorNamingStrategy.Provider()
   );
   protected final JsonFactory _jsonFactory;
   protected TypeFactory _typeFactory;
   protected InjectableValues _injectableValues;
   protected SubtypeResolver _subtypeResolver;
   protected final ConfigOverrides _configOverrides;
   protected final CoercionConfigs _coercionConfigs;
   protected SimpleMixInResolver _mixIns;
   protected SerializationConfig _serializationConfig;
   protected DefaultSerializerProvider _serializerProvider;
   protected SerializerFactory _serializerFactory;
   protected DeserializationConfig _deserializationConfig;
   protected DefaultDeserializationContext _deserializationContext;
   protected Set<Object> _registeredModuleTypes;
   protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers = new ConcurrentHashMap<>(64, 0.6F, 2);

   public ObjectMapper() {
      this(null, null, null);
   }

   public ObjectMapper(JsonFactory jf) {
      this(jf, null, null);
   }

   protected ObjectMapper(ObjectMapper src) {
      this(src, null);
   }

   protected ObjectMapper(ObjectMapper src, JsonFactory factory) {
      this._jsonFactory = factory != null ? factory : src._jsonFactory.copy();
      this._jsonFactory.setCodec(this);
      this._subtypeResolver = src._subtypeResolver.copy();
      this._typeFactory = src._typeFactory;
      this._injectableValues = src._injectableValues;
      this._configOverrides = src._configOverrides.copy();
      this._coercionConfigs = src._coercionConfigs.copy();
      this._mixIns = src._mixIns.copy();
      RootNameLookup rootNames = new RootNameLookup();
      this._serializationConfig = new SerializationConfig(src._serializationConfig, this._subtypeResolver, this._mixIns, rootNames, this._configOverrides);
      this._deserializationConfig = new DeserializationConfig(
         src._deserializationConfig, this._subtypeResolver, this._mixIns, rootNames, this._configOverrides, this._coercionConfigs
      );
      this._serializerProvider = src._serializerProvider.copy();
      this._deserializationContext = src._deserializationContext.copy();
      this._serializerFactory = src._serializerFactory;
      Set<Object> reg = src._registeredModuleTypes;
      if (reg == null) {
         this._registeredModuleTypes = null;
      } else {
         this._registeredModuleTypes = new LinkedHashSet<>(reg);
      }
   }

   public ObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
      if (jf == null) {
         this._jsonFactory = new MappingJsonFactory(this);
      } else {
         this._jsonFactory = jf;
         if (jf.getCodec() == null) {
            this._jsonFactory.setCodec(this);
         }
      }

      this._subtypeResolver = new StdSubtypeResolver();
      RootNameLookup rootNames = new RootNameLookup();
      this._typeFactory = TypeFactory.defaultInstance();
      SimpleMixInResolver mixins = new SimpleMixInResolver(null);
      this._mixIns = mixins;
      BaseSettings base = DEFAULT_BASE.withClassIntrospector(this.defaultClassIntrospector());
      this._configOverrides = new ConfigOverrides();
      this._coercionConfigs = new CoercionConfigs();
      this._serializationConfig = new SerializationConfig(
         base, this._subtypeResolver, mixins, rootNames, this._configOverrides, DatatypeFeatures.defaultFeatures()
      );
      this._deserializationConfig = new DeserializationConfig(
         base, this._subtypeResolver, mixins, rootNames, this._configOverrides, this._coercionConfigs, DatatypeFeatures.defaultFeatures()
      );
      boolean needOrder = this._jsonFactory.requiresPropertyOrdering();
      if (needOrder ^ this._serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
         this.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder);
      }

      this._serializerProvider = (DefaultSerializerProvider)(sp == null ? new DefaultSerializerProvider.Impl() : sp);
      this._deserializationContext = (DefaultDeserializationContext)(dc == null ? new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance) : dc);
      this._serializerFactory = BeanSerializerFactory.instance;
   }

   protected ClassIntrospector defaultClassIntrospector() {
      return new BasicClassIntrospector();
   }

   public ObjectMapper copy() {
      this._checkInvalidCopy(ObjectMapper.class);
      return new ObjectMapper(this);
   }

   public ObjectMapper copyWith(JsonFactory factory) {
      this._checkInvalidCopy(ObjectMapper.class);
      return new ObjectMapper(this, factory);
   }

   protected void _checkInvalidCopy(Class<?> exp) {
      if (this.getClass() != exp) {
         throw new IllegalStateException(
            "Failed copy()/copyWith(): " + this.getClass().getName() + " (version: " + this.version() + ") does not override copy()/copyWith(); it has to"
         );
      }
   }

   protected ObjectReader _newReader(DeserializationConfig config) {
      return new ObjectReader(this, config);
   }

   protected ObjectReader _newReader(
      DeserializationConfig config, JavaType valueType, Object valueToUpdate, FormatSchema schema, InjectableValues injectableValues
   ) {
      return new ObjectReader(this, config, valueType, valueToUpdate, schema, injectableValues);
   }

   protected ObjectWriter _newWriter(SerializationConfig config) {
      return new ObjectWriter(this, config);
   }

   protected ObjectWriter _newWriter(SerializationConfig config, FormatSchema schema) {
      return new ObjectWriter(this, config, schema);
   }

   protected ObjectWriter _newWriter(SerializationConfig config, JavaType rootType, PrettyPrinter pp) {
      return new ObjectWriter(this, config, rootType, pp);
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public ObjectMapper registerModule(Module module) {
      this._assertNotNull("module", module);
      String name = module.getModuleName();
      if (name == null) {
         throw new IllegalArgumentException("Module without defined name");
      } else {
         Version version = module.version();
         if (version == null) {
            throw new IllegalArgumentException("Module without defined version");
         } else {
            for (Module dep : module.getDependencies()) {
               this.registerModule(dep);
            }

            if (this.isEnabled(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)) {
               Object typeId = module.getTypeId();
               if (typeId != null) {
                  if (this._registeredModuleTypes == null) {
                     this._registeredModuleTypes = new LinkedHashSet<>();
                  }

                  if (!this._registeredModuleTypes.add(typeId)) {
                     return this;
                  }
               }
            }

            module.setupModule(new Module.SetupContext() {
               @Override
               public Version getMapperVersion() {
                  return ObjectMapper.this.version();
               }

               @Override
               public <C extends ObjectCodec> C getOwner() {
                  return (C)ObjectMapper.this;
               }

               @Override
               public TypeFactory getTypeFactory() {
                  return ObjectMapper.this._typeFactory;
               }

               @Override
               public boolean isEnabled(MapperFeature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               @Override
               public boolean isEnabled(DeserializationFeature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               @Override
               public boolean isEnabled(SerializationFeature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               @Override
               public boolean isEnabled(Feature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               @Override
               public boolean isEnabled(com.fasterxml.jackson.core.JsonParser.Feature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               @Override
               public boolean isEnabled(com.fasterxml.jackson.core.JsonGenerator.Feature f) {
                  return ObjectMapper.this.isEnabled(f);
               }

               @Override
               public MutableConfigOverride configOverride(Class<?> type) {
                  return ObjectMapper.this.configOverride(type);
               }

               @Override
               public void addDeserializers(Deserializers d) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalDeserializers(d);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               @Override
               public void addKeyDeserializers(KeyDeserializers d) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAdditionalKeyDeserializers(d);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               @Override
               public void addBeanDeserializerModifier(BeanDeserializerModifier modifier) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withDeserializerModifier(modifier);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               @Override
               public void addSerializers(Serializers s) {
                  ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalSerializers(s);
               }

               @Override
               public void addKeySerializers(Serializers s) {
                  ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withAdditionalKeySerializers(s);
               }

               @Override
               public void addBeanSerializerModifier(BeanSerializerModifier modifier) {
                  ObjectMapper.this._serializerFactory = ObjectMapper.this._serializerFactory.withSerializerModifier(modifier);
               }

               @Override
               public void addAbstractTypeResolver(AbstractTypeResolver resolver) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withAbstractTypeResolver(resolver);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               @Override
               public void addTypeModifier(TypeModifier modifier) {
                  TypeFactory f = ObjectMapper.this._typeFactory;
                  f = f.withModifier(modifier);
                  ObjectMapper.this.setTypeFactory(f);
               }

               @Override
               public void addValueInstantiators(ValueInstantiators instantiators) {
                  DeserializerFactory df = ObjectMapper.this._deserializationContext._factory.withValueInstantiators(instantiators);
                  ObjectMapper.this._deserializationContext = ObjectMapper.this._deserializationContext.with(df);
               }

               @Override
               public void setClassIntrospector(ClassIntrospector ci) {
                  ObjectMapper.this._deserializationConfig = ObjectMapper.this._deserializationConfig.with(ci);
                  ObjectMapper.this._serializationConfig = ObjectMapper.this._serializationConfig.with(ci);
               }

               @Override
               public void insertAnnotationIntrospector(AnnotationIntrospector ai) {
                  ObjectMapper.this._deserializationConfig = ObjectMapper.this._deserializationConfig.withInsertedAnnotationIntrospector(ai);
                  ObjectMapper.this._serializationConfig = ObjectMapper.this._serializationConfig.withInsertedAnnotationIntrospector(ai);
               }

               @Override
               public void appendAnnotationIntrospector(AnnotationIntrospector ai) {
                  ObjectMapper.this._deserializationConfig = ObjectMapper.this._deserializationConfig.withAppendedAnnotationIntrospector(ai);
                  ObjectMapper.this._serializationConfig = ObjectMapper.this._serializationConfig.withAppendedAnnotationIntrospector(ai);
               }

               @Override
               public void registerSubtypes(Class<?>... subtypes) {
                  ObjectMapper.this.registerSubtypes(subtypes);
               }

               @Override
               public void registerSubtypes(NamedType... subtypes) {
                  ObjectMapper.this.registerSubtypes(subtypes);
               }

               @Override
               public void registerSubtypes(Collection<Class<?>> subtypes) {
                  ObjectMapper.this.registerSubtypes(subtypes);
               }

               @Override
               public void setMixInAnnotations(Class<?> target, Class<?> mixinSource) {
                  ObjectMapper.this.addMixIn(target, mixinSource);
               }

               @Override
               public void addDeserializationProblemHandler(DeserializationProblemHandler handler) {
                  ObjectMapper.this.addHandler(handler);
               }

               @Override
               public void setNamingStrategy(PropertyNamingStrategy naming) {
                  ObjectMapper.this.setPropertyNamingStrategy(naming);
               }
            });
            return this;
         }
      }
   }

   public ObjectMapper registerModules(Module... modules) {
      for (Module module : modules) {
         this.registerModule(module);
      }

      return this;
   }

   public ObjectMapper registerModules(Iterable<? extends Module> modules) {
      this._assertNotNull("modules", modules);

      for (Module module : modules) {
         this.registerModule(module);
      }

      return this;
   }

   public Set<Object> getRegisteredModuleIds() {
      return this._registeredModuleTypes == null ? Collections.emptySet() : Collections.unmodifiableSet(this._registeredModuleTypes);
   }

   public static List<Module> findModules() {
      return findModules(null);
   }

   public static List<Module> findModules(ClassLoader classLoader) {
      ArrayList<Module> modules = new ArrayList<>();

      for (Module module : secureGetServiceLoader(Module.class, classLoader)) {
         modules.add(module);
      }

      return modules;
   }

   private static <T> ServiceLoader<T> secureGetServiceLoader(final Class<T> clazz, final ClassLoader classLoader) {
      SecurityManager sm = System.getSecurityManager();
      if (sm == null) {
         return classLoader == null ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
      } else {
         return AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<T>>() {
            public ServiceLoader<T> run() {
               return classLoader == null ? ServiceLoader.load(clazz) : ServiceLoader.load(clazz, classLoader);
            }
         });
      }
   }

   public ObjectMapper findAndRegisterModules() {
      return this.registerModules(findModules());
   }

   public JsonGenerator createGenerator(OutputStream out) throws IOException {
      this._assertNotNull("out", out);
      JsonGenerator g = this._jsonFactory.createGenerator(out, JsonEncoding.UTF8);
      this._serializationConfig.initialize(g);
      return g;
   }

   public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
      this._assertNotNull("out", out);
      JsonGenerator g = this._jsonFactory.createGenerator(out, enc);
      this._serializationConfig.initialize(g);
      return g;
   }

   public JsonGenerator createGenerator(Writer w) throws IOException {
      this._assertNotNull("w", w);
      JsonGenerator g = this._jsonFactory.createGenerator(w);
      this._serializationConfig.initialize(g);
      return g;
   }

   public JsonGenerator createGenerator(File outputFile, JsonEncoding enc) throws IOException {
      this._assertNotNull("outputFile", outputFile);
      JsonGenerator g = this._jsonFactory.createGenerator(outputFile, enc);
      this._serializationConfig.initialize(g);
      return g;
   }

   public JsonGenerator createGenerator(DataOutput out) throws IOException {
      this._assertNotNull("out", out);
      JsonGenerator g = this._jsonFactory.createGenerator(out);
      this._serializationConfig.initialize(g);
      return g;
   }

   public JsonParser createParser(File src) throws IOException {
      this._assertNotNull("src", src);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(src));
   }

   public JsonParser createParser(URL src) throws IOException {
      this._assertNotNull("src", src);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(src));
   }

   public JsonParser createParser(InputStream in) throws IOException {
      this._assertNotNull("in", in);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(in));
   }

   public JsonParser createParser(Reader r) throws IOException {
      this._assertNotNull("r", r);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(r));
   }

   public JsonParser createParser(byte[] content) throws IOException {
      this._assertNotNull("content", content);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(content));
   }

   public JsonParser createParser(byte[] content, int offset, int len) throws IOException {
      this._assertNotNull("content", content);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(content, offset, len));
   }

   public JsonParser createParser(String content) throws IOException {
      this._assertNotNull("content", content);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(content));
   }

   public JsonParser createParser(char[] content) throws IOException {
      this._assertNotNull("content", content);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(content));
   }

   public JsonParser createParser(char[] content, int offset, int len) throws IOException {
      this._assertNotNull("content", content);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(content, offset, len));
   }

   public JsonParser createParser(DataInput content) throws IOException {
      this._assertNotNull("content", content);
      return this._deserializationConfig.initialize(this._jsonFactory.createParser(content));
   }

   public JsonParser createNonBlockingByteArrayParser() throws IOException {
      return this._deserializationConfig.initialize(this._jsonFactory.createNonBlockingByteArrayParser());
   }

   public SerializationConfig getSerializationConfig() {
      return this._serializationConfig;
   }

   public DeserializationConfig getDeserializationConfig() {
      return this._deserializationConfig;
   }

   public DeserializationContext getDeserializationContext() {
      return this._deserializationContext;
   }

   public ObjectMapper setSerializerFactory(SerializerFactory f) {
      this._serializerFactory = f;
      return this;
   }

   public SerializerFactory getSerializerFactory() {
      return this._serializerFactory;
   }

   public ObjectMapper setSerializerProvider(DefaultSerializerProvider p) {
      this._serializerProvider = p;
      return this;
   }

   public SerializerProvider getSerializerProvider() {
      return this._serializerProvider;
   }

   public SerializerProvider getSerializerProviderInstance() {
      return this._serializerProvider(this._serializationConfig);
   }

   public ObjectMapper setMixIns(Map<Class<?>, Class<?>> sourceMixins) {
      this._mixIns.setLocalDefinitions(sourceMixins);
      return this;
   }

   public ObjectMapper addMixIn(Class<?> target, Class<?> mixinSource) {
      this._mixIns.addLocalDefinition(target, mixinSource);
      return this;
   }

   public ObjectMapper setMixInResolver(ClassIntrospector.MixInResolver resolver) {
      SimpleMixInResolver r = this._mixIns.withOverrides(resolver);
      if (r != this._mixIns) {
         this._mixIns = r;
         this._deserializationConfig = new DeserializationConfig(this._deserializationConfig, r);
         this._serializationConfig = new SerializationConfig(this._serializationConfig, r);
      }

      return this;
   }

   public Class<?> findMixInClassFor(Class<?> cls) {
      return this._mixIns.findMixInClassFor(cls);
   }

   public int mixInCount() {
      return this._mixIns.localSize();
   }

   @Deprecated
   public void setMixInAnnotations(Map<Class<?>, Class<?>> sourceMixins) {
      this.setMixIns(sourceMixins);
   }

   @Deprecated
   public final void addMixInAnnotations(Class<?> target, Class<?> mixinSource) {
      this.addMixIn(target, mixinSource);
   }

   public VisibilityChecker<?> getVisibilityChecker() {
      return this._serializationConfig.getDefaultVisibilityChecker();
   }

   public ObjectMapper setVisibility(VisibilityChecker<?> vc) {
      this._configOverrides.setDefaultVisibility(vc);
      return this;
   }

   public ObjectMapper setVisibility(PropertyAccessor forMethod, Visibility visibility) {
      VisibilityChecker<?> vc = this._configOverrides.getDefaultVisibility();
      vc = vc.withVisibility(forMethod, visibility);
      this._configOverrides.setDefaultVisibility(vc);
      return this;
   }

   public SubtypeResolver getSubtypeResolver() {
      return this._subtypeResolver;
   }

   public ObjectMapper setSubtypeResolver(SubtypeResolver str) {
      this._subtypeResolver = str;
      this._deserializationConfig = this._deserializationConfig.with(str);
      this._serializationConfig = this._serializationConfig.with(str);
      return this;
   }

   public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai) {
      this._serializationConfig = this._serializationConfig.with(ai);
      this._deserializationConfig = this._deserializationConfig.with(ai);
      return this;
   }

   public ObjectMapper setAnnotationIntrospectors(AnnotationIntrospector serializerAI, AnnotationIntrospector deserializerAI) {
      this._serializationConfig = this._serializationConfig.with(serializerAI);
      this._deserializationConfig = this._deserializationConfig.with(deserializerAI);
      return this;
   }

   public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s) {
      this._serializationConfig = this._serializationConfig.with(s);
      this._deserializationConfig = this._deserializationConfig.with(s);
      return this;
   }

   public PropertyNamingStrategy getPropertyNamingStrategy() {
      return this._serializationConfig.getPropertyNamingStrategy();
   }

   public ObjectMapper setAccessorNaming(AccessorNamingStrategy.Provider s) {
      this._serializationConfig = this._serializationConfig.with(s);
      this._deserializationConfig = this._deserializationConfig.with(s);
      return this;
   }

   public ObjectMapper setDefaultPrettyPrinter(PrettyPrinter pp) {
      this._serializationConfig = this._serializationConfig.withDefaultPrettyPrinter(pp);
      return this;
   }

   @Deprecated
   public void setVisibilityChecker(VisibilityChecker<?> vc) {
      this.setVisibility(vc);
   }

   public ObjectMapper setPolymorphicTypeValidator(PolymorphicTypeValidator ptv) {
      BaseSettings s = this._deserializationConfig.getBaseSettings().with(ptv);
      this._deserializationConfig = this._deserializationConfig._withBase(s);
      return this;
   }

   public PolymorphicTypeValidator getPolymorphicTypeValidator() {
      return this._deserializationConfig.getBaseSettings().getPolymorphicTypeValidator();
   }

   public ObjectMapper setSerializationInclusion(Include incl) {
      this.setPropertyInclusion(Value.construct(incl, incl));
      return this;
   }

   @Deprecated
   public ObjectMapper setPropertyInclusion(Value incl) {
      return this.setDefaultPropertyInclusion(incl);
   }

   public ObjectMapper setDefaultPropertyInclusion(Value incl) {
      this._configOverrides.setDefaultInclusion(incl);
      return this;
   }

   public ObjectMapper setDefaultPropertyInclusion(Include incl) {
      this._configOverrides.setDefaultInclusion(Value.construct(incl, incl));
      return this;
   }

   public ObjectMapper setDefaultSetterInfo(com.fasterxml.jackson.annotation.JsonSetter.Value v) {
      this._configOverrides.setDefaultSetterInfo(v);
      return this;
   }

   public ObjectMapper setDefaultVisibility(com.fasterxml.jackson.annotation.JsonAutoDetect.Value vis) {
      this._configOverrides.setDefaultVisibility(VisibilityChecker.Std.construct(vis));
      return this;
   }

   public ObjectMapper setDefaultMergeable(Boolean b) {
      this._configOverrides.setDefaultMergeable(b);
      return this;
   }

   public ObjectMapper setDefaultLeniency(Boolean b) {
      this._configOverrides.setDefaultLeniency(b);
      return this;
   }

   public void registerSubtypes(Class<?>... classes) {
      this.getSubtypeResolver().registerSubtypes(classes);
   }

   public void registerSubtypes(NamedType... types) {
      this.getSubtypeResolver().registerSubtypes(types);
   }

   public void registerSubtypes(Collection<Class<?>> subtypes) {
      this.getSubtypeResolver().registerSubtypes(subtypes);
   }

   public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv) {
      return this.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
   }

   public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv, ObjectMapper.DefaultTyping applicability) {
      return this.activateDefaultTyping(ptv, applicability, As.WRAPPER_ARRAY);
   }

   public ObjectMapper activateDefaultTyping(PolymorphicTypeValidator ptv, ObjectMapper.DefaultTyping applicability, As includeAs) {
      if (includeAs == As.EXTERNAL_PROPERTY) {
         throw new IllegalArgumentException("Cannot use includeAs of " + includeAs);
      } else {
         TypeResolverBuilder<?> typer = this._constructDefaultTypeResolverBuilder(applicability, ptv);
         typer = typer.init(Id.CLASS, null);
         typer = typer.inclusion(includeAs);
         return this.setDefaultTyping(typer);
      }
   }

   public ObjectMapper activateDefaultTypingAsProperty(PolymorphicTypeValidator ptv, ObjectMapper.DefaultTyping applicability, String propertyName) {
      TypeResolverBuilder<?> typer = this._constructDefaultTypeResolverBuilder(applicability, ptv);
      typer = typer.init(Id.CLASS, null);
      typer = typer.inclusion(As.PROPERTY);
      typer = typer.typeProperty(propertyName);
      return this.setDefaultTyping(typer);
   }

   public ObjectMapper deactivateDefaultTyping() {
      return this.setDefaultTyping(null);
   }

   public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer) {
      this._deserializationConfig = this._deserializationConfig.with(typer);
      this._serializationConfig = this._serializationConfig.with(typer);
      return this;
   }

   @Deprecated
   public ObjectMapper enableDefaultTyping() {
      return this.activateDefaultTyping(this.getPolymorphicTypeValidator());
   }

   @Deprecated
   public ObjectMapper enableDefaultTyping(ObjectMapper.DefaultTyping dti) {
      return this.enableDefaultTyping(dti, As.WRAPPER_ARRAY);
   }

   @Deprecated
   public ObjectMapper enableDefaultTyping(ObjectMapper.DefaultTyping applicability, As includeAs) {
      return this.activateDefaultTyping(this.getPolymorphicTypeValidator(), applicability, includeAs);
   }

   @Deprecated
   public ObjectMapper enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping applicability, String propertyName) {
      return this.activateDefaultTypingAsProperty(this.getPolymorphicTypeValidator(), applicability, propertyName);
   }

   @Deprecated
   public ObjectMapper disableDefaultTyping() {
      return this.setDefaultTyping(null);
   }

   public MutableConfigOverride configOverride(Class<?> type) {
      return this._configOverrides.findOrCreateOverride(type);
   }

   public MutableCoercionConfig coercionConfigDefaults() {
      return this._coercionConfigs.defaultCoercions();
   }

   public MutableCoercionConfig coercionConfigFor(LogicalType logicalType) {
      return this._coercionConfigs.findOrCreateCoercion(logicalType);
   }

   public MutableCoercionConfig coercionConfigFor(Class<?> physicalType) {
      return this._coercionConfigs.findOrCreateCoercion(physicalType);
   }

   public TypeFactory getTypeFactory() {
      return this._typeFactory;
   }

   public ObjectMapper setTypeFactory(TypeFactory f) {
      this._typeFactory = f;
      this._deserializationConfig = this._deserializationConfig.with(f);
      this._serializationConfig = this._serializationConfig.with(f);
      return this;
   }

   public JavaType constructType(Type t) {
      this._assertNotNull("t", t);
      return this._typeFactory.constructType(t);
   }

   public JavaType constructType(TypeReference<?> typeRef) {
      this._assertNotNull("typeRef", typeRef);
      return this._typeFactory.constructType(typeRef);
   }

   public JsonNodeFactory getNodeFactory() {
      return this._deserializationConfig.getNodeFactory();
   }

   public ObjectMapper setNodeFactory(JsonNodeFactory f) {
      this._deserializationConfig = this._deserializationConfig.with(f);
      return this;
   }

   public ObjectMapper setConstructorDetector(ConstructorDetector cd) {
      this._deserializationConfig = this._deserializationConfig.with(cd);
      return this;
   }

   public ObjectMapper addHandler(DeserializationProblemHandler h) {
      this._deserializationConfig = this._deserializationConfig.withHandler(h);
      return this;
   }

   public ObjectMapper clearProblemHandlers() {
      this._deserializationConfig = this._deserializationConfig.withNoProblemHandlers();
      return this;
   }

   public ObjectMapper setConfig(DeserializationConfig config) {
      this._assertNotNull("config", config);
      this._deserializationConfig = config;
      return this;
   }

   @Deprecated
   public void setFilters(FilterProvider filterProvider) {
      this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
   }

   public ObjectMapper setFilterProvider(FilterProvider filterProvider) {
      this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
      return this;
   }

   public ObjectMapper setBase64Variant(Base64Variant v) {
      this._serializationConfig = this._serializationConfig.with(v);
      this._deserializationConfig = this._deserializationConfig.with(v);
      return this;
   }

   public ObjectMapper setConfig(SerializationConfig config) {
      this._assertNotNull("config", config);
      this._serializationConfig = config;
      return this;
   }

   public JsonFactory tokenStreamFactory() {
      return this._jsonFactory;
   }

   public JsonFactory getFactory() {
      return this._jsonFactory;
   }

   public ObjectMapper setDateFormat(DateFormat dateFormat) {
      this._deserializationConfig = this._deserializationConfig.with(dateFormat);
      this._serializationConfig = this._serializationConfig.with(dateFormat);
      return this;
   }

   public DateFormat getDateFormat() {
      return this._serializationConfig.getDateFormat();
   }

   public Object setHandlerInstantiator(HandlerInstantiator hi) {
      this._deserializationConfig = this._deserializationConfig.with(hi);
      this._serializationConfig = this._serializationConfig.with(hi);
      return this;
   }

   public ObjectMapper setInjectableValues(InjectableValues injectableValues) {
      this._injectableValues = injectableValues;
      return this;
   }

   public InjectableValues getInjectableValues() {
      return this._injectableValues;
   }

   public ObjectMapper setLocale(Locale l) {
      this._deserializationConfig = this._deserializationConfig.with(l);
      this._serializationConfig = this._serializationConfig.with(l);
      return this;
   }

   public ObjectMapper setTimeZone(TimeZone tz) {
      this._deserializationConfig = this._deserializationConfig.with(tz);
      this._serializationConfig = this._serializationConfig.with(tz);
      return this;
   }

   public ObjectMapper setDefaultAttributes(ContextAttributes attrs) {
      this._deserializationConfig = this._deserializationConfig.with(attrs);
      this._serializationConfig = this._serializationConfig.with(attrs);
      return this;
   }

   public boolean isEnabled(MapperFeature f) {
      return this._serializationConfig.isEnabled(f);
   }

   @Deprecated
   public ObjectMapper configure(MapperFeature f, boolean state) {
      this._serializationConfig = state ? this._serializationConfig.with(new MapperFeature[]{f}) : this._serializationConfig.without(new MapperFeature[]{f});
      this._deserializationConfig = state
         ? this._deserializationConfig.with(new MapperFeature[]{f})
         : this._deserializationConfig.without(new MapperFeature[]{f});
      return this;
   }

   @Deprecated
   public ObjectMapper enable(MapperFeature... f) {
      this._deserializationConfig = this._deserializationConfig.with(f);
      this._serializationConfig = this._serializationConfig.with(f);
      return this;
   }

   @Deprecated
   public ObjectMapper disable(MapperFeature... f) {
      this._deserializationConfig = this._deserializationConfig.without(f);
      this._serializationConfig = this._serializationConfig.without(f);
      return this;
   }

   public boolean isEnabled(SerializationFeature f) {
      return this._serializationConfig.isEnabled(f);
   }

   public ObjectMapper configure(SerializationFeature f, boolean state) {
      this._serializationConfig = state ? this._serializationConfig.with(f) : this._serializationConfig.without(f);
      return this;
   }

   public ObjectMapper enable(SerializationFeature f) {
      this._serializationConfig = this._serializationConfig.with(f);
      return this;
   }

   public ObjectMapper enable(SerializationFeature first, SerializationFeature... f) {
      this._serializationConfig = this._serializationConfig.with(first, f);
      return this;
   }

   public ObjectMapper disable(SerializationFeature f) {
      this._serializationConfig = this._serializationConfig.without(f);
      return this;
   }

   public ObjectMapper disable(SerializationFeature first, SerializationFeature... f) {
      this._serializationConfig = this._serializationConfig.without(first, f);
      return this;
   }

   public boolean isEnabled(DeserializationFeature f) {
      return this._deserializationConfig.isEnabled(f);
   }

   public ObjectMapper configure(DeserializationFeature f, boolean state) {
      this._deserializationConfig = state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f);
      return this;
   }

   public ObjectMapper enable(DeserializationFeature feature) {
      this._deserializationConfig = this._deserializationConfig.with(feature);
      return this;
   }

   public ObjectMapper enable(DeserializationFeature first, DeserializationFeature... f) {
      this._deserializationConfig = this._deserializationConfig.with(first, f);
      return this;
   }

   public ObjectMapper disable(DeserializationFeature feature) {
      this._deserializationConfig = this._deserializationConfig.without(feature);
      return this;
   }

   public ObjectMapper disable(DeserializationFeature first, DeserializationFeature... f) {
      this._deserializationConfig = this._deserializationConfig.without(first, f);
      return this;
   }

   public ObjectMapper configure(DatatypeFeature f, boolean state) {
      if (state) {
         this._deserializationConfig = this._deserializationConfig.with(f);
         this._serializationConfig = this._serializationConfig.with(f);
      } else {
         this._deserializationConfig = this._deserializationConfig.without(f);
         this._serializationConfig = this._serializationConfig.without(f);
      }

      return this;
   }

   public boolean isEnabled(com.fasterxml.jackson.core.JsonParser.Feature f) {
      return this._deserializationConfig.isEnabled(f, this._jsonFactory);
   }

   public ObjectMapper configure(com.fasterxml.jackson.core.JsonParser.Feature f, boolean state) {
      this._jsonFactory.configure(f, state);
      return this;
   }

   public ObjectMapper enable(com.fasterxml.jackson.core.JsonParser.Feature... features) {
      for (com.fasterxml.jackson.core.JsonParser.Feature f : features) {
         this._jsonFactory.enable(f);
      }

      return this;
   }

   public ObjectMapper disable(com.fasterxml.jackson.core.JsonParser.Feature... features) {
      for (com.fasterxml.jackson.core.JsonParser.Feature f : features) {
         this._jsonFactory.disable(f);
      }

      return this;
   }

   public boolean isEnabled(com.fasterxml.jackson.core.JsonGenerator.Feature f) {
      return this._serializationConfig.isEnabled(f, this._jsonFactory);
   }

   public ObjectMapper configure(com.fasterxml.jackson.core.JsonGenerator.Feature f, boolean state) {
      this._jsonFactory.configure(f, state);
      return this;
   }

   public ObjectMapper enable(com.fasterxml.jackson.core.JsonGenerator.Feature... features) {
      for (com.fasterxml.jackson.core.JsonGenerator.Feature f : features) {
         this._jsonFactory.enable(f);
      }

      return this;
   }

   public ObjectMapper disable(com.fasterxml.jackson.core.JsonGenerator.Feature... features) {
      for (com.fasterxml.jackson.core.JsonGenerator.Feature f : features) {
         this._jsonFactory.disable(f);
      }

      return this;
   }

   public boolean isEnabled(Feature f) {
      return this._jsonFactory.isEnabled(f);
   }

   public boolean isEnabled(StreamReadFeature f) {
      return this.isEnabled(f.mappedFeature());
   }

   public boolean isEnabled(StreamWriteFeature f) {
      return this.isEnabled(f.mappedFeature());
   }

   public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("p", p);
      return (T)this._readValue(this.getDeserializationConfig(), p, this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(JsonParser p, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("p", p);
      return (T)this._readValue(this.getDeserializationConfig(), p, this._typeFactory.constructType(valueTypeRef));
   }

   public final <T> T readValue(JsonParser p, ResolvedType valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("p", p);
      return (T)this._readValue(this.getDeserializationConfig(), p, (JavaType)valueType);
   }

   public <T> T readValue(JsonParser p, JavaType valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("p", p);
      return (T)this._readValue(this.getDeserializationConfig(), p, valueType);
   }

   public <T extends TreeNode> T readTree(JsonParser p) throws IOException {
      this._assertNotNull("p", p);
      DeserializationConfig cfg = this.getDeserializationConfig();
      JsonToken t = p.currentToken();
      if (t == null) {
         t = p.nextToken();
         if (t == null) {
            return null;
         }
      }

      JsonNode n = (JsonNode)this._readValue(cfg, p, this.constructType(JsonNode.class));
      if (n == null) {
         n = this.getNodeFactory().nullNode();
      }

      return (T)n;
   }

   public <T> MappingIterator<T> readValues(JsonParser p, ResolvedType valueType) throws IOException {
      return this.readValues(p, (JavaType)valueType);
   }

   public <T> MappingIterator<T> readValues(JsonParser p, JavaType valueType) throws IOException {
      this._assertNotNull("p", p);
      DeserializationConfig config = this.getDeserializationConfig();
      DeserializationContext ctxt = this.createDeserializationContext(p, config);
      JsonDeserializer<?> deser = this._findRootDeserializer(ctxt, valueType);
      return new MappingIterator<>(valueType, p, ctxt, deser, false, null);
   }

   public <T> MappingIterator<T> readValues(JsonParser p, Class<T> valueType) throws IOException {
      return this.readValues(p, this._typeFactory.constructType(valueType));
   }

   public <T> MappingIterator<T> readValues(JsonParser p, TypeReference<T> valueTypeRef) throws IOException {
      return this.readValues(p, this._typeFactory.constructType(valueTypeRef));
   }

   public JsonNode readTree(InputStream in) throws IOException {
      this._assertNotNull("in", in);
      return this._readTreeAndClose(this._jsonFactory.createParser(in));
   }

   public JsonNode readTree(Reader r) throws IOException {
      this._assertNotNull("r", r);
      return this._readTreeAndClose(this._jsonFactory.createParser(r));
   }

   public JsonNode readTree(String content) throws JsonProcessingException, JsonMappingException {
      this._assertNotNull("content", content);

      try {
         return this._readTreeAndClose(this._jsonFactory.createParser(content));
      } catch (JsonProcessingException var3) {
         throw var3;
      } catch (IOException var4) {
         throw JsonMappingException.fromUnexpectedIOE(var4);
      }
   }

   public JsonNode readTree(byte[] content) throws IOException {
      this._assertNotNull("content", content);
      return this._readTreeAndClose(this._jsonFactory.createParser(content));
   }

   public JsonNode readTree(byte[] content, int offset, int len) throws IOException {
      this._assertNotNull("content", content);
      return this._readTreeAndClose(this._jsonFactory.createParser(content, offset, len));
   }

   public JsonNode readTree(File file) throws IOException {
      this._assertNotNull("file", file);
      return this._readTreeAndClose(this._jsonFactory.createParser(file));
   }

   public JsonNode readTree(URL source) throws IOException {
      this._assertNotNull("source", source);
      return this._readTreeAndClose(this._jsonFactory.createParser(source));
   }

   public void writeValue(JsonGenerator g, Object value) throws IOException, StreamWriteException, DatabindException {
      this._assertNotNull("g", g);
      SerializationConfig config = this.getSerializationConfig();
      if (config.isEnabled(SerializationFeature.INDENT_OUTPUT) && g.getPrettyPrinter() == null) {
         g.setPrettyPrinter(config.constructDefaultPrettyPrinter());
      }

      if (config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
         this._writeCloseableValue(g, value, config);
      } else {
         this._serializerProvider(config).serializeValue(g, value);
         if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            g.flush();
         }
      }
   }

   public void writeTree(JsonGenerator g, TreeNode rootNode) throws IOException {
      this._assertNotNull("g", g);
      SerializationConfig config = this.getSerializationConfig();
      this._serializerProvider(config).serializeValue(g, rootNode);
      if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
         g.flush();
      }
   }

   public void writeTree(JsonGenerator g, JsonNode rootNode) throws IOException {
      this._assertNotNull("g", g);
      SerializationConfig config = this.getSerializationConfig();
      this._serializerProvider(config).serializeValue(g, rootNode);
      if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
         g.flush();
      }
   }

   public ObjectNode createObjectNode() {
      return this._deserializationConfig.getNodeFactory().objectNode();
   }

   public ArrayNode createArrayNode() {
      return this._deserializationConfig.getNodeFactory().arrayNode();
   }

   public JsonNode missingNode() {
      return this._deserializationConfig.getNodeFactory().missingNode();
   }

   public JsonNode nullNode() {
      return this._deserializationConfig.getNodeFactory().nullNode();
   }

   public JsonParser treeAsTokens(TreeNode n) {
      this._assertNotNull("n", n);
      return new TreeTraversingParser((JsonNode)n, this);
   }

   public <T> T treeToValue(TreeNode n, Class<T> valueType) throws IllegalArgumentException, JsonProcessingException {
      if (n == null) {
         return null;
      } else {
         try {
            if (TreeNode.class.isAssignableFrom(valueType) && valueType.isAssignableFrom(n.getClass())) {
               return (T)n;
            } else {
               JsonToken tt = n.asToken();
               if (tt == JsonToken.VALUE_EMBEDDED_OBJECT && n instanceof POJONode) {
                  Object ob = ((POJONode)n).getPojo();
                  if (ob == null || valueType.isInstance(ob)) {
                     return (T)ob;
                  }
               }

               return this.readValue(this.treeAsTokens(n), valueType);
            }
         } catch (JsonProcessingException var5) {
            throw var5;
         } catch (IOException var6) {
            throw new IllegalArgumentException(var6.getMessage(), var6);
         }
      }
   }

   public <T> T treeToValue(TreeNode n, JavaType valueType) throws IllegalArgumentException, JsonProcessingException {
      if (n == null) {
         return null;
      } else {
         try {
            if (valueType.isTypeOrSubTypeOf(TreeNode.class) && valueType.isTypeOrSuperTypeOf(n.getClass())) {
               return (T)n;
            } else {
               JsonToken tt = n.asToken();
               if (tt == JsonToken.VALUE_EMBEDDED_OBJECT && n instanceof POJONode) {
                  Object ob = ((POJONode)n).getPojo();
                  if (ob == null || valueType.isTypeOrSuperTypeOf(ob.getClass())) {
                     return (T)ob;
                  }
               }

               return this.readValue(this.treeAsTokens(n), valueType);
            }
         } catch (JsonProcessingException var5) {
            throw var5;
         } catch (IOException var6) {
            throw new IllegalArgumentException(var6.getMessage(), var6);
         }
      }
   }

   public <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
      if (fromValue == null) {
         return (T)this.getNodeFactory().nullNode();
      } else {
         SerializationConfig config = this.getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
         DefaultSerializerProvider context = this._serializerProvider(config);
         TokenBuffer buf = context.bufferForValueConversion(this);
         if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
         }

         try {
            context.serializeValue(buf, fromValue);
            JsonParser p = buf.asParser();
            Throwable var6 = null;

            JsonNode var7;
            try {
               var7 = this.readTree(p);
            } catch (Throwable var17) {
               var6 = var17;
               throw var17;
            } finally {
               if (p != null) {
                  if (var6 != null) {
                     try {
                        p.close();
                     } catch (Throwable var16) {
                        var6.addSuppressed(var16);
                     }
                  } else {
                     p.close();
                  }
               }
            }

            return (T)var7;
         } catch (IOException var19) {
            throw new IllegalArgumentException(var19.getMessage(), var19);
         }
      }
   }

   public boolean canSerialize(Class<?> type) {
      return this._serializerProvider(this.getSerializationConfig()).hasSerializerFor(type, null);
   }

   public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause) {
      return this._serializerProvider(this.getSerializationConfig()).hasSerializerFor(type, cause);
   }

   public boolean canDeserialize(JavaType type) {
      return this.createDeserializationContext(null, this.getDeserializationConfig()).hasValueDeserializerFor(type, null);
   }

   public boolean canDeserialize(JavaType type, AtomicReference<Throwable> cause) {
      return this.createDeserializationContext(null, this.getDeserializationConfig()).hasValueDeserializerFor(type, cause);
   }

   public <T> T readValue(File src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(File src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(File src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(URL src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(URL src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(URL src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException, JsonMappingException {
      this._assertNotNull("content", content);
      return this.readValue(content, this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException, JsonMappingException {
      this._assertNotNull("content", content);
      return this.readValue(content, this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(String content, JavaType valueType) throws JsonProcessingException, JsonMappingException {
      this._assertNotNull("content", content);

      try {
         return (T)this._readMapAndClose(this._jsonFactory.createParser(content), valueType);
      } catch (JsonProcessingException var4) {
         throw var4;
      } catch (IOException var5) {
         throw JsonMappingException.fromUnexpectedIOE(var5);
      }
   }

   public <T> T readValue(Reader src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(Reader src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(Reader src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(InputStream src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(InputStream src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(byte[] src, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(byte[] src, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(byte[] src, int offset, int len, TypeReference<T> valueTypeRef) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
   }

   public <T> T readValue(byte[] src, JavaType valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public <T> T readValue(byte[] src, int offset, int len, JavaType valueType) throws IOException, StreamReadException, DatabindException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), valueType);
   }

   public <T> T readValue(DataInput src, Class<T> valueType) throws IOException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
   }

   public <T> T readValue(DataInput src, JavaType valueType) throws IOException {
      this._assertNotNull("src", src);
      return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
   }

   public void writeValue(File resultFile, Object value) throws IOException, StreamWriteException, DatabindException {
      this._writeValueAndClose(this.createGenerator(resultFile, JsonEncoding.UTF8), value);
   }

   public void writeValue(OutputStream out, Object value) throws IOException, StreamWriteException, DatabindException {
      this._writeValueAndClose(this.createGenerator(out, JsonEncoding.UTF8), value);
   }

   public void writeValue(DataOutput out, Object value) throws IOException {
      this._writeValueAndClose(this.createGenerator(out), value);
   }

   public void writeValue(Writer w, Object value) throws IOException, StreamWriteException, DatabindException {
      this._writeValueAndClose(this.createGenerator(w), value);
   }

   public String writeValueAsString(Object value) throws JsonProcessingException {
      SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());

      try {
         this._writeValueAndClose(this.createGenerator(sw), value);
      } catch (JsonProcessingException var4) {
         throw var4;
      } catch (IOException var5) {
         throw JsonMappingException.fromUnexpectedIOE(var5);
      }

      return sw.getAndClear();
   }

   public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
      try {
         ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler());
         Throwable var3 = null;

         byte[] var5;
         try {
            this._writeValueAndClose(this.createGenerator(bb, JsonEncoding.UTF8), value);
            byte[] result = bb.toByteArray();
            bb.release();
            var5 = result;
         } catch (Throwable var16) {
            var3 = var16;
            throw var16;
         } finally {
            if (bb != null) {
               if (var3 != null) {
                  try {
                     bb.close();
                  } catch (Throwable var15) {
                     var3.addSuppressed(var15);
                  }
               } else {
                  bb.close();
               }
            }
         }

         return var5;
      } catch (JsonProcessingException var18) {
         throw var18;
      } catch (IOException var19) {
         throw JsonMappingException.fromUnexpectedIOE(var19);
      }
   }

   public ObjectWriter writer() {
      return this._newWriter(this.getSerializationConfig());
   }

   public ObjectWriter writer(SerializationFeature feature) {
      return this._newWriter(this.getSerializationConfig().with(feature));
   }

   public ObjectWriter writer(SerializationFeature first, SerializationFeature... other) {
      return this._newWriter(this.getSerializationConfig().with(first, other));
   }

   public ObjectWriter writer(DateFormat df) {
      return this._newWriter(this.getSerializationConfig().with(df));
   }

   public ObjectWriter writerWithView(Class<?> serializationView) {
      return this._newWriter(this.getSerializationConfig().withView(serializationView));
   }

   public ObjectWriter writerFor(Class<?> rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
   }

   public ObjectWriter writerFor(TypeReference<?> rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
   }

   public ObjectWriter writerFor(JavaType rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType, null);
   }

   public ObjectWriter writer(PrettyPrinter pp) {
      if (pp == null) {
         pp = ObjectWriter.NULL_PRETTY_PRINTER;
      }

      return this._newWriter(this.getSerializationConfig(), null, pp);
   }

   public ObjectWriter writerWithDefaultPrettyPrinter() {
      SerializationConfig config = this.getSerializationConfig();
      return this._newWriter(config, null, config.getDefaultPrettyPrinter());
   }

   public ObjectWriter writer(FilterProvider filterProvider) {
      return this._newWriter(this.getSerializationConfig().withFilters(filterProvider));
   }

   public ObjectWriter writer(FormatSchema schema) {
      this._verifySchemaType(schema);
      return this._newWriter(this.getSerializationConfig(), schema);
   }

   public ObjectWriter writer(Base64Variant defaultBase64) {
      return this._newWriter(this.getSerializationConfig().with(defaultBase64));
   }

   public ObjectWriter writer(CharacterEscapes escapes) {
      return this._newWriter(this.getSerializationConfig()).with(escapes);
   }

   public ObjectWriter writer(ContextAttributes attrs) {
      return this._newWriter(this.getSerializationConfig().with(attrs));
   }

   @Deprecated
   public ObjectWriter writerWithType(Class<?> rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
   }

   @Deprecated
   public ObjectWriter writerWithType(TypeReference<?> rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType == null ? null : this._typeFactory.constructType(rootType), null);
   }

   @Deprecated
   public ObjectWriter writerWithType(JavaType rootType) {
      return this._newWriter(this.getSerializationConfig(), rootType, null);
   }

   public ObjectReader reader() {
      return this._newReader(this.getDeserializationConfig()).with(this._injectableValues);
   }

   public ObjectReader reader(DeserializationFeature feature) {
      return this._newReader(this.getDeserializationConfig().with(feature));
   }

   public ObjectReader reader(DeserializationFeature first, DeserializationFeature... other) {
      return this._newReader(this.getDeserializationConfig().with(first, other));
   }

   public ObjectReader readerForUpdating(Object valueToUpdate) {
      JavaType t = valueToUpdate == null ? null : this._typeFactory.constructType(valueToUpdate.getClass());
      return this._newReader(this.getDeserializationConfig(), t, valueToUpdate, null, this._injectableValues);
   }

   public ObjectReader readerFor(JavaType type) {
      return this._newReader(this.getDeserializationConfig(), type, null, null, this._injectableValues);
   }

   public ObjectReader readerFor(Class<?> type) {
      JavaType t = type == null ? null : this._typeFactory.constructType(type);
      return this._newReader(this.getDeserializationConfig(), t, null, null, this._injectableValues);
   }

   public ObjectReader readerFor(TypeReference<?> typeRef) {
      this._assertNotNull("type", typeRef);
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(typeRef), null, null, this._injectableValues);
   }

   public ObjectReader readerForArrayOf(Class<?> type) {
      this._assertNotNull("type", type);
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructArrayType(type), null, null, this._injectableValues);
   }

   public ObjectReader readerForListOf(Class<?> type) {
      this._assertNotNull("type", type);
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructCollectionType(List.class, type), null, null, this._injectableValues);
   }

   public ObjectReader readerForMapOf(Class<?> type) {
      this._assertNotNull("type", type);
      return this._newReader(
         this.getDeserializationConfig(), this._typeFactory.constructMapType(Map.class, String.class, type), null, null, this._injectableValues
      );
   }

   public ObjectReader reader(JsonNodeFactory nodeFactory) {
      this._assertNotNull("nodeFactory", nodeFactory);
      return this._newReader(this.getDeserializationConfig()).with(nodeFactory);
   }

   public ObjectReader reader(FormatSchema schema) {
      this._verifySchemaType(schema);
      return this._newReader(this.getDeserializationConfig(), null, null, schema, this._injectableValues);
   }

   public ObjectReader reader(InjectableValues injectableValues) {
      return this._newReader(this.getDeserializationConfig(), null, null, null, injectableValues);
   }

   public ObjectReader readerWithView(Class<?> view) {
      return this._newReader(this.getDeserializationConfig().withView(view));
   }

   public ObjectReader reader(Base64Variant defaultBase64) {
      return this._newReader(this.getDeserializationConfig().with(defaultBase64));
   }

   public ObjectReader reader(ContextAttributes attrs) {
      return this._newReader(this.getDeserializationConfig().with(attrs));
   }

   @Deprecated
   public ObjectReader reader(JavaType type) {
      return this._newReader(this.getDeserializationConfig(), type, null, null, this._injectableValues);
   }

   @Deprecated
   public ObjectReader reader(Class<?> type) {
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
   }

   @Deprecated
   public ObjectReader reader(TypeReference<?> type) {
      return this._newReader(this.getDeserializationConfig(), this._typeFactory.constructType(type), null, null, this._injectableValues);
   }

   public <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
      return (T)this._convert(fromValue, this._typeFactory.constructType(toValueType));
   }

   public <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) throws IllegalArgumentException {
      return (T)this._convert(fromValue, this._typeFactory.constructType(toValueTypeRef));
   }

   public <T> T convertValue(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
      return (T)this._convert(fromValue, toValueType);
   }

   protected Object _convert(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
      SerializationConfig config = this.getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
      DefaultSerializerProvider context = this._serializerProvider(config);
      TokenBuffer buf = context.bufferForValueConversion(this);
      if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
         buf = buf.forceUseOfBigDecimal(true);
      }

      try {
         context.serializeValue(buf, fromValue);
         JsonParser p = buf.asParser();
         DeserializationConfig deserConfig = this.getDeserializationConfig();
         JsonToken t = this._initForReading(p, toValueType);
         Object result;
         if (t == JsonToken.VALUE_NULL) {
            DeserializationContext ctxt = this.createDeserializationContext(p, deserConfig);
            result = this._findRootDeserializer(ctxt, toValueType).getNullValue(ctxt);
         } else if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
            DeserializationContext ctxt = this.createDeserializationContext(p, deserConfig);
            JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, toValueType);
            result = deser.deserialize(p, ctxt);
         } else {
            result = null;
         }

         p.close();
         return result;
      } catch (IOException var12) {
         throw new IllegalArgumentException(var12.getMessage(), var12);
      }
   }

   public <T> T updateValue(T valueToUpdate, Object overrides) throws JsonMappingException {
      T result = valueToUpdate;
      if (valueToUpdate != null && overrides != null) {
         SerializationConfig config = this.getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
         DefaultSerializerProvider context = this._serializerProvider(config);
         TokenBuffer buf = context.bufferForValueConversion(this);
         if (this.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
            buf = buf.forceUseOfBigDecimal(true);
         }

         try {
            context.serializeValue(buf, overrides);
            JsonParser p = buf.asParser();
            result = this.readerForUpdating(valueToUpdate).readValue(p);
            p.close();
         } catch (IOException var8) {
            if (var8 instanceof JsonMappingException) {
               throw (JsonMappingException)var8;
            }

            throw JsonMappingException.fromUnexpectedIOE(var8);
         }
      }

      return result;
   }

   @Deprecated
   public JsonSchema generateJsonSchema(Class<?> t) throws JsonMappingException {
      return this._serializerProvider(this.getSerializationConfig()).generateJsonSchema(t);
   }

   public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
      this.acceptJsonFormatVisitor(this._typeFactory.constructType(type), visitor);
   }

   public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
      if (type == null) {
         throw new IllegalArgumentException("type must be provided");
      } else {
         this._serializerProvider(this.getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
      }
   }

   protected TypeResolverBuilder<?> _constructDefaultTypeResolverBuilder(ObjectMapper.DefaultTyping applicability, PolymorphicTypeValidator ptv) {
      return ObjectMapper.DefaultTypeResolverBuilder.construct(applicability, ptv);
   }

   protected DefaultSerializerProvider _serializerProvider(SerializationConfig config) {
      return this._serializerProvider.createInstance(config, this._serializerFactory);
   }

   protected final void _writeValueAndClose(JsonGenerator g, Object value) throws IOException {
      SerializationConfig cfg = this.getSerializationConfig();
      if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
         this._writeCloseable(g, value, cfg);
      } else {
         try {
            this._serializerProvider(cfg).serializeValue(g, value);
         } catch (Exception var5) {
            ClassUtil.closeOnFailAndThrowAsIOE(g, var5);
            return;
         }

         g.close();
      }
   }

   private final void _writeCloseable(JsonGenerator g, Object value, SerializationConfig cfg) throws IOException {
      Closeable toClose = (Closeable)value;

      try {
         this._serializerProvider(cfg).serializeValue(g, value);
         Closeable tmpToClose = toClose;
         toClose = null;
         tmpToClose.close();
      } catch (Exception var6) {
         ClassUtil.closeOnFailAndThrowAsIOE(g, toClose, var6);
         return;
      }

      g.close();
   }

   private final void _writeCloseableValue(JsonGenerator g, Object value, SerializationConfig cfg) throws IOException {
      Closeable toClose = (Closeable)value;

      try {
         this._serializerProvider(cfg).serializeValue(g, value);
         if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            g.flush();
         }
      } catch (Exception var6) {
         ClassUtil.closeOnFailAndThrowAsIOE(null, toClose, var6);
         return;
      }

      toClose.close();
   }

   @Deprecated
   protected final void _configAndWriteValue(JsonGenerator g, Object value) throws IOException {
      this.getSerializationConfig().initialize(g);
      this._writeValueAndClose(g, value);
   }

   protected Object _readValue(DeserializationConfig cfg, JsonParser p, JavaType valueType) throws IOException {
      JsonToken t = this._initForReading(p, valueType);
      DefaultDeserializationContext ctxt = this.createDeserializationContext(p, cfg);
      Object result;
      if (t == JsonToken.VALUE_NULL) {
         result = this._findRootDeserializer(ctxt, valueType).getNullValue(ctxt);
      } else if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
         result = ctxt.readRootValue(p, valueType, this._findRootDeserializer(ctxt, valueType), null);
      } else {
         result = null;
      }

      p.clearCurrentToken();
      if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
         this._verifyNoTrailingTokens(p, ctxt, valueType);
      }

      return result;
   }

   protected Object _readMapAndClose(JsonParser p0, JavaType valueType) throws IOException {
      JsonParser p = p0;
      Throwable var4 = null;

      Object var9;
      try {
         DeserializationConfig cfg = this.getDeserializationConfig();
         DefaultDeserializationContext ctxt = this.createDeserializationContext(p, cfg);
         JsonToken t = this._initForReading(p, valueType);
         Object result;
         if (t == JsonToken.VALUE_NULL) {
            result = this._findRootDeserializer(ctxt, valueType).getNullValue(ctxt);
         } else if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
            result = ctxt.readRootValue(p, valueType, this._findRootDeserializer(ctxt, valueType), null);
            ctxt.checkUnresolvedObjectId();
         } else {
            result = null;
         }

         if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, valueType);
         }

         var9 = result;
      } catch (Throwable var18) {
         var4 = var18;
         throw var18;
      } finally {
         if (p0 != null) {
            if (var4 != null) {
               try {
                  p.close();
               } catch (Throwable var17) {
                  var4.addSuppressed(var17);
               }
            } else {
               p0.close();
            }
         }
      }

      return var9;
   }

   protected JsonNode _readTreeAndClose(JsonParser p0) throws IOException {
      JsonParser p = p0;
      Throwable var3 = null;

      try {
         JavaType valueType = this.constructType(JsonNode.class);
         DeserializationConfig cfg = this.getDeserializationConfig();
         cfg.initialize(p);
         JsonToken t = p.currentToken();
         if (t == null) {
            t = p.nextToken();
            if (t == null) {
               return cfg.getNodeFactory().missingNode();
            }
         }

         DefaultDeserializationContext ctxt = this.createDeserializationContext(p, cfg);
         JsonNode resultNode;
         if (t == JsonToken.VALUE_NULL) {
            resultNode = cfg.getNodeFactory().nullNode();
         } else {
            resultNode = (JsonNode)ctxt.readRootValue(p, valueType, this._findRootDeserializer(ctxt, valueType), null);
         }

         if (cfg.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)) {
            this._verifyNoTrailingTokens(p, ctxt, valueType);
         }

         return resultNode;
      } catch (Throwable var19) {
         var3 = var19;
         throw var19;
      } finally {
         if (p0 != null) {
            if (var3 != null) {
               try {
                  p.close();
               } catch (Throwable var18) {
                  var3.addSuppressed(var18);
               }
            } else {
               p0.close();
            }
         }
      }
   }

   protected DefaultDeserializationContext createDeserializationContext(JsonParser p, DeserializationConfig cfg) {
      return this._deserializationContext.createInstance(cfg, p, this._injectableValues);
   }

   protected JsonToken _initForReading(JsonParser p, JavaType targetType) throws IOException {
      this._deserializationConfig.initialize(p);
      JsonToken t = p.currentToken();
      if (t == null) {
         t = p.nextToken();
         if (t == null) {
            throw MismatchedInputException.from(p, targetType, "No content to map due to end-of-input");
         }
      }

      return t;
   }

   protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws IOException {
      JsonToken t = p.nextToken();
      if (t != null) {
         Class<?> bt = ClassUtil.rawClass(bindType);
         ctxt.reportTrailingTokens(bt, p, t);
      }
   }

   protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType) throws DatabindException {
      JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
      if (deser != null) {
         return deser;
      } else {
         deser = ctxt.findRootValueDeserializer(valueType);
         if (deser == null) {
            return ctxt.reportBadDefinition(valueType, "Cannot find a deserializer for type " + valueType);
         } else {
            this._rootDeserializers.put(valueType, deser);
            return deser;
         }
      }
   }

   protected void _verifySchemaType(FormatSchema schema) {
      if (schema != null && !this._jsonFactory.canUseSchema(schema)) {
         throw new IllegalArgumentException(
            "Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._jsonFactory.getFormatName()
         );
      }
   }

   protected final void _assertNotNull(String paramName, Object src) {
      if (src == null) {
         throw new IllegalArgumentException(String.format("argument \"%s\" is null", paramName));
      }
   }

   public static class DefaultTypeResolverBuilder extends StdTypeResolverBuilder implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final ObjectMapper.DefaultTyping _appliesFor;
      protected final PolymorphicTypeValidator _subtypeValidator;

      @Deprecated
      public DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping t) {
         this(t, LaissezFaireSubTypeValidator.instance);
      }

      public DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
         this._appliesFor = _requireNonNull(t, "Can not pass `null` DefaultTyping");
         this._subtypeValidator = _requireNonNull(ptv, "Can not pass `null` PolymorphicTypeValidator");
      }

      protected DefaultTypeResolverBuilder(ObjectMapper.DefaultTypeResolverBuilder base, Class<?> defaultImpl) {
         super(base, defaultImpl);
         this._appliesFor = base._appliesFor;
         this._subtypeValidator = base._subtypeValidator;
      }

      private static <T> T _requireNonNull(T value, String msg) {
         if (value == null) {
            throw new NullPointerException(msg);
         } else {
            return value;
         }
      }

      public static ObjectMapper.DefaultTypeResolverBuilder construct(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
         return new ObjectMapper.DefaultTypeResolverBuilder(t, ptv);
      }

      public ObjectMapper.DefaultTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
         if (this._defaultImpl == defaultImpl) {
            return this;
         } else {
            ClassUtil.verifyMustOverride(ObjectMapper.DefaultTypeResolverBuilder.class, this, "withDefaultImpl");
            return new ObjectMapper.DefaultTypeResolverBuilder(this, defaultImpl);
         }
      }

      @Override
      public PolymorphicTypeValidator subTypeValidator(MapperConfig<?> config) {
         return this._subtypeValidator;
      }

      @Override
      public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
         return this.useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
      }

      @Override
      public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
         return this.useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes) : null;
      }

      public boolean useForType(JavaType t) {
         if (t.isPrimitive()) {
            return false;
         } else {
            switch (this._appliesFor) {
               case NON_CONCRETE_AND_ARRAYS:
                  while (t.isArrayType()) {
                     t = t.getContentType();
                  }
               case OBJECT_AND_NON_CONCRETE:
                  break;
               case NON_FINAL:
                  while (t.isArrayType()) {
                     t = t.getContentType();
                  }

                  while (t.isReferenceType()) {
                     t = t.getReferencedType();
                  }

                  return !t.isFinal() && !TreeNode.class.isAssignableFrom(t.getRawClass());
               case EVERYTHING:
                  return true;
               case JAVA_LANG_OBJECT:
               default:
                  return t.isJavaLangObject();
            }

            while (t.isReferenceType()) {
               t = t.getReferencedType();
            }

            return t.isJavaLangObject() || !t.isConcrete() && !TreeNode.class.isAssignableFrom(t.getRawClass());
         }
      }
   }

   public static enum DefaultTyping {
      JAVA_LANG_OBJECT,
      OBJECT_AND_NON_CONCRETE,
      NON_CONCRETE_AND_ARRAYS,
      NON_FINAL,
      EVERYTHING;
   }
}
