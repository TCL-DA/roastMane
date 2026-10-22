package org.glassfish.jersey.server.wadl.internal.generators;

import com.sun.research.ws.wadl.Doc;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "applicationDocs",
   propOrder = {"docs"}
)
@XmlRootElement(
   name = "applicationDocs"
)
public class ApplicationDocs {
   @XmlElement(
      name = "doc"
   )
   protected List<Doc> docs;

   public List<Doc> getDocs() {
      if (this.docs == null) {
         this.docs = new ArrayList<>();
      }

      return this.docs;
   }
}
