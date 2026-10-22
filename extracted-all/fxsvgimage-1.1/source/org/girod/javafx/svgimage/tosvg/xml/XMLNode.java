package org.girod.javafx.svgimage.tosvg.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Map.Entry;

public class XMLNode {
   protected String name = null;
   protected XMLNode nodeParent = null;
   private int index = -1;
   protected final List<XMLNode> children = new ArrayList<>();
   protected final Map<String, String> attributes = new TreeMap<>();
   private String cData = null;

   public XMLNode(String nodeName) {
      this.name = nodeName;
   }

   public XMLNode(XMLNode parent, String nodeName) {
      this.name = nodeName;
      this.nodeParent = parent;
   }

   XMLNode createCopyInstanceImpl() {
      return new XMLNode(this.nodeParent, this.name);
   }

   public XMLNode copy() {
      XMLNode node = this.createCopyInstanceImpl();
      node.cData = this.cData;

      for (Entry<String, String> entry : this.attributes.entrySet()) {
         node.attributes.put(entry.getKey(), entry.getValue());
      }

      Iterator<XMLNode> it2 = this.children.iterator();

      for (int index = 0; it2.hasNext(); index++) {
         XMLNode child = it2.next();
         child = child.copy();
         child.setParent(node, index);
         node.children.add(child);
      }

      return node;
   }

   public List<XMLNode> getAllChildren(String name) {
      List<XMLNode> list = new ArrayList<>();

      for (XMLNode node : this.children) {
         if (node.getName().equals(name)) {
            list.add(node);
         }

         List<XMLNode> childList = node.getAllChildren(name);
         list.addAll(childList);
      }

      return list;
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
            int index = siblings.indexOf(this);
            return siblings.size() > index + 1 ? siblings.get(index + 1) : null;
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
            int index = siblings.indexOf(this);
            return index > 0 ? siblings.get(index - 1) : null;
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

         try {
            return Float.parseFloat(attrvalue);
         } catch (NumberFormatException var5) {
            return defaultValue;
         }
      } else {
         return defaultValue;
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

   public void addAttribute(String attrName, double value) {
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

   private String format(double f) {
      String s = String.format("%.1f", f);
      return s.replace(",", ".");
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

   @Override
   public String toString() {
      return XMLNodeUtilities.print(this, 2);
   }
}
