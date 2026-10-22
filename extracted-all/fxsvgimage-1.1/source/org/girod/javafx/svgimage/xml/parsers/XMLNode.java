package org.girod.javafx.svgimage.xml.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javafx.geometry.Bounds;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class XMLNode {
   protected String name = null;
   protected XMLNode nodeParent = null;
   private int index = -1;
   protected final List<XMLNode> children = new ArrayList<>();
   protected final Map<String, String> attributes = new TreeMap<>();
   private String cData = null;

   public XMLNode(String nodeName) {
      int comma = nodeName.indexOf(58);
      if (comma != -1) {
         this.name = nodeName.substring(comma + 1);
      } else {
         this.name = nodeName;
      }
   }

   public XMLNode(XMLNode parent, String nodeName) {
      int comma = nodeName.indexOf(58);
      if (comma != -1) {
         this.name = nodeName.substring(comma + 1);
      } else {
         this.name = nodeName;
      }

      this.nodeParent = parent;
      this.constructDefaultAttributes();
   }

   private void constructDefaultAttributes() {
      if (this.nodeParent != null) {
         for (String key : this.nodeParent.getAttributes().keySet()) {
            switch (key) {
               case "fill":
               case "stroke":
               case "font-size":
               case "font-family":
               case "font-weight":
               case "font-style":
               case "stroke-width":
               case "style":
                  String value = this.nodeParent.getAttributeValue(key);
                  this.attributes.put(key, value);
            }
         }
      }
   }

   public XMLNode getParent() {
      return this.nodeParent;
   }

   private void setParent(XMLNode parent, int index) {
      this.nodeParent = parent;
      this.index = index;
   }

   public String getName() {
      return this.name;
   }

   public List<XMLNode> getChildren() {
      return this.children;
   }

   public XMLNode getFirstChild() {
      return this.children.isEmpty() ? null : this.children.get(0);
   }

   public XMLNode getLastChild() {
      return this.children.isEmpty() ? null : this.children.get(this.children.size() - 1);
   }

   public XMLNode getNextSibling() {
      if (this.nodeParent == null) {
         return null;
      } else {
         List<XMLNode> siblings = this.nodeParent.getChildren();
         if (siblings.size() < 2) {
            return null;
         } else {
            int theIndex = siblings.indexOf(this);
            return siblings.size() > theIndex + 1 ? siblings.get(theIndex + 1) : null;
         }
      }
   }

   public XMLNode getPreviousSibling() {
      if (this.nodeParent == null) {
         return null;
      } else {
         List<XMLNode> siblings = this.nodeParent.getChildren();
         if (siblings.size() < 2) {
            return null;
         } else {
            int _index = siblings.indexOf(this);
            return _index > 0 ? siblings.get(_index - 1) : null;
         }
      }
   }

   public void addChild(XMLNode child) {
      child.setParent(this, this.children.size());
      this.children.add(child);
   }

   public boolean hasChildren() {
      return !this.children.isEmpty();
   }

   public int countChildren() {
      return this.children.size();
   }

   public int countAttributes() {
      return this.attributes.size();
   }

   public Map<String, String> getAttributes() {
      return this.attributes;
   }

   public String getAttributeValue(String attrName) {
      return this.attributes.get(attrName);
   }

   public boolean getAttributeValueAsBoolean(String attrName) {
      return this.getAttributeValueAsBoolean(attrName, false);
   }

   public boolean getAttributeValueAsBoolean(String attrName, boolean defaultValue) {
      if (this.attributes.containsKey(attrName)) {
         String attrvalue = this.attributes.get(attrName);

         try {
            return attrvalue.equals("true");
         } catch (NumberFormatException var5) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public float getAttributeValueAsFloat(String attrName) {
      return this.getAttributeValueAsFloat(attrName, 0.0F);
   }

   public float getAttributeValueAsFloat(String attrName, float defaultValue) {
      if (this.attributes.containsKey(attrName)) {
         String attrvalue = this.attributes.get(attrName);
         attrvalue = attrvalue.replace('−', '-');

         try {
            return Float.parseFloat(attrvalue);
         } catch (NumberFormatException var5) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public double getDoubleValue(String attrName) {
      return this.getLengthValue(attrName, null, 0.0);
   }

   public double getDoubleValue(String attrName, double defaultValue) {
      return this.getLengthValue(attrName, null, defaultValue);
   }

   public double getLengthValue(String attrName, Viewport viewport) {
      return this.getLengthValue(attrName, viewport, 0.0);
   }

   public double getPositionValue(String attrName, boolean isWidth, Viewport viewport) {
      return this.getPositionValue(attrName, isWidth, null, viewport, 0.0);
   }

   public double getPositionValue(String attrName, boolean isWidth, Bounds bounds, Viewport viewport) {
      return this.getPositionValue(attrName, isWidth, bounds, viewport, 0.0);
   }

   public double getLengthValue(String attrName, boolean isWidth, Viewport viewport) {
      return this.getLengthValue(attrName, isWidth, null, viewport, 0.0);
   }

   public double getLengthValue(String attrName, boolean isWidth, Bounds bounds, Viewport viewport) {
      return this.getLengthValue(attrName, isWidth, bounds, viewport, 0.0);
   }

   public double getLengthValue(String attrName, Viewport viewport, double defaultValue) {
      return this.getLengthValue(attrName, true, null, viewport, defaultValue);
   }

   public double getLineWidthValue(String attrName, Viewport viewport, double defaultValue) {
      if (this.attributes.containsKey(attrName)) {
         String attrvalue = this.attributes.get(attrName);
         attrvalue = attrvalue.replace('−', '-');

         try {
            return LengthParser.parseLineWidth(attrvalue, viewport);
         } catch (NumberFormatException var8) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public double getLengthValue(String attrName, boolean isWidth, Bounds bounds, Viewport viewport, double defaultValue) {
      if (this.attributes.containsKey(attrName)) {
         String attrvalue = this.attributes.get(attrName);
         attrvalue = attrvalue.replace('−', '-');

         try {
            return LengthParser.parseLength(attrvalue, isWidth, bounds, viewport);
         } catch (NumberFormatException var10) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public double getPositionValue(String attrName, boolean isWidth, Bounds bounds, Viewport viewport, double defaultValue) {
      if (this.attributes.containsKey(attrName)) {
         String attrvalue = this.attributes.get(attrName);
         attrvalue = attrvalue.replace('−', '-');

         try {
            return LengthParser.parsePosition(attrvalue, isWidth, bounds, viewport);
         } catch (NumberFormatException var10) {
            return viewport.getDefaultPosition(defaultValue, isWidth);
         }
      } else {
         return viewport.getDefaultPosition(defaultValue, isWidth);
      }
   }

   public int getAttributeValueAsInt(String attrName) {
      return this.getAttributeValueAsInt(attrName, 0);
   }

   public int getAttributeValueAsInt(String attrName, int defaultValue) {
      if (this.attributes.containsKey(attrName)) {
         String attrvalue = this.attributes.get(attrName);

         try {
            return Integer.parseInt(attrvalue);
         } catch (NumberFormatException var5) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   public void addAttribute(String attrName, int value) {
      this.attributes.put(attrName, Integer.toString(value));
   }

   public void addAttribute(String attrName, String value) {
      this.attributes.put(attrName, value);
   }

   public void addAttribute(String attrName, float value) {
      this.attributes.put(attrName, this.format(value));
   }

   public void addAttribute(String attrName, boolean value) {
      this.attributes.put(attrName, value ? "true" : "false");
   }

   public void setCDATA(String cData) {
      this.cData = cData;
   }

   public String getCDATA() {
      return this.cData;
   }

   public boolean hasCDATA() {
      return this.cData != null;
   }

   public boolean hasAttribute(String attrName) {
      return this.attributes.containsKey(attrName);
   }

   private String format(float f) {
      String s = String.format("%.1f", f);
      return s.replace(",", ".");
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 23 * hash + Objects.hashCode(this.name);
      hash = 23 * hash + Objects.hashCode(this.nodeParent);
      return 23 * hash + this.index;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         XMLNode other = (XMLNode)obj;
         if (this.index != other.index) {
            return false;
         } else {
            return !Objects.equals(this.name, other.name) ? false : Objects.equals(this.nodeParent, other.nodeParent);
         }
      }
   }
}
