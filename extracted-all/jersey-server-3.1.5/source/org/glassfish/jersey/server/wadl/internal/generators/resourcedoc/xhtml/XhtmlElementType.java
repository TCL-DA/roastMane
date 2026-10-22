package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.xhtml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "foo",
   propOrder = {}
)
@XmlRootElement(
   name = "foo"
)
public class XhtmlElementType {
   @XmlAnyElement
   protected List<Object> any;

   public List<Object> getChildNodes() {
      if (this.any == null) {
         this.any = new ArrayList<>();
      }

      return this.any;
   }
}
