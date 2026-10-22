package com.sun.research.ws.wadl;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(
   name = "HTTPMethods"
)
@XmlEnum
public enum HTTPMethods {
   GET,
   POST,
   PUT,
   HEAD,
   DELETE;

   public String value() {
      return this.name();
   }

   public static HTTPMethods fromValue(String v) {
      return valueOf(v);
   }
}
