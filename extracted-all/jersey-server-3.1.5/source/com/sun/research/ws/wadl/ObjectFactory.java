package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
   public Resources createResources() {
      return new Resources();
   }

   public Doc createDoc() {
      return new Doc();
   }

   public Resource createResource() {
      return new Resource();
   }

   public Param createParam() {
      return new Param();
   }

   public Option createOption() {
      return new Option();
   }

   public Link createLink() {
      return new Link();
   }

   public Method createMethod() {
      return new Method();
   }

   public Request createRequest() {
      return new Request();
   }

   public Representation createRepresentation() {
      return new Representation();
   }

   public Response createResponse() {
      return new Response();
   }

   public Application createApplication() {
      return new Application();
   }

   public Grammars createGrammars() {
      return new Grammars();
   }

   public Include createInclude() {
      return new Include();
   }

   public ResourceType createResourceType() {
      return new ResourceType();
   }
}
