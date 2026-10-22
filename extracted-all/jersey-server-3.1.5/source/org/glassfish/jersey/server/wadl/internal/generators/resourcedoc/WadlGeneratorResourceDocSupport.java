package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc;

import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import jakarta.inject.Provider;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import org.glassfish.jersey.server.wadl.internal.WadlUtils;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ClassDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.MethodDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ParamDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.RepresentationDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResourceDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResponseDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.WadlParamType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.xhtml.Elements;

public class WadlGeneratorResourceDocSupport implements WadlGenerator {
   private WadlGenerator delegate;
   private File resourceDocFile;
   private InputStream resourceDocStream;
   private ResourceDocAccessor resourceDoc;
   @Context
   private Provider<SAXParserFactory> saxFactoryProvider;

   public WadlGeneratorResourceDocSupport() {
   }

   public WadlGeneratorResourceDocSupport(WadlGenerator wadlGenerator, ResourceDocType resourceDoc) {
      this.delegate = wadlGenerator;
      this.resourceDoc = new ResourceDocAccessor(resourceDoc);
   }

   @Override
   public void setWadlGeneratorDelegate(WadlGenerator delegate) {
      this.delegate = delegate;
   }

   public void setResourceDocFile(File resourceDocFile) {
      if (this.resourceDocStream != null) {
         throw new IllegalStateException(
            "The resourceDocStream property is already set, therefore you cannot set the resourceDocFile property. Only one of both can be set at a time."
         );
      } else {
         this.resourceDocFile = resourceDocFile;
      }
   }

   public void setResourceDocStream(InputStream resourceDocStream) {
      if (this.resourceDocStream != null) {
         throw new IllegalStateException(
            "The resourceDocFile property is already set, therefore you cannot set the resourceDocStream property. Only one of both can be set at a time."
         );
      } else {
         this.resourceDocStream = resourceDocStream;
      }
   }

   @Override
   public void init() throws Exception {
      if (this.resourceDocFile == null && this.resourceDocStream == null) {
         throw new IllegalStateException("Neither the resourceDocFile nor the resourceDocStream is set, one of both is required.");
      } else {
         this.delegate.init();

         try {
            InputStream inputStream = this.resourceDocFile != null ? Files.newInputStream(this.resourceDocFile.toPath()) : this.resourceDocStream;

            try {
               ResourceDocType resourceDocType = WadlUtils.unmarshall(inputStream, (SAXParserFactory)this.saxFactoryProvider.get(), ResourceDocType.class);
               this.resourceDoc = new ResourceDocAccessor(resourceDocType);
            } catch (Throwable var9) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (inputStream != null) {
               inputStream.close();
            }
         } finally {
            this.resourceDocFile = null;
         }
      }
   }

   @Override
   public String getRequiredJaxbContextPath() {
      String name = Elements.class.getName();
      name = name.substring(0, name.lastIndexOf(46));
      return this.delegate.getRequiredJaxbContextPath() == null ? name : this.delegate.getRequiredJaxbContextPath() + ":" + name;
   }

   @Override
   public Application createApplication() {
      return this.delegate.createApplication();
   }

   @Override
   public Resource createResource(org.glassfish.jersey.server.model.Resource r, String path) {
      Resource result = this.delegate.createResource(r, path);

      for (Class<?> resourceClass : r.getHandlerClasses()) {
         ClassDocType classDoc = this.resourceDoc.getClassDoc(resourceClass);
         if (classDoc != null && !this.isEmpty(classDoc.getCommentText())) {
            Doc doc = new Doc();
            doc.getContent().add(classDoc.getCommentText());
            result.getDoc().add(doc);
         }
      }

      return result;
   }

   @Override
   public Method createMethod(org.glassfish.jersey.server.model.Resource resource, ResourceMethod resourceMethod) {
      Method result = this.delegate.createMethod(resource, resourceMethod);
      java.lang.reflect.Method method = resourceMethod.getInvocable().getDefinitionMethod();
      MethodDocType methodDoc = this.resourceDoc.getMethodDoc(method.getDeclaringClass(), method);
      if (methodDoc != null && !this.isEmpty(methodDoc.getCommentText())) {
         Doc doc = new Doc();
         doc.getContent().add(methodDoc.getCommentText());
         result.getDoc().add(doc);
      }

      return result;
   }

   @Override
   public Representation createRequestRepresentation(org.glassfish.jersey.server.model.Resource r, ResourceMethod m, MediaType mediaType) {
      Representation result = this.delegate.createRequestRepresentation(r, m, mediaType);
      RepresentationDocType requestRepresentation = this.resourceDoc
         .getRequestRepresentation(m.getInvocable().getDefinitionMethod().getDeclaringClass(), m.getInvocable().getDefinitionMethod(), result.getMediaType());
      if (requestRepresentation != null) {
         result.setElement(requestRepresentation.getElement());
         this.addDocForExample(result.getDoc(), requestRepresentation.getExample());
      }

      return result;
   }

   @Override
   public Request createRequest(org.glassfish.jersey.server.model.Resource r, ResourceMethod m) {
      return this.delegate.createRequest(r, m);
   }

   @Override
   public List<Response> createResponses(org.glassfish.jersey.server.model.Resource r, ResourceMethod m) {
      ResponseDocType responseDoc = this.resourceDoc
         .getResponse(m.getInvocable().getDefinitionMethod().getDeclaringClass(), m.getInvocable().getDefinitionMethod());
      List<Response> responses = new ArrayList<>();
      if (responseDoc != null && responseDoc.hasRepresentations()) {
         for (RepresentationDocType representationDoc : responseDoc.getRepresentations()) {
            Response response = new Response();
            Representation wadlRepresentation = new Representation();
            wadlRepresentation.setElement(representationDoc.getElement());
            wadlRepresentation.setMediaType(representationDoc.getMediaType());
            this.addDocForExample(wadlRepresentation.getDoc(), representationDoc.getExample());
            this.addDoc(wadlRepresentation.getDoc(), representationDoc.getDoc());
            response.getStatus().add(representationDoc.getStatus());
            response.getRepresentation().add(wadlRepresentation);
            responses.add(response);
         }

         if (!responseDoc.getWadlParams().isEmpty()) {
            for (WadlParamType wadlParamType : responseDoc.getWadlParams()) {
               Param param = new Param();
               param.setName(wadlParamType.getName());
               param.setStyle(ParamStyle.fromValue(wadlParamType.getStyle()));
               param.setType(wadlParamType.getType());
               this.addDoc(param.getDoc(), wadlParamType.getDoc());

               for (Response response : responses) {
                  response.getParam().add(param);
               }
            }
         }

         if (!this.isEmpty(responseDoc.getReturnDoc())) {
            for (Response response : responses) {
               this.addDoc(response.getDoc(), responseDoc.getReturnDoc());
            }
         }
      } else {
         responses = this.delegate.createResponses(r, m);
      }

      return responses;
   }

   private void addDocForExample(List<Doc> docs, String example) {
      if (!this.isEmpty(example)) {
         Doc doc = new Doc();
         Elements pElement = Elements.el("p").add(Elements.val("h6", "Example")).add(Elements.el("pre").add(Elements.val("code", example)));
         doc.getContent().add(pElement);
         docs.add(doc);
      }
   }

   private void addDoc(List<Doc> docs, String text) {
      if (!this.isEmpty(text)) {
         Doc doc = new Doc();
         doc.getContent().add(text);
         docs.add(doc);
      }
   }

   @Override
   public Param createParam(org.glassfish.jersey.server.model.Resource r, ResourceMethod m, Parameter p) {
      Param result = this.delegate.createParam(r, m, p);
      if (result != null) {
         ParamDocType paramDoc = this.resourceDoc
            .getParamDoc(m.getInvocable().getDefinitionMethod().getDeclaringClass(), m.getInvocable().getDefinitionMethod(), p);
         if (paramDoc != null && !this.isEmpty(paramDoc.getCommentText())) {
            Doc doc = new Doc();
            doc.getContent().add(paramDoc.getCommentText());
            result.getDoc().add(doc);
         }
      }

      return result;
   }

   @Override
   public Resources createResources() {
      return this.delegate.createResources();
   }

   private boolean isEmpty(String text) {
      return text == null || text.isEmpty() || "".equals(text.trim());
   }

   @Override
   public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
      return this.delegate.createExternalGrammar();
   }

   @Override
   public void attachTypes(ApplicationDescription egd) {
      this.delegate.attachTypes(egd);
   }
}
