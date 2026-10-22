package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "resourceDoc",
   propOrder = {"classDoc"}
)
@XmlRootElement(
   name = "resourceDoc"
)
public class ResourceDocType {
   @XmlElementWrapper(
      name = "classDocs"
   )
   protected List<ClassDocType> classDoc;

   public List<ClassDocType> getDocs() {
      if (this.classDoc == null) {
         this.classDoc = new ArrayList<>();
      }

      return this.classDoc;
   }
}
