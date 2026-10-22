package jakarta.json.spi;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonException;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPatch;
import jakarta.json.JsonPatchBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonReader;
import jakarta.json.JsonReaderFactory;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonGeneratorFactory;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParserFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JsonProvider {
   public static final String JSONP_PROVIDER_FACTORY = "jakarta.json.provider";
   private static final String DEFAULT_PROVIDER = "org.eclipse.parsson.JsonProviderImpl";
   private static final Logger LOG = Logger.getLogger(JsonProvider.class.getName());
   private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "org.glassfish.hk2.osgiresourcelocator.ServiceLoader";

   protected JsonProvider() {
   }

   public static JsonProvider provider() {
      LOG.log(Level.FINE, "Checking system property {0}", "jakarta.json.provider");
      String factoryClassName = System.getSecurityManager() != null
         ? AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty("jakarta.json.provider")))
         : System.getProperty("jakarta.json.provider");
      if (factoryClassName != null) {
         JsonProvider provider = newInstance(factoryClassName);
         LOG.log(Level.FINE, "System property used; returning object [{0}]", provider.getClass().getName());
         return provider;
      } else {
         LOG.log(Level.FINE, "Checking ServiceLoader");
         ServiceLoader<JsonProvider> loader = ServiceLoader.load(JsonProvider.class);
         Iterator<JsonProvider> it = loader.iterator();
         if (it.hasNext()) {
            JsonProvider provider = it.next();
            LOG.log(Level.FINE, "ServiceLoader loading Facility used; returning object [{0}]", provider.getClass().getName());
            return provider;
         } else {
            if (isOsgi()) {
               LOG.log(Level.FINE, "Checking OSGi");
               JsonProvider provider = lookupUsingOSGiServiceLoader(JsonProvider.class);
               if (provider != null) {
                  LOG.log(Level.FINE, "OSGi loading facility used; returning object [{0}].", provider.getClass().getName());
                  return provider;
               }
            }

            LOG.fine("Trying to create the platform default provider");
            return newInstance("org.eclipse.parsson.JsonProviderImpl");
         }
      }
   }

   private static JsonProvider newInstance(String className) {
      try {
         checkPackageAccess(className);
         Class<JsonProvider> clazz = (Class<JsonProvider>)Class.forName(className);
         return clazz.getConstructor().newInstance();
      } catch (ClassNotFoundException var2) {
         throw new JsonException("Provider " + className + " not found", var2);
      } catch (Exception var3) {
         throw new JsonException("Provider " + className + " could not be instantiated: " + var3, var3);
      }
   }

   private static void checkPackageAccess(String className) {
      SecurityManager s = System.getSecurityManager();
      if (s != null) {
         int i = className.lastIndexOf(46);
         if (i != -1) {
            s.checkPackageAccess(className.substring(0, i));
         }
      }
   }

   public abstract JsonParser createParser(Reader var1);

   public abstract JsonParser createParser(InputStream var1);

   public abstract JsonParserFactory createParserFactory(Map<String, ?> var1);

   public abstract JsonGenerator createGenerator(Writer var1);

   public abstract JsonGenerator createGenerator(OutputStream var1);

   public abstract JsonGeneratorFactory createGeneratorFactory(Map<String, ?> var1);

   public abstract JsonReader createReader(Reader var1);

   public abstract JsonReader createReader(InputStream var1);

   public abstract JsonWriter createWriter(Writer var1);

   public abstract JsonWriter createWriter(OutputStream var1);

   public abstract JsonWriterFactory createWriterFactory(Map<String, ?> var1);

   public abstract JsonReaderFactory createReaderFactory(Map<String, ?> var1);

   public abstract JsonObjectBuilder createObjectBuilder();

   public JsonObjectBuilder createObjectBuilder(JsonObject object) {
      throw new UnsupportedOperationException();
   }

   public JsonObjectBuilder createObjectBuilder(Map<String, ?> map) {
      throw new UnsupportedOperationException();
   }

   public abstract JsonArrayBuilder createArrayBuilder();

   public JsonArrayBuilder createArrayBuilder(JsonArray array) {
      throw new UnsupportedOperationException();
   }

   public JsonPointer createPointer(String jsonPointer) {
      throw new UnsupportedOperationException();
   }

   public JsonPatchBuilder createPatchBuilder() {
      throw new UnsupportedOperationException();
   }

   public JsonPatchBuilder createPatchBuilder(JsonArray array) {
      throw new UnsupportedOperationException();
   }

   public JsonPatch createPatch(JsonArray array) {
      throw new UnsupportedOperationException();
   }

   public JsonPatch createDiff(JsonStructure source, JsonStructure target) {
      throw new UnsupportedOperationException();
   }

   public JsonMergePatch createMergePatch(JsonValue patch) {
      throw new UnsupportedOperationException();
   }

   public JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
      throw new UnsupportedOperationException();
   }

   public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
      throw new UnsupportedOperationException();
   }

   public abstract JsonBuilderFactory createBuilderFactory(Map<String, ?> var1);

   public JsonString createValue(String value) {
      throw new UnsupportedOperationException();
   }

   public JsonNumber createValue(int value) {
      throw new UnsupportedOperationException();
   }

   public JsonNumber createValue(long value) {
      throw new UnsupportedOperationException();
   }

   public JsonNumber createValue(double value) {
      throw new UnsupportedOperationException();
   }

   public JsonNumber createValue(BigDecimal value) {
      throw new UnsupportedOperationException();
   }

   public JsonNumber createValue(BigInteger value) {
      throw new UnsupportedOperationException();
   }

   public JsonNumber createValue(Number number) {
      if (number instanceof Integer) {
         return this.createValue(number.intValue());
      } else if (number instanceof Long) {
         return this.createValue(number.longValue());
      } else if (number instanceof Double) {
         return this.createValue(number.doubleValue());
      } else if (number instanceof BigInteger) {
         return this.createValue((BigInteger)number);
      } else if (number instanceof BigDecimal) {
         return this.createValue((BigDecimal)number);
      } else {
         throw new UnsupportedOperationException(number + " type is not known");
      }
   }

   private static boolean isOsgi() {
      try {
         Class.forName("org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   private static <T> T lookupUsingOSGiServiceLoader(Class<? extends T> serviceClass) {
      try {
         Class<?>[] args = new Class[]{serviceClass};
         Class<?> target = Class.forName("org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
         Method m = target.getMethod("lookupProviderInstances", Class.class);
         Iterator<? extends T> iter = ((Iterable)m.invoke(null, args)).iterator();
         return (T)(iter.hasNext() ? iter.next() : null);
      } catch (Exception var5) {
         LOG.log(Level.FINE, "Unable to find from OSGi: [" + serviceClass.getName() + "]", (Throwable)var5);
         return null;
      }
   }
}
