package com.sun.javafx.scene.traversal;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;

final class TabOrderHelper {
   private static Node findPreviousFocusableInList(List<Node> var0, int var1) {
      for (int var2 = var1; var2 >= 0; var2--) {
         Node var3 = (Node)var0.get(var2);
         if (!isDisabledOrInvisible(var3)) {
            ParentTraversalEngine var4 = var3 instanceof Parent ? ParentHelper.getTraversalEngine((Parent)var3) : null;
            if (var3 instanceof Parent) {
               if (var4 != null && var4.canTraverse()) {
                  Node var7 = var4.selectLast();
                  if (var7 != null) {
                     return var7;
                  }
               } else {
                  ObservableList var5 = ((Parent)var3).getChildrenUnmodifiable();
                  if (var5.size() > 0) {
                     Node var6 = findPreviousFocusableInList(var5, var5.size() - 1);
                     if (var6 != null) {
                        return var6;
                     }
                  }
               }
            }

            if (var4 != null ? var4.isParentTraversable() : var3.isFocusTraversable()) {
               return var3;
            }
         }
      }

      return null;
   }

   private static boolean isDisabledOrInvisible(Node var0) {
      return var0.isDisabled() || !NodeHelper.isTreeVisible(var0);
   }

   public static Node findPreviousFocusablePeer(Node var0, Parent var1) {
      Object var2 = var0;
      Object var3 = null;
      List var4 = findPeers(var0);
      if (var4 == null) {
         ObservableList var11 = ((Parent)var0).getChildrenUnmodifiable();
         return findPreviousFocusableInList(var11, var11.size() - 1);
      } else {
         int var5 = var4.indexOf(var0);
         var3 = findPreviousFocusableInList(var4, var5 - 1);

         while (var3 == null && ((Node)var2).getParent() != var1) {
            Parent var8 = ((Node)var2).getParent();
            if (var8 != null) {
               ParentTraversalEngine var9 = ParentHelper.getTraversalEngine(var8);
               if (var9 != null ? !var9.isParentTraversable() : !var8.isFocusTraversable()) {
                  List var6 = findPeers(var8);
                  if (var6 != null) {
                     int var7 = var6.indexOf(var8);
                     var3 = findPreviousFocusableInList(var6, var7 - 1);
                  }
               } else {
                  var3 = var8;
               }
            }

            var2 = var8;
         }

         return (Node)var3;
      }
   }

   private static List<Node> findPeers(Node var0) {
      ObservableList var1 = null;
      Parent var2 = var0.getParent();
      if (var2 != null) {
         var1 = var2.getChildrenUnmodifiable();
      }

      return var1;
   }

   private static Node findNextFocusableInList(List<Node> var0, int var1) {
      for (int var2 = var1; var2 < var0.size(); var2++) {
         Node var3 = (Node)var0.get(var2);
         if (!isDisabledOrInvisible(var3)) {
            ParentTraversalEngine var4 = var3 instanceof Parent ? ParentHelper.getTraversalEngine((Parent)var3) : null;
            if (var4 != null ? var4.isParentTraversable() : var3.isFocusTraversable()) {
               return var3;
            }

            if (var3 instanceof Parent) {
               if (var4 != null && var4.canTraverse()) {
                  Node var7 = var4.selectFirst();
                  if (var7 != null) {
                     return var7;
                  }
               } else {
                  ObservableList var5 = ((Parent)var3).getChildrenUnmodifiable();
                  if (var5.size() > 0) {
                     Node var6 = findNextFocusableInList(var5, 0);
                     if (var6 != null) {
                        return var6;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   public static Node findNextFocusablePeer(Node var0, Parent var1, boolean var2) {
      Object var3 = var0;
      Node var4 = null;
      if (var2 && var0 instanceof Parent) {
         var4 = findNextFocusableInList(((Parent)var0).getChildrenUnmodifiable(), 0);
      }

      if (var4 == null) {
         List var5 = findPeers(var0);
         if (var5 == null) {
            return null;
         }

         int var6 = var5.indexOf(var0);
         var4 = findNextFocusableInList(var5, var6 + 1);
      }

      while (var4 == null && ((Node)var3).getParent() != var1) {
         Parent var7 = ((Node)var3).getParent();
         if (var7 != null) {
            List var8 = findPeers(var7);
            if (var8 != null) {
               int var9 = var8.indexOf(var7);
               var4 = findNextFocusableInList(var8, var9 + 1);
            }
         }

         var3 = var7;
      }

      return var4;
   }

   public static Node getFirstTargetNode(Parent var0) {
      if (var0 != null && !isDisabledOrInvisible(var0)) {
         ParentTraversalEngine var1 = ParentHelper.getTraversalEngine(var0);
         if (var1 != null && var1.canTraverse()) {
            Node var2 = var1.selectFirst();
            if (var2 != null) {
               return var2;
            }
         }

         for (Node var4 : var0.getChildrenUnmodifiable()) {
            if (!isDisabledOrInvisible(var4)) {
               ParentTraversalEngine var5 = var4 instanceof Parent ? ParentHelper.getTraversalEngine((Parent)var4) : null;
               if (var5 != null ? var5.isParentTraversable() : var4.isFocusTraversable()) {
                  return var4;
               }

               if (var4 instanceof Parent) {
                  Node var6 = getFirstTargetNode((Parent)var4);
                  if (var6 != null) {
                     return var6;
                  }
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static Node getLastTargetNode(Parent var0) {
      if (var0 != null && !isDisabledOrInvisible(var0)) {
         ParentTraversalEngine var1 = ParentHelper.getTraversalEngine(var0);
         if (var1 != null && var1.canTraverse()) {
            Node var2 = var1.selectLast();
            if (var2 != null) {
               return var2;
            }
         }

         ObservableList var6 = var0.getChildrenUnmodifiable();

         for (int var3 = var6.size() - 1; var3 >= 0; var3--) {
            Node var4 = (Node)var6.get(var3);
            if (!isDisabledOrInvisible(var4)) {
               if (var4 instanceof Parent) {
                  Node var5 = getLastTargetNode((Parent)var4);
                  if (var5 != null) {
                     return var5;
                  }
               }

               ParentTraversalEngine var7 = var4 instanceof Parent ? ParentHelper.getTraversalEngine((Parent)var4) : null;
               if (var7 != null ? var7.isParentTraversable() : var4.isFocusTraversable()) {
                  return var4;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
