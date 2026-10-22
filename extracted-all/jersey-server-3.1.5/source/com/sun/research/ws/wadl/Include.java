package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
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
   propOrder = {"doc"}
)
@XmlRootElement(
   name = "include"
)
public class Include {
   protected List<Doc> doc;
   @XmlAttribute(
      name = "href"
   )
   @XmlSchemaType(
      name = "anyURI"
   )
   protected String href;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap<>();

   public List<Doc> getDoc() {
      if (this.doc == null) {
         this.doc = new ArrayList<>();
      }

      return this.doc;
   }

   public String getHref() {
      return this.href;
   }

   public void setHref(String value) {
      this.href = value;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
