package org.apache.logging.log4j.core.config.plugins.processor;

import aQute.bnd.annotation.spi.ServiceProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;
import org.apache.logging.log4j.core.config.plugins.processor.internal.Annotations;
import org.apache.logging.log4j.core.config.plugins.processor.internal.ReachabilityMetadata;
import org.jspecify.annotations.Nullable;

@SupportedAnnotationTypes({"org.apache.logging.log4j.core.config.plugins.validation.Constraint", "org.apache.logging.log4j.core.config.plugins.Plugin", "org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute", "org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory", "org.apache.logging.log4j.core.config.plugins.PluginConfiguration", "org.apache.logging.log4j.core.config.plugins.PluginElement", "org.apache.logging.log4j.core.config.plugins.PluginFactory", "org.apache.logging.log4j.core.config.plugins.PluginLoggerContext", "org.apache.logging.log4j.core.config.plugins.PluginNode", "org.apache.logging.log4j.core.config.plugins.PluginValue", "org.apache.logging.log4j.core.config.plugins.PluginVisitorStrategy"})
@SupportedOptions({"log4j.graalvm.groupId", "log4j.graalvm.artifactId"})
@ServiceProvider(
   value = Processor.class,
   resolution = "optional"
)
public class GraalVmProcessor extends AbstractProcessor {
   static final String GROUP_ID = "log4j.graalvm.groupId";
   static final String ARTIFACT_ID = "log4j.graalvm.artifactId";
   private static final String LOCATION_PREFIX = "META-INF/native-image/log4j-generated/";
   private static final String LOCATION_SUFFIX = "/reflect-config.json";
   private static final String PROCESSOR_NAME = GraalVmProcessor.class.getSimpleName();
   private final Map<String, ReachabilityMetadata.Type> reachableTypes = new HashMap<>();
   private final List<Element> processedElements = new ArrayList<>();
   private Annotations annotationUtil;

   @Override
   public synchronized void init(ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      this.annotationUtil = new Annotations(processingEnv.getElementUtils());
   }

   @Override
   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest();
   }

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      Messager messager = this.processingEnv.getMessager();

      for (TypeElement annotation : annotations) {
         Annotations.Type annotationType = this.annotationUtil.classifyAnnotation(annotation);

         for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            switch (annotationType) {
               case PLUGIN:
                  this.processPlugin(element);
                  break;
               case CONSTRAINT_OR_VISITOR:
                  this.processConstraintOrVisitor(element, annotation);
                  break;
               case PARAMETER:
                  this.processParameter(element);
                  break;
               case FACTORY:
                  this.processFactory(element);
                  break;
               case UNKNOWN:
                  messager.printMessage(Kind.WARNING, String.format("The annotation type `%s` is not handled by %s", annotation, PROCESSOR_NAME), annotation);
            }

            this.processedElements.add(element);
         }
      }

      if (roundEnv.processingOver() && !this.reachableTypes.isEmpty()) {
         this.writeReachabilityMetadata();
      }

      return false;
   }

   private void processPlugin(Element element) {
      TypeElement typeElement = this.safeCast(element, TypeElement.class);

      for (Element child : typeElement.getEnclosedElements()) {
         if (child instanceof ExecutableElement) {
            ExecutableElement executableChild = (ExecutableElement)child;
            if (executableChild.getModifiers().contains(Modifier.PUBLIC)) {
               String var6 = executableChild.getSimpleName().toString();
               switch (var6) {
                  case "<init>":
                     this.addMethod(typeElement, executableChild);
                     break;
                  case "newInstance":
                     if (executableChild.getModifiers().contains(Modifier.STATIC)) {
                        this.addMethod(typeElement, executableChild);
                     }
               }
            }
         }
      }
   }

   private void processConstraintOrVisitor(Element element, TypeElement annotation) {
      this.processPlugin(this.annotationUtil.getAnnotationClassValue(element, annotation));
   }

   private void processParameter(Element element) {
      switch (element.getKind()) {
         case FIELD:
            this.addField(this.safeCast(element.getEnclosingElement(), TypeElement.class), this.safeCast(element, VariableElement.class));
         case PARAMETER:
            return;
         default:
            String msg = String.format("Invalid Log4j parameter element `%s`.", element);
            this.processingEnv.getMessager().printMessage(Kind.ERROR, msg, element);
            throw new IllegalStateException(msg);
      }
   }

   private void processFactory(Element element) {
      this.addMethod(this.safeCast(element.getEnclosingElement(), TypeElement.class), this.safeCast(element, ExecutableElement.class));
   }

   private void writeReachabilityMetadata() {
      ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

      try {
         ReachabilityMetadata.writeReflectConfig(this.reachableTypes.values(), arrayOutputStream);
      } catch (IOException var10) {
         String message = String.format("%s: an error occurred while generating reachability metadata: %s", PROCESSOR_NAME, var10.getMessage());
         this.processingEnv.getMessager().printMessage(Kind.ERROR, message);
         return;
      }

      byte[] data = arrayOutputStream.toByteArray();
      Map<String, String> options = this.processingEnv.getOptions();
      String reachabilityMetadataPath = this.getReachabilityMetadataPath(
         options.get("log4j.graalvm.groupId"), options.get("log4j.graalvm.artifactId"), Integer.toHexString(Arrays.hashCode(data))
      );
      Messager messager = this.processingEnv.getMessager();
      messager.printMessage(
         Kind.NOTE,
         String.format("%s: writing GraalVM metadata for %d Java classes to `%s`.", PROCESSOR_NAME, this.reachableTypes.size(), reachabilityMetadataPath)
      );

      try {
         OutputStream output = this.processingEnv
            .getFiler()
            .createResource(StandardLocation.CLASS_OUTPUT, "", reachabilityMetadataPath, this.processedElements.toArray(new Element[0]))
            .openOutputStream();

         try {
            output.write(data);
         } catch (Throwable var11) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable var9) {
                  var11.addSuppressed(var9);
               }
            }

            throw var11;
         }

         if (output != null) {
            output.close();
         }
      } catch (IOException var12) {
         String message = String.format("%s: unable to write reachability metadata to file `%s`", PROCESSOR_NAME, reachabilityMetadataPath);
         messager.printMessage(Kind.ERROR, message);
         throw new IllegalArgumentException(message, var12);
      }
   }

   String getReachabilityMetadataPath(@Nullable String groupId, @Nullable String artifactId, String fallbackFolderName) {
      if (groupId != null && artifactId != null) {
         return "META-INF/native-image/log4j-generated/" + groupId + '/' + artifactId + "/reflect-config.json";
      } else {
         String message = String.format(
            "The `%1$s` annotation processor is missing the recommended `%2$s` and `%3$s` options.%nTo follow the GraalVM recommendations, please add the following options to your build tool:%n  -A%2$s=<groupId>%n  -A%3$s=<artifactId>%n",
            PROCESSOR_NAME,
            "log4j.graalvm.groupId",
            "log4j.graalvm.artifactId"
         );
         this.processingEnv.getMessager().printMessage(Kind.WARNING, message);
         return "META-INF/native-image/log4j-generated/" + fallbackFolderName + "/reflect-config.json";
      }
   }

   private void addField(TypeElement parent, VariableElement element) {
      ReachabilityMetadata.Type reachableType = this.reachableTypes.computeIfAbsent(this.toString(parent), ReachabilityMetadata.Type::new);
      reachableType.addField(new ReachabilityMetadata.Field(element.getSimpleName().toString()));
   }

   private void addMethod(TypeElement parent, ExecutableElement element) {
      ReachabilityMetadata.Type reachableType = this.reachableTypes.computeIfAbsent(this.toString(parent), ReachabilityMetadata.Type::new);
      ReachabilityMetadata.Method method = new ReachabilityMetadata.Method(element.getSimpleName().toString());
      element.getParameters().stream().map(v -> this.toString(v.asType())).forEach(method::addParameterType);
      reachableType.addMethod(method);
   }

   private <T extends Element> T safeCast(Element element, Class<T> type) {
      if (type.isInstance(element)) {
         return type.cast(element);
      } else {
         String msg = String.format("Unexpected type of element `%s`: expecting `%s` but found `%s`", element, type.getName(), element.getClass().getName());
         this.processingEnv.getMessager().printMessage(Kind.ERROR, msg, element);
         throw new IllegalStateException(msg);
      }
   }

   private String toString(TypeMirror type) {
      return type.accept(new SimpleTypeVisitor8<String, Void>() {
         protected String defaultAction(final TypeMirror e, @Nullable Void unused) {
            return e.toString();
         }

         public String visitArray(final ArrayType t, @Nullable Void unused) {
            return this.visit(t.getComponentType(), unused) + "[]";
         }

         @Nullable
         public String visitDeclared(final DeclaredType t, final Void unused) {
            return GraalVmProcessor.this.toString(GraalVmProcessor.this.safeCast(t.asElement(), TypeElement.class));
         }
      }, null);
   }

   private String toString(TypeElement element) {
      return this.processingEnv.getElementUtils().getBinaryName(element).toString();
   }
}
