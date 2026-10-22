package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "representationDoc",
   propOrder = {}
)
public class RepresentationDocType {
   @XmlAttribute
   private QName element;
   private String example;
   @XmlAttribute
   private Long status;
   @XmlAttribute
   private String mediaType;
   private String doc;

   public QName getElement() {
      return this.element;
   }

   public void setElement(QName element) {
      this.element = element;
   }

   public String getExample() {
      return this.example;
   }

   public void setExample(String example) {
      this.example = example;
   }

   public Long getStatus() {
      return this.status;
   }

   public void setStatus(Long status) {
      this.status = status;
   }

   public String getMediaType() {
      return this.mediaType;
   }

   public void setMediaType(String mediaType) {
      this.mediaType = mediaType;
   }

   public String getDoc() {
      return this.doc;
   }

   public void setDoc(String doc) {
      this.doc = doc;
   }
}
