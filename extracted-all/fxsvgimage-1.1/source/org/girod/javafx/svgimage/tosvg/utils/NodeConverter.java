package org.girod.javafx.svgimage.tosvg.utils;

import java.util.Map;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public interface NodeConverter {
   XMLNode convert();

   Map<String, CSSProperty> getCSSProperties();

   Map<String, Object> getAllProperties();

   Map<String, Object> getProperties();

   void applyStyle(XMLNode var1, String var2);
}
