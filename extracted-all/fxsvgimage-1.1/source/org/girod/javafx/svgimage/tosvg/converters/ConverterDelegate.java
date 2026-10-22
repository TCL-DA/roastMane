package org.girod.javafx.svgimage.tosvg.converters;

import java.io.File;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;
import org.girod.javafx.svgimage.tosvg.xml.XMLRoot;

public class ConverterDelegate {
   private File file = null;
   private Node root = null;
   private final ClipConstructor clipConstructor = new ClipConstructor();
   private XMLNode defsNode = null;

   public void setSVGFile(File file) {
      this.file = file;
   }

   public File getSVGFile() {
      return this.file;
   }

   public XMLNode getDefsNode() {
      return this.defsNode;
   }

   public Node getRoot() {
      return this.root;
   }

   private String applyClip(Node node) {
      Node clip = node.getClip();
      if (clip != null) {
         XMLNode xmlClip = this.clipConstructor.createClip(clip);
         if (xmlClip != null) {
            String clipID = this.clipConstructor.getClipID();
            XMLNode clipPath = new XMLNode("clipPath");
            clipPath.addAttribute("id", clipID);
            this.defsNode.addChild(clipPath);
            clipPath.addChild(xmlClip);
            return clipID;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void convertRoot(Node root, XMLRoot xmlRoot) {
      this.root = root;
      this.defsNode = new XMLNode("defs");
      xmlRoot.addChild(this.defsNode);
      AbstractConverter conv = this.getConverter(root, xmlRoot);
      if (conv != null) {
         conv.applyTransforms(xmlRoot);
         XMLNode xmlNode = conv.convert();
         if (xmlNode != null) {
            String clipID = this.applyClip(root);
            conv.applyStyle(xmlNode, clipID);
         }

         if (root instanceof Parent) {
            Parent parent = (Parent)root;

            for (Node child : parent.getChildrenUnmodifiable()) {
               if (child.isVisible()) {
                  xmlNode = this.convert(child, xmlRoot);
                  if (xmlNode != null) {
                     String clipID = this.applyClip(child);
                     conv.applyStyle(xmlNode, clipID);
                  }
               }
            }
         }
      }
   }

   private AbstractConverter getConverter(Node node, XMLNode xmlParent) {
      AbstractConverter conv = null;
      if (node instanceof Shape) {
         Shape shape = (Shape)node;
         conv = new ShapeConverter(this, shape, xmlParent);
      } else if (node instanceof Control) {
         Control control = (Control)node;
         conv = new ControlConverter(this, control, xmlParent);
      } else if (node instanceof Region) {
         Region region = (Region)node;
         conv = new RegionConverter(this, region, xmlParent);
      } else if (node instanceof ImageView) {
         ImageView view = (ImageView)node;
         conv = new ImageViewConverter(this, view, xmlParent);
      } else if (node instanceof Group) {
         Group group = (Group)node;
         conv = new GroupConverter(this, group, xmlParent);
      } else if (node instanceof SubScene) {
         SubScene subScene = (SubScene)node;
         conv = new SubSceneConverter(this, subScene, xmlParent);
      } else if (node instanceof Shape3D) {
         Shape3D shape = (Shape3D)node;
         conv = new Shape3DConverter(this, shape, xmlParent);
      }

      return conv;
   }

   private XMLNode convert(Node node, XMLNode xmlParent) {
      AbstractConverter conv = this.getConverter(node, xmlParent);
      if (conv != null) {
         boolean isVisible = node.isVisible();
         if (conv.hasVisibility()) {
            if (!conv.isVisible()) {
               return null;
            }

            isVisible = true;
         }

         if (isVisible) {
            XMLNode xmlNode = conv.convert();
            conv.applyTransforms(xmlNode);
            String clipID = this.applyClip(node);
            conv.applyStyle(xmlNode, clipID);
            Node additionalNode = conv.getAdditionalNode();
            if (additionalNode != null && additionalNode.isVisible()) {
               this.convert(additionalNode, xmlParent);
            }

            Parent parent = conv.getParent();
            if (parent != null) {
               for (Node child : parent.getChildrenUnmodifiable()) {
                  this.convert(child, xmlNode);
               }
            }

            return xmlNode;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
