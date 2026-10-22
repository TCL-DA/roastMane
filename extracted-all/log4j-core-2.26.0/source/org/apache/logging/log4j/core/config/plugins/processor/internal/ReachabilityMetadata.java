package org.apache.logging.log4j.core.config.plugins.processor.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.IntStream;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ReachabilityMetadata {
   public static final String FIELD_OR_METHOD_NAME = "name";
   public static final String PARAMETER_TYPES = "parameterTypes";
   public static final String TYPE_NAME = "name";
   public static final String FIELDS = "fields";
   public static final String METHODS = "methods";

   public static void writeReflectConfig(Collection<ReachabilityMetadata.Type> types, OutputStream output) throws IOException {
      Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);

      try {
         ReachabilityMetadata.Reflection reflection = new ReachabilityMetadata.Reflection(types);
         ReachabilityMetadata.MinimalJsonWriter jsonWriter = new ReachabilityMetadata.MinimalJsonWriter(writer);
         reflection.toJson(jsonWriter);
      } catch (Throwable var6) {
         try {
            writer.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      writer.close();
   }

   private ReachabilityMetadata() {
   }

   public static final class Field implements Comparable<ReachabilityMetadata.Field> {
      private final String name;

      public Field(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      void toJson(ReachabilityMetadata.MinimalJsonWriter jsonWriter) throws IOException {
         jsonWriter.writeObjectStart();
         jsonWriter.writeObjectKey("name");
         jsonWriter.writeString(this.name);
         jsonWriter.writeObjectEnd();
      }

      public int compareTo(ReachabilityMetadata.Field other) {
         return this.name.compareTo(other.name);
      }
   }

   public static final class Method implements Comparable<ReachabilityMetadata.Method> {
      private final String name;
      private final List<String> parameterTypes = new ArrayList<>();

      public Method(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public void addParameterType(final String parameterType) {
         this.parameterTypes.add(parameterType);
      }

      void toJson(ReachabilityMetadata.MinimalJsonWriter jsonWriter) throws IOException {
         jsonWriter.writeObjectStart();
         jsonWriter.writeObjectKey("name");
         jsonWriter.writeString(this.name);
         jsonWriter.writeSeparator();
         jsonWriter.writeObjectKey("parameterTypes");
         jsonWriter.writeArrayStart();
         boolean first = true;

         for (String parameterType : this.parameterTypes) {
            if (!first) {
               jsonWriter.writeSeparator();
            }

            first = false;
            jsonWriter.writeString(parameterType);
         }

         jsonWriter.writeArrayEnd();
         jsonWriter.writeObjectEnd();
      }

      public int compareTo(ReachabilityMetadata.Method other) {
         int result = this.name.compareTo(other.name);
         if (result == 0) {
            result = this.parameterTypes.size() - other.parameterTypes.size();
         }

         if (result == 0) {
            result = IntStream.range(0, this.parameterTypes.size())
               .map(idx -> this.parameterTypes.get(idx).compareTo(other.parameterTypes.get(idx)))
               .filter(r -> r != 0)
               .findFirst()
               .orElse(0);
         }

         return result;
      }
   }

   private static final class MinimalJsonWriter {
      private final Appendable output;

      public MinimalJsonWriter(Appendable output) {
         this.output = output;
      }

      public void writeString(CharSequence input) throws IOException {
         this.output.append('"');
         StringBuilder sb = new StringBuilder();
         JsonUtils.quoteAsString(input, sb);
         this.output.append(sb);
         this.output.append('"');
      }

      public void writeObjectStart() throws IOException {
         this.output.append('{');
      }

      public void writeObjectEnd() throws IOException {
         this.output.append('}');
      }

      public void writeObjectKey(CharSequence key) throws IOException {
         this.writeString(key);
         this.output.append(':');
      }

      public void writeArrayStart() throws IOException {
         this.output.append('[');
      }

      public void writeSeparator() throws IOException {
         this.output.append(',');
      }

      public void writeArrayEnd() throws IOException {
         this.output.append(']');
      }
   }

   public static final class Reflection {
      private final Collection<ReachabilityMetadata.Type> types = new TreeSet<>(Comparator.comparing(ReachabilityMetadata.Type::getType));

      public Reflection(Collection<ReachabilityMetadata.Type> types) {
         this.types.addAll(types);
      }

      void toJson(ReachabilityMetadata.MinimalJsonWriter jsonWriter) throws IOException {
         boolean first = true;
         jsonWriter.writeArrayStart();

         for (ReachabilityMetadata.Type type : this.types) {
            if (!first) {
               jsonWriter.writeSeparator();
            }

            first = false;
            type.toJson(jsonWriter);
         }

         jsonWriter.writeArrayEnd();
      }
   }

   public static final class Type {
      private final String type;
      private final Collection<ReachabilityMetadata.Method> methods = new TreeSet<>();
      private final Collection<ReachabilityMetadata.Field> fields = new TreeSet<>();

      public Type(String type) {
         this.type = type;
      }

      public String getType() {
         return this.type;
      }

      public void addMethod(ReachabilityMetadata.Method method) {
         this.methods.add(method);
      }

      public void addField(ReachabilityMetadata.Field field) {
         this.fields.add(field);
      }

      void toJson(ReachabilityMetadata.MinimalJsonWriter jsonWriter) throws IOException {
         jsonWriter.writeObjectStart();
         jsonWriter.writeObjectKey("name");
         jsonWriter.writeString(this.type);
         jsonWriter.writeSeparator();
         boolean first = true;
         jsonWriter.writeObjectKey("methods");
         jsonWriter.writeArrayStart();

         for (ReachabilityMetadata.Method method : this.methods) {
            if (!first) {
               jsonWriter.writeSeparator();
            }

            first = false;
            method.toJson(jsonWriter);
         }

         jsonWriter.writeArrayEnd();
         jsonWriter.writeSeparator();
         first = true;
         jsonWriter.writeObjectKey("fields");
         jsonWriter.writeArrayStart();

         for (ReachabilityMetadata.Field field : this.fields) {
            if (!first) {
               jsonWriter.writeSeparator();
            }

            first = false;
            field.toJson(jsonWriter);
         }

         jsonWriter.writeArrayEnd();
         jsonWriter.writeObjectEnd();
      }
   }
}
