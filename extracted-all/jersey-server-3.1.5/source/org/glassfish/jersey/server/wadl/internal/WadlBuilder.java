package org.glassfish.jersey.server.wadl.internal;

import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.ParamStyle;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.glassfish.jersey.internal.Version;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlBuilder {
   private final WadlGenerator _wadlGenerator;
   private final UriInfo uriInfo;
   private final boolean detailedWadl;

   public WadlBuilder(WadlGenerator wadlGenerator, boolean detailedWadl, UriInfo uriInfo) {
      this.detailedWadl = detailedWadl;
      this._wadlGenerator = wadlGenerator;
      this.uriInfo = uriInfo;
   }

   public ApplicationDescription generate(List<Resource> resources) {
      Application wadlApplication = this._wadlGenerator.createApplication();
      Resources wadlResources = this._wadlGenerator.createResources();

      for (Resource r : resources) {
         com.sun.research.ws.wadl.Resource wadlResource = this.generateResource(r, r.getPath());
         if (wadlResource != null) {
            wadlResources.getResource().add(wadlResource);
         }
      }

      wadlApplication.getResources().add(wadlResources);
      this.addVersion(wadlApplication);
      this.addHint(wadlApplication);
      WadlGenerator.ExternalGrammarDefinition external = this._wadlGenerator.createExternalGrammar();
      ApplicationDescription description = new ApplicationDescription(wadlApplication, external);
      this._wadlGenerator.attachTypes(description);
      return description;
   }

   public Application generate(ApplicationDescription description, Resource resource) {
      try {
         Application wadlApplication = this._wadlGenerator.createApplication();
         Resources wadlResources = this._wadlGenerator.createResources();
         com.sun.research.ws.wadl.Resource wadlResource = this.generateResource(resource, null);
         if (wadlResource == null) {
            return null;
         } else {
            wadlResources.getResource().add(wadlResource);
            wadlApplication.getResources().add(wadlResources);
            this.addVersion(wadlApplication);
            this._wadlGenerator.attachTypes(description);
            return wadlApplication;
         }
      } catch (Exception var6) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_RESOURCE(resource), var6);
      }
   }

   private void addVersion(Application wadlApplication) {
      Doc d = new Doc();
      d.getOtherAttributes().put(new QName("http://jersey.java.net/", "generatedBy", "jersey"), Version.getBuildId());
      wadlApplication.getDoc().add(d);
   }

   private void addHint(Application wadlApplication) {
      if (this.uriInfo != null) {
         Doc d = new Doc();
         String message;
         if (this.detailedWadl) {
            String uriWithoutQueryParam = UriBuilder.fromUri(this.uriInfo.getRequestUri()).replaceQuery("").build(new Object[0]).toString();
            message = LocalizationMessages.WADL_DOC_EXTENDED_WADL("detail", uriWithoutQueryParam);
         } else {
            String uriWithQueryParam = UriBuilder.fromUri(this.uriInfo.getRequestUri())
               .queryParam("detail", new Object[]{"true"})
               .build(new Object[0])
               .toString();
            message = LocalizationMessages.WADL_DOC_SIMPLE_WADL("detail", uriWithQueryParam);
         }

         d.getOtherAttributes().put(new QName("http://jersey.java.net/", "hint", "jersey"), message);
         wadlApplication.getDoc().add(d);
      }
   }

   private Method generateMethod(Resource parentResource, Map<String, Param> wadlResourceParams, ResourceMethod resourceMethod) {
      try {
         if (!this.detailedWadl && resourceMethod.isExtended()) {
            return null;
         } else {
            Method wadlMethod = this._wadlGenerator.createMethod(parentResource, resourceMethod);
            Request wadlRequest = this.generateRequest(parentResource, resourceMethod, wadlResourceParams);
            if (wadlRequest != null) {
               wadlMethod.setRequest(wadlRequest);
            }

            List<Response> responses = this.generateResponses(parentResource, resourceMethod);
            if (responses != null) {
               wadlMethod.getResponse().addAll(responses);
            }

            return wadlMethod;
         }
      } catch (Exception var7) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_METHOD(resourceMethod, parentResource), var7);
      }
   }

   private Request generateRequest(Resource parentResource, ResourceMethod resourceMethod, Map<String, Param> wadlResourceParams) {
      try {
         List<Parameter> requestParams = new LinkedList<>(resourceMethod.getInvocable().getParameters());
         requestParams.addAll(resourceMethod.getInvocable().getHandler().getParameters());
         if (requestParams.isEmpty()) {
            return null;
         } else {
            Request wadlRequest = this._wadlGenerator.createRequest(parentResource, resourceMethod);
            this.processRequestParameters(parentResource, resourceMethod, wadlResourceParams, requestParams, wadlRequest);
            return wadlRequest.getRepresentation().size() + wadlRequest.getParam().size() == 0 ? null : wadlRequest;
         }
      } catch (Exception var6) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_REQUEST(resourceMethod, parentResource), var6);
      }
   }

   private void processRequestParameters(
      Resource parentResource,
      ResourceMethod resourceMethod,
      Map<String, Param> wadlResourceParams,
      Collection<Parameter> requestParameters,
      Request wadlRequest
   ) {
      for (Parameter parameter : requestParameters) {
         if (parameter.getSource() == Source.ENTITY || parameter.getSource() == Source.UNKNOWN) {
            for (MediaType mediaType : resourceMethod.getConsumedTypes()) {
               this.setRepresentationForMediaType(parentResource, resourceMethod, mediaType, wadlRequest);
            }
         } else if (parameter.getSourceAnnotation().annotationType() == FormParam.class) {
            List<MediaType> supportedInputTypes = resourceMethod.getConsumedTypes();
            if (supportedInputTypes.isEmpty() || supportedInputTypes.size() == 1 && supportedInputTypes.get(0).isWildcardType()) {
               supportedInputTypes = Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
            }

            for (MediaType mediaType : supportedInputTypes) {
               Representation wadlRepresentation = this.setRepresentationForMediaType(parentResource, resourceMethod, mediaType, wadlRequest);
               if (this.getParamByName(wadlRepresentation.getParam(), parameter.getSourceName()) == null) {
                  Param wadlParam = this.generateParam(parentResource, resourceMethod, parameter);
                  if (wadlParam != null) {
                     wadlRepresentation.getParam().add(wadlParam);
                  }
               }
            }
         } else if (!"org.glassfish.jersey.media.multipart.FormDataParam".equals(parameter.getSourceAnnotation().annotationType().getName())) {
            if (parameter instanceof Parameter.BeanParameter) {
               this.processRequestParameters(
                  parentResource, resourceMethod, wadlResourceParams, ((Parameter.BeanParameter)parameter).getParameters(), wadlRequest
               );
            } else {
               Param wadlParam = this.generateParam(parentResource, resourceMethod, parameter);
               if (wadlParam != null) {
                  if (wadlParam.getStyle() != ParamStyle.TEMPLATE && wadlParam.getStyle() != ParamStyle.MATRIX) {
                     wadlRequest.getParam().add(wadlParam);
                  } else {
                     wadlResourceParams.put(wadlParam.getName(), wadlParam);
                  }
               }
            }
         } else {
            List<MediaType> supportedInputTypes = resourceMethod.getConsumedTypes();
            if (supportedInputTypes.isEmpty() || supportedInputTypes.size() == 1 && supportedInputTypes.get(0).isWildcardType()) {
               supportedInputTypes = Collections.singletonList(MediaType.MULTIPART_FORM_DATA_TYPE);
            }

            for (MediaType mediaTypex : supportedInputTypes) {
               Representation wadlRepresentation = this.setRepresentationForMediaType(parentResource, resourceMethod, mediaTypex, wadlRequest);
               if (this.getParamByName(wadlRepresentation.getParam(), parameter.getSourceName()) == null) {
                  Param wadlParam = this.generateParam(parentResource, resourceMethod, parameter);
                  if (wadlParam != null) {
                     wadlRepresentation.getParam().add(wadlParam);
                  }
               }
            }
         }
      }
   }

   private Param getParamByName(List<Param> params, String name) {
      for (Param param : params) {
         if (param.getName().equals(name)) {
            return param;
         }
      }

      return null;
   }

   private Representation setRepresentationForMediaType(Resource r, ResourceMethod m, MediaType mediaType, Request wadlRequest) {
      try {
         Representation wadlRepresentation = this.getRepresentationByMediaType(wadlRequest.getRepresentation(), mediaType);
         if (wadlRepresentation == null) {
            wadlRepresentation = this._wadlGenerator.createRequestRepresentation(r, m, mediaType);
            wadlRequest.getRepresentation().add(wadlRepresentation);
         }

         return wadlRepresentation;
      } catch (Exception var6) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_REQUEST_MEDIA_TYPE(mediaType, m, r), var6);
      }
   }

   private Representation getRepresentationByMediaType(List<Representation> representations, MediaType mediaType) {
      for (Representation representation : representations) {
         if (mediaType.toString().equals(representation.getMediaType())) {
            return representation;
         }
      }

      return null;
   }

   private Param generateParam(Resource resource, ResourceMethod method, Parameter param) {
      try {
         return param.getSource() != Source.ENTITY && param.getSource() != Source.CONTEXT ? this._wadlGenerator.createParam(resource, method, param) : null;
      } catch (Exception var5) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_PARAM(param, resource, method), var5);
      }
   }

   private com.sun.research.ws.wadl.Resource generateResource(Resource r, String path) {
      return this.generateResource(r, path, Collections.emptySet());
   }

   private com.sun.research.ws.wadl.Resource generateResource(Resource resource, String path, Set<Resource> visitedResources) {
      try {
         if (!this.detailedWadl && resource.isExtended()) {
            return null;
         } else {
            com.sun.research.ws.wadl.Resource wadlResource = this._wadlGenerator.createResource(resource, path);
            if (visitedResources.contains(resource)) {
               return wadlResource;
            } else {
               Set<Resource> var14 = new HashSet<>(visitedResources);
               var14.add(resource);
               ResourceMethod locator = resource.getResourceLocator();
               if (locator != null) {
                  try {
                     Resource.Builder builder = Resource.builder(locator.getInvocable().getRawResponseType());
                     if (builder == null) {
                        builder = Resource.builder().path(resource.getPath());
                     }

                     Resource subResource = builder.build();
                     com.sun.research.ws.wadl.Resource wadlSubResource = this.generateResource(subResource, resource.getPath(), var14);
                     if (wadlSubResource == null) {
                        return null;
                     } else {
                        if (locator.isExtended()) {
                           wadlSubResource.getAny().add(WadlApplicationContextImpl.EXTENDED_ELEMENT);
                        }

                        for (Parameter param : locator.getInvocable().getParameters()) {
                           Param wadlParam = this.generateParam(resource, locator, param);
                           if (wadlParam != null && wadlParam.getStyle() == ParamStyle.TEMPLATE) {
                              wadlSubResource.getParam().add(wadlParam);
                           }
                        }

                        return wadlSubResource;
                     }
                  } catch (RuntimeException var12) {
                     throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_RESOURCE_LOCATOR(locator, resource), var12);
                  }
               } else {
                  Map<String, Param> wadlResourceParams = new HashMap<>();

                  for (ResourceMethod method : resource.getResourceMethods()) {
                     if (this.detailedWadl || !method.isExtended()) {
                        Method wadlMethod = this.generateMethod(resource, wadlResourceParams, method);
                        wadlResource.getMethodOrResource().add(wadlMethod);
                     }
                  }

                  for (Param wadlParam : wadlResourceParams.values()) {
                     wadlResource.getParam().add(wadlParam);
                  }

                  new HashMap();
                  new HashMap();

                  for (Resource childResource : resource.getChildResources()) {
                     com.sun.research.ws.wadl.Resource childWadlResource = this.generateResource(childResource, childResource.getPath(), var14);
                     if (childWadlResource != null) {
                        wadlResource.getMethodOrResource().add(childWadlResource);
                     }
                  }

                  return wadlResource;
               }
            }
         }
      } catch (Exception var13) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_RESOURCE_PATH(resource, path), var13);
      }
   }

   private List<Response> generateResponses(Resource r, ResourceMethod m) {
      try {
         return m.getInvocable().getRawResponseType() == void.class ? null : this._wadlGenerator.createResponses(r, m);
      } catch (Exception var4) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_BUILDER_GENERATION_RESPONSE(m, r), var4);
      }
   }
}
