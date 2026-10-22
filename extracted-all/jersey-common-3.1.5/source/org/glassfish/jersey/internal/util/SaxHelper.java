package org.glassfish.jersey.internal.util;

import java.security.AccessController;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

public final class SaxHelper {
   private SaxHelper() {
   }

   public static boolean isXdkParserFactory(SAXParserFactory parserFactory) {
      return isXdkFactory(parserFactory, "oracle.xml.jaxp.JXSAXParserFactory");
   }

   public static boolean isXdkDocumentBuilderFactory(DocumentBuilderFactory builderFactory) {
      return isXdkFactory(builderFactory, "oracle.xml.jaxp.JXDocumentBuilderFactory");
   }

   private static boolean isXdkFactory(Object factory, String className) {
      Class<?> xdkFactoryClass = AccessController.doPrivileged(ReflectionHelper.classForNamePA(className, null));
      return xdkFactoryClass == null ? false : xdkFactoryClass.isAssignableFrom(factory.getClass());
   }
}
