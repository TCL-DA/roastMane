package org.apache.logging.log4j.core.config.plugins.processor;

import aQute.bnd.annotation.spi.ServiceProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor7;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.util.Strings;

@SupportedAnnotationTypes({"org.apache.logging.log4j.core.config.plugins.Plugin"})
@SupportedOptions({"log4j.plugin.processor.minAllowedMessageKind"})
@ServiceProvider(
   value = Processor.class,
   resolution = "optional"
)
public class PluginProcessor extends AbstractProcessor {
   private static final Element[] EMPTY_ELEMENT_ARRAY = new Element[0];
   private static final String SUPPRESS_WARNING_PUBLIC_SETTER_STRING = "log4j.public.setter";
   static final String MIN_ALLOWED_MESSAGE_KIND_OPTION = "log4j.plugin.processor.minAllowedMessageKind";
   public static final String PLUGIN_CACHE_FILE = "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat";
   private final List<Element> processedElements = new ArrayList<>();
   private final PluginCache pluginCache = new PluginCache();
   private Kind minAllowedMessageKind = Kind.NOTE;
   private static final String PLUGIN_BUILDER_ATTRIBUTE_ANNOTATION = "org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute";

   @Override
   public void init(final ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      String kindValue = processingEnv.getOptions().get("log4j.plugin.processor.minAllowedMessageKind");
      if (kindValue != null) {
         try {
            this.minAllowedMessageKind = Kind.valueOf(kindValue.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var4) {
            processingEnv.getMessager()
               .printMessage(
                  Kind.WARNING,
                  String.format(
                     "%s: unrecognized value `%s` for option `%s`, using default `%s`. Valid values: %s",
                     PluginProcessor.class.getName(),
                     kindValue,
                     "log4j.plugin.processor.minAllowedMessageKind",
                     Kind.NOTE,
                     Arrays.toString((Object[])Kind.values())
                  )
               );
         }
      }
   }

   @Override
   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest();
   }

   private void printMessage(final Kind kind, final String message) {
      if (kind.ordinal() <= this.minAllowedMessageKind.ordinal()) {
         this.processingEnv.getMessager().printMessage(kind, message);
      }
   }

   private void printMessage(final Kind kind, final String message, final Element element) {
      if (kind.ordinal() <= this.minAllowedMessageKind.ordinal()) {
         this.processingEnv.getMessager().printMessage(kind, message, element);
      }
   }

   @Override
   public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
      Elements elementUtils = this.processingEnv.getElementUtils();
      if (!annotations.isEmpty()) {
         Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Plugin.class);
         this.collectPlugins(elements);
         this.processedElements.addAll(elements);
         Set<? extends Element> pluginAttributeBuilderElements = roundEnv.getElementsAnnotatedWith(
            elementUtils.getTypeElement("org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute")
         );
         this.processBuilderAttribute(pluginAttributeBuilderElements);
         this.processedElements.addAll(pluginAttributeBuilderElements);
      }

      if (roundEnv.processingOver() && !this.processedElements.isEmpty()) {
         try {
            this.printMessage(
               Kind.NOTE,
               String.format(
                  "%s: writing plugin descriptor for %d Log4j Plugins to `%s`.",
                  PluginProcessor.class.getSimpleName(),
                  this.processedElements.size(),
                  "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat"
               )
            );
            this.writeCacheFile(this.processedElements.toArray(EMPTY_ELEMENT_ARRAY));
         } catch (Exception var6) {
            StringWriter sw = new StringWriter();
            sw.append(PluginProcessor.class.getSimpleName())
               .append(": unable to write plugin descriptor to file ")
               .append("META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat")
               .append("\n");
            var6.printStackTrace(new PrintWriter(sw));
            this.printMessage(Kind.ERROR, sw.toString());
         }
      }

      return false;
   }

   private void processBuilderAttribute(final Iterable<? extends Element> elements) {
      for (Element element : elements) {
         if (element instanceof VariableElement) {
            this.processBuilderAttribute((VariableElement)element);
         }
      }
   }

   private void processBuilderAttribute(final VariableElement element) {
      String fieldName = element.getSimpleName().toString();
      SuppressWarnings suppress = element.getAnnotation(SuppressWarnings.class);
      if (suppress == null || !Arrays.asList(suppress.value()).contains("log4j.public.setter")) {
         Element enclosingElement = element.getEnclosingElement();
         if (enclosingElement instanceof TypeElement) {
            TypeElement typeElement = (TypeElement)enclosingElement;

            for (Element enclosedElement : typeElement.getEnclosedElements()) {
               if (enclosedElement instanceof ExecutableElement) {
                  ExecutableElement methodElement = (ExecutableElement)enclosedElement;
                  String methodName = methodElement.getSimpleName().toString();
                  if (methodName.toLowerCase(Locale.ROOT).startsWith("set") && methodElement.getParameters().size() == 1) {
                     Types typeUtils = this.processingEnv.getTypeUtils();
                     boolean followsNamePattern = methodName.equals(String.format("set%s", expectedFieldNameInASetter(fieldName)))
                        || methodName.equals(String.format("with%s", expectedFieldNameInASetter(fieldName)));
                     boolean isPublicMethod = methodElement.getModifiers().contains(Modifier.PUBLIC);
                     boolean checkForAssignable = typeUtils.isAssignable(methodElement.getReturnType(), methodElement.getEnclosingElement().asType());
                     boolean foundPublicSetter = followsNamePattern && checkForAssignable && isPublicMethod;
                     if (foundPublicSetter) {
                        return;
                     }
                  }
               }
            }

            this.printMessage(
               Kind.ERROR,
               String.format(
                  "The field `%s` does not have a public setter, Note that @SuppressWarnings(\"%s\"), can be used on the field to suppress the compilation error. ",
                  fieldName,
                  "log4j.public.setter"
               ),
               element
            );
         }
      }
   }

   private static String expectedFieldNameInASetter(String fieldName) {
      if (fieldName.startsWith("is")) {
         fieldName = fieldName.substring(2);
      }

      return fieldName.isEmpty() ? fieldName : Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
   }

   private void collectPlugins(final Iterable<? extends Element> elements) {
      Elements elementUtils = this.processingEnv.getElementUtils();
      ElementVisitor<PluginEntry, Plugin> pluginVisitor = new PluginProcessor.PluginElementVisitor(elementUtils);
      ElementVisitor<Collection<PluginEntry>, Plugin> pluginAliasesVisitor = new PluginProcessor.PluginAliasesElementVisitor(elementUtils);

      for (Element element : elements) {
         Plugin plugin = element.getAnnotation(Plugin.class);
         if (plugin != null) {
            PluginEntry entry = element.accept(pluginVisitor, plugin);
            Map<String, PluginEntry> category = this.pluginCache.getCategory(entry.getCategory());
            category.put(entry.getKey(), entry);

            for (PluginEntry pluginEntry : element.accept(pluginAliasesVisitor, plugin)) {
               category.put(pluginEntry.getKey(), pluginEntry);
            }
         }
      }
   }

   private void writeCacheFile(final Element... elements) throws IOException {
      FileObject fileObject = this.processingEnv
         .getFiler()
         .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat", elements);
      OutputStream out = fileObject.openOutputStream();

      try {
         this.pluginCache.writeCache(out);
      } catch (Throwable var7) {
         if (out != null) {
            try {
               out.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (out != null) {
         out.close();
      }
   }

   private static final class PluginAliasesElementVisitor extends SimpleElementVisitor7<Collection<PluginEntry>, Plugin> {
      private final Elements elements;

      private PluginAliasesElementVisitor(final Elements elements) {
         super(Collections.emptyList());
         this.elements = elements;
      }

      public Collection<PluginEntry> visitType(final TypeElement e, final Plugin plugin) {
         PluginAliases aliases = e.getAnnotation(PluginAliases.class);
         if (aliases == null) {
            return this.DEFAULT_VALUE;
         } else {
            Collection<PluginEntry> entries = new ArrayList<>(aliases.value().length);

            for (String alias : aliases.value()) {
               PluginEntry entry = new PluginEntry();
               entry.setKey(Strings.toRootLowerCase(alias));
               entry.setClassName(this.elements.getBinaryName(e).toString());
               entry.setName("".equals(plugin.elementType()) ? alias : plugin.elementType());
               entry.setPrintable(plugin.printObject());
               entry.setDefer(plugin.deferChildren());
               entry.setCategory(plugin.category());
               entries.add(entry);
            }

            return entries;
         }
      }
   }

   private static final class PluginElementVisitor extends SimpleElementVisitor7<PluginEntry, Plugin> {
      private final Elements elements;

      private PluginElementVisitor(final Elements elements) {
         this.elements = elements;
      }

      public PluginEntry visitType(final TypeElement e, final Plugin plugin) {
         Objects.requireNonNull(plugin, "Plugin annotation is null.");
         PluginEntry entry = new PluginEntry();
         entry.setKey(Strings.toRootLowerCase(plugin.name()));
         entry.setClassName(this.elements.getBinaryName(e).toString());
         entry.setName("".equals(plugin.elementType()) ? plugin.name() : plugin.elementType());
         entry.setPrintable(plugin.printObject());
         entry.setDefer(plugin.deferChildren());
         entry.setCategory(plugin.category());
         return entry;
      }
   }
}
