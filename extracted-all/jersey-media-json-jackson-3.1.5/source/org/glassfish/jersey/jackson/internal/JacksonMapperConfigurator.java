package org.glassfish.jersey.jackson.internal;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector;
import java.security.AccessController;
import java.util.ArrayList;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JsonMapperConfigurator;

public class JacksonMapperConfigurator extends JsonMapperConfigurator {
   private static LazyValue<Boolean> jaxb2AnnotationIntrospector = Values.lazy(() -> {
      Class<?> aClass = AccessController.doPrivileged(ReflectionHelper.classForNamePA("com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector"));
      return aClass != null;
   });

   public JacksonMapperConfigurator(ObjectMapper mapper, Annotations[] defAnnotations) {
      super(mapper, defAnnotations);
   }

   @Override
   protected AnnotationIntrospector _resolveIntrospectors(Annotations[] annotationsToUse) {
      ArrayList<AnnotationIntrospector> intr = new ArrayList<>();

      for (Annotations a : annotationsToUse) {
         if (a != null) {
            this._resolveIntrospector(a, intr);
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

   protected void _resolveIntrospector(Annotations ann, ArrayList<AnnotationIntrospector> intr) {
      switch (ann) {
         case JAXB:
            try {
               if (this._jaxbIntrospectorClass == null) {
                  this._jaxbIntrospectorClass = JakartaXmlBindAnnotationIntrospector.class;
               }

               intr.add((AnnotationIntrospector)JakartaXmlBindAnnotationIntrospector.class.newInstance());
            } catch (Exception var4) {
               throw new IllegalStateException("Failed to instantiate JakartaXmlBindAnnotationIntrospector: " + var4.getMessage(), var4);
            }

            if ((Boolean)jaxb2AnnotationIntrospector.get()) {
               Class<? extends AnnotationIntrospector> tempJaxbIntrospectorClass = this._jaxbIntrospectorClass;
               this._jaxbIntrospectorClass = null;
               intr.add(super._resolveIntrospector(ann));
               this._jaxbIntrospectorClass = tempJaxbIntrospectorClass;
            }
            break;
         default:
            intr.add(super._resolveIntrospector(ann));
      }
   }
}
