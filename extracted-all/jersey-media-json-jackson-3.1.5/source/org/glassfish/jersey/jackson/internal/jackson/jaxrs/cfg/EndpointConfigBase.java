package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.annotation.JacksonFeatures;

public abstract class EndpointConfigBase<THIS extends EndpointConfigBase<THIS>> {
   protected final MapperConfig<?> _config;
   protected Class<?> _activeView;
   protected String _rootName;
   protected DeserializationFeature[] _deserEnable;
   protected DeserializationFeature[] _deserDisable;
   protected ObjectReader _reader;
   protected SerializationFeature[] _serEnable;
   protected SerializationFeature[] _serDisable;
   protected ObjectWriter _writer;

   protected EndpointConfigBase(MapperConfig<?> config) {
      this._config = config;
   }

   protected THIS add(Annotation[] annotations, boolean forWriting) {
      if (annotations != null) {
         for (Annotation annotation : annotations) {
            this.addAnnotation(annotation.annotationType(), annotation, forWriting);
         }
      }

      return (THIS)this;
   }

   protected void addAnnotation(Class<? extends Annotation> type, Annotation annotation, boolean forWriting) {
      if (type == JsonView.class) {
         Class<?>[] views = ((JsonView)annotation).value();
         this._activeView = views.length > 0 ? views[0] : null;
      } else if (type == JacksonFeatures.class) {
         JacksonFeatures feats = (JacksonFeatures)annotation;
         if (forWriting) {
            this._serEnable = nullIfEmpty(feats.serializationEnable());
            this._serDisable = nullIfEmpty(feats.serializationDisable());
         } else {
            this._deserEnable = nullIfEmpty(feats.deserializationEnable());
            this._deserDisable = nullIfEmpty(feats.deserializationDisable());
         }
      } else if (type == JsonRootName.class) {
         this._rootName = ((JsonRootName)annotation).value();
      } else if (type != JacksonAnnotationsInside.class) {
         JacksonAnnotationsInside inside = type.getAnnotation(JacksonAnnotationsInside.class);
         if (inside != null) {
            this.add(type.getAnnotations(), forWriting);
         }
      }
   }

   protected THIS initReader(ObjectReader reader) {
      if (this._activeView != null) {
         reader = reader.withView(this._activeView);
      }

      if (this._rootName != null) {
         reader = reader.withRootName(this._rootName);
      }

      if (this._deserEnable != null) {
         reader = reader.withFeatures(this._deserEnable);
      }

      if (this._deserDisable != null) {
         reader = reader.withoutFeatures(this._deserDisable);
      }

      this._reader = reader;
      return (THIS)this;
   }

   protected THIS initWriter(ObjectWriter writer) {
      if (this._activeView != null) {
         writer = writer.withView(this._activeView);
      }

      if (this._rootName != null) {
         writer = writer.withRootName(this._rootName);
      }

      if (this._serEnable != null) {
         writer = writer.withFeatures(this._serEnable);
      }

      if (this._serDisable != null) {
         writer = writer.withoutFeatures(this._serDisable);
      }

      this._writer = writer;
      return (THIS)this;
   }

   public String getRootName() {
      return this._rootName;
   }

   public Class<?> getActiveView() {
      return this._activeView;
   }

   public final ObjectReader getReader() {
      if (this._reader == null) {
         throw new IllegalStateException();
      } else {
         return this._reader;
      }
   }

   public final ObjectWriter getWriter() {
      if (this._writer == null) {
         throw new IllegalStateException();
      } else {
         return this._writer;
      }
   }

   public abstract Object modifyBeforeWrite(Object var1);

   protected static <T> T[] nullIfEmpty(T[] arg) {
      return arg != null && arg.length != 0 ? arg : null;
   }
}
