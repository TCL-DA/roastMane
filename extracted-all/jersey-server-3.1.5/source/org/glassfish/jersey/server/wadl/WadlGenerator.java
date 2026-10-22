package org.glassfish.jersey.server.wadl;

import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;

public interface WadlGenerator {
   void setWadlGeneratorDelegate(WadlGenerator var1);

   void init() throws Exception;

   String getRequiredJaxbContextPath();

   Application createApplication();

   Resources createResources();

   Resource createResource(org.glassfish.jersey.server.model.Resource var1, String var2);

   Method createMethod(org.glassfish.jersey.server.model.Resource var1, ResourceMethod var2);

   Request createRequest(org.glassfish.jersey.server.model.Resource var1, ResourceMethod var2);

   Representation createRequestRepresentation(org.glassfish.jersey.server.model.Resource var1, ResourceMethod var2, MediaType var3);

   List<Response> createResponses(org.glassfish.jersey.server.model.Resource var1, ResourceMethod var2);

   Param createParam(org.glassfish.jersey.server.model.Resource var1, ResourceMethod var2, Parameter var3);

   WadlGenerator.ExternalGrammarDefinition createExternalGrammar();

   void attachTypes(ApplicationDescription var1);

   public static class ExternalGrammarDefinition {
      public final Map<String, ApplicationDescription.ExternalGrammar> map = new HashMap<>();
      private List<WadlGenerator.Resolver> typeResolvers = new ArrayList<>();

      public void addResolver(WadlGenerator.Resolver resolver) {
         assert !this.typeResolvers.contains(resolver) : "Already in list";

         this.typeResolvers.add(resolver);
      }

      public QName resolve(Class type) {
         QName name = null;

         for (WadlGenerator.Resolver resolver : this.typeResolvers) {
            name = resolver.resolve(type);
            if (name != null) {
               break;
            }
         }

         return name;
      }
   }

   public interface Resolver {
      QName resolve(Class var1);
   }
}
