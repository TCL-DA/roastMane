package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"doc", "resource", "any"}
)
@XmlRootElement(
   name = "resources"
)
public class Resources {
   protected List<Doc> doc;
   @XmlElement(
      required = true
   )
   protected List<Resource> resource;
   @XmlAnyElement(
      lax = true
   )
   protected List<Object> any;
   @XmlAttribute(
      name = "base"
   )
   @XmlSchemaType(
      name = "anyURI"
   )
   protected String base;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap<>();

   public List<Doc> getDoc() {
      if (this.doc == null) {
         this.doc = new ArrayList<>();
      }

      return this.doc;
   }

   public List<Resource> getResource() {
      if (this.resource == null) {
         this.resource = new ArrayList<>();
      }

      return this.resource;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList<>();
      }

      return this.any;
   }

   public String getBase() {
      return this.base;
   }

   public void setBase(String value) {
      this.base = value;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
