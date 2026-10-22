package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(
   name = "ParamStyle"
)
@XmlEnum
public enum ParamStyle {
   @XmlEnumValue("plain")
   PLAIN("plain"),
   @XmlEnumValue("query")
   QUERY("query"),
   @XmlEnumValue("matrix")
   MATRIX("matrix"),
   @XmlEnumValue("header")
   HEADER("header"),
   @XmlEnumValue("template")
   TEMPLATE("template");

   private final String value;

   private ParamStyle(String v) {
      this.value = v;
   }

   public String value() {
      return this.value;
   }

   public static ParamStyle fromValue(String v) {
      for (ParamStyle c : values()) {
         if (c.value.equals(v)) {
            return c;
         }
      }

      throw new IllegalArgumentException(v);
   }
}
