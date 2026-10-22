package javafx.fxml;

import com.sun.javafx.fxml.BeanAdapter;
import com.sun.javafx.fxml.builder.JavaFXFontBuilder;
import com.sun.javafx.fxml.builder.JavaFXImageBuilder;
import com.sun.javafx.fxml.builder.JavaFXSceneBuilder;
import com.sun.javafx.fxml.builder.ProxyBuilder;
import com.sun.javafx.fxml.builder.TriangleMeshBuilder;
import com.sun.javafx.fxml.builder.URLBuilder;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.reflect.ConstructorUtil;
import com.sun.javafx.reflect.MethodUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public final class JavaFXBuilderFactory implements BuilderFactory {
   private final ClassLoader classLoader;
   private final boolean webSupported;
   private static final String WEBVIEW_NAME = "javafx.scene.web.WebView";
   private static final String WEBVIEW_BUILDER_NAME = "com.sun.javafx.fxml.builder.web.WebViewBuilder";

   public JavaFXBuilderFactory() {
      this(FXMLLoader.getDefaultClassLoader());
   }

   public JavaFXBuilderFactory(ClassLoader var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.classLoader = var1;
         this.webSupported = Platform.isSupported(ConditionalFeature.WEB);
      }
   }

   public Builder<?> getBuilder(Class<?> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object var2;
         if (var1 == Scene.class) {
            var2 = new JavaFXSceneBuilder();
         } else if (var1 == Font.class) {
            var2 = new JavaFXFontBuilder();
         } else if (var1 == Image.class) {
            var2 = new JavaFXImageBuilder();
         } else if (var1 == URL.class) {
            var2 = new URLBuilder(this.classLoader);
         } else if (var1 == TriangleMesh.class) {
            var2 = new TriangleMeshBuilder();
         } else if (this.webSupported && var1.getName().equals("javafx.scene.web.WebView")) {
            try {
               Class var3 = this.classLoader.loadClass("com.sun.javafx.fxml.builder.web.WebViewBuilder");
               JavaFXBuilderFactory.ObjectBuilderWrapper var4 = new JavaFXBuilderFactory.ObjectBuilderWrapper(var3);
               var2 = var4.createBuilder();
            } catch (Exception var5) {
               var2 = null;
            }
         } else if (this.scanForConstructorAnnotations(var1)) {
            var2 = new ProxyBuilder(var1);
         } else {
            var2 = null;
         }

         return (Builder<?>)var2;
      }
   }

   private boolean scanForConstructorAnnotations(Class<?> var1) {
      Constructor[] var2 = ConstructorUtil.getConstructors(var1);

      for (Constructor var6 : var2) {
         Annotation[][] var7 = var6.getParameterAnnotations();

         for (int var8 = 0; var8 < var6.getParameterTypes().length; var8++) {
            for (Annotation var12 : var7[var8]) {
               if (var12 instanceof NamedArg) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static final class ObjectBuilderWrapper {
      private static final Object[] NO_ARGS = new Object[0];
      private static final Class<?>[] NO_SIG = new Class[0];
      private final Class<?> builderClass;
      private final Method createMethod;
      private final Method buildMethod;
      private final Map<String, Method> methods = new HashMap<>();
      private final Map<String, Method> getters = new HashMap<>();
      private final Map<String, Method> setters = new HashMap<>();

      ObjectBuilderWrapper() {
         this.builderClass = null;
         this.createMethod = null;
         this.buildMethod = null;
      }

      ObjectBuilderWrapper(Class<?> var1) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
         this.builderClass = var1;
         this.createMethod = MethodUtil.getMethod(var1, "create", NO_SIG);
         this.buildMethod = MethodUtil.getMethod(var1, "build", NO_SIG);

         assert Modifier.isStatic(this.createMethod.getModifiers());

         assert !Modifier.isStatic(this.buildMethod.getModifiers());
      }

      Builder<Object> createBuilder() {
         return new JavaFXBuilderFactory.ObjectBuilderWrapper.ObjectBuilder();
      }

      private Method findMethod(String var1) {
         if (var1.length() > 1 && Character.isUpperCase(var1.charAt(1))) {
            var1 = Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
         }

         for (Method var5 : MethodUtil.getMethods(this.builderClass)) {
            if (var5.getName().equals(var1)) {
               return var5;
            }
         }

         throw new IllegalArgumentException("Method " + var1 + " could not be found at class " + this.builderClass.getName());
      }

      public Class<?> getTargetClass() {
         return this.buildMethod.getReturnType();
      }

      final class ObjectBuilder extends AbstractMap<String, Object> implements Builder<Object> {
         private final Map<String, Object> containers = new HashMap<>();
         private Object builder = null;
         private Map<Object, Object> properties;

         private ObjectBuilder() {
            try {
               this.builder = ObjectBuilderWrapper.this.createMethod.invoke(null, JavaFXBuilderFactory.ObjectBuilderWrapper.NO_ARGS);
            } catch (Exception var3) {
               throw new RuntimeException("Creation of the builder " + ObjectBuilderWrapper.this.builderClass.getName() + " failed.", var3);
            }
         }

         public Object build() {
            for (Entry var2 : this.containers.entrySet()) {
               this.put((String)var2.getKey(), var2.getValue());
            }

            Object var10;
            try {
               var10 = ObjectBuilderWrapper.this.buildMethod.invoke(this.builder, JavaFXBuilderFactory.ObjectBuilderWrapper.NO_ARGS);
               if (this.properties != null && var10 instanceof Node) {
                  ((Node)var10).getProperties().putAll(this.properties);
               }
            } catch (InvocationTargetException var7) {
               throw new RuntimeException(var7);
            } catch (IllegalAccessException var8) {
               throw new RuntimeException(var8);
            } finally {
               this.builder = null;
            }

            return var10;
         }

         @Override
         public int size() {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean isEmpty() {
            throw new UnsupportedOperationException();
         }

         @Override
         public boolean containsKey(Object var1) {
            return this.getTemporaryContainer(var1.toString()) != null;
         }

         @Override
         public boolean containsValue(Object var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public Object get(Object var1) {
            return this.getTemporaryContainer(var1.toString());
         }

         public Object put(String var1, Object var2) {
            if (Node.class.isAssignableFrom(ObjectBuilderWrapper.this.getTargetClass()) && "properties".equals(var1)) {
               this.properties = (Map<Object, Object>)var2;
               return null;
            } else {
               try {
                  Method var3 = ObjectBuilderWrapper.this.methods.get(var1);
                  if (var3 == null) {
                     var3 = ObjectBuilderWrapper.this.findMethod(var1);
                     ObjectBuilderWrapper.this.methods.put(var1, var3);
                  }

                  try {
                     Class var11 = var3.getParameterTypes()[0];
                     if (var11.isArray()) {
                        List var12;
                        if (var2 instanceof List) {
                           var12 = (List)var2;
                        } else {
                           var12 = Arrays.asList(var2.toString().split(","));
                        }

                        Class var6 = var11.getComponentType();
                        Object var7 = Array.newInstance(var6, var12.size());

                        for (int var8 = 0; var8 < var12.size(); var8++) {
                           Array.set(var7, var8, BeanAdapter.coerce(var12.get(var8), var6));
                        }

                        var2 = var7;
                     }

                     var3.invoke(this.builder, BeanAdapter.coerce(var2, var11));
                  } catch (Exception var9) {
                     String var5 = "Method " + var3.getName() + " failed";
                     PlatformLogger.getLogger(JavaFXBuilderFactory.ObjectBuilderWrapper.class.getName()).warning(var5, var9);
                  }

                  return null;
               } catch (Exception var10) {
                  String var4 = "Failed to set " + ObjectBuilderWrapper.this.getTargetClass() + "." + var1 + " using " + ObjectBuilderWrapper.this.builderClass;
                  PlatformLogger.getLogger(JavaFXBuilderFactory.ObjectBuilderWrapper.class.getName()).warning(var4, var10);
                  return null;
               }
            }
         }

         Object getReadOnlyProperty(String var1) {
            if (ObjectBuilderWrapper.this.setters.get(var1) != null) {
               return null;
            } else {
               Method var2 = ObjectBuilderWrapper.this.getters.get(var1);
               if (var2 == null) {
                  Method var3 = null;
                  Class var4 = ObjectBuilderWrapper.this.getTargetClass();
                  String var5 = Character.toUpperCase(var1.charAt(0)) + var1.substring(1);

                  try {
                     var2 = MethodUtil.getMethod(var4, "get" + var5, JavaFXBuilderFactory.ObjectBuilderWrapper.NO_SIG);
                     var3 = MethodUtil.getMethod(var4, "set" + var5, new Class[]{var2.getReturnType()});
                  } catch (Exception var7) {
                  }

                  if (var2 != null) {
                     ObjectBuilderWrapper.this.getters.put(var1, var2);
                     ObjectBuilderWrapper.this.setters.put(var1, var3);
                  }

                  if (var3 != null) {
                     return null;
                  }
               }

               Class var8;
               if (var2 == null) {
                  Method var9 = ObjectBuilderWrapper.this.findMethod(var1);
                  if (var9 == null) {
                     return null;
                  }

                  var8 = var9.getParameterTypes()[0];
                  if (var8.isArray()) {
                     var8 = List.class;
                  }
               } else {
                  var8 = var2.getReturnType();
               }

               if (ObservableMap.class.isAssignableFrom(var8)) {
                  return FXCollections.observableMap(new HashMap());
               } else if (Map.class.isAssignableFrom(var8)) {
                  return new HashMap();
               } else if (ObservableList.class.isAssignableFrom(var8)) {
                  return FXCollections.observableArrayList();
               } else if (List.class.isAssignableFrom(var8)) {
                  return new ArrayList();
               } else {
                  return Set.class.isAssignableFrom(var8) ? new HashSet() : null;
               }
            }
         }

         public Object getTemporaryContainer(String var1) {
            Object var2 = this.containers.get(var1);
            if (var2 == null) {
               var2 = this.getReadOnlyProperty(var1);
               if (var2 != null) {
                  this.containers.put(var1, var2);
               }
            }

            return var2;
         }

         @Override
         public Object remove(Object var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void putAll(Map<? extends String, ? extends Object> var1) {
            throw new UnsupportedOperationException();
         }

         @Override
         public void clear() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Set<String> keySet() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Collection<Object> values() {
            throw new UnsupportedOperationException();
         }

         @Override
         public Set<Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException();
         }
      }
   }
}
