package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlID;
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
   propOrder = {"doc", "param", "methodOrResource", "any"}
)
@XmlRootElement(
   name = "resource_type"
)
public class ResourceType {
   protected List<Doc> doc;
   protected List<Param> param;
   @XmlElements({@XmlElement(
         name = "method",
         type = Method.class
      ), @XmlElement(
         name = "resource",
         type = Resource.class
      )})
   protected List<Object> methodOrResource;
   @XmlAnyElement(
      lax = true
   )
   protected List<Object> any;
   @XmlAttribute(
      name = "id"
   )
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   @XmlID
   @XmlSchemaType(
      name = "ID"
   )
   protected String id;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap<>();

   public List<Doc> getDoc() {
      if (this.doc == null) {
         this.doc = new ArrayList<>();
      }

      return this.doc;
   }

   public List<Param> getParam() {
      if (this.param == null) {
         this.param = new ArrayList<>();
      }

      return this.param;
   }

   public List<Object> getMethodOrResource() {
      if (this.methodOrResource == null) {
         this.methodOrResource = new ArrayList<>();
      }

      return this.methodOrResource;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList<>();
      }

      return this.any;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String value) {
      this.id = value;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
