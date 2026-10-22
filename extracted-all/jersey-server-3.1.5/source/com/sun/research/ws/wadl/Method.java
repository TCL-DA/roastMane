package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
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
   propOrder = {"doc", "request", "response", "any"}
)
@XmlRootElement(
   name = "method"
)
public class Method {
   protected List<Doc> doc;
   protected Request request;
   protected List<Response> response;
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
   @XmlAttribute(
      name = "name"
   )
   protected String name;
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

   public Request getRequest() {
      return this.request;
   }

   public void setRequest(Request value) {
      this.request = value;
   }

   public List<Response> getResponse() {
      if (this.response == null) {
         this.response = new ArrayList<>();
      }

      return this.response;
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

   public String getName() {
      return this.name;
   }

   public void setName(String value) {
      this.name = value;
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
