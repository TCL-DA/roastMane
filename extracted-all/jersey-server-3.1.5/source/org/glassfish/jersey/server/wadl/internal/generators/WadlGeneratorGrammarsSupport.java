package org.glassfish.jersey.server.wadl.internal.generators;

import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Grammars;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import jakarta.inject.Provider;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParserFactory;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import org.glassfish.jersey.server.wadl.internal.WadlUtils;

public class WadlGeneratorGrammarsSupport implements WadlGenerator {
   private static final Logger LOG = Logger.getLogger(WadlGeneratorGrammarsSupport.class.getName());
   private WadlGenerator _delegate;
   private File _grammarsFile;
   private InputStream _grammarsStream;
   private Grammars _grammars;
   private Boolean overrideGrammars = false;
   @Context
   private Provider<SAXParserFactory> saxFactoryProvider;

   public WadlGeneratorGrammarsSupport() {
   }

   public WadlGeneratorGrammarsSupport(WadlGenerator delegate, Grammars grammars) {
      this._delegate = delegate;
      this._grammars = grammars;
   }

   @Override
   public void setWadlGeneratorDelegate(WadlGenerator delegate) {
      this._delegate = delegate;
   }

   public void setOverrideGrammars(Boolean overrideGrammars) {
      this.overrideGrammars = overrideGrammars;
   }

   @Override
   public String getRequiredJaxbContextPath() {
      return this._delegate.getRequiredJaxbContextPath();
   }

   public void setGrammarsFile(File grammarsFile) {
      if (this._grammarsStream != null) {
         throw new IllegalStateException(
            "The grammarsStream property is already set, therefore you cannot set the grammarsFile property. Only one of both can be set at a time."
         );
      } else {
         this._grammarsFile = grammarsFile;
      }
   }

   public void setGrammarsStream(InputStream grammarsStream) {
      if (this._grammarsFile != null) {
         throw new IllegalStateException(
            "The grammarsFile property is already set, therefore you cannot set the grammarsStream property. Only one of both can be set at a time."
         );
      } else {
         this._grammarsStream = grammarsStream;
      }
   }

   @Override
   public void init() throws Exception {
      if (this._grammarsFile == null && this._grammarsStream == null) {
         throw new IllegalStateException("Neither the grammarsFile nor the grammarsStream is set, one of both is required.");
      } else {
         this._delegate.init();
         this._grammars = WadlUtils.unmarshall(
            this._grammarsFile != null ? Files.newInputStream(this._grammarsFile.toPath()) : this._grammarsStream,
            (SAXParserFactory)this.saxFactoryProvider.get(),
            Grammars.class
         );
      }
   }

   @Override
   public Application createApplication() {
      Application result = this._delegate.createApplication();
      if (result.getGrammars() != null && !this.overrideGrammars) {
         LOG.info(
            "The wadl application created by the delegate ("
               + this._delegate
               + ") already contains a grammars element, we're adding elements of the provided grammars file."
         );
         if (!this._grammars.getAny().isEmpty()) {
            result.getGrammars().getAny().addAll(this._grammars.getAny());
         }

         if (!this._grammars.getDoc().isEmpty()) {
            result.getGrammars().getDoc().addAll(this._grammars.getDoc());
         }

         if (!this._grammars.getInclude().isEmpty()) {
            result.getGrammars().getInclude().addAll(this._grammars.getInclude());
         }
      } else {
         result.setGrammars(this._grammars);
      }

      return result;
   }

   @Override
   public Method createMethod(Resource ar, ResourceMethod arm) {
      return this._delegate.createMethod(ar, arm);
   }

   @Override
   public Request createRequest(Resource ar, ResourceMethod arm) {
      return this._delegate.createRequest(ar, arm);
   }

   @Override
   public Param createParam(Resource ar, ResourceMethod am, Parameter p) {
      return this._delegate.createParam(ar, am, p);
   }

   @Override
   public Representation createRequestRepresentation(Resource ar, ResourceMethod arm, MediaType mt) {
      return this._delegate.createRequestRepresentation(ar, arm, mt);
   }

   @Override
   public com.sun.research.ws.wadl.Resource createResource(Resource ar, String path) {
      return this._delegate.createResource(ar, path);
   }

   @Override
   public Resources createResources() {
      return this._delegate.createResources();
   }

   @Override
   public List<Response> createResponses(Resource ar, ResourceMethod arm) {
      return this._delegate.createResponses(ar, arm);
   }

   @Override
   public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
      return this.overrideGrammars ? new WadlGenerator.ExternalGrammarDefinition() : this._delegate.createExternalGrammar();
   }

   @Override
   public void attachTypes(ApplicationDescription egd) {
      this._delegate.attachTypes(egd);
   }
}
