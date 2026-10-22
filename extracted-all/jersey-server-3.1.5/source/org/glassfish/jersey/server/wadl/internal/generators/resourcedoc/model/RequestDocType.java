package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "requestDoc",
   propOrder = {}
)
public class RequestDocType {
   private RepresentationDocType representationDoc;

   public RepresentationDocType getRepresentationDoc() {
      return this.representationDoc;
   }

   public void setRepresentationDoc(RepresentationDocType representationDoc) {
      this.representationDoc = representationDoc;
   }
}
