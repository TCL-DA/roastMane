package org.glassfish.jersey.jackson.internal.jackson.jaxrs.json;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import java.util.ArrayList;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.MapperConfiguratorBase;

public class JsonMapperConfigurator extends MapperConfiguratorBase<JsonMapperConfigurator, ObjectMapper> {
   public JsonMapperConfigurator(ObjectMapper mapper, Annotations[] defAnnotations) {
      super(mapper, defAnnotations);
   }

   @Override
   public synchronized ObjectMapper getConfiguredMapper() {
      return this._mapper;
   }

   @Override
   public synchronized ObjectMapper getDefaultMapper() {
      if (this._defaultMapper == null) {
         this._defaultMapper = new ObjectMapper();
         this._setAnnotations(this._defaultMapper, this._defaultAnnotationsToUse);
      }

      return this._defaultMapper;
   }

   @Override
   protected ObjectMapper mapper() {
      if (this._mapper == null) {
         this._mapper = new ObjectMapper();
         this._setAnnotations(this._mapper, this._defaultAnnotationsToUse);
      }

      return this._mapper;
   }

   @Override
   protected AnnotationIntrospector _resolveIntrospectors(Annotations[] annotationsToUse) {
      ArrayList<AnnotationIntrospector> intr = new ArrayList<>();

      for (Annotations a : annotationsToUse) {
         if (a != null) {
            intr.add(this._resolveIntrospector(a));
         }
      }

      int count = intr.size();
      if (count == 0) {
         return AnnotationIntrospector.nopInstance();
      } else {
         AnnotationIntrospector curr = intr.get(0);
         int i = 1;

         for (int len = intr.size(); i < len; i++) {
            curr = AnnotationIntrospector.pair(curr, intr.get(i));
         }

         return curr;
      }
   }

   protected AnnotationIntrospector _resolveIntrospector(Annotations ann) {
      switch (ann) {
         case JACKSON:
            return new JacksonAnnotationIntrospector();
         case JAXB:
            try {
               if (this._jaxbIntrospectorClass == null) {
                  this._jaxbIntrospectorClass = JaxbAnnotationIntrospector.class;
               }

               return this._jaxbIntrospectorClass.newInstance();
            } catch (Exception var3) {
               throw new IllegalStateException("Failed to instantiate JaxbAnnotationIntrospector: " + var3.getMessage(), var3);
            }
         default:
            throw new IllegalStateException();
      }
   }
}
