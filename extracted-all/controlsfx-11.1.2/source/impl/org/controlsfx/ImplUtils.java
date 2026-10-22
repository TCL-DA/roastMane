package impl.org.controlsfx;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class ImplUtils {
   private ImplUtils() {
   }

   public static void injectAsRootPane(Scene scene, Parent injectedParent, boolean useReflection) {
      Parent originalParent = scene.getRoot();
      scene.setRoot(injectedParent);
      if (injectedParent instanceof Region) {
         Region region = (Region)injectedParent;
         region.setMaxWidth(Double.MAX_VALUE);
         region.setMaxHeight(Double.MAX_VALUE);
      }

      if (originalParent != null) {
         getChildren(injectedParent, useReflection).add(0, originalParent);
         injectedParent.getProperties().putAll(originalParent.getProperties());
      }
   }

   public static void injectPane(Parent parent, Parent injectedParent, boolean useReflection) {
      if (parent == null) {
         throw new IllegalArgumentException("parent can not be null");
      } else {
         List<Node> ownerParentChildren = getChildren(parent.getParent(), useReflection);
         int ownerPos = ownerParentChildren.indexOf(parent);
         ownerParentChildren.remove(ownerPos);
         ownerParentChildren.add(ownerPos, injectedParent);
         getChildren(injectedParent, useReflection).add(0, parent);
         injectedParent.getProperties().putAll(parent.getProperties());
      }
   }

   public static void stripRootPane(Scene scene, Parent originalParent, boolean useReflection) {
      Parent oldParent = scene.getRoot();
      getChildren(oldParent, useReflection).remove(originalParent);
      originalParent.getStyleClass().remove("root");
      scene.setRoot(originalParent);
   }

   public static List<Node> getChildren(Node n, boolean useReflection) {
      return n instanceof Parent ? getChildren((Parent)n, useReflection) : Collections.emptyList();
   }

   public static List<Node> getChildren(Parent p, boolean useReflection) {
      ObservableList<Node> children = null;
      if (p instanceof Pane) {
         children = ((Pane)p).getChildren();
      } else if (p instanceof Group) {
         children = ((Group)p).getChildren();
      } else if (p instanceof Control) {
         Control c = (Control)p;
         Skin<?> s = c.getSkin();
         children = s instanceof SkinBase ? ((SkinBase)s).getChildren() : getChildrenReflectively(p);
      } else if (useReflection) {
         children = getChildrenReflectively(p);
      }

      if (children == null) {
         throw new RuntimeException("Unable to get children for Parent of type " + p.getClass() + ". useReflection is set to " + useReflection);
      } else {
         return children == null ? FXCollections.emptyObservableList() : children;
      }
   }

   public static ObservableList<Node> getChildrenReflectively(Parent p) {
      ObservableList<Node> children = null;

      try {
         Method getChildrenMethod = Parent.class.getDeclaredMethod("getChildren");
         if (getChildrenMethod != null) {
            if (!getChildrenMethod.isAccessible()) {
               getChildrenMethod.setAccessible(true);
            }

            children = (ObservableList<Node>)getChildrenMethod.invoke(p);
         }

         return children;
      } catch (IllegalArgumentException | ReflectiveOperationException var3) {
         throw new RuntimeException("Unable to get children for Parent of type " + p.getClass(), var3);
      }
   }

   public static void focusPreviousSibling(Node node) {
      ObservableList<Node> children = node.getParent().getChildrenUnmodifiable();
      int index = children.indexOf(node);
      if (index > 0) {
         ((Node)children.get(index - 1)).requestFocus();
      } else {
         node.getParent().requestFocus();
      }
   }

   public static void focusNextSibling(Node node) {
      ObservableList<Node> children = node.getParent().getChildrenUnmodifiable();
      int index = children.indexOf(node);
      if (index < children.size() - 1) {
         ((Node)children.get(index + 1)).requestFocus();
      } else {
         focusNextSibling(node.getParent());
      }
   }
}
