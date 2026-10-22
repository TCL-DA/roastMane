package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "wadlParam",
   propOrder = {}
)
public class WadlParamType {
   @XmlAttribute
   private String name;
   @XmlAttribute
   private String style;
   @XmlAttribute
   private QName type;
   private String doc;

   public String getDoc() {
      return this.doc;
   }

   public void setDoc(String commentText) {
      this.doc = commentText;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String paramName) {
      this.name = paramName;
   }

   public String getStyle() {
      return this.style;
   }

   public void setStyle(String style) {
      this.style = style;
   }

   public QName getType() {
      return this.type;
   }

   public void setType(QName type) {
      this.type = type;
   }
}
