package org.glassfish.jersey.jackson.internal.jackson.jaxrs.base;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.AnnotationBundleKey;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.EndpointConfigBase;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.JaxRSFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.MapperConfiguratorBase;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectReaderInjector;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectReaderModifier;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterInjector;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterModifier;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.util.ClassKey;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.util.LRUMap;

public abstract class ProviderBase<THIS extends ProviderBase<THIS, MAPPER, EP_CONFIG, MAPPER_CONFIG>, MAPPER extends ObjectMapper, EP_CONFIG extends EndpointConfigBase<EP_CONFIG>, MAPPER_CONFIG extends MapperConfiguratorBase<MAPPER_CONFIG, MAPPER>>
   implements MessageBodyReader<Object>,
   MessageBodyWriter<Object>,
   Versioned {
   public static final String HEADER_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
   protected static final String CLASS_NAME_NO_CONTENT_EXCEPTION = "jakarta.ws.rs.core.NoContentException";
   private static final String NO_CONTENT_MESSAGE = "No content (empty input stream)";
   protected static final HashSet<ClassKey> DEFAULT_UNTOUCHABLES = new HashSet<>();
   public static final Class<?>[] DEFAULT_UNREADABLES = new Class[]{InputStream.class, Reader.class};
   public static final Class<?>[] DEFAULT_UNWRITABLES = new Class[]{InputStream.class, OutputStream.class, Writer.class, StreamingOutput.class, Response.class};
   protected static final int JAXRS_FEATURE_DEFAULTS = JaxRSFeature.collectDefaults();
   protected final MAPPER_CONFIG _mapperConfig;
   protected HashMap<ClassKey, Boolean> _cfgCustomUntouchables;
   protected boolean _cfgCheckCanSerialize = false;
   protected boolean _cfgCheckCanDeserialize = false;
   protected int _jaxRSFeatures;
   protected Class<?> _defaultReadView;
   protected Class<?> _defaultWriteView;
   public static final HashSet<ClassKey> _untouchables = DEFAULT_UNTOUCHABLES;
   public static final Class<?>[] _unreadableClasses = DEFAULT_UNREADABLES;
   public static final Class<?>[] _unwritableClasses = DEFAULT_UNWRITABLES;
   protected final LRUMap<AnnotationBundleKey, EP_CONFIG> _readers = new LRUMap<>(16, 120);
   protected final LRUMap<AnnotationBundleKey, EP_CONFIG> _writers = new LRUMap<>(16, 120);

   protected ProviderBase(MAPPER_CONFIG mconfig) {
      this._mapperConfig = mconfig;
      this._jaxRSFeatures = JAXRS_FEATURE_DEFAULTS;
   }

   @Deprecated
   protected ProviderBase() {
      this._mapperConfig = null;
      this._jaxRSFeatures = JAXRS_FEATURE_DEFAULTS;
   }

   public void checkCanDeserialize(boolean state) {
      this._cfgCheckCanDeserialize = state;
   }

   public void checkCanSerialize(boolean state) {
      this._cfgCheckCanSerialize = state;
   }

   public void addUntouchable(Class<?> type) {
      if (this._cfgCustomUntouchables == null) {
         this._cfgCustomUntouchables = new HashMap<>();
      }

      this._cfgCustomUntouchables.put(new ClassKey(type), Boolean.TRUE);
   }

   public void removeUntouchable(Class<?> type) {
      if (this._cfgCustomUntouchables == null) {
         this._cfgCustomUntouchables = new HashMap<>();
      }

      this._cfgCustomUntouchables.put(new ClassKey(type), Boolean.FALSE);
   }

   public void setAnnotationsToUse(Annotations[] annotationsToUse) {
      this._mapperConfig.setAnnotationsToUse(annotationsToUse);
   }

   public void setMapper(MAPPER m) {
      this._mapperConfig.setMapper(m);
   }

   public THIS setDefaultReadView(Class<?> view) {
      this._defaultReadView = view;
      return this._this();
   }

   public THIS setDefaultWriteView(Class<?> view) {
      this._defaultWriteView = view;
      return this._this();
   }

   public THIS setDefaultView(Class<?> view) {
      this._defaultReadView = this._defaultWriteView = view;
      return this._this();
   }

   public THIS configure(JaxRSFeature feature, boolean state) {
      return state ? this.enable(feature) : this.disable(feature);
   }

   public THIS enable(JaxRSFeature feature) {
      this._jaxRSFeatures = this._jaxRSFeatures | feature.getMask();
      return this._this();
   }

   public THIS enable(JaxRSFeature first, JaxRSFeature... f2) {
      this._jaxRSFeatures = this._jaxRSFeatures | first.getMask();

      for (JaxRSFeature f : f2) {
         this._jaxRSFeatures = this._jaxRSFeatures | f.getMask();
      }

      return this._this();
   }

   public THIS disable(JaxRSFeature feature) {
      this._jaxRSFeatures = this._jaxRSFeatures & ~feature.getMask();
      return this._this();
   }

   public THIS disable(JaxRSFeature first, JaxRSFeature... f2) {
      this._jaxRSFeatures = this._jaxRSFeatures & ~first.getMask();

      for (JaxRSFeature f : f2) {
         this._jaxRSFeatures = this._jaxRSFeatures & ~f.getMask();
      }

      return this._this();
   }

   public boolean isEnabled(JaxRSFeature f) {
      return (this._jaxRSFeatures & f.getMask()) != 0;
   }

   public THIS configure(DeserializationFeature f, boolean state) {
      this._mapperConfig.configure(f, state);
      return this._this();
   }

   public THIS enable(DeserializationFeature f) {
      this._mapperConfig.configure(f, true);
      return this._this();
   }

   public THIS disable(DeserializationFeature f) {
      this._mapperConfig.configure(f, false);
      return this._this();
   }

   public THIS configure(SerializationFeature f, boolean state) {
      this._mapperConfig.configure(f, state);
      return this._this();
   }

   public THIS enable(SerializationFeature f) {
      this._mapperConfig.configure(f, true);
      return this._this();
   }

   public THIS disable(SerializationFeature f) {
      this._mapperConfig.configure(f, false);
      return this._this();
   }

   public THIS enable(Feature f) {
      this._mapperConfig.configure(f, true);
      return this._this();
   }

   public THIS enable(com.fasterxml.jackson.core.JsonGenerator.Feature f) {
      this._mapperConfig.configure(f, true);
      return this._this();
   }

   public THIS disable(Feature f) {
      this._mapperConfig.configure(f, false);
      return this._this();
   }

   public THIS disable(com.fasterxml.jackson.core.JsonGenerator.Feature f) {
      this._mapperConfig.configure(f, false);
      return this._this();
   }

   public THIS configure(Feature f, boolean state) {
      this._mapperConfig.configure(f, state);
      return this._this();
   }

   public THIS configure(com.fasterxml.jackson.core.JsonGenerator.Feature f, boolean state) {
      this._mapperConfig.configure(f, state);
      return this._this();
   }

   protected boolean hasMatchingMediaTypeForReading(MediaType mediaType) {
      return this.hasMatchingMediaType(mediaType);
   }

   protected boolean hasMatchingMediaTypeForWriting(MediaType mediaType) {
      return this.hasMatchingMediaType(mediaType);
   }

   protected abstract boolean hasMatchingMediaType(MediaType var1);

   protected abstract MAPPER _locateMapperViaProvider(Class<?> var1, MediaType var2);

   protected EP_CONFIG _configForReading(MAPPER mapper, Annotation[] annotations, Class<?> defaultView) {
      ObjectReader r;
      if (defaultView != null) {
         r = mapper.readerWithView(defaultView);
      } else {
         r = mapper.reader();
      }

      if (JaxRSFeature.READ_FULL_STREAM.enabledIn(this._jaxRSFeatures)) {
         r = r.withFeatures(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_TRAILING_TOKENS});
      }

      return this._configForReading(r, annotations);
   }

   protected EP_CONFIG _configForWriting(MAPPER mapper, Annotation[] annotations, Class<?> defaultView) {
      ObjectWriter w;
      if (defaultView != null) {
         w = mapper.writerWithView(defaultView);
      } else {
         w = mapper.writer();
      }

      return this._configForWriting(w, annotations);
   }

   protected abstract EP_CONFIG _configForReading(ObjectReader var1, Annotation[] var2);

   protected abstract EP_CONFIG _configForWriting(ObjectWriter var1, Annotation[] var2);

   public long getSize(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1L;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      if (!this.hasMatchingMediaType(mediaType)) {
         return false;
      } else {
         Boolean customUntouchable = this._findCustomUntouchable(type);
         if (customUntouchable != null) {
            return !customUntouchable;
         } else if (this._isIgnorableForWriting(new ClassKey(type))) {
            return false;
         } else {
            for (Class<?> cls : _unwritableClasses) {
               if (cls.isAssignableFrom(type)) {
                  return false;
               }
            }

            return !this._cfgCheckCanSerialize || this.locateMapper(type, mediaType).canSerialize(type);
         }
      }
   }

   public void writeTo(
      Object value,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      EP_CONFIG endpoint = this._endpointForWriting(value, type, genericType, annotations, mediaType, httpHeaders);
      this._modifyHeaders(value, type, genericType, annotations, httpHeaders, endpoint);
      ObjectWriter writer = endpoint.getWriter();
      JsonEncoding enc = this.findEncoding(mediaType, httpHeaders);
      JsonGenerator g = this._createGenerator(writer, entityStream, enc);
      boolean ok = false;

      try {
         if (writer.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            g.useDefaultPrettyPrinter();
         }

         JavaType rootType = null;
         if (genericType != null && value != null && !(genericType instanceof Class)) {
            TypeFactory typeFactory = writer.getTypeFactory();
            JavaType baseType = typeFactory.constructType(genericType);
            rootType = typeFactory.constructSpecializedType(baseType, type);
            if (rootType.getRawClass() == Object.class) {
               rootType = null;
            }
         }

         if (rootType != null) {
            writer = writer.forType(rootType);
         }

         value = endpoint.modifyBeforeWrite(value);
         ObjectWriterModifier mod = ObjectWriterInjector.getAndClear();
         if (mod != null) {
            writer = mod.modify(endpoint, httpHeaders, value, writer, g);
         }

         writer.writeValue(g, value);
         ok = true;
      } finally {
         if (ok) {
            g.close();
         } else {
            try {
               g.close();
            } catch (Exception var21) {
            }
         }
      }
   }

   protected JsonEncoding findEncoding(MediaType mediaType, MultivaluedMap<String, Object> httpHeaders) {
      return JsonEncoding.UTF8;
   }

   protected void _modifyHeaders(
      Object value, Class<?> type, Type genericType, Annotation[] annotations, MultivaluedMap<String, Object> httpHeaders, EP_CONFIG endpoint
   ) throws IOException {
      if (this.isEnabled(JaxRSFeature.ADD_NO_SNIFF_HEADER)) {
         httpHeaders.add("X-Content-Type-Options", "nosniff");
      }
   }

   protected JsonGenerator _createGenerator(ObjectWriter writer, OutputStream rawStream, JsonEncoding enc) throws IOException {
      JsonGenerator g = writer.getFactory().createGenerator(rawStream, enc);
      g.disable(com.fasterxml.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_TARGET);
      return g;
   }

   protected EP_CONFIG _endpointForWriting(
      Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders
   ) {
      if (!this.isEnabled(JaxRSFeature.CACHE_ENDPOINT_WRITERS)) {
         return this._configForWriting(this.locateMapper(type, mediaType), annotations, this._defaultWriteView);
      } else {
         AnnotationBundleKey key = new AnnotationBundleKey(annotations, type);
         EP_CONFIG endpoint = this._writers.get(key);
         if (endpoint == null) {
            MAPPER mapper = this.locateMapper(type, mediaType);
            endpoint = this._configForWriting(mapper, annotations, this._defaultWriteView);
            this._writers.put(key.immutableKey(), endpoint);
         }

         return endpoint;
      }
   }

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      if (!this.hasMatchingMediaType(mediaType)) {
         return false;
      } else {
         Boolean customUntouchable = this._findCustomUntouchable(type);
         if (customUntouchable != null) {
            return !customUntouchable;
         } else if (this._isIgnorableForReading(new ClassKey(type))) {
            return false;
         } else {
            for (Class<?> cls : _unreadableClasses) {
               if (cls.isAssignableFrom(type)) {
                  return false;
               }
            }

            if (this._cfgCheckCanDeserialize) {
               if (this._isSpecialReadable(type)) {
                  return true;
               }

               ObjectMapper mapper = this.locateMapper(type, mediaType);
               if (!mapper.canDeserialize(mapper.constructType(type))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public Object readFrom(
      Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream
   ) throws IOException {
      EP_CONFIG endpoint = this._endpointForReading(type, genericType, annotations, mediaType, httpHeaders);
      ObjectReader reader = endpoint.getReader();
      JsonParser p = this._createParser(reader, entityStream);
      if (p != null && p.nextToken() != null) {
         if (type == JsonParser.class) {
            return p;
         } else {
            TypeFactory tf = reader.getTypeFactory();
            JavaType resolvedType = tf.constructType(genericType);
            boolean multiValued = type == MappingIterator.class;
            if (multiValued) {
               JavaType[] contents = tf.findTypeParameters(resolvedType, MappingIterator.class);
               JavaType valueType = contents != null && contents.length != 0 ? contents[0] : tf.constructType(Object.class);
               reader = reader.forType(valueType);
            } else {
               reader = reader.forType(resolvedType);
            }

            ObjectReaderModifier mod = ObjectReaderInjector.getAndClear();
            if (mod != null) {
               reader = mod.modify(endpoint, httpHeaders, resolvedType, reader, p);
            }

            return multiValued ? reader.readValues(p) : reader.readValue(p);
         }
      } else if (JaxRSFeature.ALLOW_EMPTY_INPUT.enabledIn(this._jaxRSFeatures)) {
         return null;
      } else {
         throw this._createNoContentException();
      }
   }

   protected JsonParser _createParser(ObjectReader reader, InputStream rawStream) throws IOException {
      JsonParser p = reader.getFactory().createParser(rawStream);
      p.disable(Feature.AUTO_CLOSE_SOURCE);
      return p;
   }

   protected EP_CONFIG _endpointForReading(
      Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders
   ) {
      if (!this.isEnabled(JaxRSFeature.CACHE_ENDPOINT_READERS)) {
         return this._configForReading(this.locateMapper(type, mediaType), annotations, this._defaultReadView);
      } else {
         AnnotationBundleKey key = new AnnotationBundleKey(annotations, type);
         EP_CONFIG endpoint = this._readers.get(key);
         if (endpoint == null) {
            MAPPER mapper = this.locateMapper(type, mediaType);
            endpoint = this._configForReading(mapper, annotations, this._defaultReadView);
            this._readers.put(key.immutableKey(), endpoint);
         }

         return endpoint;
      }
   }

   public MAPPER locateMapper(Class<?> type, MediaType mediaType) {
      if (this.isEnabled(JaxRSFeature.DYNAMIC_OBJECT_MAPPER_LOOKUP)) {
         MAPPER m = this._locateMapperViaProvider(type, mediaType);
         if (m == null) {
            m = this._mapperConfig.getConfiguredMapper();
            if (m == null) {
               m = this._mapperConfig.getDefaultMapper();
            }
         }

         return m;
      } else {
         MAPPER m = this._mapperConfig.getConfiguredMapper();
         if (m == null) {
            m = this._locateMapperViaProvider(type, mediaType);
            if (m == null) {
               m = this._mapperConfig.getDefaultMapper();
            }
         }

         return m;
      }
   }

   protected boolean _isSpecialReadable(Class<?> type) {
      return JsonParser.class == type;
   }

   protected boolean _isIgnorableForReading(ClassKey typeKey) {
      return _untouchables.contains(typeKey);
   }

   protected boolean _isIgnorableForWriting(ClassKey typeKey) {
      return _untouchables.contains(typeKey);
   }

   protected IOException _createNoContentException() {
      return new NoContentException("No content (empty input stream)");
   }

   protected static boolean _containedIn(Class<?> mainType, HashSet<ClassKey> set) {
      if (set != null) {
         ClassKey key = new ClassKey(mainType);
         if (set.contains(key)) {
            return true;
         }

         for (Class<?> cls : findSuperTypes(mainType, null)) {
            key.reset(cls);
            if (set.contains(key)) {
               return true;
            }
         }
      }

      return false;
   }

   protected Boolean _findCustomUntouchable(Class<?> mainType) {
      if (this._cfgCustomUntouchables != null) {
         ClassKey key = new ClassKey(mainType);
         Boolean b = this._cfgCustomUntouchables.get(key);
         if (b != null) {
            return b;
         }

         for (Class<?> cls : findSuperTypes(mainType, null)) {
            key.reset(cls);
            b = this._cfgCustomUntouchables.get(key);
            if (b != null) {
               return b;
            }
         }
      }

      return null;
   }

   protected static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
      return findSuperTypes(cls, endBefore, new ArrayList<>(8));
   }

   protected static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
      _addSuperTypes(cls, endBefore, result, false);
      return result;
   }

   protected static void _addSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
      if (cls != endBefore && cls != null && cls != Object.class) {
         if (addClassItself) {
            if (result.contains(cls)) {
               return;
            }

            result.add(cls);
         }

         for (Class<?> intCls : cls.getInterfaces()) {
            _addSuperTypes(intCls, endBefore, result, true);
         }

         _addSuperTypes(cls.getSuperclass(), endBefore, result, true);
      }
   }

   private final THIS _this() {
      return (THIS)this;
   }

   static {
      DEFAULT_UNTOUCHABLES.add(new ClassKey(InputStream.class));
      DEFAULT_UNTOUCHABLES.add(new ClassKey(Reader.class));
      DEFAULT_UNTOUCHABLES.add(new ClassKey(OutputStream.class));
      DEFAULT_UNTOUCHABLES.add(new ClassKey(Writer.class));
      DEFAULT_UNTOUCHABLES.add(new ClassKey(char[].class));
      DEFAULT_UNTOUCHABLES.add(new ClassKey(String.class));
      DEFAULT_UNTOUCHABLES.add(new ClassKey(byte[].class));
   }
}
