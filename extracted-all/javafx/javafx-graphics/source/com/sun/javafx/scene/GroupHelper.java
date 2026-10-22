package com.sun.javafx.scene;

import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;

public class GroupHelper extends ParentHelper {
   private static final GroupHelper theInstance = new GroupHelper();
   private static GroupHelper.GroupAccessor groupAccessor;

   private static GroupHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Group var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return super.createPeerImpl(var1);
   }

   @Override
   protected Bounds computeLayoutBoundsImpl(Node var1) {
      groupAccessor.doComputeLayoutBounds(var1);
      return super.computeLayoutBoundsImpl(var1);
   }

   public static void setGroupAccessor(GroupHelper.GroupAccessor var0) {
      if (groupAccessor != null) {
         throw new IllegalStateException();
      } else {
         groupAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Group.class);
   }

   public interface GroupAccessor {
      Bounds doComputeLayoutBounds(Node var1);
   }
}
