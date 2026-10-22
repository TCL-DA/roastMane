package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.xhtml;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class Elements extends JAXBElement<XhtmlElementType> {
   private static final long serialVersionUID = 1L;

   public static Elements el(String elementName) {
      return createElement(elementName);
   }

   public static Object val(String elementName, String value) {
      return createElement(elementName, value);
   }

   public Elements(QName name, Class<XhtmlElementType> clazz, XhtmlElementType element) {
      super(name, clazz, element);
   }

   public Elements add(Object... childNodes) {
      if (childNodes != null) {
         for (Object childNode : childNodes) {
            ((XhtmlElementType)this.getValue()).getChildNodes().add(childNode);
         }
      }

      return this;
   }

   public Elements addChild(Object child) {
      ((XhtmlElementType)this.getValue()).getChildNodes().add(child);
      return this;
   }

   private static Elements createElement(String elementName) {
      try {
         XhtmlElementType element = new XhtmlElementType();
         return new Elements(new QName("http://www.w3.org/1999/xhtml", elementName), XhtmlElementType.class, element);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   private static JAXBElement<XhtmlValueType> createElement(String elementName, String value) {
      try {
         XhtmlValueType element = new XhtmlValueType();
         element.value = value;
         return new JAXBElement(new QName("http://www.w3.org/1999/xhtml", elementName), XhtmlValueType.class, element);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }
}
