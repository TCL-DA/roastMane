package org.glassfish.jersey.server.wadl.internal.generators;

import com.sun.research.ws.wadl.Application;
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
import javax.xml.parsers.SAXParserFactory;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import org.glassfish.jersey.server.wadl.internal.WadlUtils;

public class WadlGeneratorApplicationDoc implements WadlGenerator {
   private WadlGenerator _delegate;
   private File _applicationDocsFile;
   private InputStream _applicationDocsStream;
   private ApplicationDocs _applicationDocs;
   @Context
   private Provider<SAXParserFactory> saxFactoryProvider;

   @Override
   public void setWadlGeneratorDelegate(WadlGenerator delegate) {
      this._delegate = delegate;
   }

   @Override
   public String getRequiredJaxbContextPath() {
      return this._delegate.getRequiredJaxbContextPath();
   }

   public void setApplicationDocsFile(File applicationDocsFile) {
      if (this._applicationDocsStream != null) {
         throw new IllegalStateException(
            "The applicationDocsStream property is already set, therefore you cannot set the applicationDocsFile property. Only one of both can be set at a time."
         );
      } else {
         this._applicationDocsFile = applicationDocsFile;
      }
   }

   public void setApplicationDocsStream(InputStream applicationDocsStream) {
      if (this._applicationDocsFile != null) {
         throw new IllegalStateException(
            "The applicationDocsFile property is already set, therefore you cannot set the applicationDocsStream property. Only one of both can be set at a time."
         );
      } else {
         this._applicationDocsStream = applicationDocsStream;
      }
   }

   @Override
   public void init() throws Exception {
      if (this._applicationDocsFile == null && this._applicationDocsStream == null) {
         throw new IllegalStateException("Neither the applicationDocsFile nor the applicationDocsStream is set, one of both is required.");
      } else {
         this._delegate.init();
         InputStream inputStream;
         if (this._applicationDocsFile != null) {
            inputStream = Files.newInputStream(this._applicationDocsFile.toPath());
         } else {
            inputStream = this._applicationDocsStream;
         }

         this._applicationDocs = WadlUtils.unmarshall(inputStream, (SAXParserFactory)this.saxFactoryProvider.get(), ApplicationDocs.class);
      }
   }

   @Override
   public Application createApplication() {
      Application result = this._delegate.createApplication();
      if (this._applicationDocs != null && this._applicationDocs.getDocs() != null && !this._applicationDocs.getDocs().isEmpty()) {
         result.getDoc().addAll(this._applicationDocs.getDocs());
      }

      return result;
   }

   @Override
   public Method createMethod(Resource r, ResourceMethod m) {
      return this._delegate.createMethod(r, m);
   }

   @Override
   public Representation createRequestRepresentation(Resource r, ResourceMethod m, MediaType mediaType) {
      return this._delegate.createRequestRepresentation(r, m, mediaType);
   }

   @Override
   public Request createRequest(Resource r, ResourceMethod m) {
      return this._delegate.createRequest(r, m);
   }

   @Override
   public Param createParam(Resource r, ResourceMethod m, Parameter p) {
      return this._delegate.createParam(r, m, p);
   }

   @Override
   public com.sun.research.ws.wadl.Resource createResource(Resource r, String path) {
      return this._delegate.createResource(r, path);
   }

   @Override
   public List<Response> createResponses(Resource r, ResourceMethod m) {
      return this._delegate.createResponses(r, m);
   }

   @Override
   public Resources createResources() {
      return this._delegate.createResources();
   }

   @Override
   public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
      return this._delegate.createExternalGrammar();
   }

   @Override
   public void attachTypes(ApplicationDescription egd) {
      this._delegate.attachTypes(egd);
   }
}
