package org.eclipse.yasson.internal;

import jakarta.json.JsonStructure;
import jakarta.json.bind.JsonbException;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;
import org.eclipse.yasson.YassonJsonb;
import org.eclipse.yasson.internal.jsonstructure.JsonGeneratorToStructureAdapter;
import org.eclipse.yasson.internal.jsonstructure.JsonStructureToParserAdapter;

public class JsonBinding implements YassonJsonb {
   private final JsonbContext jsonbContext;

   JsonBinding(JsonBindingBuilder builder) {
      this.jsonbContext = new JsonbContext(builder.getConfig(), builder.getProvider().orElseGet(JsonProvider::provider));

      for (Class<?> eagerInitClass : this.jsonbContext.getConfigProperties().getEagerInitClasses()) {
         this.jsonbContext.getChainModelCreator().deserializerChain(eagerInitClass);
         this.jsonbContext.getSerializationModelCreator().serializerChain(eagerInitClass, true, true);
      }
   }

   private <T> T deserialize(Type type, JsonParser parser, DeserializationContextImpl unmarshaller) {
      return unmarshaller.deserialize(type, parser);
   }

   public <T> T fromJson(String str, Class<T> type) throws JsonbException {
      JsonParser parser = this.jsonbContext.getJsonProvider().createParser(new StringReader(str));

      Object var5;
      try {
         DeserializationContextImpl unmarshaller = new DeserializationContextImpl(this.jsonbContext);
         var5 = this.deserialize(type, parser, unmarshaller);
      } catch (Throwable var7) {
         if (parser != null) {
            try {
               parser.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (parser != null) {
         parser.close();
      }

      return (T)var5;
   }

   public <T> T fromJson(String str, Type type) throws JsonbException {
      JsonParser parser = this.jsonbContext.getJsonProvider().createParser(new StringReader(str));

      Object var5;
      try {
         DeserializationContextImpl unmarshaller = new DeserializationContextImpl(this.jsonbContext);
         var5 = this.deserialize(type, parser, unmarshaller);
      } catch (Throwable var7) {
         if (parser != null) {
            try {
               parser.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (parser != null) {
         parser.close();
      }

      return (T)var5;
   }

   public <T> T fromJson(Reader reader, Class<T> type) throws JsonbException {
      JsonParser parser = this.jsonbContext.getJsonProvider().createParser(reader);

      Object var5;
      try {
         DeserializationContextImpl unmarshaller = new DeserializationContextImpl(this.jsonbContext);
         var5 = this.deserialize(type, parser, unmarshaller);
      } catch (Throwable var7) {
         if (parser != null) {
            try {
               parser.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (parser != null) {
         parser.close();
      }

      return (T)var5;
   }

   public <T> T fromJson(Reader reader, Type type) throws JsonbException {
      JsonParser parser = this.jsonbContext.getJsonProvider().createParser(reader);

      Object var5;
      try {
         DeserializationContextImpl unmarshaller = new DeserializationContextImpl(this.jsonbContext);
         var5 = this.deserialize(type, parser, unmarshaller);
      } catch (Throwable var7) {
         if (parser != null) {
            try {
               parser.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (parser != null) {
         parser.close();
      }

      return (T)var5;
   }

   public <T> T fromJson(InputStream stream, Class<T> clazz) throws JsonbException {
      DeserializationContextImpl unmarshaller = new DeserializationContextImpl(this.jsonbContext);
      JsonParser parser = this.inputStreamParser(stream);

      Object var5;
      try {
         var5 = this.deserialize(clazz, this.inputStreamParser(stream), unmarshaller);
      } catch (Throwable var8) {
         if (parser != null) {
            try {
               parser.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (parser != null) {
         parser.close();
      }

      return (T)var5;
   }

   public <T> T fromJson(InputStream stream, Type type) throws JsonbException {
      DeserializationContextImpl unmarshaller = new DeserializationContextImpl(this.jsonbContext);
      JsonParser parser = this.inputStreamParser(stream);

      Object var5;
      try {
         var5 = this.deserialize(type, this.inputStreamParser(stream), unmarshaller);
      } catch (Throwable var8) {
         if (parser != null) {
            try {
               parser.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (parser != null) {
         parser.close();
      }

      return (T)var5;
   }

   @Override
   public <T> T fromJsonStructure(JsonStructure jsonStructure, Class<T> type) throws JsonbException {
      JsonParser parser = new JsonStructureToParserAdapter(jsonStructure);

      Object var4;
      try {
         var4 = this.deserialize(type, parser, new DeserializationContextImpl(this.jsonbContext));
      } catch (Throwable var7) {
         try {
            parser.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      parser.close();
      return (T)var4;
   }

   @Override
   public <T> T fromJsonStructure(JsonStructure jsonStructure, Type runtimeType) throws JsonbException {
      JsonParser parser = new JsonStructureToParserAdapter(jsonStructure);

      Object var4;
      try {
         var4 = this.deserialize(runtimeType, parser, new DeserializationContextImpl(this.jsonbContext));
      } catch (Throwable var7) {
         try {
            parser.close();
         } catch (Throwable var6) {
            var7.addSuppressed(var6);
         }

         throw var7;
      }

      parser.close();
      return (T)var4;
   }

   private JsonParser inputStreamParser(InputStream stream) {
      return this.jsonbContext
         .getJsonParserFactory()
         .createParser(stream, Charset.forName(this.jsonbContext.getConfig().getProperty("jsonb.encoding").orElse("UTF-8")));
   }

   public String toJson(Object object) throws JsonbException {
      StringWriter writer = new StringWriter();
      JsonGenerator generator = this.writerGenerator(writer);

      try {
         new SerializationContextImpl(this.jsonbContext).marshall(object, generator);
      } catch (Throwable var7) {
         if (generator != null) {
            try {
               generator.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (generator != null) {
         generator.close();
      }

      return writer.toString();
   }

   public String toJson(Object object, Type type) throws JsonbException {
      StringWriter writer = new StringWriter();
      JsonGenerator generator = this.writerGenerator(writer);

      try {
         new SerializationContextImpl(this.jsonbContext, type).marshall(object, generator);
      } catch (Throwable var8) {
         if (generator != null) {
            try {
               generator.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (generator != null) {
         generator.close();
      }

      return writer.toString();
   }

   public void toJson(Object object, Writer writer) throws JsonbException {
      SerializationContextImpl marshaller = new SerializationContextImpl(this.jsonbContext);
      JsonGenerator generator = this.writerGenerator(writer);

      try {
         marshaller.marshallWithoutClose(object, generator);
      } catch (Throwable var8) {
         if (generator != null) {
            try {
               generator.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (generator != null) {
         generator.close();
      }
   }

   public void toJson(Object object, Type type, Writer writer) throws JsonbException {
      SerializationContextImpl marshaller = new SerializationContextImpl(this.jsonbContext, type);
      JsonGenerator generator = this.writerGenerator(writer);

      try {
         marshaller.marshallWithoutClose(object, generator);
      } catch (Throwable var9) {
         if (generator != null) {
            try {
               generator.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (generator != null) {
         generator.close();
      }
   }

   private JsonGenerator writerGenerator(Writer writer) {
      Map<String, ?> factoryProperties = this.jsonbContext.createJsonpProperties(this.jsonbContext.getConfig());
      return factoryProperties.isEmpty()
         ? this.jsonbContext.getJsonProvider().createGenerator(writer)
         : this.jsonbContext.getJsonProvider().createGeneratorFactory(factoryProperties).createGenerator(writer);
   }

   public void toJson(Object object, OutputStream stream) throws JsonbException {
      SerializationContextImpl marshaller = new SerializationContextImpl(this.jsonbContext);
      JsonGenerator generator = this.streamGenerator(stream);

      try {
         marshaller.marshall(object, generator);
      } catch (Throwable var8) {
         if (generator != null) {
            try {
               generator.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (generator != null) {
         generator.close();
      }
   }

   public void toJson(Object object, Type type, OutputStream stream) throws JsonbException {
      SerializationContextImpl marshaller = new SerializationContextImpl(this.jsonbContext, type);
      JsonGenerator generator = this.streamGenerator(stream);

      try {
         marshaller.marshall(object, generator);
      } catch (Throwable var9) {
         if (generator != null) {
            try {
               generator.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (generator != null) {
         generator.close();
      }
   }

   @Override
   public <T> T fromJson(JsonParser jsonParser, Class<T> type) throws JsonbException {
      DeserializationContextImpl unmarshaller = new DeserializationContextImpl(this.jsonbContext);
      return unmarshaller.deserialize(type, jsonParser);
   }

   @Override
   public <T> T fromJson(JsonParser jsonParser, Type runtimeType) throws JsonbException {
      DeserializationContextImpl unmarshaller = new DeserializationContextImpl(this.jsonbContext);
      return unmarshaller.deserialize(runtimeType, jsonParser);
   }

   @Override
   public void toJson(Object object, JsonGenerator jsonGenerator) throws JsonbException {
      SerializationContextImpl marshaller = new SerializationContextImpl(this.jsonbContext);
      marshaller.marshallWithoutClose(object, jsonGenerator);
   }

   @Override
   public void toJson(Object object, Type runtimeType, JsonGenerator jsonGenerator) throws JsonbException {
      SerializationContextImpl marshaller = new SerializationContextImpl(this.jsonbContext, runtimeType);
      marshaller.marshallWithoutClose(object, jsonGenerator);
   }

   @Override
   public JsonStructure toJsonStructure(Object object) throws JsonbException {
      JsonGeneratorToStructureAdapter structureGenerator = new JsonGeneratorToStructureAdapter(this.jsonbContext.getJsonProvider());
      SerializationContextImpl marshaller = new SerializationContextImpl(this.jsonbContext);
      marshaller.marshall(object, structureGenerator);
      return structureGenerator.getRootStructure();
   }

   @Override
   public JsonStructure toJsonStructure(Object object, Type runtimeType) throws JsonbException {
      JsonGeneratorToStructureAdapter structureGenerator = new JsonGeneratorToStructureAdapter(this.jsonbContext.getJsonProvider());
      SerializationContextImpl marshaller = new SerializationContextImpl(this.jsonbContext, runtimeType);
      marshaller.marshall(object, structureGenerator);
      return structureGenerator.getRootStructure();
   }

   private JsonGenerator streamGenerator(OutputStream stream) {
      Map<String, ?> factoryProperties = this.jsonbContext.createJsonpProperties(this.jsonbContext.getConfig());
      String encoding = this.jsonbContext.getConfig().getProperty("jsonb.encoding").orElse("UTF-8");
      return this.jsonbContext.getJsonProvider().createGeneratorFactory(factoryProperties).createGenerator(stream, Charset.forName(encoding));
   }

   public void close() throws Exception {
      this.jsonbContext.getComponentInstanceCreator().close();
   }
}
