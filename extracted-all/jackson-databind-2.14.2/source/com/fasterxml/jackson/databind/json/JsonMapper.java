package com.fasterxml.jackson.databind.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.cfg.PackageVersion;

public class JsonMapper extends ObjectMapper {
   private static final long serialVersionUID = 1L;

   public JsonMapper() {
      this(new JsonFactory());
   }

   public JsonMapper(JsonFactory f) {
      super(f);
   }

   protected JsonMapper(JsonMapper src) {
      super(src);
   }

   public JsonMapper copy() {
      this._checkInvalidCopy(JsonMapper.class);
      return new JsonMapper(this);
   }

   public static JsonMapper.Builder builder() {
      return new JsonMapper.Builder(new JsonMapper());
   }

   public static JsonMapper.Builder builder(JsonFactory streamFactory) {
      return new JsonMapper.Builder(new JsonMapper(streamFactory));
   }

   public JsonMapper.Builder rebuild() {
      return new JsonMapper.Builder(this.copy());
   }

   @Override
   public Version version() {
      return PackageVersion.VERSION;
   }

   @Override
   public JsonFactory getFactory() {
      return this._jsonFactory;
   }

   public boolean isEnabled(JsonReadFeature f) {
      return this.isEnabled(f.mappedFeature());
   }

   public boolean isEnabled(JsonWriteFeature f) {
      return this.isEnabled(f.mappedFeature());
   }

   public static class Builder extends MapperBuilder<JsonMapper, JsonMapper.Builder> {
      public Builder(JsonMapper m) {
         super(m);
      }

      public JsonMapper.Builder enable(JsonReadFeature... features) {
         for (JsonReadFeature f : features) {
            this._mapper.enable(new Feature[]{f.mappedFeature()});
         }

         return this;
      }

      public JsonMapper.Builder disable(JsonReadFeature... features) {
         for (JsonReadFeature f : features) {
            this._mapper.disable(new Feature[]{f.mappedFeature()});
         }

         return this;
      }

      public JsonMapper.Builder configure(JsonReadFeature f, boolean state) {
         if (state) {
            this._mapper.enable(new Feature[]{f.mappedFeature()});
         } else {
            this._mapper.disable(new Feature[]{f.mappedFeature()});
         }

         return this;
      }

      public JsonMapper.Builder enable(JsonWriteFeature... features) {
         for (JsonWriteFeature f : features) {
            this._mapper.enable(new com.fasterxml.jackson.core.JsonGenerator.Feature[]{f.mappedFeature()});
         }

         return this;
      }

      public JsonMapper.Builder disable(JsonWriteFeature... features) {
         for (JsonWriteFeature f : features) {
            this._mapper.disable(new com.fasterxml.jackson.core.JsonGenerator.Feature[]{f.mappedFeature()});
         }

         return this;
      }

      public JsonMapper.Builder configure(JsonWriteFeature f, boolean state) {
         if (state) {
            this._mapper.enable(new com.fasterxml.jackson.core.JsonGenerator.Feature[]{f.mappedFeature()});
         } else {
            this._mapper.disable(new com.fasterxml.jackson.core.JsonGenerator.Feature[]{f.mappedFeature()});
         }

         return this;
      }
   }
}
