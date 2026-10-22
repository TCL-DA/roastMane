package org.girod.javafx.svgimage.tosvg.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class XMLNodeUtilities {
   private XMLNodeUtilities() {
   }

   private static void writeEncoding(StringBuilder buf, String encoding) {
      if (encoding != null) {
         buf.append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\"?>");
         buf.append("\n");
      }
   }

   private static void writeEncoding(BufferedWriter writer, String encoding) throws IOException {
      if (encoding != null) {
         writer.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");
         writer.newLine();
      }
   }

   public static void print(XMLNode node, int tab, File outputFile) throws IOException {
      print(node, tab, outputFile, null);
   }

   public static void print(XMLNode node, int tab, File outputFile, String encoding) throws IOException {
      char[] chars = new char[tab];
      Arrays.fill(chars, ' ');
      String tabS = new String(chars);

      try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
         if (node instanceof XMLRoot && encoding == null) {
            XMLRoot root = (XMLRoot)node;
            encoding = root.getEncoding();
         }

         if (encoding != null) {
            writeEncoding(writer, encoding);
         }

         printNode(writer, node, "", tabS, false, true);
         writer.flush();
      }
   }

   public static void print(XMLNode node, int tab, URL outputURL) throws IOException {
      File file = new File(outputURL.getFile());
      print(node, tab, file);
   }

   private static void printNode(BufferedWriter writer, XMLNode node, String tabTotal, String tab, boolean isFirst, boolean isLast) throws IOException {
      boolean parentHasCData = false;
      XMLNode parent = node.getParent();
      if (parent != null) {
         parentHasCData = parent.hasCDATA();
      }

      if (!parentHasCData | (parentHasCData && !isFirst)) {
         writer.write(tabTotal);
      }

      writer.write("<");
      writer.write(node.getName());

      for (Entry<String, String> entry : node.getAttributes().entrySet()) {
         writer.write(" ");
         writer.write(entry.getKey());
         writer.write("=\"");
         writer.write(entry.getValue());
         writer.write("\"");
      }

      if (node.hasChildren()) {
         writer.write(">");
         boolean hasCData = false;
         if (node.getCDATA() != null) {
            writer.write(node.getCDATA());
            hasCData = true;
         } else {
            writer.newLine();
         }

         String tabChildren = tabTotal + tab;
         List<XMLNode> children = node.getChildren();
         boolean hasMoreThanOneChild = children.size() > 1;
         boolean isFirstForParent = true;

         for (XMLNode child : children) {
            printNode(writer, child, tabChildren, tab, isFirstForParent, false);
            isFirstForParent = false;
         }

         if (!hasCData || hasMoreThanOneChild) {
            writer.write(tabTotal);
         }

         writer.write("</");
         writer.write(node.getName());
         writer.write(">");
         if (!isLast) {
            writer.newLine();
         }
      } else {
         if (node.getCDATA() != null) {
            writer.write(">");
            writer.write(node.getCDATA());
            writer.write("</");
            writer.write(node.getName());
            writer.write(">");
         } else {
            writer.write("/>");
         }

         if (!isLast) {
            writer.newLine();
         }
      }
   }

   public static String print(XMLNode node, int tab) {
      return print(node, tab, (String)null);
   }

   public static String print(XMLNode node, int tab, String encoding) {
      char[] chars = new char[tab];
      Arrays.fill(chars, ' ');
      String tabS = new String(chars);
      StringBuilder buf = new StringBuilder();
      if (node instanceof XMLRoot && encoding == null) {
         XMLRoot root = (XMLRoot)node;
         encoding = root.getEncoding();
      }

      if (encoding != null) {
         writeEncoding(buf, encoding);
      }

      printNode(buf, node, "", tabS, true, true);
      return buf.toString();
   }

   private static void printNode(StringBuilder buf, XMLNode node, String tabTotal, String tab, boolean isFirst, boolean isLast) {
      boolean parentHasCData = false;
      XMLNode parent = node.getParent();
      if (parent != null) {
         parentHasCData = parent.hasCDATA();
      }

      if (!parentHasCData | (parentHasCData && !isFirst)) {
         buf.append(tabTotal);
      }

      buf.append("<").append(node.getName());

      for (Entry<String, String> entry : node.getAttributes().entrySet()) {
         buf.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
      }

      if (node.hasChildren()) {
         if (node.hasCDATA()) {
            buf.append(">").append(node.getCDATA());
         } else {
            buf.append(">\n");
         }

         String tabChildren = tabTotal + tab;
         boolean isFirstInParent = true;

         for (XMLNode child : node.getChildren()) {
            printNode(buf, child, tabChildren, tab, isFirstInParent, false);
            isFirstInParent = false;
         }

         if (node.hasCDATA()) {
            buf.append(tabTotal);
         }

         buf.append("</").append(node.getName()).append(">");
         if (!isLast) {
            buf.append("\n");
         }
      } else {
         if (node.getCDATA() != null) {
            buf.append(">").append(node.getCDATA());
            buf.append("</").append(node.getName()).append(">");
         } else {
            buf.append("/>");
         }

         if (!isLast) {
            buf.append("\n");
         }
      }
   }
}
