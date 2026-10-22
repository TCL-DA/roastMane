package org.girod.javafx.svgimage.xml.parsers;

import java.util.Objects;

public class XMLRoot extends XMLNode {
   private String encoding = null;

   public XMLRoot(String nodeName) {
      super(nodeName);
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public String getEncoding() {
      return this.encoding;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof XMLNode)) {
         return false;
      } else if (o.getClass() != XMLRoot.class) {
         return false;
      } else {
         XMLRoot node = (XMLRoot)o;
         if (!node.getName().equals(this.name)) {
            return false;
         } else if (node.encoding == null && this.encoding != null) {
            return false;
         } else if (node.encoding != null && this.encoding == null) {
            return false;
         } else if (this.encoding != null && !node.encoding.equals(this.encoding)) {
            return false;
         } else if (node.getAttributes().size() != this.attributes.size()) {
            return false;
         } else {
            for (String key : node.getAttributes().keySet()) {
               if (!this.attributes.containsKey(key)) {
                  return false;
               }

               String value = node.getAttributes().get(key);
               String value2 = this.attributes.get(key);
               if (!value.equals(value2)) {
                  return false;
               }
            }

            if (node.getChildren().size() != this.children.size()) {
               return false;
            } else {
               for (int i = 0; i < node.getChildren().size(); i++) {
                  XMLNode child = node.getChildren().get(i);
                  XMLNode child2 = this.children.get(i);
                  if (!child.equals(child2)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 59 * hash + Objects.hashCode(this.encoding);
      hash = 59 * hash + Objects.hashCode(this.name);
      hash = 59 * hash + Objects.hashCode(this.children);
      return 59 * hash + Objects.hashCode(this.attributes);
   }
}
