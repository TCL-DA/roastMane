package org.glassfish.jersey.server.wadl.internal;

import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Grammars;
import com.sun.research.ws.wadl.Include;
import com.sun.research.ws.wadl.Resources;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.net.URI;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.ExtendedResourceContext;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.wadl.WadlApplicationContext;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import org.glassfish.jersey.server.wadl.config.WadlGeneratorConfig;
import org.glassfish.jersey.server.wadl.config.WadlGeneratorConfigLoader;
import org.glassfish.jersey.server.wadl.internal.generators.WadlGeneratorJAXBGrammarGenerator;

public final class WadlApplicationContextImpl implements WadlApplicationContext {
   private static final Logger LOGGER = Logger.getLogger(WadlApplicationContextImpl.class.getName());
   static final String WADL_JERSEY_NAMESPACE = "http://jersey.java.net/";
   static final JAXBElement EXTENDED_ELEMENT = new JAXBElement(new QName("http://jersey.java.net/", "extended", "jersey"), String.class, "true");
   private final ExtendedResourceContext resourceContext;
   private final InjectionManager injectionManager;
   private final WadlGeneratorConfig wadlGeneratorConfig;
   private final JAXBContext jaxbContext;
   private volatile boolean wadlGenerationEnabled = true;

   @Inject
   public WadlApplicationContextImpl(InjectionManager injectionManager, Configuration configuration, ExtendedResourceContext resourceContext) {
      this.injectionManager = injectionManager;
      this.wadlGeneratorConfig = WadlGeneratorConfigLoader.loadWadlGeneratorsFromConfig(configuration.getProperties());
      this.resourceContext = resourceContext;
      WadlGenerator wadlGenerator = this.wadlGeneratorConfig.createWadlGenerator(injectionManager);

      try {
         this.jaxbContext = getJAXBContextFromWadlGenerator(wadlGenerator);
      } catch (JAXBException var6) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_JAXB_CONTEXT(), var6);
      }
   }

   public static JAXBContext getJAXBContextFromWadlGenerator(WadlGenerator wadlGenerator) throws JAXBException {
      JAXBContext jaxbContextCandidate = null;
      ClassLoader contextClassLoader = AccessController.doPrivileged(ReflectionHelper.getContextClassLoaderPA());

      try {
         ClassLoader jerseyModuleClassLoader = AccessController.doPrivileged(ReflectionHelper.getClassLoaderPA(wadlGenerator.getClass()));
         AccessController.doPrivileged(ReflectionHelper.setContextClassLoaderPA(jerseyModuleClassLoader));
         jaxbContextCandidate = JAXBContext.newInstance(wadlGenerator.getRequiredJaxbContextPath(), jerseyModuleClassLoader);
      } catch (JAXBException var10) {
         JAXBException ex = var10;

         try {
            LOGGER.log(Level.FINE, LocalizationMessages.WADL_JAXB_CONTEXT_FALLBACK(), (Throwable)ex);
            jaxbContextCandidate = JAXBContext.newInstance(wadlGenerator.getRequiredJaxbContextPath());
         } catch (JAXBException var9) {
            throw var10;
         }
      } finally {
         AccessController.doPrivileged(ReflectionHelper.setContextClassLoaderPA(contextClassLoader));
      }

      return jaxbContextCandidate;
   }

   @Override
   public ApplicationDescription getApplication(UriInfo uriInfo, boolean detailedWadl) {
      ApplicationDescription applicationDescription = this.getWadlBuilder(detailedWadl, uriInfo)
         .generate(this.resourceContext.getResourceModel().getRootResources());
      Application application = applicationDescription.getApplication();

      for (Resources resources : application.getResources()) {
         if (resources.getBase() == null) {
            resources.setBase(uriInfo.getBaseUri().toString());
         }
      }

      this.attachExternalGrammar(application, applicationDescription, uriInfo.getRequestUri());
      return applicationDescription;
   }

   @Override
   public Application getApplication(UriInfo info, Resource resource, boolean detailedWadl) {
      ApplicationDescription description = this.getApplication(info, detailedWadl);
      WadlGenerator wadlGenerator = this.wadlGeneratorConfig.createWadlGenerator(this.injectionManager);
      Application application = new WadlBuilder(wadlGenerator, detailedWadl, info).generate(description, resource);
      if (application == null) {
         return null;
      } else {
         for (Resources resources : application.getResources()) {
            resources.setBase(info.getBaseUri().toString());
         }

         this.attachExternalGrammar(application, description, info.getRequestUri());

         for (Resources resources : application.getResources()) {
            com.sun.research.ws.wadl.Resource r = resources.getResource().get(0);
            r.setPath(info.getBaseUri().relativize(info.getAbsolutePath()).toString());
            r.getParam().clear();
         }

         return application;
      }
   }

   @Override
   public JAXBContext getJAXBContext() {
      return this.jaxbContext;
   }

   private WadlBuilder getWadlBuilder(boolean detailedWadl, UriInfo uriInfo) {
      return this.wadlGenerationEnabled ? new WadlBuilder(this.wadlGeneratorConfig.createWadlGenerator(this.injectionManager), detailedWadl, uriInfo) : null;
   }

   @Override
   public void setWadlGenerationEnabled(boolean wadlGenerationEnabled) {
      this.wadlGenerationEnabled = wadlGenerationEnabled;
   }

   @Override
   public boolean isWadlGenerationEnabled() {
      return this.wadlGenerationEnabled;
   }

   private void attachExternalGrammar(Application application, ApplicationDescription applicationDescription, URI requestURI) {
      try {
         String requestURIPath = requestURI.getPath();
         if (requestURIPath.endsWith("application.wadl")) {
            requestURI = UriBuilder.fromUri(requestURI).replacePath(requestURIPath.substring(0, requestURIPath.lastIndexOf(47) + 1)).build(new Object[0]);
         }

         String root = application.getResources().get(0).getBase();
         UriBuilder extendedPath = root != null ? UriBuilder.fromPath(root).path("/application.wadl/") : UriBuilder.fromPath("./application.wadl/");
         URI rootURI = root != null ? UriBuilder.fromPath(root).build(new Object[0]) : null;
         Grammars grammars;
         if (application.getGrammars() != null) {
            LOGGER.info(LocalizationMessages.ERROR_WADL_GRAMMAR_ALREADY_CONTAINS());
            grammars = application.getGrammars();
         } else {
            grammars = new Grammars();
            application.setGrammars(grammars);
         }

         for (String path : applicationDescription.getExternalMetadataKeys()) {
            URI schemaURI = extendedPath.clone().path(path).build(new Object[0]);
            String schemaPath = rootURI != null ? requestURI.relativize(schemaURI).toString() : schemaURI.toString();
            Include include = new Include();
            include.setHref(schemaPath);
            Doc doc = new Doc();
            doc.setLang("en");
            doc.setTitle("Generated");
            include.getDoc().add(doc);
            grammars.getInclude().add(include);
         }
      } catch (Exception var15) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_EXTERNAL_GRAMMAR(), var15);
      }
   }

   public static boolean isJaxbImplAvailable() {
      try {
         return null != getJAXBContextFromWadlGenerator(new WadlGeneratorJAXBGrammarGenerator());
      } catch (JAXBException var1) {
         return false;
      }
   }
}
