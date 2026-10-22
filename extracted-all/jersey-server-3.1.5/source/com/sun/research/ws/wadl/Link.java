package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"doc", "any"}
)
@XmlRootElement(
   name = "link"
)
public class Link {
   protected List<Doc> doc;
   @XmlAnyElement(
      lax = true
   )
   protected List<Object> any;
   @XmlAttribute(
      name = "resource_type"
   )
   @XmlSchemaType(
      name = "anyURI"
   )
   protected String resourceType;
   @XmlAttribute(
      name = "rel"
   )
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   @XmlSchemaType(
      name = "token"
   )
   protected String rel;
   @XmlAttribute(
      name = "rev"
   )
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   @XmlSchemaType(
      name = "token"
   )
   protected String rev;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap<>();

   public List<Doc> getDoc() {
      if (this.doc == null) {
         this.doc = new ArrayList<>();
      }

      return this.doc;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList<>();
      }

      return this.any;
   }

   public String getResourceType() {
      return this.resourceType;
   }

   public void setResourceType(String value) {
      this.resourceType = value;
   }

   public String getRel() {
      return this.rel;
   }

   public void setRel(String value) {
      this.rel = value;
   }

   public String getRev() {
      return this.rev;
   }

   public void setRev(String value) {
      this.rev = value;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
