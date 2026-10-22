package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class MapperConfiguratorBase<IMPL extends MapperConfiguratorBase<IMPL, MAPPER>, MAPPER extends ObjectMapper> {
   protected MAPPER _mapper;
   protected MAPPER _defaultMapper;
   protected Annotations[] _defaultAnnotationsToUse;
   protected Class<? extends AnnotationIntrospector> _jaxbIntrospectorClass;

   public MapperConfiguratorBase(MAPPER mapper, Annotations[] defaultAnnotations) {
      this._mapper = mapper;
      this._defaultAnnotationsToUse = defaultAnnotations;
   }

   public abstract MAPPER getConfiguredMapper();

   public abstract MAPPER getDefaultMapper();

   protected abstract MAPPER mapper();

   protected abstract AnnotationIntrospector _resolveIntrospectors(Annotations[] var1);

   public final synchronized void setMapper(MAPPER m) {
      this._mapper = m;
   }

   public final synchronized void setAnnotationsToUse(Annotations[] annotationsToUse) {
      this._setAnnotations(this.mapper(), annotationsToUse);
   }

   public final synchronized void configure(DeserializationFeature f, boolean state) {
      this.mapper().configure(f, state);
   }

   public final synchronized void configure(SerializationFeature f, boolean state) {
      this.mapper().configure(f, state);
   }

   public final synchronized void configure(Feature f, boolean state) {
      this.mapper().configure(f, state);
   }

   public final synchronized void configure(com.fasterxml.jackson.core.JsonGenerator.Feature f, boolean state) {
      this.mapper().configure(f, state);
   }

   protected final void _setAnnotations(ObjectMapper mapper, Annotations[] annotationsToUse) {
      AnnotationIntrospector intr;
      if (annotationsToUse != null && annotationsToUse.length != 0) {
         intr = this._resolveIntrospectors(annotationsToUse);
      } else {
         intr = AnnotationIntrospector.nopInstance();
      }

      mapper.setAnnotationIntrospector(intr);
   }
}
