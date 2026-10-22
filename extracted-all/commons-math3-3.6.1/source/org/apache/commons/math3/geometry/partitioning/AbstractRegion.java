package org.apache.commons.math3.geometry.partitioning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.Vector;

public abstract class AbstractRegion<S extends Space, T extends Space> implements Region<S> {
   private BSPTree<S> tree;
   private final double tolerance;
   private double size;
   private Point<S> barycenter;

   protected AbstractRegion(double tolerance) {
      this.tree = new BSPTree<>(Boolean.TRUE);
      this.tolerance = tolerance;
   }

   protected AbstractRegion(BSPTree<S> tree, double tolerance) {
      this.tree = tree;
      this.tolerance = tolerance;
   }

   protected AbstractRegion(Collection<SubHyperplane<S>> boundary, double tolerance) {
      this.tolerance = tolerance;
      if (boundary.size() == 0) {
         this.tree = new BSPTree<>(Boolean.TRUE);
      } else {
         TreeSet<SubHyperplane<S>> ordered = new TreeSet<>(new Comparator<SubHyperplane<S>>() {
            public int compare(SubHyperplane<S> o1, SubHyperplane<S> o2) {
               double size1 = o1.getSize();
               double size2 = o2.getSize();
               return size2 < size1 ? -1 : (o1 == o2 ? 0 : 1);
            }
         });
         ordered.addAll(boundary);
         this.tree = new BSPTree<>();
         this.insertCuts(this.tree, ordered);
         this.tree.visit(new BSPTreeVisitor<S>() {
            @Override
            public BSPTreeVisitor.Order visitOrder(BSPTree<S> node) {
               return BSPTreeVisitor.Order.PLUS_SUB_MINUS;
            }

            @Override
            public void visitInternalNode(BSPTree<S> node) {
            }

            @Override
            public void visitLeafNode(BSPTree<S> node) {
               if (node.getParent() != null && node != node.getParent().getMinus()) {
                  node.setAttribute(Boolean.FALSE);
               } else {
                  node.setAttribute(Boolean.TRUE);
               }
            }
         });
      }
   }

   public AbstractRegion(Hyperplane<S>[] hyperplanes, double tolerance) {
      this.tolerance = tolerance;
      if (hyperplanes != null && hyperplanes.length != 0) {
         this.tree = hyperplanes[0].wholeSpace().getTree(false);
         BSPTree<S> node = this.tree;
         node.setAttribute(Boolean.TRUE);

         for (Hyperplane<S> hyperplane : hyperplanes) {
            if (node.insertCut(hyperplane)) {
               node.setAttribute(null);
               node.getPlus().setAttribute(Boolean.FALSE);
               node = node.getMinus();
               node.setAttribute(Boolean.TRUE);
            }
         }
      } else {
         this.tree = new BSPTree<>(Boolean.FALSE);
      }
   }

   public abstract AbstractRegion<S, T> buildNew(BSPTree<S> var1);

   public double getTolerance() {
      return this.tolerance;
   }

   private void insertCuts(BSPTree<S> node, Collection<SubHyperplane<S>> boundary) {
      Iterator<SubHyperplane<S>> iterator = boundary.iterator();
      Hyperplane<S> inserted = null;

      while (inserted == null && iterator.hasNext()) {
         inserted = iterator.next().getHyperplane();
         if (!node.insertCut(inserted.copySelf())) {
            inserted = null;
         }
      }

      if (iterator.hasNext()) {
         ArrayList<SubHyperplane<S>> plusList = new ArrayList<>();
         ArrayList<SubHyperplane<S>> minusList = new ArrayList<>();

         while (iterator.hasNext()) {
            SubHyperplane<S> other = iterator.next();
            SubHyperplane.SplitSubHyperplane<S> split = other.split(inserted);
            switch (split.getSide()) {
               case PLUS:
                  plusList.add(other);
                  break;
               case MINUS:
                  minusList.add(other);
                  break;
               case BOTH:
                  plusList.add(split.getPlus());
                  minusList.add(split.getMinus());
            }
         }

         this.insertCuts(node.getPlus(), plusList);
         this.insertCuts(node.getMinus(), minusList);
      }
   }

   public AbstractRegion<S, T> copySelf() {
      return this.buildNew(this.tree.copySelf());
   }

   @Override
   public boolean isEmpty() {
      return this.isEmpty(this.tree);
   }

   @Override
   public boolean isEmpty(BSPTree<S> node) {
      return node.getCut() == null ? !(Boolean)node.getAttribute() : this.isEmpty(node.getMinus()) && this.isEmpty(node.getPlus());
   }

   @Override
   public boolean isFull() {
      return this.isFull(this.tree);
   }

   @Override
   public boolean isFull(BSPTree<S> node) {
      return node.getCut() == null ? (Boolean)node.getAttribute() : this.isFull(node.getMinus()) && this.isFull(node.getPlus());
   }

   @Override
   public boolean contains(Region<S> region) {
      return new RegionFactory<S>().difference(region, this).isEmpty();
   }

   @Override
   public BoundaryProjection<S> projectToBoundary(Point<S> point) {
      BoundaryProjector<S, T> projector = new BoundaryProjector<>(point);
      this.getTree(true).visit(projector);
      return projector.getProjection();
   }

   public Region.Location checkPoint(Vector<S> point) {
      return this.checkPoint(point);
   }

   @Override
   public Region.Location checkPoint(Point<S> point) {
      return this.checkPoint(this.tree, point);
   }

   protected Region.Location checkPoint(BSPTree<S> node, Vector<S> point) {
      return this.checkPoint(node, point);
   }

   protected Region.Location checkPoint(BSPTree<S> node, Point<S> point) {
      BSPTree<S> cell = node.getCell(point, this.tolerance);
      if (cell.getCut() == null) {
         return cell.getAttribute() ? Region.Location.INSIDE : Region.Location.OUTSIDE;
      } else {
         Region.Location minusCode = this.checkPoint(cell.getMinus(), point);
         Region.Location plusCode = this.checkPoint(cell.getPlus(), point);
         return minusCode == plusCode ? minusCode : Region.Location.BOUNDARY;
      }
   }

   @Override
   public BSPTree<S> getTree(boolean includeBoundaryAttributes) {
      if (includeBoundaryAttributes && this.tree.getCut() != null && this.tree.getAttribute() == null) {
         this.tree.visit(new BoundaryBuilder<>());
      }

      return this.tree;
   }

   @Override
   public double getBoundarySize() {
      BoundarySizeVisitor<S> visitor = new BoundarySizeVisitor<>();
      this.getTree(true).visit(visitor);
      return visitor.getSize();
   }

   @Override
   public double getSize() {
      if (this.barycenter == null) {
         this.computeGeometricalProperties();
      }

      return this.size;
   }

   protected void setSize(double size) {
      this.size = size;
   }

   @Override
   public Point<S> getBarycenter() {
      if (this.barycenter == null) {
         this.computeGeometricalProperties();
      }

      return this.barycenter;
   }

   protected void setBarycenter(Vector<S> barycenter) {
      this.setBarycenter(barycenter);
   }

   protected void setBarycenter(Point<S> barycenter) {
      this.barycenter = barycenter;
   }

   protected abstract void computeGeometricalProperties();

   @Deprecated
   @Override
   public Side side(Hyperplane<S> hyperplane) {
      InsideFinder<S> finder = new InsideFinder<>(this);
      finder.recurseSides(this.tree, hyperplane.wholeHyperplane());
      return finder.plusFound() ? (finder.minusFound() ? Side.BOTH : Side.PLUS) : (finder.minusFound() ? Side.MINUS : Side.HYPER);
   }

   @Override
   public SubHyperplane<S> intersection(SubHyperplane<S> sub) {
      return this.recurseIntersection(this.tree, sub);
   }

   private SubHyperplane<S> recurseIntersection(BSPTree<S> node, SubHyperplane<S> sub) {
      if (node.getCut() == null) {
         return node.getAttribute() ? sub.copySelf() : null;
      } else {
         Hyperplane<S> hyperplane = node.getCut().getHyperplane();
         SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
         if (split.getPlus() != null) {
            if (split.getMinus() != null) {
               SubHyperplane<S> plus = this.recurseIntersection(node.getPlus(), split.getPlus());
               SubHyperplane<S> minus = this.recurseIntersection(node.getMinus(), split.getMinus());
               if (plus == null) {
                  return minus;
               } else {
                  return minus == null ? plus : plus.reunite(minus);
               }
            } else {
               return this.recurseIntersection(node.getPlus(), sub);
            }
         } else {
            return split.getMinus() != null
               ? this.recurseIntersection(node.getMinus(), sub)
               : this.recurseIntersection(node.getPlus(), this.recurseIntersection(node.getMinus(), sub));
         }
      }
   }

   public AbstractRegion<S, T> applyTransform(Transform<S, T> transform) {
      Map<BSPTree<S>, BSPTree<S>> map = new HashMap<>();
      BSPTree<S> transformedTree = this.recurseTransform(this.getTree(false), transform, map);

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

      return this.buildNew(transformedTree);
   }

   private BSPTree<S> recurseTransform(BSPTree<S> node, Transform<S, T> transform, Map<BSPTree<S>, BSPTree<S>> map) {
      BSPTree<S> transformedNode;
      if (node.getCut() == null) {
         transformedNode = new BSPTree<>(node.getAttribute());
      } else {
         SubHyperplane<S> sub = node.getCut();
         SubHyperplane<S> tSub = ((AbstractSubHyperplane)sub).applyTransform(transform);
         BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
         if (attribute != null) {
            SubHyperplane<S> tPO = attribute.getPlusOutside() == null ? null : ((AbstractSubHyperplane)attribute.getPlusOutside()).applyTransform(transform);
            SubHyperplane<S> tPI = attribute.getPlusInside() == null ? null : ((AbstractSubHyperplane)attribute.getPlusInside()).applyTransform(transform);
            attribute = new BoundaryAttribute<>(tPO, tPI, new NodesSet<>());
         }

         transformedNode = new BSPTree<>(
            tSub, this.recurseTransform(node.getPlus(), transform, map), this.recurseTransform(node.getMinus(), transform, map), attribute
         );
      }

      map.put(node, transformedNode);
      return transformedNode;
   }
}
