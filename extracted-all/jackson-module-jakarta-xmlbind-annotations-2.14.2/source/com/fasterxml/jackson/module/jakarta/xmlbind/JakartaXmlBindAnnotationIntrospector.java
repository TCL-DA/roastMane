package com.fasterxml.jackson.module.jakarta.xmlbind;

import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.AnnotationIntrospector.XmlExtensions;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.module.jakarta.xmlbind.deser.DataHandlerDeserializer;
import com.fasterxml.jackson.module.jakarta.xmlbind.ser.DataHandlerSerializer;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessOrder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorOrder;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.XmlElement.DEFAULT;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JakartaXmlBindAnnotationIntrospector extends AnnotationIntrospector implements XmlExtensions, Versioned {
   private static final long serialVersionUID = -1L;
   protected static final String DEFAULT_NAME_FOR_XML_VALUE = "value";
   protected static final boolean DEFAULT_IGNORE_XMLIDREF = false;
   protected static final String MARKER_FOR_DEFAULT = "##default";
   protected static final Value FORMAT_STRING = new Value().withShape(Shape.STRING);
   protected static final Value FORMAT_INT = new Value().withShape(Shape.NUMBER_INT);
   protected final String _jaxbPackageName;
   protected final JsonSerializer<?> _dataHandlerSerializer;
   protected final JsonDeserializer<?> _dataHandlerDeserializer;
   protected final TypeFactory _typeFactory;
   protected final boolean _ignoreXmlIDREF;
   protected String _xmlValueName = "value";
   protected Include _nonNillableInclusion = null;

   @Deprecated
   public JakartaXmlBindAnnotationIntrospector() {
      this(TypeFactory.defaultInstance());
   }

   public JakartaXmlBindAnnotationIntrospector(MapperConfig<?> config) {
      this(config.getTypeFactory());
   }

   public JakartaXmlBindAnnotationIntrospector(TypeFactory typeFactory) {
      this(typeFactory, false);
   }

   public JakartaXmlBindAnnotationIntrospector(TypeFactory typeFactory, boolean ignoreXmlIDREF) {
      this._typeFactory = typeFactory == null ? TypeFactory.defaultInstance() : typeFactory;
      this._ignoreXmlIDREF = ignoreXmlIDREF;
      this._jaxbPackageName = XmlElement.class.getPackage().getName();
      JsonSerializer<?> dataHandlerSerializer = null;
      JsonDeserializer<?> dataHandlerDeserializer = null;

      try {
         dataHandlerSerializer = (JsonSerializer<?>)DataHandlerSerializer.class.newInstance();
         dataHandlerDeserializer = (JsonDeserializer<?>)DataHandlerDeserializer.class.newInstance();
      } catch (Throwable var6) {
      }

      this._dataHandlerSerializer = dataHandlerSerializer;
      this._dataHandlerDeserializer = dataHandlerDeserializer;
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public void setNameUsedForXmlValue(String name) {
      this._xmlValueName = name;
   }

   public String getNameUsedForXmlValue() {
      return this._xmlValueName;
   }

   public JakartaXmlBindAnnotationIntrospector setNonNillableInclusion(Include incl) {
      this._nonNillableInclusion = incl;
      return this;
   }

   public Include getNonNillableInclusion() {
      return this._nonNillableInclusion;
   }

   public String findNamespace(MapperConfig<?> config, Annotated ann) {
      String ns = null;
      if (ann instanceof AnnotatedClass) {
         XmlRootElement elem = this.findRootElementAnnotation((AnnotatedClass)ann);
         if (elem != null) {
            ns = elem.namespace();
         }
      } else {
         XmlElement elem = this.findAnnotation(XmlElement.class, ann, false, false, false);
         if (elem != null) {
            ns = elem.namespace();
         }

         if (ns == null || "##default".equals(ns)) {
            XmlAttribute attr = this.findAnnotation(XmlAttribute.class, ann, false, false, false);
            if (attr != null) {
               ns = attr.namespace();
            }
         }
      }

      if ("##default".equals(ns)) {
         ns = null;
      }

      return ns;
   }

   public Boolean isOutputAsAttribute(MapperConfig<?> config, Annotated ann) {
      XmlAttribute attr = this.findAnnotation(XmlAttribute.class, ann, false, false, false);
      if (attr != null) {
         return Boolean.TRUE;
      } else {
         XmlElement elem = this.findAnnotation(XmlElement.class, ann, false, false, false);
         return elem != null ? Boolean.FALSE : null;
      }
   }

   public Boolean isOutputAsText(MapperConfig<?> config, Annotated ann) {
      XmlValue attr = this.findAnnotation(XmlValue.class, ann, false, false, false);
      return attr != null ? Boolean.TRUE : null;
   }

   public Boolean isOutputAsCData(MapperConfig<?> config, Annotated ann) {
      return null;
   }

   public ObjectIdInfo findObjectIdInfo(Annotated ann) {
      if (!(ann instanceof AnnotatedClass)) {
         return null;
      } else {
         AnnotatedClass ac = (AnnotatedClass)ann;
         PropertyName idPropName = null;

         label42:
         for (AnnotatedMethod m : ac.memberMethods()) {
            XmlID idProp = (XmlID)m.getAnnotation(XmlID.class);
            if (idProp != null) {
               switch (m.getParameterCount()) {
                  case 0:
                     idPropName = this.findJaxbPropertyName(m, m.getRawType(), this._okNameForGetter(m));
                     break label42;
                  case 1:
                     idPropName = this.findJaxbPropertyName(m, m.getRawType(), this._okNameForMutator(m));
                     break label42;
               }
            }
         }

         if (idPropName == null) {
            for (AnnotatedField f : ac.fields()) {
               XmlID idProp = (XmlID)f.getAnnotation(XmlID.class);
               if (idProp != null) {
                  idPropName = this.findJaxbPropertyName(f, f.getRawType(), f.getName());
                  break;
               }
            }
         }

         if (idPropName != null) {
            Class<?> scope = Object.class;
            return new ObjectIdInfo(idPropName, scope, PropertyGenerator.class, SimpleObjectIdResolver.class);
         } else {
            return null;
         }
      }
   }

   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo base) {
      if (!this._ignoreXmlIDREF) {
         XmlIDREF idref = (XmlIDREF)ann.getAnnotation(XmlIDREF.class);
         if (idref != null) {
            if (base == null) {
               base = ObjectIdInfo.empty();
            }

            base = base.withAlwaysAsId(true);
         }
      }

      return base;
   }

   public PropertyName findRootName(AnnotatedClass ac) {
      XmlRootElement elem = this.findRootElementAnnotation(ac);
      return elem != null ? _combineNames(elem.name(), elem.namespace(), "") : null;
   }

   public Boolean isIgnorableType(AnnotatedClass ac) {
      return null;
   }

   public boolean hasIgnoreMarker(AnnotatedMember m) {
      return m.getAnnotation(XmlTransient.class) != null;
   }

   public Boolean hasRequiredMarker(AnnotatedMember m) {
      XmlAttribute attr = (XmlAttribute)m.getAnnotation(XmlAttribute.class);
      if (attr != null) {
         return attr.required();
      } else {
         XmlElement elem = (XmlElement)m.getAnnotation(XmlElement.class);
         return elem != null ? elem.required() : null;
      }
   }

   public PropertyName findWrapperName(Annotated ann) {
      XmlElementWrapper w = this.findAnnotation(XmlElementWrapper.class, ann, false, false, false);
      if (w != null) {
         PropertyName name = _combineNames(w.name(), w.namespace(), "");
         if (!name.hasSimpleName()) {
            if (ann instanceof AnnotatedMethod) {
               AnnotatedMethod am = (AnnotatedMethod)ann;
               String str;
               if (am.getParameterCount() == 0) {
                  str = this._okNameForGetter(am);
               } else {
                  str = this._okNameForMutator(am);
               }

               if (str != null) {
                  return name.withSimpleName(str);
               }
            }

            return name.withSimpleName(ann.getName());
         } else {
            return name;
         }
      } else {
         return null;
      }
   }

   public String findImplicitPropertyName(AnnotatedMember m) {
      XmlValue valueInfo = (XmlValue)m.getAnnotation(XmlValue.class);
      return valueInfo != null ? this._xmlValueName : null;
   }

   public Value findFormat(Annotated m) {
      if (m instanceof AnnotatedClass) {
         XmlEnum ann = (XmlEnum)m.getAnnotation(XmlEnum.class);
         if (ann != null) {
            Class<?> type = ann.value();
            if (type == String.class || type.isEnum()) {
               return FORMAT_STRING;
            }

            if (Number.class.isAssignableFrom(type)) {
               return FORMAT_INT;
            }
         }
      }

      return null;
   }

   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
      XmlAccessType at = this.findAccessType(ac);
      if (at == null) {
         return checker;
      } else {
         switch (at) {
            case FIELD:
               return checker.withFieldVisibility(Visibility.ANY)
                  .withSetterVisibility(Visibility.NONE)
                  .withGetterVisibility(Visibility.NONE)
                  .withIsGetterVisibility(Visibility.NONE);
            case NONE:
               return checker.withFieldVisibility(Visibility.NONE)
                  .withSetterVisibility(Visibility.NONE)
                  .withGetterVisibility(Visibility.NONE)
                  .withIsGetterVisibility(Visibility.NONE);
            case PROPERTY:
               return checker.withFieldVisibility(Visibility.NONE)
                  .withSetterVisibility(Visibility.PUBLIC_ONLY)
                  .withGetterVisibility(Visibility.PUBLIC_ONLY)
                  .withIsGetterVisibility(Visibility.PUBLIC_ONLY);
            case PUBLIC_MEMBER:
               return checker.withFieldVisibility(Visibility.PUBLIC_ONLY)
                  .withSetterVisibility(Visibility.PUBLIC_ONLY)
                  .withGetterVisibility(Visibility.PUBLIC_ONLY)
                  .withIsGetterVisibility(Visibility.PUBLIC_ONLY);
            default:
               return checker;
         }
      }
   }

   protected XmlAccessType findAccessType(Annotated ac) {
      XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, ac, true, true, true);
      return at == null ? null : at.value();
   }

   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
      return null;
   }

   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
      return baseType.isContainerType() ? null : this._typeResolverFromXmlElements(am);
   }

   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
      if (containerType.getContentType() == null) {
         throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
      } else {
         return this._typeResolverFromXmlElements(am);
      }
   }

   protected TypeResolverBuilder<?> _typeResolverFromXmlElements(AnnotatedMember am) {
      XmlElements elems = this.findAnnotation(XmlElements.class, am, false, false, false);
      XmlElementRefs elemRefs = this.findAnnotation(XmlElementRefs.class, am, false, false, false);
      if (elems == null && elemRefs == null) {
         return null;
      } else {
         TypeResolverBuilder<?> b = new StdTypeResolverBuilder();
         b = b.init(Id.NAME, null);
         return b.inclusion(As.WRAPPER_OBJECT);
      }
   }

   public List<NamedType> findSubtypes(Annotated a) {
      XmlElements elems = this.findAnnotation(XmlElements.class, a, false, false, false);
      ArrayList<NamedType> result = null;
      if (elems != null) {
         result = new ArrayList<>();

         for (XmlElement elem : elems.value()) {
            String name = elem.name();
            if ("##default".equals(name)) {
               name = null;
            }

            result.add(new NamedType(elem.type(), name));
         }
      } else {
         XmlElementRefs elemRefs = this.findAnnotation(XmlElementRefs.class, a, false, false, false);
         if (elemRefs != null) {
            result = new ArrayList<>();

            for (XmlElementRef elemRef : elemRefs.value()) {
               Class<?> refType = elemRef.type();
               if (!JAXBElement.class.isAssignableFrom(refType)) {
                  String name = elemRef.name();
                  if (name == null || "##default".equals(name)) {
                     XmlRootElement rootElement = refType.getAnnotation(XmlRootElement.class);
                     if (rootElement != null) {
                        name = rootElement.name();
                     }
                  }

                  if (name == null || "##default".equals(name)) {
                     name = this._decapitalize(refType.getSimpleName());
                  }

                  result.add(new NamedType(refType, name));
               }
            }
         }
      }

      XmlSeeAlso ann = (XmlSeeAlso)a.getAnnotation(XmlSeeAlso.class);
      if (ann != null) {
         if (result == null) {
            result = new ArrayList<>();
         }

         for (Class<?> cls : ann.value()) {
            result.add(new NamedType(cls));
         }
      }

      return result;
   }

   public String findTypeName(AnnotatedClass ac) {
      XmlType type = this.findAnnotation(XmlType.class, ac, false, false, false);
      if (type != null) {
         String name = type.name();
         if (!"##default".equals(name)) {
            return name;
         }
      }

      return null;
   }

   public JsonSerializer<?> findSerializer(Annotated am) {
      Class<?> type = this._rawSerializationType(am);
      return type != null && this._dataHandlerSerializer != null && this.isDataHandler(type) ? this._dataHandlerSerializer : null;
   }

   private boolean isDataHandler(Class<?> type) {
      return type != null && Object.class != type && ("jakarta.activation.DataHandler".equals(type.getName()) || this.isDataHandler(type.getSuperclass()));
   }

   public Object findContentSerializer(Annotated a) {
      return null;
   }

   @Deprecated
   public Class<?> findSerializationType(Annotated a) {
      Class<?> allegedType = this._getTypeFromXmlElement(a);
      if (allegedType != null) {
         Class<?> rawPropType = this._rawSerializationType(a);
         if (!this.isContainerType(rawPropType)) {
            return allegedType;
         }
      }

      return null;
   }

   public com.fasterxml.jackson.annotation.JsonInclude.Value findPropertyInclusion(Annotated a) {
      Include incl = this._serializationInclusion(a, null);
      return incl == null
         ? com.fasterxml.jackson.annotation.JsonInclude.Value.empty()
         : com.fasterxml.jackson.annotation.JsonInclude.Value.construct(incl, null);
   }

   Include _serializationInclusion(Annotated a, Include defValue) {
      XmlElementWrapper w = (XmlElementWrapper)a.getAnnotation(XmlElementWrapper.class);
      if (w != null) {
         if (w.nillable()) {
            return Include.ALWAYS;
         }

         if (this._nonNillableInclusion != null) {
            return this._nonNillableInclusion;
         }
      }

      XmlElement e = (XmlElement)a.getAnnotation(XmlElement.class);
      if (e != null) {
         if (e.nillable()) {
            return Include.ALWAYS;
         }

         if (this._nonNillableInclusion != null) {
            return this._nonNillableInclusion;
         }
      }

      return defValue;
   }

   public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      Class<?> serClass = this._getTypeFromXmlElement(a);
      if (serClass == null) {
         return baseType;
      } else {
         TypeFactory tf = config.getTypeFactory();
         if (baseType.getContentType() == null) {
            if (!serClass.isAssignableFrom(baseType.getRawClass())) {
               return baseType;
            } else if (baseType.hasRawClass(serClass)) {
               return baseType.withStaticTyping();
            } else {
               try {
                  return tf.constructGeneralizedType(baseType, serClass);
               } catch (IllegalArgumentException var8) {
                  throw new JsonMappingException(
                     null,
                     String.format(
                        "Failed to widen type %s with annotation (value %s), from '%s': %s", baseType, serClass.getName(), a.getName(), var8.getMessage()
                     ),
                     var8
                  );
               }
            }
         } else {
            JavaType contentType = baseType.getContentType();
            if (contentType != null) {
               if (!serClass.isAssignableFrom(contentType.getRawClass())) {
                  return baseType;
               } else {
                  if (contentType.hasRawClass(serClass)) {
                     contentType = contentType.withStaticTyping();
                  } else {
                     try {
                        contentType = tf.constructGeneralizedType(contentType, serClass);
                     } catch (IllegalArgumentException var9) {
                        throw new JsonMappingException(
                           null,
                           String.format(
                              "Failed to widen value type of %s with concrete-type annotation (value %s), from '%s': %s",
                              baseType,
                              serClass.getName(),
                              a.getName(),
                              var9.getMessage()
                           ),
                           var9
                        );
                     }
                  }

                  return baseType.withContentType(contentType);
               }
            } else {
               return baseType;
            }
         }
      }
   }

   public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
      XmlType type = this.findAnnotation(XmlType.class, ac, true, true, true);
      if (type == null) {
         return null;
      } else {
         String[] order = type.propOrder();
         return order != null && order.length != 0 ? order : null;
      }
   }

   public Boolean findSerializationSortAlphabetically(Annotated ann) {
      return this._findAlpha(ann);
   }

   private final Boolean _findAlpha(Annotated ann) {
      XmlAccessorOrder order = this.findAnnotation(XmlAccessorOrder.class, ann, true, true, true);
      return order == null ? null : order.value() == XmlAccessOrder.ALPHABETICAL;
   }

   public Object findSerializationConverter(Annotated a) {
      Class<?> serType = this._rawSerializationType(a);
      XmlAdapter<?, ?> adapter = this.findAdapter(a, true, serType);
      return adapter != null ? this._converter(adapter, true) : null;
   }

   public Object findSerializationContentConverter(AnnotatedMember a) {
      Class<?> serType = this._rawSerializationType(a);
      if (this.isContainerType(serType)) {
         XmlAdapter<?, ?> adapter = this._findContentAdapter(a, true);
         if (adapter != null) {
            return this._converter(adapter, true);
         }
      }

      return null;
   }

   public PropertyName findNameForSerialization(Annotated a) {
      if (a instanceof AnnotatedMethod) {
         AnnotatedMethod am = (AnnotatedMethod)a;
         return this.isVisible(am) ? this.findJaxbPropertyName(am, am.getRawType(), this._okNameForGetter(am)) : null;
      } else if (a instanceof AnnotatedField) {
         AnnotatedField af = (AnnotatedField)a;
         return this.isVisible(af) ? this.findJaxbPropertyName(af, af.getRawType(), null) : null;
      } else {
         return null;
      }
   }

   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
      HashMap<String, String> expl = null;

      for (Field f : enumType.getDeclaredFields()) {
         if (f.isEnumConstant()) {
            XmlEnumValue enumValue = f.getAnnotation(XmlEnumValue.class);
            if (enumValue != null) {
               String n = enumValue.value();
               if (!n.isEmpty()) {
                  if (expl == null) {
                     expl = new HashMap<>();
                  }

                  expl.put(f.getName(), n);
               }
            }
         }
      }

      if (expl != null) {
         int i = 0;

         for (int end = enumValues.length; i < end; i++) {
            String defName = enumValues[i].name();
            String explValue = expl.get(defName);
            if (explValue != null) {
               names[i] = explValue;
            }
         }
      }

      return names;
   }

   public Object findDeserializer(Annotated am) {
      Class<?> type = this._rawDeserializationType(am);
      return type != null && this._dataHandlerDeserializer != null && this.isDataHandler(type) ? this._dataHandlerDeserializer : null;
   }

   public Object findKeyDeserializer(Annotated am) {
      return null;
   }

   public Object findContentDeserializer(Annotated a) {
      return null;
   }

   protected Class<?> _doFindDeserializationType(Annotated a, JavaType baseType) {
      if (a.hasAnnotation(XmlJavaTypeAdapter.class)) {
         return null;
      } else {
         XmlElement annotation = this.findAnnotation(XmlElement.class, a, false, false, false);
         if (annotation != null) {
            Class<?> type = annotation.type();
            if (type != DEFAULT.class) {
               return type;
            }
         }

         return null;
      }
   }

   public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
      Class<?> deserClass = this._getTypeFromXmlElement(a);
      if (deserClass == null) {
         return baseType;
      } else {
         TypeFactory tf = config.getTypeFactory();
         if (baseType.getContentType() == null) {
            if (baseType.hasRawClass(deserClass)) {
               return baseType;
            } else if (!baseType.getRawClass().isAssignableFrom(deserClass)) {
               return baseType;
            } else {
               try {
                  return tf.constructSpecializedType(baseType, deserClass);
               } catch (IllegalArgumentException var8) {
                  throw new JsonMappingException(
                     null,
                     String.format(
                        "Failed to narrow type %s with annotation (value %s), from '%s': %s", baseType, deserClass.getName(), a.getName(), var8.getMessage()
                     ),
                     var8
                  );
               }
            }
         } else {
            JavaType contentType = baseType.getContentType();
            if (contentType != null) {
               if (!contentType.getRawClass().isAssignableFrom(deserClass)) {
                  return baseType;
               } else {
                  try {
                     contentType = tf.constructSpecializedType(contentType, deserClass);
                     return baseType.withContentType(contentType);
                  } catch (IllegalArgumentException var9) {
                     throw new JsonMappingException(
                        null,
                        String.format(
                           "Failed to narrow type %s with annotation (value %s), from '%s': %s", baseType, deserClass.getName(), a.getName(), var9.getMessage()
                        ),
                        var9
                     );
                  }
               }
            } else {
               return baseType;
            }
         }
      }
   }

   public PropertyName findNameForDeserialization(Annotated a) {
      if (a instanceof AnnotatedMethod) {
         AnnotatedMethod am = (AnnotatedMethod)a;
         if (!this.isVisible(am)) {
            return null;
         } else {
            Class<?> rawType = am.getRawParameterType(0);
            return this.findJaxbPropertyName(am, rawType, this._okNameForMutator(am));
         }
      } else if (a instanceof AnnotatedField) {
         AnnotatedField af = (AnnotatedField)a;
         return this.isVisible(af) ? this.findJaxbPropertyName(af, af.getRawType(), null) : null;
      } else {
         return null;
      }
   }

   public Object findDeserializationConverter(Annotated a) {
      Class<?> deserType = this._rawDeserializationType(a);
      if (this.isContainerType(deserType)) {
         XmlAdapter<?, ?> adapter = this.findAdapter(a, true, deserType);
         if (adapter != null) {
            return this._converter(adapter, false);
         }
      } else {
         XmlAdapter<?, ?> adapter = this.findAdapter(a, true, deserType);
         if (adapter != null) {
            return this._converter(adapter, false);
         }
      }

      return null;
   }

   public Object findDeserializationContentConverter(AnnotatedMember a) {
      Class<?> deserType = this._rawDeserializationType(a);
      if (this.isContainerType(deserType)) {
         XmlAdapter<?, ?> adapter = this._findContentAdapter(a, false);
         if (adapter != null) {
            return this._converter(adapter, false);
         }
      }

      return null;
   }

   private boolean isVisible(AnnotatedField f) {
      for (Annotation annotation : f.getAnnotated().getDeclaredAnnotations()) {
         if (this.isJAXBAnnotation(annotation)) {
            return true;
         }
      }

      XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
      XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, f, true, true, true);
      if (at != null) {
         accessType = at.value();
      }

      if (accessType == XmlAccessType.FIELD) {
         return true;
      } else {
         return accessType == XmlAccessType.PUBLIC_MEMBER ? Modifier.isPublic(f.getAnnotated().getModifiers()) : false;
      }
   }

   private boolean isVisible(AnnotatedMethod m) {
      for (Annotation annotation : m.getAnnotated().getDeclaredAnnotations()) {
         if (this.isJAXBAnnotation(annotation)) {
            return true;
         }
      }

      XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;
      XmlAccessorType at = this.findAnnotation(XmlAccessorType.class, m, true, true, true);
      if (at != null) {
         accessType = at.value();
      }

      return accessType != XmlAccessType.PROPERTY && accessType != XmlAccessType.PUBLIC_MEMBER ? false : Modifier.isPublic(m.getModifiers());
   }

   private <A extends Annotation> A findAnnotation(
      Class<A> annotationClass, Annotated annotated, boolean includePackage, boolean includeClass, boolean includeSuperclasses
   ) {
      A annotation = (A)annotated.getAnnotation(annotationClass);
      if (annotation != null) {
         return annotation;
      } else {
         Class<?> memberClass = null;
         if (annotated instanceof AnnotatedParameter) {
            memberClass = ((AnnotatedParameter)annotated).getDeclaringClass();
         } else {
            AnnotatedElement annType = annotated.getAnnotated();
            if (annType instanceof Member) {
               memberClass = ((Member)annType).getDeclaringClass();
               if (includeClass) {
                  annotation = memberClass.getAnnotation(annotationClass);
                  if (annotation != null) {
                     return annotation;
                  }
               }
            } else if (annType instanceof Class) {
               memberClass = (Class<?>)annType;
            }
         }

         if (memberClass != null) {
            if (includeSuperclasses) {
               for (Class<?> superclass = memberClass.getSuperclass();
                  superclass != null && superclass != Object.class;
                  superclass = superclass.getSuperclass()
               ) {
                  annotation = superclass.getAnnotation(annotationClass);
                  if (annotation != null) {
                     return annotation;
                  }
               }
            }

            if (includePackage) {
               Package pkg = memberClass.getPackage();
               if (pkg != null) {
                  return memberClass.getPackage().getAnnotation(annotationClass);
               }
            }
         }

         return null;
      }
   }

   protected boolean isJAXBAnnotation(Annotation ann) {
      Class<?> cls = ann.annotationType();
      Package pkg = cls.getPackage();
      String pkgName = pkg != null ? pkg.getName() : cls.getName();
      return pkgName.startsWith(this._jaxbPackageName);
   }

   private PropertyName findJaxbPropertyName(Annotated ae, Class<?> aeType, String defaultName) {
      XmlAttribute attribute = (XmlAttribute)ae.getAnnotation(XmlAttribute.class);
      if (attribute != null) {
         return _combineNames(attribute.name(), attribute.namespace(), defaultName);
      } else {
         XmlElement element = (XmlElement)ae.getAnnotation(XmlElement.class);
         if (element != null) {
            return _combineNames(element.name(), element.namespace(), defaultName);
         } else {
            XmlElementRef elementRef = (XmlElementRef)ae.getAnnotation(XmlElementRef.class);
            boolean hasAName = elementRef != null;
            if (hasAName) {
               if (!"##default".equals(elementRef.name())) {
                  return _combineNames(elementRef.name(), elementRef.namespace(), defaultName);
               }

               if (aeType != null) {
                  XmlRootElement rootElement = aeType.getAnnotation(XmlRootElement.class);
                  if (rootElement != null) {
                     String name = rootElement.name();
                     if (!"##default".equals(name)) {
                        return _combineNames(name, rootElement.namespace(), defaultName);
                     }

                     return new PropertyName(this._decapitalize(aeType.getSimpleName()));
                  }
               }
            }

            if (!hasAName) {
               hasAName = ae.hasAnnotation(XmlElementWrapper.class) || ae.hasAnnotation(XmlElements.class) || ae.hasAnnotation(XmlValue.class);
            }

            return hasAName ? PropertyName.USE_DEFAULT : null;
         }
      }
   }

   private static PropertyName _combineNames(String localName, String namespace, String defaultName) {
      if ("##default".equals(localName)) {
         return "##default".equals(namespace) ? new PropertyName(defaultName) : new PropertyName(defaultName, namespace);
      } else {
         return "##default".equals(namespace) ? new PropertyName(localName) : new PropertyName(localName, namespace);
      }
   }

   private XmlRootElement findRootElementAnnotation(AnnotatedClass ac) {
      return this.findAnnotation(XmlRootElement.class, ac, true, false, true);
   }

   private XmlAdapter<Object, Object> findAdapter(Annotated am, boolean forSerialization, Class<?> type) {
      if (am instanceof AnnotatedClass) {
         return this.findAdapterForClass((AnnotatedClass)am, forSerialization);
      } else {
         XmlJavaTypeAdapter adapterInfo = this.findAnnotation(XmlJavaTypeAdapter.class, am, true, false, false);
         if (adapterInfo != null) {
            XmlAdapter<Object, Object> adapter = this.checkAdapter(adapterInfo, type, forSerialization);
            if (adapter != null) {
               return adapter;
            }
         }

         XmlJavaTypeAdapters adapters = this.findAnnotation(XmlJavaTypeAdapters.class, am, true, false, false);
         if (adapters != null) {
            for (XmlJavaTypeAdapter info : adapters.value()) {
               XmlAdapter<Object, Object> adapter = this.checkAdapter(info, type, forSerialization);
               if (adapter != null) {
                  return adapter;
               }
            }
         }

         return null;
      }
   }

   private final XmlAdapter<Object, Object> checkAdapter(XmlJavaTypeAdapter adapterInfo, Class<?> typeNeeded, boolean forSerialization) {
      Class<?> adaptedType = adapterInfo.type();
      if (adaptedType == jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT.class) {
         JavaType type = this._typeFactory.constructType(adapterInfo.value());
         JavaType[] params = this._typeFactory.findTypeParameters(type, XmlAdapter.class);
         adaptedType = params[1].getRawClass();
      }

      if (adaptedType.isAssignableFrom(typeNeeded)) {
         Class<? extends XmlAdapter> cls = adapterInfo.value();
         return (XmlAdapter<Object, Object>)ClassUtil.createInstance(cls, true);
      } else {
         return null;
      }
   }

   private XmlAdapter<Object, Object> findAdapterForClass(AnnotatedClass ac, boolean forSerialization) {
      XmlJavaTypeAdapter adapterInfo = ac.getAnnotated().getAnnotation(XmlJavaTypeAdapter.class);
      if (adapterInfo != null) {
         Class<? extends XmlAdapter> cls = adapterInfo.value();
         return (XmlAdapter<Object, Object>)ClassUtil.createInstance(cls, true);
      } else {
         return null;
      }
   }

   protected final TypeFactory getTypeFactory() {
      return this._typeFactory;
   }

   private boolean isContainerType(Class<?> raw) {
      return raw.isArray() || Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw);
   }

   private boolean adapterTypeMatches(XmlAdapter<?, ?> adapter, Class<?> targetType) {
      return this.findAdapterBoundType(adapter).isAssignableFrom(targetType);
   }

   private Class<?> findAdapterBoundType(XmlAdapter<?, ?> adapter) {
      TypeFactory tf = this.getTypeFactory();
      JavaType adapterType = tf.constructType(adapter.getClass());
      JavaType[] params = tf.findTypeParameters(adapterType, XmlAdapter.class);
      return params != null && params.length >= 2 ? params[1].getRawClass() : Object.class;
   }

   protected XmlAdapter<?, ?> _findContentAdapter(Annotated ann, boolean forSerialization) {
      Class<?> rawType = forSerialization ? this._rawSerializationType(ann) : this._rawDeserializationType(ann);
      if (this.isContainerType(rawType) && ann instanceof AnnotatedMember) {
         AnnotatedMember member = (AnnotatedMember)ann;
         JavaType fullType = forSerialization ? this._fullSerializationType(member) : this._fullDeserializationType(member);
         Class<?> contentType = fullType.getContentType().getRawClass();
         XmlAdapter<Object, Object> adapter = this.findAdapter(member, forSerialization, contentType);
         if (adapter != null && this.adapterTypeMatches(adapter, contentType)) {
            return adapter;
         }
      }

      return null;
   }

   protected String _propertyNameToString(PropertyName n) {
      return n == null ? null : n.getSimpleName();
   }

   protected Class<?> _rawDeserializationType(Annotated a) {
      if (a instanceof AnnotatedMethod) {
         AnnotatedMethod am = (AnnotatedMethod)a;
         if (am.getParameterCount() == 1) {
            return am.getRawParameterType(0);
         }
      }

      return a.getRawType();
   }

   protected JavaType _fullDeserializationType(AnnotatedMember am) {
      if (am instanceof AnnotatedMethod) {
         AnnotatedMethod method = (AnnotatedMethod)am;
         if (method.getParameterCount() == 1) {
            return ((AnnotatedMethod)am).getParameterType(0);
         }
      }

      return am.getType();
   }

   protected Class<?> _rawSerializationType(Annotated a) {
      return a.getRawType();
   }

   protected JavaType _fullSerializationType(AnnotatedMember am) {
      return am.getType();
   }

   protected Converter<Object, Object> _converter(XmlAdapter<?, ?> adapter, boolean forSerialization) {
      TypeFactory tf = this.getTypeFactory();
      JavaType adapterType = tf.constructType(adapter.getClass());
      JavaType[] pt = tf.findTypeParameters(adapterType, XmlAdapter.class);
      return forSerialization ? new AdapterConverter(adapter, pt[1], pt[0], forSerialization) : new AdapterConverter(adapter, pt[0], pt[1], forSerialization);
   }

   protected Class<?> _getTypeFromXmlElement(Annotated a) {
      XmlElement annotation = this.findAnnotation(XmlElement.class, a, false, false, false);
      if (annotation != null) {
         if (a.getAnnotation(XmlJavaTypeAdapter.class) != null) {
            return null;
         }

         Class<?> type = annotation.type();
         if (type != DEFAULT.class) {
            return type;
         }
      }

      return null;
   }

   protected String _okNameForGetter(AnnotatedMethod am) {
      String name = am.getName();
      if (name.startsWith("is")) {
         Class<?> rt = am.getRawType();
         if (rt == Boolean.class || rt == boolean.class) {
            return this._stdManglePropertyName(name, 2);
         }
      }

      return name.startsWith("get") ? this._stdManglePropertyName(name, 3) : null;
   }

   protected String _okNameForMutator(AnnotatedMethod am) {
      String name = am.getName();
      return name.startsWith("set") ? this._stdManglePropertyName(name, 3) : null;
   }

   protected String _stdManglePropertyName(String basename, int offset) {
      int end = basename.length();
      if (end == offset) {
         return null;
      } else {
         char c0 = basename.charAt(offset);
         char c1 = Character.toLowerCase(c0);
         if (c0 == c1) {
            return basename.substring(offset);
         } else if (offset + 1 < end && Character.isUpperCase(basename.charAt(offset + 1))) {
            return basename.substring(offset);
         } else {
            StringBuilder sb = new StringBuilder(end - offset);
            sb.append(c1);
            sb.append(basename, offset + 1, end);
            return sb.toString();
         }
      }
   }

   protected String _decapitalize(String name) {
      if (name.length() > 0) {
         char firstOrig = name.charAt(0);
         char firstLC = Character.toLowerCase(firstOrig);
         if (firstOrig != firstLC) {
            if (name.length() == 1) {
               return String.valueOf(firstLC);
            }

            if (!Character.isUpperCase(name.charAt(1))) {
               char[] chars = name.toCharArray();
               chars[0] = Character.toLowerCase(chars[0]);
               return new String(chars);
            }
         }
      }

      return name;
   }
}
