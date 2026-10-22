package org.glassfish.jersey.server.wadl.internal;

import com.sun.research.ws.wadl.Application;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.ExtendedResource;
import org.glassfish.jersey.server.wadl.WadlApplicationContext;

@Singleton
@Path("application.wadl")
@ExtendedResource
public final class WadlResource {
   public static final String HTTPDATEFORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
   private volatile URI lastBaseUri;
   private volatile boolean lastDetailedWadl;
   private byte[] wadlXmlRepresentation;
   private String lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
   @Context
   private WadlApplicationContext wadlContext;

   private boolean isCached(UriInfo uriInfo, boolean detailedWadl) {
      return this.lastBaseUri != null && this.lastBaseUri.equals(uriInfo.getBaseUri()) && this.lastDetailedWadl == detailedWadl;
   }

   @Produces({"application/vnd.sun.wadl+xml", "application/xml"})
   @GET
   public synchronized Response getWadl(@Context UriInfo uriInfo) {
      try {
         if (!this.wadlContext.isWadlGenerationEnabled()) {
            return Response.status(Status.NOT_FOUND).build();
         } else {
            boolean detailedWadl = WadlUtils.isDetailedWadlRequested(uriInfo);
            if (this.wadlXmlRepresentation == null || !this.isCached(uriInfo, detailedWadl)) {
               this.lastBaseUri = uriInfo.getBaseUri();
               this.lastDetailedWadl = detailedWadl;
               this.lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
               ApplicationDescription applicationDescription = this.wadlContext.getApplication(uriInfo, detailedWadl);
               Application application = applicationDescription.getApplication();

               try {
                  Marshaller marshaller = this.wadlContext.getJAXBContext().createMarshaller();
                  marshaller.setProperty("jaxb.formatted.output", true);
                  ByteArrayOutputStream os = new ByteArrayOutputStream();
                  marshaller.marshal(application, os);
                  this.wadlXmlRepresentation = os.toByteArray();
                  os.close();
               } catch (Exception var7) {
                  throw new ProcessingException("Could not marshal the wadl Application.", var7);
               }
            }

            return Response.ok(new ByteArrayInputStream(this.wadlXmlRepresentation)).header("Last-modified", this.lastModified).build();
         }
      } catch (Exception var8) {
         throw new ProcessingException("Error generating /application.wadl.", var8);
      }
   }

   @Produces({"application/xml"})
   @GET
   @Path("{path}")
   public synchronized Response getExternalGrammar(@Context UriInfo uriInfo, @PathParam("path") String path) {
      try {
         if (!this.wadlContext.isWadlGenerationEnabled()) {
            return Response.status(Status.NOT_FOUND).build();
         } else {
            ApplicationDescription applicationDescription = this.wadlContext.getApplication(uriInfo, WadlUtils.isDetailedWadlRequested(uriInfo));
            ApplicationDescription.ExternalGrammar externalMetadata = applicationDescription.getExternalGrammar(path);
            return externalMetadata == null
               ? Response.status(Status.NOT_FOUND).build()
               : Response.ok().type(externalMetadata.getType()).entity(externalMetadata.getContent()).build();
         }
      } catch (Exception var5) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_RESOURCE_EXTERNAL_GRAMMAR(), var5);
      }
   }
}
