package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "responseDoc",
   propOrder = {}
)
public class ResponseDocType {
   private String returnDoc;
   @XmlElementWrapper(
      name = "wadlParams"
   )
   protected List<WadlParamType> wadlParam;
   @XmlElementWrapper(
      name = "representations"
   )
   protected List<RepresentationDocType> representation;

   public List<WadlParamType> getWadlParams() {
      if (this.wadlParam == null) {
         this.wadlParam = new ArrayList<>();
      }

      return this.wadlParam;
   }

   public List<RepresentationDocType> getRepresentations() {
      if (this.representation == null) {
         this.representation = new ArrayList<>();
      }

      return this.representation;
   }

   public boolean hasRepresentations() {
      return this.representation != null && !this.representation.isEmpty();
   }

   public String getReturnDoc() {
      return this.returnDoc;
   }

   public void setReturnDoc(String returnDoc) {
      this.returnDoc = returnDoc;
   }
}
