package com.sun.javafx.embed.swing;

import com.sun.javafx.embed.swing.newimpl.FXDnDInteropN;
import java.awt.Component;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javafx.embed.swing.SwingNode;

public final class FXDnD {
   public static boolean fxAppThreadIsDispatchThread;
   private FXDnDInteropN fxdndiop = new FXDnDInteropN();

   public FXDnD(SwingNode var1) {
      this.fxdndiop.setNode(var1);
   }

   public Object createDragSourceContext(DragGestureEvent var1) throws InvalidDnDOperationException {
      return this.fxdndiop.createDragSourceContext(var1);
   }

   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5) {
      return this.fxdndiop.createDragGestureRecognizer(var2, var3, var4, var5);
   }

   public void addDropTarget(DropTarget var1) {
      SwingNode var2 = this.fxdndiop.getNode();
      if (var2 != null) {
         this.fxdndiop.addDropTarget(var1, var2);
      }
   }

   public void removeDropTarget(DropTarget var1) {
      SwingNode var2 = this.fxdndiop.getNode();
      if (var2 != null) {
         this.fxdndiop.removeDropTarget(var1, var2);
      }
   }

   static {
      Object var0 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
         @Override
         public Object run() {
            FXDnD.fxAppThreadIsDispatchThread = "true".equals(System.getProperty("javafx.embed.singleThread"));
            return null;
         }
      });
   }
}
