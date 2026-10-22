package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.xhtml;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
   public XhtmlElementType createXhtmlElementType() {
      return new XhtmlElementType();
   }

   public XhtmlValueType createXhtmlCodeType() {
      return new XhtmlValueType();
   }
}
