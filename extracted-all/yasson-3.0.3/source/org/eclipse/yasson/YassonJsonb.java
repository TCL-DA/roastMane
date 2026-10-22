package org.eclipse.yasson;

import jakarta.json.JsonStructure;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;

public interface YassonJsonb extends Jsonb {
   <T> T fromJson(JsonParser var1, Class<T> var2) throws JsonbException;

   <T> T fromJson(JsonParser var1, Type var2) throws JsonbException;

   <T> T fromJsonStructure(JsonStructure var1, Class<T> var2) throws JsonbException;

   <T> T fromJsonStructure(JsonStructure var1, Type var2) throws JsonbException;

   void toJson(Object var1, JsonGenerator var2) throws JsonbException;

   void toJson(Object var1, Type var2, JsonGenerator var3) throws JsonbException;

   JsonStructure toJsonStructure(Object var1) throws JsonbException;

   JsonStructure toJsonStructure(Object var1, Type var2) throws JsonbException;
}
