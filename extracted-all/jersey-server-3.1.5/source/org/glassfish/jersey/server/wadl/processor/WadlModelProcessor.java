package org.glassfish.jersey.server.wadl.processor;

import com.sun.research.ws.wadl.Application;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.server.model.internal.ModelProcessorUtil;
import org.glassfish.jersey.server.wadl.WadlApplicationContext;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.glassfish.jersey.server.wadl.internal.WadlUtils;

@Priority(10000)
public class WadlModelProcessor implements ModelProcessor {
   private final List<ModelProcessorUtil.Method> methodList = new ArrayList<>();

   public WadlModelProcessor() {
      this.methodList.add(new ModelProcessorUtil.Method("OPTIONS", MediaType.WILDCARD_TYPE, MediaTypes.WADL_TYPE, WadlModelProcessor.OptionsHandler.class));
   }

   @Override
   public ResourceModel processResourceModel(ResourceModel resourceModel, Configuration configuration) {
      boolean disabled = PropertiesHelper.isProperty(configuration.getProperty("jersey.config.server.wadl.disableWadl"));
      if (disabled) {
         return resourceModel;
      } else {
         ResourceModel.Builder builder = ModelProcessorUtil.enhanceResourceModel(resourceModel, false, this.methodList, true);
         if (!configuration.getClasses().contains(WadlResource.class)) {
            Resource wadlResource = Resource.builder(WadlResource.class).build();
            builder.addResource(wadlResource);
         }

         return builder.build();
      }
   }

   @Override
   public ResourceModel processSubResource(ResourceModel resourceModel, Configuration configuration) {
      boolean disabled = PropertiesHelper.isProperty(configuration.getProperty("jersey.config.server.wadl.disableWadl"));
      return disabled ? resourceModel : ModelProcessorUtil.enhanceResourceModel(resourceModel, true, this.methodList, true).build();
   }

   public static class OptionsHandler implements Inflector<ContainerRequestContext, Response> {
      private final String lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
      @Inject
      private Provider<ExtendedUriInfo> extendedUriInfo;
      @Context
      private WadlApplicationContext wadlApplicationContext;

      public Response apply(ContainerRequestContext containerRequestContext) {
         RuntimeResource resource = ((ExtendedUriInfo)this.extendedUriInfo.get()).getMatchedRuntimeResources().get(0);
         UriInfo uriInfo = containerRequestContext.getUriInfo();
         Application wadlApplication = this.wadlApplicationContext
            .getApplication(uriInfo, resource.getResources().get(0), WadlUtils.isDetailedWadlRequested(uriInfo));
         if (wadlApplication == null) {
            return Response.status(Status.NOT_FOUND).build();
         } else {
            byte[] bytes;
            try {
               Marshaller marshaller = this.wadlApplicationContext.getJAXBContext().createMarshaller();
               marshaller.setProperty("jaxb.formatted.output", true);
               ByteArrayOutputStream os = new ByteArrayOutputStream();
               marshaller.marshal(wadlApplication, os);
               bytes = os.toByteArray();
               os.close();
            } catch (Exception var8) {
               throw new ProcessingException("Could not marshal the wadl Application.", var8);
            }

            return Response.ok()
               .type(MediaTypes.WADL_TYPE)
               .allow(ModelProcessorUtil.getAllowedMethods(resource))
               .header("Last-modified", this.lastModified)
               .entity(bytes)
               .build();
         }
      }
   }
}
