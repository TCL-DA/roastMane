package jakarta.json.bind;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

public interface Jsonb extends AutoCloseable {
   <T> T fromJson(String var1, Class<T> var2) throws JsonbException;

   <T> T fromJson(String var1, Type var2) throws JsonbException;

   <T> T fromJson(Reader var1, Class<T> var2) throws JsonbException;

   <T> T fromJson(Reader var1, Type var2) throws JsonbException;

   <T> T fromJson(InputStream var1, Class<T> var2) throws JsonbException;

   <T> T fromJson(InputStream var1, Type var2) throws JsonbException;

   String toJson(Object var1) throws JsonbException;

   String toJson(Object var1, Type var2) throws JsonbException;

   void toJson(Object var1, Writer var2) throws JsonbException;

   void toJson(Object var1, Type var2, Writer var3) throws JsonbException;

   void toJson(Object var1, OutputStream var2) throws JsonbException;

   void toJson(Object var1, Type var2, OutputStream var3) throws JsonbException;
}
