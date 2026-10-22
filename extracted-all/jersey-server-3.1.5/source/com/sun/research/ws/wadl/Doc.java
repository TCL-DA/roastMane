package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlMixed;
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
   propOrder = {"content"}
)
@XmlRootElement(
   name = "doc"
)
public class Doc {
   @XmlMixed
   @XmlAnyElement(
      lax = true
   )
   protected List<Object> content;
   @XmlAttribute(
      name = "title"
   )
   protected String title;
   @XmlAttribute(
      name = "lang",
      namespace = "http://www.w3.org/XML/1998/namespace"
   )
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   @XmlSchemaType(
      name = "language"
   )
   protected String lang;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap<>();

   public List<Object> getContent() {
      if (this.content == null) {
         this.content = new ArrayList<>();
      }

      return this.content;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String value) {
      this.title = value;
   }

   public String getLang() {
      return this.lang;
   }

   public void setLang(String value) {
      this.lang = value;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
