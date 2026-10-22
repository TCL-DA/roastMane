package org.glassfish.jersey.server.wadl.internal;

import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorImpl implements WadlGenerator {
   @Override
   public String getRequiredJaxbContextPath() {
      String name = Application.class.getName();
      return name.substring(0, name.lastIndexOf(46));
   }

   @Override
   public void init() {
   }

   @Override
   public void setWadlGeneratorDelegate(WadlGenerator delegate) {
      throw new UnsupportedOperationException("No delegate supported.");
   }

   @Override
   public Resources createResources() {
      return new Resources();
   }

   @Override
   public Application createApplication() {
      return new Application();
   }

   @Override
   public Method createMethod(Resource r, ResourceMethod m) {
      Method wadlMethod = new Method();
      wadlMethod.setName(m.getHttpMethod());
      wadlMethod.setId(m.getInvocable().getDefinitionMethod().getName());
      if (m.isExtended()) {
         wadlMethod.getAny().add(WadlApplicationContextImpl.EXTENDED_ELEMENT);
      }

      return wadlMethod;
   }

   @Override
   public Representation createRequestRepresentation(Resource r, ResourceMethod m, MediaType mediaType) {
      Representation wadlRepresentation = new Representation();
      wadlRepresentation.setMediaType(mediaType.toString());
      return wadlRepresentation;
   }

   @Override
   public Request createRequest(Resource r, ResourceMethod m) {
      return new Request();
   }

   @Override
   public Param createParam(Resource r, ResourceMethod m, Parameter p) {
      if (p.getSource() == Source.UNKNOWN) {
         return null;
      } else {
         Param wadlParam = new Param();
         wadlParam.setName(p.getSourceName());
         switch (p.getSource()) {
            case FORM:
               wadlParam.setStyle(ParamStyle.QUERY);
               break;
            case QUERY:
               wadlParam.setStyle(ParamStyle.QUERY);
               break;
            case MATRIX:
               wadlParam.setStyle(ParamStyle.MATRIX);
               break;
            case PATH:
               wadlParam.setStyle(ParamStyle.TEMPLATE);
               break;
            case HEADER:
               wadlParam.setStyle(ParamStyle.HEADER);
               break;
            case COOKIE:
               wadlParam.setStyle(ParamStyle.HEADER);
               wadlParam.setName("Cookie");
               wadlParam.setPath(p.getSourceName());
         }

         if (p.hasDefaultValue()) {
            wadlParam.setDefault(p.getDefaultValue());
         }

         Class<?> pClass = p.getRawType();
         if (pClass.isArray()) {
            wadlParam.setRepeating(true);
            pClass = pClass.getComponentType();
         }

         if (pClass.equals(int.class) || pClass.equals(Integer.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "int", "xs"));
         } else if (pClass.equals(boolean.class) || pClass.equals(Boolean.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "boolean", "xs"));
         } else if (pClass.equals(long.class) || pClass.equals(Long.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "long", "xs"));
         } else if (pClass.equals(short.class) || pClass.equals(Short.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "short", "xs"));
         } else if (pClass.equals(byte.class) || pClass.equals(Byte.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "byte", "xs"));
         } else if (pClass.equals(float.class) || pClass.equals(Float.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "float", "xs"));
         } else if (!pClass.equals(double.class) && !pClass.equals(Double.class)) {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "string", "xs"));
         } else {
            wadlParam.setType(new QName("http://www.w3.org/2001/XMLSchema", "double", "xs"));
         }

         return wadlParam;
      }
   }

   @Override
   public com.sun.research.ws.wadl.Resource createResource(Resource resource, String path) {
      com.sun.research.ws.wadl.Resource wadlResource = new com.sun.research.ws.wadl.Resource();
      if (path != null) {
         wadlResource.setPath(path);
      } else if (resource.getPath() != null) {
         wadlResource.setPath(resource.getPath());
      }

      if (resource.isExtended()) {
         wadlResource.getAny().add(WadlApplicationContextImpl.EXTENDED_ELEMENT);
      }

      return wadlResource;
   }

   @Override
   public List<Response> createResponses(Resource r, ResourceMethod m) {
      Response response = new Response();
      if (this.hasEmptyProducibleMediaTypeSet(m)) {
         Representation wadlRepresentation = this.createResponseRepresentation(r, m, MediaType.WILDCARD_TYPE);
         response.getRepresentation().add(wadlRepresentation);
      } else {
         for (MediaType mediaType : m.getProducedTypes()) {
            Representation wadlRepresentation = this.createResponseRepresentation(r, m, mediaType);
            response.getRepresentation().add(wadlRepresentation);
         }
      }

      List<Response> responses = new ArrayList<>();
      responses.add(response);
      return responses;
   }

   private boolean hasEmptyProducibleMediaTypeSet(ResourceMethod method) {
      return method.getProducedTypes().isEmpty();
   }

   public Representation createResponseRepresentation(Resource r, ResourceMethod m, MediaType mediaType) {
      Representation wadlRepresentation = new Representation();
      wadlRepresentation.setMediaType(mediaType.toString());
      return wadlRepresentation;
   }

   @Override
   public WadlGenerator.ExternalGrammarDefinition createExternalGrammar() {
      return new WadlGenerator.ExternalGrammarDefinition();
   }

   @Override
   public void attachTypes(ApplicationDescription egd) {
   }
}
