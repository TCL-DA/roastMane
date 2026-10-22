package javafx.scene;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.javafx.scene.CssFlags;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.LayoutFlags;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.sg.prism.NGGroup;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.TempState;
import com.sun.javafx.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.Selector;
import javafx.stage.Window;

public abstract class Parent extends Node {
   static final int DIRTY_CHILDREN_THRESHOLD = 10;
   private static final boolean warnOnAutoMove = PropertyHelper.getBooleanProperty("javafx.sg.warn");
   private static final int REMOVED_CHILDREN_THRESHOLD = 20;
   private boolean removedChildrenOptimizationDisabled = false;
   private final Set<Node> childSet = new HashSet<>();
   private int startIdx = 0;
   private int pgChildrenSize = 0;
   private final List<Node> viewOrderChildren = new ArrayList<>(1);
   private boolean childrenTriggerPermutation = false;
   private List<Node> removed;
   private boolean geomChanged;
   private boolean childSetModified;
   private final ObservableList<Node> children = new VetoableListDecorator<Node>(new TrackableObservableList<Node>() {
      protected void onChanged(Change<Node> var1) {
         Parent.this.unmodifiableManagedChildren = null;
         boolean var2 = false;
         boolean var3 = false;
         if (Parent.this.childSetModified) {
            while (true) {
               if (!var1.next()) {
                  if (Parent.this.dirtyChildren == null && Parent.this.children.size() > 10) {
                     Parent.this.dirtyChildren = new ArrayList<>(20);
                     if (Parent.this.dirtyChildrenCount > 0) {
                        int var11 = Parent.this.children.size();

                        for (int var14 = 0; var14 < var11; var14++) {
                           Node var18 = (Node)Parent.this.children.get(var14);
                           if (var18.isVisible() && var18.boundsChanged) {
                              Parent.this.dirtyChildren.add(var18);
                           }
                        }
                     }
                  }
                  break;
               }

               int var10 = var1.getFrom();
               int var13 = var1.getTo();

               for (int var16 = var10; var16 < var13; var16++) {
                  Node var7 = (Node)Parent.this.children.get(var16);
                  if (var7.getParent() != null && var7.getParent() != Parent.this) {
                     if (Parent.warnOnAutoMove) {
                        System.err.println("WARNING added to a new parent without first removing it from its current");
                        System.err.println("    parent. It will be automatically removed from its current parent.");
                        System.err.println("    node=" + var7 + " oldparent= " + var7.getParent() + " newparent=" + this);
                     }

                     var7.getParent().children.remove(var7);
                     if (Parent.warnOnAutoMove) {
                        Thread.dumpStack();
                     }
                  }
               }

               List var17 = var1.getRemoved();
               int var19 = var17.size();

               for (int var8 = 0; var8 < var19; var8++) {
                  Node var9 = (Node)var17.get(var8);
                  if (var9.isManaged()) {
                     var2 = true;
                  }
               }

               if ((var19 > 0 || var13 - var10 > 0) && !Parent.this.viewOrderChildren.isEmpty()) {
                  var3 = true;
               }

               for (int var20 = var10; var20 < var13; var20++) {
                  Node var21 = (Node)Parent.this.children.get(var20);
                  if (var21.getViewOrder() != 0.0) {
                     var3 = true;
                  }

                  if (var21.isManaged() || var21 instanceof Parent && ((Parent)var21).layoutFlag != LayoutFlags.CLEAN) {
                     var2 = true;
                  }

                  var21.setParent(Parent.this);
                  var21.setScenes(Parent.this.getScene(), Parent.this.getSubScene());
                  if (var21.isVisible()) {
                     Parent.this.geomChanged = true;
                     Parent.this.childIncluded(var21);
                  }
               }
            }
         } else {
            label149:
            while (var1.next()) {
               List var4 = var1.getRemoved();
               int var5 = 0;

               for (int var6 = var4.size(); var5 < var6; var5++) {
                  if (((Node)var4.get(var5)).isManaged()) {
                     var2 = true;
                     break label149;
                  }
               }

               var5 = var1.getFrom();

               for (int var15 = var1.getTo(); var5 < var15; var5++) {
                  if (((Node)Parent.this.children.get(var5)).isManaged()) {
                     var2 = true;
                     break label149;
                  }
               }
            }
         }

         if (var2) {
            Parent.this.requestLayout();
         }

         if (Parent.this.geomChanged) {
            NodeHelper.geomChanged(Parent.this);
         }

         var1.reset();
         var1.next();
         if (Parent.this.startIdx > var1.getFrom()) {
            Parent.this.startIdx = var1.getFrom();
         }

         NodeHelper.markDirty(Parent.this, DirtyBits.PARENT_CHILDREN);
         NodeHelper.markDirty(Parent.this, DirtyBits.NODE_FORCE_SYNC);
         if (var3) {
            Parent.this.markViewOrderChildrenDirty();
         }
      }
   }) {
      protected void onProposedChange(List<Node> var1, int[] var2) {
         Scene var3 = Parent.this.getScene();
         if (var3 != null) {
            Window var4 = var3.getWindow();
            if (var4 != null && WindowHelper.getPeer(var4) != null) {
               Toolkit.getToolkit().checkFxUserThread();
            }
         }

         Parent.this.geomChanged = false;
         long var12 = (long)(Parent.this.children.size() + var1.size());
         int var6 = 0;

         for (byte var7 = 0; var7 < var2.length; var7 += 2) {
            var6 += var2[var7 + 1] - var2[var7];
         }

         var12 -= (long)var6;
         if (Parent.this.childrenTriggerPermutation) {
            Parent.this.childSetModified = false;
         } else {
            Parent.this.childSetModified = true;
            if (var12 == (long)Parent.this.childSet.size()) {
               Parent.this.childSetModified = false;

               for (int var14 = var1.size() - 1; var14 >= 0; var14--) {
                  Node var8 = (Node)var1.get(var14);
                  if (!Parent.this.childSet.contains(var8)) {
                     Parent.this.childSetModified = true;
                     break;
                  }
               }
            }

            for (byte var15 = 0; var15 < var2.length; var15 += 2) {
               for (int var18 = var2[var15]; var18 < var2[var15 + 1]; var18++) {
                  Parent.this.childSet.remove(Parent.this.children.get(var18));
               }
            }

            try {
               if (Parent.this.childSetModified) {
                  for (int var16 = var1.size() - 1; var16 >= 0; var16--) {
                     Node var19 = (Node)var1.get(var16);
                     if (var19 == null) {
                        throw new NullPointerException(this.constructExceptionMessage("child node is null", null));
                     }

                     if (var19.getClipParent() != null) {
                        throw new IllegalArgumentException(this.constructExceptionMessage("node already used as a clip", var19));
                     }

                     if (Parent.this.wouldCreateCycle(Parent.this, var19)) {
                        throw new IllegalArgumentException(this.constructExceptionMessage("cycle detected", var19));
                     }
                  }
               }

               Parent.this.childSet.addAll(var1);
               if ((long)Parent.this.childSet.size() != var12) {
                  throw new IllegalArgumentException(this.constructExceptionMessage("duplicate children added", null));
               }
            } catch (RuntimeException var11) {
               Parent.this.childSet.clear();
               Parent.this.childSet.addAll(Parent.this.children);
               throw var11;
            }

            if (Parent.this.childSetModified) {
               if (Parent.this.removed == null) {
                  Parent.this.removed = new ArrayList<>();
               }

               if (Parent.this.removed.size() + var6 > 20 || !Parent.this.isTreeVisible()) {
                  Parent.this.removedChildrenOptimizationDisabled = true;
               }

               for (byte var17 = 0; var17 < var2.length; var17 += 2) {
                  for (int var20 = var2[var17]; var20 < var2[var17 + 1]; var20++) {
                     Node var9 = (Node)Parent.this.children.get(var20);
                     Scene var10 = var9.getScene();
                     if (var10 != null) {
                        var10.generateMouseExited(var9);
                     }

                     if (Parent.this.dirtyChildren != null) {
                        Parent.this.dirtyChildren.remove(var9);
                     }

                     if (var9.isVisible()) {
                        Parent.this.geomChanged = true;
                        Parent.this.childExcluded(var9);
                     }

                     if (var9.getParent() == Parent.this) {
                        var9.setParent(null);
                        var9.setScenes(null, null);
                     }

                     if (var3 != null && !Parent.this.removedChildrenOptimizationDisabled) {
                        Parent.this.removed.add(var9);
                     }
                  }
               }
            }
         }
      }

      private String constructExceptionMessage(String var1, Node var2) {
         StringBuilder var3 = new StringBuilder("Children: ");
         var3.append(var1);
         var3.append(": parent = ").append(Parent.this);
         if (var2 != null) {
            var3.append(", node = ").append(var2);
         }

         return var3.toString();
      }
   };
   private final ObservableList<Node> unmodifiableChildren = FXCollections.unmodifiableObservableList(this.children);
   private List<Node> unmodifiableManagedChildren = null;
   private ParentTraversalEngine traversalEngine;
   private ReadOnlyBooleanWrapper needsLayout;
   LayoutFlags layoutFlag = LayoutFlags.CLEAN;
   private boolean performingLayout = false;
   private boolean sizeCacheClear = true;
   private double prefWidthCache = -1.0;
   private double prefHeightCache = -1.0;
   private double minWidthCache = -1.0;
   private double minHeightCache = -1.0;
   private boolean forceParentLayout = false;
   private Node currentLayoutChild = null;
   private boolean sceneRoot = false;
   boolean layoutRoot = false;
   private final ObservableList<String> stylesheets = new TrackableObservableList<String>() {
      protected void onChanged(Change<String> var1) {
         Scene var2 = Parent.this.getScene();
         if (var2 != null) {
            StyleManager.getInstance().stylesheetsChanged(Parent.this, var1);
            var1.reset();

            while (var1.next() && !var1.wasRemoved()) {
            }

            Parent.this.reapplyCSS();
         }
      }
   };
   private BaseBounds tmp;
   private BaseBounds cachedBounds;
   private boolean cachedBoundsInvalid;
   private int dirtyChildrenCount;
   private ArrayList<Node> dirtyChildren;
   private Node top;
   private Node left;
   private Node bottom;
   private Node right;
   private Node near;
   private Node far;
   private final int LEFT_INVALID;
   private final int TOP_INVALID;
   private final int NEAR_INVALID;
   private final int RIGHT_INVALID;
   private final int BOTTOM_INVALID;
   private final int FAR_INVALID;
   private Node currentlyProcessedChild;

   private void doUpdatePeer() {
      NGGroup var1 = this.getPeer();
      if (Utils.assertionEnabled()) {
         List var2 = var1.getChildren();
         if (var2.size() != this.pgChildrenSize) {
            System.err.println("*** pgnodes.size() [" + var2.size() + "] != pgChildrenSize [" + this.pgChildrenSize + "]");
         }
      }

      if (this.isDirty(DirtyBits.PARENT_CHILDREN)) {
         var1.clearFrom(this.startIdx);

         for (int var3 = this.startIdx; var3 < this.children.size(); var3++) {
            var1.add(var3, ((Node)this.children.get(var3)).getPeer());
         }

         if (this.removedChildrenOptimizationDisabled) {
            var1.markDirty();
            this.removedChildrenOptimizationDisabled = false;
         } else if (this.removed != null && !this.removed.isEmpty()) {
            for (int var4 = 0; var4 < this.removed.size(); var4++) {
               var1.addToRemoved(this.removed.get(var4).getPeer());
            }
         }

         if (this.removed != null) {
            this.removed.clear();
         }

         this.pgChildrenSize = this.children.size();
         this.startIdx = this.pgChildrenSize;
      }

      if (this.isDirty(DirtyBits.PARENT_CHILDREN_VIEW_ORDER)) {
         this.computeViewOrderChildren();
         var1.setViewOrderChildren(this.viewOrderChildren);
      }

      if (Utils.assertionEnabled()) {
         this.validatePG();
      }
   }

   void validatePG() {
      boolean var1 = false;
      NGGroup var2 = this.getPeer();
      List var3 = var2.getChildren();
      if (var3.size() != this.children.size()) {
         System.err.println("*** pgnodes.size validatePG() [" + var3.size() + "] != children.size() [" + this.children.size() + "]");
         var1 = true;
      } else {
         for (int var4 = 0; var4 < this.children.size(); var4++) {
            Node var5 = (Node)this.children.get(var4);
            if (var5.getParent() != this) {
               System.err.println("*** this=" + this + " validatePG children[" + var4 + "].parent= " + var5.getParent());
               var1 = true;
            }

            if (var5.getPeer() != var3.get(var4)) {
               System.err.println("*** pgnodes[" + var4 + "] validatePG != children[" + var4 + "]");
               var1 = true;
            }
         }
      }

      if (var1) {
         throw new AssertionError("validation of PGGroup children failed");
      }
   }

   void printSeq(String var1, List<Node> var2) {
      Object var3 = var1;

      for (Node var5 : var2) {
         var3 = var3 + var5 + " ";
      }

      System.out.println((String)var3);
   }

   void markViewOrderChildrenDirty() {
      this.viewOrderChildren.clear();
      NodeHelper.markDirty(this, DirtyBits.PARENT_CHILDREN_VIEW_ORDER);
   }

   private void computeViewOrderChildren() {
      boolean var1 = false;

      for (Node var3 : this.children) {
         double var4 = var3.getViewOrder();
         if (!var1 && var4 != 0.0) {
            var1 = true;
         }
      }

      this.viewOrderChildren.clear();
      if (var1) {
         this.viewOrderChildren.addAll(this.children);
         Collections.sort(
            this.viewOrderChildren, (var0, var1x) -> var0.getViewOrder() < var1x.getViewOrder() ? 1 : (var0.getViewOrder() == var1x.getViewOrder() ? 0 : -1)
         );
      }
   }

   private List<Node> getOrderedChildren() {
      if (this.isDirty(DirtyBits.PARENT_CHILDREN_VIEW_ORDER)) {
         this.computeViewOrderChildren();
      }

      return (List<Node>)(!this.viewOrderChildren.isEmpty() ? this.viewOrderChildren : this.children);
   }

   protected ObservableList<Node> getChildren() {
      return this.children;
   }

   public ObservableList<Node> getChildrenUnmodifiable() {
      return this.unmodifiableChildren;
   }

   protected <E extends Node> List<E> getManagedChildren() {
      if (this.unmodifiableManagedChildren == null) {
         this.unmodifiableManagedChildren = new ArrayList<>();
         int var1 = 0;

         for (int var2 = this.children.size(); var1 < var2; var1++) {
            Node var3 = (Node)this.children.get(var1);
            if (var3.isManaged()) {
               this.unmodifiableManagedChildren.add(var3);
            }
         }
      }

      return (List<E>)this.unmodifiableManagedChildren;
   }

   final void managedChildChanged() {
      this.requestLayout();
      this.unmodifiableManagedChildren = null;
   }

   final void toFront(Node var1) {
      if (Utils.assertionEnabled() && !this.childSet.contains(var1)) {
         throw new AssertionError("specified node is not in the list of children");
      } else {
         if (this.children.get(this.children.size() - 1) != var1) {
            this.childrenTriggerPermutation = true;

            try {
               this.children.remove(var1);
               this.children.add(var1);
            } finally {
               this.childrenTriggerPermutation = false;
            }
         }
      }
   }

   final void toBack(Node var1) {
      if (Utils.assertionEnabled() && !this.childSet.contains(var1)) {
         throw new AssertionError("specified node is not in the list of children");
      } else {
         if (this.children.get(0) != var1) {
            this.childrenTriggerPermutation = true;

            try {
               this.children.remove(var1);
               this.children.add(0, var1);
            } finally {
               this.childrenTriggerPermutation = false;
            }
         }
      }
   }

   @Override
   void scenesChanged(Scene var1, SubScene var2, Scene var3, SubScene var4) {
      if (var3 != null && var1 == null) {
         StyleManager.getInstance().forget(this);
         if (this.removed != null) {
            this.removed.clear();
         }
      }

      for (int var5 = 0; var5 < this.children.size(); var5++) {
         ((Node)this.children.get(var5)).setScenes(var1, var2);
      }

      boolean var6 = this.layoutFlag != LayoutFlags.CLEAN;
      this.sceneRoot = var2 != null && var2.getRoot() == this || var1 != null && var1.getRoot() == this;
      this.layoutRoot = !this.isManaged() || this.sceneRoot;
      if (var6 && var1 != null && this.layoutRoot && var2 != null) {
         var2.setDirtyLayout(this);
      }
   }

   @Override
   void setDerivedDepthTest(boolean var1) {
      super.setDerivedDepthTest(var1);
      int var2 = 0;

      for (int var3 = this.children.size(); var2 < var3; var2++) {
         Node var4 = (Node)this.children.get(var2);
         var4.computeDerivedDepthTest();
      }
   }

   boolean pickChildrenNode(PickRay var1, PickResultChooser var2) {
      List var3 = this.getOrderedChildren();

      for (int var4 = var3.size() - 1; var4 >= 0; var4--) {
         ((Node)var3.get(var4)).pickNode(var1, var2);
         if (var2.isClosed()) {
            return false;
         }
      }

      return true;
   }

   private void doPickNodeLocal(PickRay var1, PickResultChooser var2) {
      double var3 = this.intersectsBounds(var1);
      if (!Double.isNaN(var3) && this.pickChildrenNode(var1, var2) && this.isPickOnBounds()) {
         var2.offer(this, var3, PickResultChooser.computePoint(var1, var3));
      }
   }

   @Override
   boolean isConnected() {
      return super.isConnected() || this.sceneRoot;
   }

   @Override
   public Node lookup(String var1) {
      Node var2 = super.lookup(var1);
      if (var2 == null) {
         int var3 = 0;

         for (int var4 = this.children.size(); var3 < var4; var3++) {
            Node var5 = (Node)this.children.get(var3);
            var2 = var5.lookup(var1);
            if (var2 != null) {
               return var2;
            }
         }
      }

      return var2;
   }

   @Override
   List<Node> lookupAll(Selector var1, List<Node> var2) {
      var2 = super.lookupAll(var1, var2);
      int var3 = 0;

      for (int var4 = this.children.size(); var3 < var4; var3++) {
         Node var5 = (Node)this.children.get(var3);
         var2 = var5.lookupAll(var1, var2);
      }

      return var2;
   }

   private final void setTraversalEngine(ParentTraversalEngine var1) {
      this.traversalEngine = var1;
   }

   private final ParentTraversalEngine getTraversalEngine() {
      return this.traversalEngine;
   }

   protected final void setNeedsLayout(boolean var1) {
      if (var1) {
         this.markDirtyLayout(true, false);
      } else if (this.layoutFlag == LayoutFlags.NEEDS_LAYOUT) {
         boolean var2 = false;
         int var3 = 0;

         for (int var4 = this.children.size(); var3 < var4; var3++) {
            Node var5 = (Node)this.children.get(var3);
            if (var5 instanceof Parent && ((Parent)var5).layoutFlag != LayoutFlags.CLEAN) {
               var2 = true;
               break;
            }
         }

         this.setLayoutFlag(var2 ? LayoutFlags.DIRTY_BRANCH : LayoutFlags.CLEAN);
      }
   }

   public final boolean isNeedsLayout() {
      return this.layoutFlag == LayoutFlags.NEEDS_LAYOUT;
   }

   public final ReadOnlyBooleanProperty needsLayoutProperty() {
      if (this.needsLayout == null) {
         this.needsLayout = new ReadOnlyBooleanWrapper(this, "needsLayout", this.layoutFlag == LayoutFlags.NEEDS_LAYOUT);
      }

      return this.needsLayout;
   }

   boolean isPerformingLayout() {
      return this.performingLayout;
   }

   void setLayoutFlag(LayoutFlags var1) {
      if (this.needsLayout != null) {
         this.needsLayout.set(var1 == LayoutFlags.NEEDS_LAYOUT);
      }

      this.layoutFlag = var1;
   }

   private void markDirtyLayout(boolean var1, boolean var2) {
      this.setLayoutFlag(LayoutFlags.NEEDS_LAYOUT);
      if (!var1 && !this.layoutRoot) {
         this.requestParentLayout(var2);
      } else if (this.sceneRoot) {
         Toolkit.getToolkit().requestNextPulse();
         if (this.getSubScene() != null) {
            this.getSubScene().setDirtyLayout(this);
         }
      } else {
         this.markDirtyLayoutBranch();
      }
   }

   public void requestLayout() {
      this.clearSizeCache();
      this.markDirtyLayout(false, this.forceParentLayout);
   }

   void requestLayout(boolean var1) {
      boolean var2 = this.forceParentLayout;
      this.forceParentLayout = var1;
      this.requestLayout();
      this.forceParentLayout = var2;
   }

   protected final void requestParentLayout() {
      this.requestParentLayout(false);
   }

   void requestParentLayout(boolean var1) {
      if (!this.layoutRoot) {
         Parent var2 = this.getParent();
         if (var2 != null && (!var2.performingLayout || var1)) {
            var2.requestLayout();
         }
      }
   }

   void clearSizeCache() {
      if (!this.sizeCacheClear) {
         this.sizeCacheClear = true;
         this.prefWidthCache = -1.0;
         this.prefHeightCache = -1.0;
         this.minWidthCache = -1.0;
         this.minHeightCache = -1.0;
      }
   }

   @Override
   public double prefWidth(double var1) {
      if (var1 == -1.0) {
         if (this.prefWidthCache == -1.0) {
            this.prefWidthCache = this.computePrefWidth(-1.0);
            if (Double.isNaN(this.prefWidthCache) || this.prefWidthCache < 0.0) {
               this.prefWidthCache = 0.0;
            }

            this.sizeCacheClear = false;
         }

         return this.prefWidthCache;
      } else {
         double var3 = this.computePrefWidth(var1);
         return !Double.isNaN(var3) && !(var3 < 0.0) ? var3 : 0.0;
      }
   }

   @Override
   public double prefHeight(double var1) {
      if (var1 == -1.0) {
         if (this.prefHeightCache == -1.0) {
            this.prefHeightCache = this.computePrefHeight(-1.0);
            if (Double.isNaN(this.prefHeightCache) || this.prefHeightCache < 0.0) {
               this.prefHeightCache = 0.0;
            }

            this.sizeCacheClear = false;
         }

         return this.prefHeightCache;
      } else {
         double var3 = this.computePrefHeight(var1);
         return !Double.isNaN(var3) && !(var3 < 0.0) ? var3 : 0.0;
      }
   }

   @Override
   public double minWidth(double var1) {
      if (var1 == -1.0) {
         if (this.minWidthCache == -1.0) {
            this.minWidthCache = this.computeMinWidth(-1.0);
            if (Double.isNaN(this.minWidthCache) || this.minWidthCache < 0.0) {
               this.minWidthCache = 0.0;
            }

            this.sizeCacheClear = false;
         }

         return this.minWidthCache;
      } else {
         double var3 = this.computeMinWidth(var1);
         return !Double.isNaN(var3) && !(var3 < 0.0) ? var3 : 0.0;
      }
   }

   @Override
   public double minHeight(double var1) {
      if (var1 == -1.0) {
         if (this.minHeightCache == -1.0) {
            this.minHeightCache = this.computeMinHeight(-1.0);
            if (Double.isNaN(this.minHeightCache) || this.minHeightCache < 0.0) {
               this.minHeightCache = 0.0;
            }

            this.sizeCacheClear = false;
         }

         return this.minHeightCache;
      } else {
         double var3 = this.computeMinHeight(var1);
         return !Double.isNaN(var3) && !(var3 < 0.0) ? var3 : 0.0;
      }
   }

   protected double computePrefWidth(double var1) {
      double var3 = 0.0;
      double var5 = 0.0;
      int var7 = 0;

      for (int var8 = this.children.size(); var7 < var8; var7++) {
         Node var9 = (Node)this.children.get(var7);
         if (var9.isManaged()) {
            double var10 = var9.getLayoutBounds().getMinX() + var9.getLayoutX();
            var3 = Math.min(var3, var10);
            var5 = Math.max(var5, var10 + this.boundedSize(var9.prefWidth(-1.0), var9.minWidth(-1.0), var9.maxWidth(-1.0)));
         }
      }

      return var5 - var3;
   }

   protected double computePrefHeight(double var1) {
      double var3 = 0.0;
      double var5 = 0.0;
      int var7 = 0;

      for (int var8 = this.children.size(); var7 < var8; var7++) {
         Node var9 = (Node)this.children.get(var7);
         if (var9.isManaged()) {
            double var10 = var9.getLayoutBounds().getMinY() + var9.getLayoutY();
            var3 = Math.min(var3, var10);
            var5 = Math.max(var5, var10 + this.boundedSize(var9.prefHeight(-1.0), var9.minHeight(-1.0), var9.maxHeight(-1.0)));
         }
      }

      return var5 - var3;
   }

   protected double computeMinWidth(double var1) {
      return this.prefWidth(var1);
   }

   protected double computeMinHeight(double var1) {
      return this.prefHeight(var1);
   }

   @Override
   public double getBaselineOffset() {
      int var1 = 0;

      for (int var2 = this.children.size(); var1 < var2; var1++) {
         Node var3 = (Node)this.children.get(var1);
         if (var3.isManaged()) {
            double var4 = var3.getBaselineOffset();
            if (var4 != Double.NEGATIVE_INFINITY) {
               return var3.getLayoutBounds().getMinY() + var3.getLayoutY() + var4;
            }
         }
      }

      return super.getBaselineOffset();
   }

   boolean isCurrentLayoutChild(Node var1) {
      return var1 == this.currentLayoutChild;
   }

   public final void layout() {
      LayoutFlags var1 = this.layoutFlag;
      this.setLayoutFlag(LayoutFlags.CLEAN);
      switch (var1) {
         case CLEAN:
         default:
            break;
         case NEEDS_LAYOUT:
            if (this.performingLayout) {
               break;
            }

            this.performingLayout = true;
            this.layoutChildren();
         case DIRTY_BRANCH:
            int var2 = 0;

            for (int var3 = this.children.size(); var2 < var3; var2++) {
               Node var4 = (Node)this.children.get(var2);
               this.currentLayoutChild = var4;
               if (var4 instanceof Parent) {
                  ((Parent)var4).layout();
               } else if (var4 instanceof SubScene) {
                  ((SubScene)var4).layoutPass();
               }
            }

            this.currentLayoutChild = null;
            this.performingLayout = false;
      }
   }

   protected void layoutChildren() {
      int var1 = 0;

      for (int var2 = this.children.size(); var1 < var2; var1++) {
         Node var3 = (Node)this.children.get(var1);
         this.currentLayoutChild = var3;
         if (var3.isResizable() && var3.isManaged()) {
            var3.autosize();
         }
      }

      this.currentLayoutChild = null;
   }

   @Override
   final void notifyManagedChanged() {
      this.layoutRoot = !this.isManaged() || this.sceneRoot;
   }

   final boolean isSceneRoot() {
      return this.sceneRoot;
   }

   public final ObservableList<String> getStylesheets() {
      return this.stylesheets;
   }

   private List<String> doGetAllParentStylesheets() {
      Object var1 = null;
      Parent var2 = this.getParent();
      if (var2 != null) {
         var1 = ParentHelper.getAllParentStylesheets(var2);
      }

      if (this.stylesheets != null && !this.stylesheets.isEmpty()) {
         if (var1 == null) {
            var1 = new ArrayList(this.stylesheets.size());
         }

         int var3 = 0;

         for (int var4 = this.stylesheets.size(); var3 < var4; var3++) {
            var1.add((String)this.stylesheets.get(var3));
         }
      }

      return (List<String>)var1;
   }

   private void doProcessCSS() {
      if (this.cssFlag != CssFlags.CLEAN) {
         if (this.cssFlag == CssFlags.DIRTY_BRANCH) {
            super.processCSS();
         } else {
            ParentHelper.superProcessCSS(this);
            if (!this.children.isEmpty()) {
               Node[] var1 = (Node[])this.children.toArray(new Node[this.children.size()]);

               for (int var2 = 0; var2 < var1.length; var2++) {
                  Node var3 = var1[var2];
                  Parent var4 = var3.getParent();
                  if (var4 != null && var4 == this) {
                     if (CssFlags.UPDATE.compareTo(var3.cssFlag) > 0) {
                        var3.cssFlag = CssFlags.UPDATE;
                     }

                     NodeHelper.processCSS(var3);
                  }
               }
            }
         }
      }
   }

   protected Parent() {
      ParentHelper.initHelper(this);
      this.tmp = new RectBounds();
      this.cachedBounds = new RectBounds();
      this.LEFT_INVALID = 1;
      this.TOP_INVALID = 2;
      this.NEAR_INVALID = 4;
      this.RIGHT_INVALID = 8;
      this.BOTTOM_INVALID = 16;
      this.FAR_INVALID = 32;
      this.layoutFlag = LayoutFlags.NEEDS_LAYOUT;
      this.setAccessibleRole(AccessibleRole.PARENT);
   }

   private NGNode doCreatePeer() {
      return new NGGroup();
   }

   @Override
   void nodeResolvedOrientationChanged() {
      int var1 = 0;

      for (int var2 = this.children.size(); var1 < var2; var1++) {
         ((Node)this.children.get(var1)).parentResolvedOrientationInvalidated();
      }
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      if (this.children.isEmpty()) {
         return var1.makeEmpty();
      } else if (var2.isTranslateOrIdentity()) {
         if (this.cachedBoundsInvalid) {
            this.recomputeBounds();
            if (this.dirtyChildren != null) {
               this.dirtyChildren.clear();
            }

            this.cachedBoundsInvalid = false;
            this.dirtyChildrenCount = 0;
         }

         if (!var2.isIdentity()) {
            var1 = var1.deriveWithNewBounds(
               (float)((double)this.cachedBounds.getMinX() + var2.getMxt()),
               (float)((double)this.cachedBounds.getMinY() + var2.getMyt()),
               (float)((double)this.cachedBounds.getMinZ() + var2.getMzt()),
               (float)((double)this.cachedBounds.getMaxX() + var2.getMxt()),
               (float)((double)this.cachedBounds.getMaxY() + var2.getMyt()),
               (float)((double)this.cachedBounds.getMaxZ() + var2.getMzt())
            );
         } else {
            var1 = var1.deriveWithNewBounds(this.cachedBounds);
         }

         return var1;
      } else {
         double var3 = Double.MAX_VALUE;
         double var5 = Double.MAX_VALUE;
         double var7 = Double.MAX_VALUE;
         double var9 = Double.MIN_VALUE;
         double var11 = Double.MIN_VALUE;
         double var13 = Double.MIN_VALUE;
         boolean var15 = true;
         int var16 = 0;

         for (int var17 = this.children.size(); var16 < var17; var16++) {
            Node var18 = (Node)this.children.get(var16);
            if (var18.isVisible()) {
               var1 = this.getChildTransformedBounds(var18, var2, var1);
               if (!var1.isEmpty()) {
                  if (var15) {
                     var3 = (double)var1.getMinX();
                     var5 = (double)var1.getMinY();
                     var7 = (double)var1.getMinZ();
                     var9 = (double)var1.getMaxX();
                     var11 = (double)var1.getMaxY();
                     var13 = (double)var1.getMaxZ();
                     var15 = false;
                  } else {
                     var3 = Math.min((double)var1.getMinX(), var3);
                     var5 = Math.min((double)var1.getMinY(), var5);
                     var7 = Math.min((double)var1.getMinZ(), var7);
                     var9 = Math.max((double)var1.getMaxX(), var9);
                     var11 = Math.max((double)var1.getMaxY(), var11);
                     var13 = Math.max((double)var1.getMaxZ(), var13);
                  }
               }
            }
         }

         if (var15) {
            var1.makeEmpty();
         } else {
            var1 = var1.deriveWithNewBounds((float)var3, (float)var5, (float)var7, (float)var9, (float)var11, (float)var13);
         }

         return var1;
      }
   }

   private void setChildDirty(Node var1, boolean var2) {
      if (var1.boundsChanged != var2) {
         var1.boundsChanged = var2;
         if (var2) {
            if (this.dirtyChildren != null) {
               this.dirtyChildren.add(var1);
            }

            this.dirtyChildrenCount++;
         } else {
            if (this.dirtyChildren != null) {
               this.dirtyChildren.remove(var1);
            }

            this.dirtyChildrenCount--;
         }
      }
   }

   private void childIncluded(Node var1) {
      this.cachedBoundsInvalid = true;
      this.setChildDirty(var1, true);
   }

   private void childExcluded(Node var1) {
      if (var1 == this.left) {
         this.left = null;
         this.cachedBoundsInvalid = true;
      }

      if (var1 == this.top) {
         this.top = null;
         this.cachedBoundsInvalid = true;
      }

      if (var1 == this.near) {
         this.near = null;
         this.cachedBoundsInvalid = true;
      }

      if (var1 == this.right) {
         this.right = null;
         this.cachedBoundsInvalid = true;
      }

      if (var1 == this.bottom) {
         this.bottom = null;
         this.cachedBoundsInvalid = true;
      }

      if (var1 == this.far) {
         this.far = null;
         this.cachedBoundsInvalid = true;
      }

      this.setChildDirty(var1, false);
   }

   private void recomputeBounds() {
      if (this.children.isEmpty()) {
         this.cachedBounds.makeEmpty();
      } else if (this.children.size() == 1) {
         Node var1 = (Node)this.children.get(0);
         var1.boundsChanged = false;
         if (var1.isVisible()) {
            this.cachedBounds = this.getChildTransformedBounds(var1, BaseTransform.IDENTITY_TRANSFORM, this.cachedBounds);
            this.top = this.left = this.bottom = this.right = this.near = this.far = var1;
         } else {
            this.cachedBounds.makeEmpty();
         }
      } else {
         if (this.dirtyChildrenCount == 0
            || !this.updateCachedBounds((List<Node>)(this.dirtyChildren != null ? this.dirtyChildren : this.children), this.dirtyChildrenCount)) {
            this.createCachedBounds(this.children);
         }
      }
   }

   private boolean updateCachedBounds(List<Node> var1, int var2) {
      if (this.cachedBounds.isEmpty()) {
         this.createCachedBounds(var1);
         return true;
      } else {
         byte var3 = 0;
         if (this.left == null || this.left.boundsChanged) {
            var3 |= 1;
         }

         if (this.top == null || this.top.boundsChanged) {
            var3 |= 2;
         }

         if (this.near == null || this.near.boundsChanged) {
            var3 |= 4;
         }

         if (this.right == null || this.right.boundsChanged) {
            var3 |= 8;
         }

         if (this.bottom == null || this.bottom.boundsChanged) {
            var3 |= 16;
         }

         if (this.far == null || this.far.boundsChanged) {
            var3 |= 32;
         }

         float var4 = this.cachedBounds.getMinX();
         float var5 = this.cachedBounds.getMinY();
         float var6 = this.cachedBounds.getMinZ();
         float var7 = this.cachedBounds.getMaxX();
         float var8 = this.cachedBounds.getMaxY();
         float var9 = this.cachedBounds.getMaxZ();

         for (int var10 = var1.size() - 1; var2 > 0; var10--) {
            Node var11 = (Node)var1.get(var10);
            if (var11.boundsChanged) {
               var11.boundsChanged = false;
               var2--;
               this.tmp = this.getChildTransformedBounds(var11, BaseTransform.IDENTITY_TRANSFORM, this.tmp);
               if (!this.tmp.isEmpty()) {
                  float var12 = this.tmp.getMinX();
                  float var13 = this.tmp.getMinY();
                  float var14 = this.tmp.getMinZ();
                  float var15 = this.tmp.getMaxX();
                  float var16 = this.tmp.getMaxY();
                  float var17 = this.tmp.getMaxZ();
                  if (var12 <= var4) {
                     var4 = var12;
                     this.left = var11;
                     var3 &= -2;
                  }

                  if (var13 <= var5) {
                     var5 = var13;
                     this.top = var11;
                     var3 &= -3;
                  }

                  if (var14 <= var6) {
                     var6 = var14;
                     this.near = var11;
                     var3 &= -5;
                  }

                  if (var15 >= var7) {
                     var7 = var15;
                     this.right = var11;
                     var3 &= -9;
                  }

                  if (var16 >= var8) {
                     var8 = var16;
                     this.bottom = var11;
                     var3 &= -17;
                  }

                  if (var17 >= var9) {
                     var9 = var17;
                     this.far = var11;
                     var3 &= -33;
                  }
               }
            }
         }

         if (var3 != 0) {
            return false;
         } else {
            this.cachedBounds = this.cachedBounds.deriveWithNewBounds(var4, var5, var6, var7, var8, var9);
            return true;
         }
      }
   }

   private void createCachedBounds(List<Node> var1) {
      int var8 = var1.size();

      int var9;
      for (var9 = 0; var9 < var8; var9++) {
         Node var10 = (Node)var1.get(var9);
         var10.boundsChanged = false;
         if (var10.isVisible()) {
            this.tmp = var10.getTransformedBounds(this.tmp, BaseTransform.IDENTITY_TRANSFORM);
            if (!this.tmp.isEmpty()) {
               this.left = this.top = this.near = this.right = this.bottom = this.far = var10;
               break;
            }
         }
      }

      if (var9 == var8) {
         this.left = this.top = this.near = this.right = this.bottom = this.far = null;
         this.cachedBounds.makeEmpty();
      } else {
         float var2 = this.tmp.getMinX();
         float var3 = this.tmp.getMinY();
         float var4 = this.tmp.getMinZ();
         float var5 = this.tmp.getMaxX();
         float var6 = this.tmp.getMaxY();
         float var7 = this.tmp.getMaxZ();
         var9++;

         for (; var9 < var8; var9++) {
            Node var18 = (Node)var1.get(var9);
            var18.boundsChanged = false;
            if (var18.isVisible()) {
               this.tmp = var18.getTransformedBounds(this.tmp, BaseTransform.IDENTITY_TRANSFORM);
               if (!this.tmp.isEmpty()) {
                  float var11 = this.tmp.getMinX();
                  float var12 = this.tmp.getMinY();
                  float var13 = this.tmp.getMinZ();
                  float var14 = this.tmp.getMaxX();
                  float var15 = this.tmp.getMaxY();
                  float var16 = this.tmp.getMaxZ();
                  if (var11 < var2) {
                     var2 = var11;
                     this.left = var18;
                  }

                  if (var12 < var3) {
                     var3 = var12;
                     this.top = var18;
                  }

                  if (var13 < var4) {
                     var4 = var13;
                     this.near = var18;
                  }

                  if (var14 > var5) {
                     var5 = var14;
                     this.right = var18;
                  }

                  if (var15 > var6) {
                     var6 = var15;
                     this.bottom = var18;
                  }

                  if (var16 > var7) {
                     var7 = var16;
                     this.far = var18;
                  }
               }
            }
         }

         this.cachedBounds = this.cachedBounds.deriveWithNewBounds(var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   protected void updateBounds() {
      int var1 = 0;

      for (int var2 = this.children.size(); var1 < var2; var1++) {
         ((Node)this.children.get(var1)).updateBounds();
      }

      super.updateBounds();
   }

   private BaseBounds getChildTransformedBounds(Node var1, BaseTransform var2, BaseBounds var3) {
      this.currentlyProcessedChild = var1;
      var3 = var1.getTransformedBounds(var3, var2);
      this.currentlyProcessedChild = null;
      return var3;
   }

   void childBoundsChanged(Node var1) {
      if (var1 != this.currentlyProcessedChild) {
         this.cachedBoundsInvalid = true;
         this.setChildDirty(var1, true);
         NodeHelper.geomChanged(this);
      }
   }

   void childVisibilityChanged(Node var1) {
      if (var1.isVisible()) {
         this.childIncluded(var1);
      } else {
         this.childExcluded(var1);
      }

      NodeHelper.geomChanged(this);
   }

   private boolean doComputeContains(double var1, double var3) {
      Point2D var5 = TempState.getInstance().point;
      int var6 = 0;

      for (int var7 = this.children.size(); var6 < var7; var6++) {
         Node var8 = (Node)this.children.get(var6);
         var5.x = (float)var1;
         var5.y = (float)var3;

         try {
            var8.parentToLocal(var5);
         } catch (NoninvertibleTransformException var10) {
            continue;
         }

         if (var8.contains((double)var5.x, (double)var5.y)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case CHILDREN:
            return this.getChildrenUnmodifiable();
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   void releaseAccessible() {
      int var1 = 0;

      for (int var2 = this.children.size(); var1 < var2; var1++) {
         Node var3 = (Node)this.children.get(var1);
         var3.releaseAccessible();
      }

      super.releaseAccessible();
   }

   List<Node> test_getRemoved() {
      return this.removed;
   }

   List<Node> test_getViewOrderChildren() {
      return this.viewOrderChildren;
   }

   static {
      ParentHelper.setParentAccessor(new ParentHelper.ParentAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Parent)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Parent)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Parent)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Parent)var1).doComputeContains(var2, var4);
         }

         @Override
         public void doProcessCSS(Node var1) {
            ((Parent)var1).doProcessCSS();
         }

         @Override
         public void doPickNodeLocal(Node var1, PickRay var2, PickResultChooser var3) {
            ((Parent)var1).doPickNodeLocal(var2, var3);
         }

         @Override
         public boolean pickChildrenNode(Parent var1, PickRay var2, PickResultChooser var3) {
            return var1.pickChildrenNode(var2, var3);
         }

         @Override
         public void setTraversalEngine(Parent var1, ParentTraversalEngine var2) {
            var1.setTraversalEngine(var2);
         }

         @Override
         public ParentTraversalEngine getTraversalEngine(Parent var1) {
            return var1.getTraversalEngine();
         }

         @Override
         public List<String> doGetAllParentStylesheets(Parent var1) {
            return var1.doGetAllParentStylesheets();
         }
      });
   }
}
