package org.glassfish.jersey.server.wadl.internal.generators;

import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.SchemaOutputResolver;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import org.glassfish.jersey.server.wadl.internal.WadlGeneratorImpl;

public class WadlGeneratorJAXBGrammarGenerator implements WadlGenerator {
   private static final Logger LOGGER = Logger.getLogger(WadlGeneratorJAXBGrammarGenerator.class.getName());
   private static final Set<Class> SPECIAL_GENERIC_TYPES = new HashSet<Class>() {
      {
         this.add(List.class);
      }
   };
   private WadlGenerator wadlGeneratorDelegate = new WadlGeneratorImpl();
   private Set<Class> seeAlsoClasses;
   private List<WadlGeneratorJAXBGrammarGenerator.TypeCallbackPair> nameCallbacks;

   @Override
   public void setWadlGeneratorDelegate(WadlGenerator delegate) {
      this.wadlGeneratorDelegate = delegate;
   }

   @Override
   public String getRequiredJaxbContextPath() {
      return this.wadlGeneratorDelegate.getRequiredJaxbContextPath();
   }

   @Override
   public void init() throws Exception {
      this.wadlGeneratorDelegate.init();
      this.seeAlsoClasses = new HashSet<>();
      this.nameCallbacks = new ArrayList<>();
   }

   @Override
   public Application createApplication() {
      return this.wadlGeneratorDelegate.createApplication();
   }

   @Override
   public Method createMethod(Resource ar, ResourceMethod arm) {
      return this.wadlGeneratorDelegate.createMethod(ar, arm);
   }

   @Override
   public Request createRequest(Resource ar, ResourceMethod arm) {
      return this.wadlGeneratorDelegate.createRequest(ar, arm);
   }

   @Override
   public Param createParam(Resource ar, ResourceMethod am, Parameter p) {
      final Param param = this.wadlGeneratorDelegate.createParam(ar, am, p);
      if (p.getSource() == Source.ENTITY) {
         this.nameCallbacks
            .add(
               new WadlGeneratorJAXBGrammarGenerator.TypeCallbackPair(
                  new GenericType(p.getType()), new WadlGeneratorJAXBGrammarGenerator.NameCallbackSetter() {
                     @Override
                     public void setName(QName name) {
                        param.setType(name);
                     }
                  }
               )
            );
      }

      return param;
   }

   @Override
   public Representation createRequestRepresentation(Resource ar, ResourceMethod arm, MediaType mt) {
      final Representation rt = this.wadlGeneratorDelegate.createRequestRepresentation(ar, arm, mt);

      for (Parameter p : arm.getInvocable().getParameters()) {
         if (p.getSource() == Source.ENTITY) {
            this.nameCallbacks
               .add(
                  new WadlGeneratorJAXBGrammarGenerator.TypeCallbackPair(
                     new GenericType(p.getType()), new WadlGeneratorJAXBGrammarGenerator.NameCallbackSetter() {
                        @Override
                        public void setName(QName name) {
                           rt.setElement(name);
                        }
                     }
                  )
               );
         }
      }

      return rt;
   }

   @Override
   public com.sun.research.ws.wadl.Resource createResource(Resource ar, String path) {
      for (Class<?> resClass : ar.getHandlerClasses()) {
         XmlSeeAlso seeAlso = resClass.getAnnotation(XmlSeeAlso.class);
         if (seeAlso != null) {
            Collections.addAll(this.seeAlsoClasses, seeAlso.value());
         }
      }

      return this.wadlGeneratorDelegate.createResource(ar, path);
   }

   @Override
   public Resources createResources() {
      return this.wadlGeneratorDelegate.createResources();
   }

   @Override
   public List<Response> createResponses(Resource resource, ResourceMethod resourceMethod) {
      List<Response> responses = this.wadlGeneratorDelegate.createResponses(resource, resourceMethod);
      if (responses != null) {
         for (Response response : responses) {
            for (final Representation representation : response.getRepresentation()) {
               this.nameCallbacks
                  .add(
                     new WadlGeneratorJAXBGrammarGenerator.TypeCallbackPair(
                        new GenericType(resourceMethod.getInvocable().getResponseType()), new WadlGeneratorJAXBGrammarGenerator.NameCallbackSetter() {
                           @Override
                           public void setName(QName name) {
                              representation.setElement(name);
                           }
                        }
                     )
                  );
            }
         }
      }

      return responses;
   }

   @Override
   public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
      Map<String, ApplicationDescription.ExternalGrammar> extraFiles = new HashMap<>();
      WadlGenerator.Resolver resolver = this.buildModelAndSchemas(extraFiles);
      WadlGenerator.ExternalGrammarDefinition previous = this.wadlGeneratorDelegate.createExternalGrammar();
      previous.map.putAll(extraFiles);
      if (resolver != null) {
         previous.addResolver(resolver);
      }

      return previous;
   }

   private WadlGenerator.Resolver buildModelAndSchemas(Map<String, ApplicationDescription.ExternalGrammar> extraFiles) {
      Set<Class> classSet = new HashSet<>(this.seeAlsoClasses);

      for (WadlGeneratorJAXBGrammarGenerator.TypeCallbackPair pair : this.nameCallbacks) {
         GenericType genericType = pair.genericType;
         Class<?> clazz = genericType.getRawType();
         if (clazz.getAnnotation(XmlRootElement.class) != null) {
            classSet.add(clazz);
         } else if (SPECIAL_GENERIC_TYPES.contains(clazz)) {
            Type type = genericType.getType();
            if (type instanceof ParameterizedType) {
               Type parameterType = ((ParameterizedType)type).getActualTypeArguments()[0];
               if (parameterType instanceof Class) {
                  classSet.add((Class)parameterType);
               }
            }
         }
      }

      JAXBIntrospector introspector = null;

      try {
         JAXBContext context = JAXBContext.newInstance(classSet.toArray(new Class[classSet.size()]));
         final List<StreamResult> results = new ArrayList<>();
         context.generateSchema(new SchemaOutputResolver() {
            int counter = 0;

            public Result createOutput(String namespaceUri, String suggestedFileName) {
               StreamResult result = new StreamResult(new CharArrayWriter());
               result.setSystemId("xsd" + this.counter++ + ".xsd");
               results.add(result);
               return result;
            }
         });

         for (StreamResult result : results) {
            CharArrayWriter writer = (CharArrayWriter)result.getWriter();
            byte[] contents = writer.toString().getBytes("UTF8");
            extraFiles.put(result.getSystemId(), new ApplicationDescription.ExternalGrammar(MediaType.APPLICATION_XML_TYPE, contents));
         }

         introspector = context.createJAXBIntrospector();
      } catch (JAXBException var10) {
         LOGGER.log(Level.SEVERE, "Failed to generate the schema for the JAX-B elements", (Throwable)var10);
      } catch (IOException var11) {
         LOGGER.log(Level.SEVERE, "Failed to generate the schema for the JAX-B elements due to an IO error", (Throwable)var11);
      }

      if (introspector != null) {
         final JAXBIntrospector copy = introspector;
         return new WadlGenerator.Resolver() {
            @Override
            public QName resolve(final Class type) {
               Object parameterClassInstance = null;

               try {
                  Constructor<?> defaultConstructor = AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor<?>>() {
                     public Constructor<?> run() throws NoSuchMethodException {
                        Constructor<?> constructor = type.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        return constructor;
                     }
                  });
                  parameterClassInstance = defaultConstructor.newInstance();
               } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException var5) {
                  WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, var5);
               } catch (PrivilegedActionException var6) {
                  WadlGeneratorJAXBGrammarGenerator.LOGGER.log(Level.FINE, null, var6.getCause());
               }

               if (parameterClassInstance == null) {
                  return null;
               } else {
                  try {
                     return copy.getElementName(parameterClassInstance);
                  } catch (NullPointerException var4) {
                     return null;
                  }
               }
            }
         };
      } else {
         return null;
      }
   }

   @Override
   public void attachTypes(ApplicationDescription introspector) {
      if (introspector != null) {
         for (WadlGeneratorJAXBGrammarGenerator.TypeCallbackPair pair : this.nameCallbacks) {
            Class<?> parameterClass = pair.genericType.getRawType();
            if (SPECIAL_GENERIC_TYPES.contains(parameterClass)) {
               Type type = pair.genericType.getType();
               if (!ParameterizedType.class.isAssignableFrom(type.getClass())
                  || !Class.class.isAssignableFrom(((ParameterizedType)type).getActualTypeArguments()[0].getClass())) {
                  LOGGER.fine("Couldn't find JAX-B element due to nested parameterized type " + type);
                  return;
               }

               parameterClass = (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
            }

            QName name = introspector.resolve(parameterClass);
            if (name != null) {
               pair.nameCallbackSetter.setName(name);
            } else {
               LOGGER.fine("Couldn't find JAX-B element for class " + parameterClass.getName());
            }
         }
      }
   }

   private interface NameCallbackSetter {
      void setName(QName var1);
   }

   private class TypeCallbackPair {
      GenericType<?> genericType;
      WadlGeneratorJAXBGrammarGenerator.NameCallbackSetter nameCallbackSetter;

      public TypeCallbackPair(GenericType<?> genericType, WadlGeneratorJAXBGrammarGenerator.NameCallbackSetter nameCallbackSetter) {
         this.genericType = genericType;
         this.nameCallbackSetter = nameCallbackSetter;
      }
   }
}
