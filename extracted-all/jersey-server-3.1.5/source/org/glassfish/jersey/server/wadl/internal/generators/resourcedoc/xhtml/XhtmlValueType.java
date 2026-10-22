package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.xhtml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "valueType",
   propOrder = {}
)
@XmlRootElement(
   name = "valueType"
)
public class XhtmlValueType {
   @XmlValue
   protected String value;
}
