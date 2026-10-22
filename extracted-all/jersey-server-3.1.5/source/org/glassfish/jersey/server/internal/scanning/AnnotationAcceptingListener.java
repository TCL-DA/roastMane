package org.glassfish.jersey.server.internal.scanning;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.ext.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import jersey.repackaged.org.objectweb.asm.AnnotationVisitor;
import jersey.repackaged.org.objectweb.asm.Attribute;
import jersey.repackaged.org.objectweb.asm.ClassReader;
import jersey.repackaged.org.objectweb.asm.ClassVisitor;
import jersey.repackaged.org.objectweb.asm.FieldVisitor;
import jersey.repackaged.org.objectweb.asm.MethodVisitor;
import jersey.repackaged.org.objectweb.asm.ModuleVisitor;
import jersey.repackaged.org.objectweb.asm.RecordComponentVisitor;
import jersey.repackaged.org.objectweb.asm.TypePath;
import org.glassfish.jersey.internal.OsgiRegistry;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.internal.LocalizationMessages;

public final class AnnotationAcceptingListener implements ResourceProcessor {
   private final ClassLoader classloader;
   private final Set<Class<?>> classes;
   private final Set<String> annotations;
   private final AnnotationAcceptingListener.AnnotatedClassVisitor classVisitor;

   public static AnnotationAcceptingListener newJaxrsResourceAndProviderListener() {
      return new AnnotationAcceptingListener(Path.class, Provider.class);
   }

   public static AnnotationAcceptingListener newJaxrsResourceAndProviderListener(ClassLoader classLoader) {
      return new AnnotationAcceptingListener(classLoader, Path.class, Provider.class);
   }

   public AnnotationAcceptingListener(Class<? extends Annotation>... annotations) {
      this(AccessController.doPrivileged(ReflectionHelper.getContextClassLoaderPA()), annotations);
   }

   public AnnotationAcceptingListener(ClassLoader classloader, Class<? extends Annotation>... annotations) {
      this.classloader = classloader;
      this.classes = new LinkedHashSet<>();
      this.annotations = this.getAnnotationSet(annotations);
      this.classVisitor = new AnnotationAcceptingListener.AnnotatedClassVisitor();
   }

   public Set<Class<?>> getAnnotatedClasses() {
      return this.classes;
   }

   private Set<String> getAnnotationSet(Class<? extends Annotation>... annotations) {
      Set<String> a = new HashSet<>();

      for (Class c : annotations) {
         a.add("L" + c.getName().replaceAll("\\.", "/") + ";");
      }

      return a;
   }

   @Override
   public boolean accept(String name) {
      return name != null && !name.isEmpty() && name.endsWith(".class");
   }

   @Override
   public void process(String name, InputStream in) throws IOException {
      new AnnotationAcceptingListener.ClassReaderWrapper(in).accept(this.classVisitor, 0);
   }

   private final class AnnotatedClassVisitor extends ClassVisitor {
      private String className;
      private boolean isScoped;
      private boolean isAnnotated;

      private AnnotatedClassVisitor() {
         super(589824);
      }

      @Override
      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.className = name;
         this.isScoped = (access & 1) != 0;
         this.isAnnotated = false;
      }

      @Override
      public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
         this.isAnnotated = this.isAnnotated | AnnotationAcceptingListener.this.annotations.contains(desc);
         return null;
      }

      @Override
      public void visitInnerClass(String name, String outerName, String innerName, int access) {
         if (this.className.equals(name)) {
            this.isScoped = (access & 1) != 0;
            this.isScoped &= (access & 8) == 8;
         }
      }

      @Override
      public void visitEnd() {
         if (this.isScoped && this.isAnnotated) {
            AnnotationAcceptingListener.this.classes.add(this.getClassForName(this.className.replaceAll("/", ".")));
         }
      }

      @Override
      public void visitOuterClass(String string, String string0, String string1) {
      }

      @Override
      public FieldVisitor visitField(int i, String string, String string0, String string1, Object object) {
         return null;
      }

      @Override
      public void visitSource(String string, String string0) {
      }

      @Override
      public void visitAttribute(Attribute attribute) {
      }

      @Override
      public MethodVisitor visitMethod(int i, String string, String string0, String string1, String[] string2) {
         return null;
      }

      @Override
      public ModuleVisitor visitModule(String name, int access, String version) {
         return null;
      }

      @Override
      public void visitNestHost(String nestHost) {
      }

      @Override
      public void visitNestMember(String nestMember) {
      }

      @Override
      public void visitPermittedSubclass(String permittedSubclass) {
      }

      @Override
      public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
         return null;
      }

      @Override
      public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
         return null;
      }

      @Override
      public ClassVisitor getDelegate() {
         return null;
      }

      private Class getClassForName(String className) {
         try {
            OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
            return osgiRegistry != null
               ? osgiRegistry.classForNameWithException(className)
               : AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(className, AnnotationAcceptingListener.this.classloader));
         } catch (ClassNotFoundException var4) {
            throw new RuntimeException(LocalizationMessages.ERROR_SCANNING_CLASS_NOT_FOUND(className), var4);
         } catch (PrivilegedActionException var5) {
            Throwable cause = var5.getCause();
            if (cause instanceof ClassNotFoundException) {
               throw new RuntimeException(LocalizationMessages.ERROR_SCANNING_CLASS_NOT_FOUND(className), cause);
            } else if (cause instanceof RuntimeException) {
               throw (RuntimeException)cause;
            } else {
               throw new RuntimeException(cause);
            }
         }
      }
   }

   private static class ClassReaderWrapper {
      private static final Logger LOGGER = Logger.getLogger(ClassReader.class.getName());
      private static final int WARN_VERSION = 66;
      private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;
      private final byte[] b;

      private ClassReaderWrapper(InputStream inputStream) throws IOException {
         this.b = readStream(inputStream);
      }

      private void accept(ClassVisitor classVisitor, int parsingOptions) {
         int originalVersion = getMajorVersion(this.b);
         if (originalVersion > 66) {
            setMajorVersion(66, this.b);
            LOGGER.warning("Unsupported class file major version " + originalVersion);
         }

         ClassReader classReader = new ClassReader(this.b);
         setMajorVersion(originalVersion, this.b);
         classReader.accept(classVisitor, parsingOptions);
      }

      private static void setMajorVersion(int majorVersion, byte[] b) {
         b[6] = (byte)(majorVersion >>> 8);
         b[7] = (byte)majorVersion;
      }

      private static int getMajorVersion(byte[] b) {
         return (b[6] & 0xFF) << 8 | b[7] & 0xFF;
      }

      private static byte[] readStream(InputStream inputStream) throws IOException {
         if (inputStream == null) {
            throw new IOException("Class not found");
         } else {
            byte[] var4;
            try {
               ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

               try {
                  byte[] data = new byte[4096];

                  int bytesRead;
                  while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                     outputStream.write(data, 0, bytesRead);
                  }

                  outputStream.flush();
                  var4 = outputStream.toByteArray();
               } catch (Throwable var10) {
                  try {
                     outputStream.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }

                  throw var10;
               }

               outputStream.close();
            } finally {
               inputStream.close();
            }

            return var4;
         }
      }
   }
}
