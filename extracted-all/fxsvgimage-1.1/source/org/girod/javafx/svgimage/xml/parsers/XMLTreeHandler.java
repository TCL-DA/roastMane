package org.girod.javafx.svgimage.xml.parsers;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class XMLTreeHandler extends DefaultHandler2 implements SVGTags {
   private XMLNode node = null;
   private final Stack<XMLNode> nodes = new Stack<>();
   private XMLRoot root = null;
   private String encoding = null;
   private StringBuilder buf = null;

   public XMLTreeHandler() {
   }

   public XMLTreeHandler(String encoding) {
      this.encoding = encoding;
   }

   public XMLRoot getRoot() {
      return this.root;
   }

   @Override
   public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException {
      this.parseElement(qname, attr);
   }

   @Override
   public void endElement(String uri, String localname, String qname) {
      if (!this.nodes.empty()) {
         this.node = this.nodes.pop();
         if (this.buf != null) {
            String cdata = this.buf.toString();
            if (!cdata.trim().isEmpty()) {
               this.node.setCDATA(cdata);
            }

            this.buf = null;
         }

         this.node = this.node.getParent();
      }
   }

   @Override
   public void characters(char[] characters, int start, int length) {
      if (this.buf != null) {
         this.buf.append(characters, start, length);
      }
   }

   private void parseElement(String qname, Attributes attr) {
      if (this.buf != null && this.node != null) {
         String cdata = this.buf.toString();
         if (!cdata.trim().isEmpty()) {
            this.node.setCDATA(cdata);
         }

         this.buf = null;
      }

      XMLNode childNode;
      if (this.node == null) {
         this.root = new XMLRoot(qname);
         this.root.setEncoding(this.encoding);
         childNode = this.root;
      } else {
         childNode = new XMLNode(this.node, qname);
      }

      this.buf = new StringBuilder();
      if (this.node != null) {
         this.node.addChild(childNode);
      }

      for (int i = 0; i < attr.getLength(); i++) {
         String attrname = attr.getQName(i);
         String attrvalue = attr.getValue(i);
         childNode.addAttribute(attrname, attrvalue);
      }

      if (this.node != null) {
         ParserUtils.propagateStyleAttributes(this.node, childNode);
      }

      this.nodes.push(childNode);
      this.node = childNode;
   }
}
