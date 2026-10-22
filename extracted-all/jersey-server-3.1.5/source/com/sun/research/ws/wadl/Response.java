package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "",
   propOrder = {"doc", "param", "representation", "any"}
)
@XmlRootElement(
   name = "response"
)
public class Response {
   protected List<Doc> doc;
   protected List<Param> param;
   protected List<Representation> representation;
   @XmlAnyElement(
      lax = true
   )
   protected List<Object> any;
   @XmlAttribute(
      name = "status"
   )
   protected List<Long> status;
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

   public List<Representation> getRepresentation() {
      if (this.representation == null) {
         this.representation = new ArrayList<>();
      }

      return this.representation;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList<>();
      }

      return this.any;
   }

   public List<Long> getStatus() {
      if (this.status == null) {
         this.status = new ArrayList<>();
      }

      return this.status;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
