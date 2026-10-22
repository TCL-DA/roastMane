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
   propOrder = {"doc", "option", "link", "any"}
)
@XmlRootElement(
   name = "param"
)
public class Param {
   protected List<Doc> doc;
   protected List<Option> option;
   protected Link link;
   @XmlAnyElement(
      lax = true
   )
   protected List<Object> any;
   @XmlAttribute(
      name = "href"
   )
   @XmlSchemaType(
      name = "anyURI"
   )
   protected String href;
   @XmlAttribute(
      name = "name"
   )
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   @XmlSchemaType(
      name = "NMTOKEN"
   )
   protected String name;
   @XmlAttribute(
      name = "style"
   )
   protected ParamStyle style;
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
      name = "type"
   )
   protected QName type;
   @XmlAttribute(
      name = "default"
   )
   protected String _default;
   @XmlAttribute(
      name = "required"
   )
   protected Boolean required;
   @XmlAttribute(
      name = "repeating"
   )
   protected Boolean repeating;
   @XmlAttribute(
      name = "fixed"
   )
   protected String fixed;
   @XmlAttribute(
      name = "path"
   )
   protected String path;
   @XmlAnyAttribute
   private Map<QName, String> otherAttributes = new HashMap<>();

   public List<Doc> getDoc() {
      if (this.doc == null) {
         this.doc = new ArrayList<>();
      }

      return this.doc;
   }

   public List<Option> getOption() {
      if (this.option == null) {
         this.option = new ArrayList<>();
      }

      return this.option;
   }

   public Link getLink() {
      return this.link;
   }

   public void setLink(Link value) {
      this.link = value;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList<>();
      }

      return this.any;
   }

   public String getHref() {
      return this.href;
   }

   public void setHref(String value) {
      this.href = value;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String value) {
      this.name = value;
   }

   public ParamStyle getStyle() {
      return this.style;
   }

   public void setStyle(ParamStyle value) {
      this.style = value;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String value) {
      this.id = value;
   }

   public QName getType() {
      return this.type == null ? new QName("http://www.w3.org/2001/XMLSchema", "string", "xs") : this.type;
   }

   public void setType(QName value) {
      this.type = value;
   }

   public String getDefault() {
      return this._default;
   }

   public void setDefault(String value) {
      this._default = value;
   }

   public boolean isRequired() {
      return this.required == null ? false : this.required;
   }

   public void setRequired(Boolean value) {
      this.required = value;
   }

   public boolean isRepeating() {
      return this.repeating == null ? false : this.repeating;
   }

   public void setRepeating(Boolean value) {
      this.repeating = value;
   }

   public String getFixed() {
      return this.fixed;
   }

   public void setFixed(String value) {
      this.fixed = value;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String value) {
      this.path = value;
   }

   public Map<QName, String> getOtherAttributes() {
      return this.otherAttributes;
   }
}
