package com.fasterxml.jackson.module.jakarta.xmlbind;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.StdConverter;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class AdapterConverter extends StdConverter<Object, Object> {
   protected final JavaType _inputType;
   protected final JavaType _targetType;
   protected final XmlAdapter<Object, Object> _adapter;
   protected final boolean _forSerialization;

   public AdapterConverter(XmlAdapter<?, ?> adapter, JavaType inType, JavaType outType, boolean ser) {
      this._adapter = (XmlAdapter<Object, Object>)adapter;
      this._inputType = inType;
      this._targetType = outType;
      this._forSerialization = ser;
   }

   public Object convert(Object value) {
      try {
         return this._forSerialization ? this._adapter.marshal(value) : this._adapter.unmarshal(value);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   public JavaType getInputType(TypeFactory typeFactory) {
      return this._inputType;
   }

   public JavaType getOutputType(TypeFactory typeFactory) {
      return this._targetType;
   }
}
