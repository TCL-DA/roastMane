package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "annotationDoc",
   propOrder = {}
)
public class AnnotationDocType {
   private String annotationTypeName;
   @XmlElementWrapper(
      name = "attributes"
   )
   protected List<NamedValueType> attribute;

   public List<NamedValueType> getAttributeDocs() {
      if (this.attribute == null) {
         this.attribute = new ArrayList<>();
      }

      return this.attribute;
   }

   public boolean hasAttributeDocs() {
      return this.attribute != null && !this.attribute.isEmpty();
   }

   public String getAnnotationTypeName() {
      return this.annotationTypeName;
   }

   public void setAnnotationTypeName(String annotationTypeName) {
      this.annotationTypeName = annotationTypeName;
   }
}
