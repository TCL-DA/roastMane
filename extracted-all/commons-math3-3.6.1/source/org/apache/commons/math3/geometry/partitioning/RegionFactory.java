package org.apache.commons.math3.geometry.partitioning;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public class RegionFactory<S extends Space> {
   private final RegionFactory<S>.NodesCleaner nodeCleaner = new RegionFactory.NodesCleaner();

   public Region<S> buildConvex(Hyperplane<S>... hyperplanes) {
      if (hyperplanes != null && hyperplanes.length != 0) {
         Region<S> region = hyperplanes[0].wholeSpace();
         BSPTree<S> node = region.getTree(false);
         node.setAttribute(Boolean.TRUE);

         for (Hyperplane<S> hyperplane : hyperplanes) {
            if (node.insertCut(hyperplane)) {
               node.setAttribute(null);
               node.getPlus().setAttribute(Boolean.FALSE);
               node = node.getMinus();
               node.setAttribute(Boolean.TRUE);
            } else {
               SubHyperplane<S> s = hyperplane.wholeHyperplane();

               for (BSPTree<S> tree = node; tree.getParent() != null && s != null; tree = tree.getParent()) {
                  Hyperplane<S> other = tree.getParent().getCut().getHyperplane();
                  SubHyperplane.SplitSubHyperplane<S> split = s.split(other);
                  switch (split.getSide()) {
                     case HYPER:
                        if (!hyperplane.sameOrientationAs(other)) {
                           return this.getComplement(hyperplanes[0].wholeSpace());
                        }
                        break;
                     case PLUS:
                        throw new MathIllegalArgumentException(LocalizedFormats.NOT_CONVEX_HYPERPLANES);
                     default:
                        s = split.getMinus();
                  }
               }
            }
         }

         return region;
      } else {
         return null;
      }
   }

   public Region<S> union(Region<S> region1, Region<S> region2) {
      BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new RegionFactory.UnionMerger());
      tree.visit(this.nodeCleaner);
      return region1.buildNew(tree);
   }

   public Region<S> intersection(Region<S> region1, Region<S> region2) {
      BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new RegionFactory.IntersectionMerger());
      tree.visit(this.nodeCleaner);
      return region1.buildNew(tree);
   }

   public Region<S> xor(Region<S> region1, Region<S> region2) {
      BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new RegionFactory.XorMerger());
      tree.visit(this.nodeCleaner);
      return region1.buildNew(tree);
   }

   public Region<S> difference(Region<S> region1, Region<S> region2) {
      BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new RegionFactory.DifferenceMerger(region1, region2));
      tree.visit(this.nodeCleaner);
      return region1.buildNew(tree);
   }

   public Region<S> getComplement(Region<S> region) {
      return region.buildNew(this.recurseComplement(region.getTree(false)));
   }

   private BSPTree<S> recurseComplement(BSPTree<S> node) {
      Map<BSPTree<S>, BSPTree<S>> map = new HashMap<>();
      BSPTree<S> transformedTree = this.recurseComplement(node, map);

      for (Entry<BSPTree<S>, BSPTree<S>> entry : map.entrySet()) {
         if (entry.getKey().getCut() != null) {
            BoundaryAttribute<S> original = (BoundaryAttribute<S>)entry.getKey().getAttribute();
            if (original != null) {
               BoundaryAttribute<S> transformed = (BoundaryAttribute<S>)entry.getValue().getAttribute();

               for (BSPTree<S> splitter : original.getSplitters()) {
                  transformed.getSplitters().add(map.get(splitter));
               }
            }
         }
      }

      return transformedTree;
   }

   private BSPTree<S> recurseComplement(BSPTree<S> node, Map<BSPTree<S>, BSPTree<S>> map) {
      BSPTree<S> transformedNode;
      if (node.getCut() == null) {
         transformedNode = new BSPTree<>(node.getAttribute() ? Boolean.FALSE : Boolean.TRUE);
      } else {
         BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
         if (attribute != null) {
            SubHyperplane<S> plusOutside = attribute.getPlusInside() == null ? null : attribute.getPlusInside().copySelf();
            SubHyperplane<S> plusInside = attribute.getPlusOutside() == null ? null : attribute.getPlusOutside().copySelf();
            attribute = new BoundaryAttribute<>(plusOutside, plusInside, new NodesSet<>());
         }

         transformedNode = new BSPTree<>(
            node.getCut().copySelf(), this.recurseComplement(node.getPlus(), map), this.recurseComplement(node.getMinus(), map), attribute
         );
      }

      map.put(node, transformedNode);
      return transformedNode;
   }

   private class DifferenceMerger implements BSPTree.LeafMerger<S>, BSPTree.VanishingCutHandler<S> {
      private final Region<S> region1;
      private final Region<S> region2;

      DifferenceMerger(Region<S> region1, Region<S> region2) {
         this.region1 = region1.copySelf();
         this.region2 = region2.copySelf();
      }

      @Override
      public BSPTree<S> merge(BSPTree<S> leaf, BSPTree<S> tree, BSPTree<S> parentTree, boolean isPlusChild, boolean leafFromInstance) {
         if ((Boolean)leaf.getAttribute()) {
            BSPTree<S> argTree = RegionFactory.this.recurseComplement(leafFromInstance ? tree : leaf);
            argTree.insertInTree(parentTree, isPlusChild, this);
            return argTree;
         } else {
            BSPTree<S> instanceTree = leafFromInstance ? leaf : tree;
            instanceTree.insertInTree(parentTree, isPlusChild, this);
            return instanceTree;
         }
      }

      @Override
      public BSPTree<S> fixNode(BSPTree<S> node) {
         BSPTree<S> cell = node.pruneAroundConvexCell(Boolean.TRUE, Boolean.FALSE, null);
         Region<S> r = this.region1.buildNew(cell);
         Point<S> p = r.getBarycenter();
         return new BSPTree<>(this.region1.checkPoint(p) == Region.Location.INSIDE && this.region2.checkPoint(p) == Region.Location.OUTSIDE);
      }
   }

   private class IntersectionMerger implements BSPTree.LeafMerger<S> {
      private IntersectionMerger() {
      }

      @Override
      public BSPTree<S> merge(BSPTree<S> leaf, BSPTree<S> tree, BSPTree<S> parentTree, boolean isPlusChild, boolean leafFromInstance) {
         if ((Boolean)leaf.getAttribute()) {
            tree.insertInTree(parentTree, isPlusChild, RegionFactory.this.new VanishingToLeaf(true));
            return tree;
         } else {
            leaf.insertInTree(parentTree, isPlusChild, RegionFactory.this.new VanishingToLeaf(false));
            return leaf;
         }
      }
   }

   private class NodesCleaner implements BSPTreeVisitor<S> {
      private NodesCleaner() {
      }

      @Override
      public BSPTreeVisitor.Order visitOrder(BSPTree<S> node) {
         return BSPTreeVisitor.Order.PLUS_SUB_MINUS;
      }

      @Override
      public void visitInternalNode(BSPTree<S> node) {
         node.setAttribute(null);
      }

      @Override
      public void visitLeafNode(BSPTree<S> node) {
      }
   }

   private class UnionMerger implements BSPTree.LeafMerger<S> {
      private UnionMerger() {
      }

      @Override
      public BSPTree<S> merge(BSPTree<S> leaf, BSPTree<S> tree, BSPTree<S> parentTree, boolean isPlusChild, boolean leafFromInstance) {
         if ((Boolean)leaf.getAttribute()) {
            leaf.insertInTree(parentTree, isPlusChild, RegionFactory.this.new VanishingToLeaf(true));
            return leaf;
         } else {
            tree.insertInTree(parentTree, isPlusChild, RegionFactory.this.new VanishingToLeaf(false));
            return tree;
         }
      }
   }

   private class VanishingToLeaf implements BSPTree.VanishingCutHandler<S> {
      private final boolean inside;

      VanishingToLeaf(boolean inside) {
         this.inside = inside;
      }

      @Override
      public BSPTree<S> fixNode(BSPTree<S> node) {
         return node.getPlus().getAttribute().equals(node.getMinus().getAttribute())
            ? new BSPTree<>(node.getPlus().getAttribute())
            : new BSPTree<>(this.inside);
      }
   }

   private class XorMerger implements BSPTree.LeafMerger<S> {
      private XorMerger() {
      }

      @Override
      public BSPTree<S> merge(BSPTree<S> leaf, BSPTree<S> tree, BSPTree<S> parentTree, boolean isPlusChild, boolean leafFromInstance) {
         BSPTree<S> t = tree;
         if ((Boolean)leaf.getAttribute()) {
            t = RegionFactory.this.recurseComplement(tree);
         }

         t.insertInTree(parentTree, isPlusChild, RegionFactory.this.new VanishingToLeaf(true));
         return t;
      }
   }
}
