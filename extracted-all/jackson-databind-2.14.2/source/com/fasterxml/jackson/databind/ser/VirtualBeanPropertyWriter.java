package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.util.Annotations;
import java.io.Serializable;

public abstract class VirtualBeanPropertyWriter extends BeanPropertyWriter implements Serializable {
   private static final long serialVersionUID = 1L;

   protected VirtualBeanPropertyWriter(BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType) {
      this(propDef, contextAnnotations, declaredType, null, null, null, propDef.findInclusion());
   }

   protected VirtualBeanPropertyWriter() {
   }

   protected VirtualBeanPropertyWriter(
      BeanPropertyDefinition propDef,
      Annotations contextAnnotations,
      JavaType declaredType,
      JsonSerializer<?> ser,
      TypeSerializer typeSer,
      JavaType serType,
      Value inclusion,
      Class<?>[] includeInViews
   ) {
      super(
         propDef,
         propDef.getPrimaryMember(),
         contextAnnotations,
         declaredType,
         ser,
         typeSer,
         serType,
         _suppressNulls(inclusion),
         _suppressableValue(inclusion),
         includeInViews
      );
   }

   @Deprecated
   protected VirtualBeanPropertyWriter(
      BeanPropertyDefinition propDef,
      Annotations contextAnnotations,
      JavaType declaredType,
      JsonSerializer<?> ser,
      TypeSerializer typeSer,
      JavaType serType,
      Value inclusion
   ) {
      this(propDef, contextAnnotations, declaredType, ser, typeSer, serType, inclusion, null);
   }

   protected VirtualBeanPropertyWriter(VirtualBeanPropertyWriter base) {
      super(base);
   }

   protected VirtualBeanPropertyWriter(VirtualBeanPropertyWriter base, PropertyName name) {
      super(base, name);
   }

   protected static boolean _suppressNulls(Value inclusion) {
      if (inclusion == null) {
         return false;
      } else {
         Include incl = inclusion.getValueInclusion();
         return incl != Include.ALWAYS && incl != Include.USE_DEFAULTS;
      }
   }

   protected static Object _suppressableValue(Value inclusion) {
      if (inclusion == null) {
         return false;
      } else {
         Include incl = inclusion.getValueInclusion();
         return incl != Include.ALWAYS && incl != Include.NON_NULL && incl != Include.USE_DEFAULTS ? MARKER_FOR_EMPTY : null;
      }
   }

   @Override
   public boolean isVirtual() {
      return true;
   }

   protected abstract Object value(Object var1, JsonGenerator var2, SerializerProvider var3) throws Exception;

   public abstract VirtualBeanPropertyWriter withConfig(MapperConfig<?> var1, AnnotatedClass var2, BeanPropertyDefinition var3, JavaType var4);

   @Override
   public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
      Object value = this.value(bean, gen, prov);
      if (value == null) {
         if (this._nullSerializer != null) {
            gen.writeFieldName(this._name);
            this._nullSerializer.serialize(null, gen, prov);
         }
      } else {
         JsonSerializer<Object> ser = this._serializer;
         if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap m = this._dynamicSerializers;
            ser = m.serializerFor(cls);
            if (ser == null) {
               ser = this._findAndAddDynamic(m, cls, prov);
            }
         }

         if (this._suppressableValue != null) {
            if (MARKER_FOR_EMPTY == this._suppressableValue) {
               if (ser.isEmpty(prov, value)) {
                  return;
               }
            } else if (this._suppressableValue.equals(value)) {
               return;
            }
         }

         if (value != bean || !this._handleSelfReference(bean, gen, prov, ser)) {
            gen.writeFieldName(this._name);
            if (this._typeSerializer == null) {
               ser.serialize(value, gen, prov);
            } else {
               ser.serializeWithType(value, gen, prov, this._typeSerializer);
            }
         }
      }
   }

   @Override
   public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
      Object value = this.value(bean, gen, prov);
      if (value == null) {
         if (this._nullSerializer != null) {
            this._nullSerializer.serialize(null, gen, prov);
         } else {
            gen.writeNull();
         }
      } else {
         JsonSerializer<Object> ser = this._serializer;
         if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
               ser = this._findAndAddDynamic(map, cls, prov);
            }
         }

         if (this._suppressableValue != null) {
            if (MARKER_FOR_EMPTY == this._suppressableValue) {
               if (ser.isEmpty(prov, value)) {
                  this.serializeAsPlaceholder(bean, gen, prov);
                  return;
               }
            } else if (this._suppressableValue.equals(value)) {
               this.serializeAsPlaceholder(bean, gen, prov);
               return;
            }
         }

         if (value != bean || !this._handleSelfReference(bean, gen, prov, ser)) {
            if (this._typeSerializer == null) {
               ser.serialize(value, gen, prov);
            } else {
               ser.serializeWithType(value, gen, prov, this._typeSerializer);
            }
         }
      }
   }
}
