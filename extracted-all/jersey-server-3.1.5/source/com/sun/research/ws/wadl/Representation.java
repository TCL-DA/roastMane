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
   propOrder = {"doc", "param", "any"}
)
@XmlRootElement(
   name = "representation"
)
public class Representation {
   protected List<Doc> doc;
   protected List<Param> param;
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
      name = "element"
   )
   protected QName element;
   @XmlAttribute(
      name = "mediaType"
   )
   protected String mediaType;
   @XmlAttribute(
      name = "href"
   )
   @XmlSchemaType(
      name = "anyURI"
   )
   protected String href;
   @XmlAttribute(
      name = "profile"
   )
   protected List<String> profile;
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

   public QName getElement() {
      return this.element;
   }

   public void setElement(QName value) {
      this.element = value;
   }

   public String getMediaType() {
      return this.mediaType;
   }

   public void setMediaType(String value) {
      this.mediaType = value;
   }

   public String getHref() {
      return this.href;
   }

   public void setHref(String value) {
      this.href = value;
   }

   public List<String> getProfile() {
      if (this.profile == null) {
         this.profile = new ArrayList<>();
      }

      return this.profile;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
