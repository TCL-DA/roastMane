package org.apache.commons.math3.geometry.partitioning;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.math3.geometry.Space;

public abstract class AbstractSubHyperplane<S extends Space, T extends Space> implements SubHyperplane<S> {
   private final Hyperplane<S> hyperplane;
   private final Region<T> remainingRegion;

   protected AbstractSubHyperplane(Hyperplane<S> hyperplane, Region<T> remainingRegion) {
      this.hyperplane = hyperplane;
      this.remainingRegion = remainingRegion;
   }

   protected abstract AbstractSubHyperplane<S, T> buildNew(Hyperplane<S> var1, Region<T> var2);

   public AbstractSubHyperplane<S, T> copySelf() {
      return this.buildNew(this.hyperplane.copySelf(), this.remainingRegion);
   }

   @Override
   public Hyperplane<S> getHyperplane() {
      return this.hyperplane;
   }

   public Region<T> getRemainingRegion() {
      return this.remainingRegion;
   }

   @Override
   public double getSize() {
      return this.remainingRegion.getSize();
   }

   public AbstractSubHyperplane<S, T> reunite(SubHyperplane<S> other) {
      AbstractSubHyperplane<S, T> o = (AbstractSubHyperplane<S, T>)other;
      return this.buildNew(this.hyperplane, new RegionFactory<T>().union(this.remainingRegion, o.remainingRegion));
   }

   public AbstractSubHyperplane<S, T> applyTransform(Transform<S, T> transform) {
      Hyperplane<S> tHyperplane = transform.apply(this.hyperplane);
      Map<BSPTree<T>, BSPTree<T>> map = new HashMap<>();
      BSPTree<T> tTree = this.recurseTransform(this.remainingRegion.getTree(false), tHyperplane, transform, map);

      for (Entry<BSPTree<T>, BSPTree<T>> entry : map.entrySet()) {
         if (entry.getKey().getCut() != null) {
            BoundaryAttribute<T> original = (BoundaryAttribute<T>)entry.getKey().getAttribute();
            if (original != null) {
               BoundaryAttribute<T> transformed = (BoundaryAttribute<T>)entry.getValue().getAttribute();

               for (BSPTree<T> splitter : original.getSplitters()) {
                  transformed.getSplitters().add(map.get(splitter));
               }
            }
         }
      }

      return this.buildNew(tHyperplane, this.remainingRegion.buildNew(tTree));
   }

   private BSPTree<T> recurseTransform(BSPTree<T> node, Hyperplane<S> transformed, Transform<S, T> transform, Map<BSPTree<T>, BSPTree<T>> map) {
      BSPTree<T> transformedNode;
      if (node.getCut() == null) {
         transformedNode = new BSPTree<>(node.getAttribute());
      } else {
         BoundaryAttribute<T> attribute = (BoundaryAttribute<T>)node.getAttribute();
         if (attribute != null) {
            SubHyperplane<T> tPO = attribute.getPlusOutside() == null ? null : transform.apply(attribute.getPlusOutside(), this.hyperplane, transformed);
            SubHyperplane<T> tPI = attribute.getPlusInside() == null ? null : transform.apply(attribute.getPlusInside(), this.hyperplane, transformed);
            attribute = new BoundaryAttribute<>(tPO, tPI, new NodesSet<>());
         }

         transformedNode = new BSPTree<>(
            transform.apply(node.getCut(), this.hyperplane, transformed),
            this.recurseTransform(node.getPlus(), transformed, transform, map),
            this.recurseTransform(node.getMinus(), transformed, transform, map),
            attribute
         );
      }

      map.put(node, transformedNode);
      return transformedNode;
   }

   @Deprecated
   @Override
   public Side side(Hyperplane<S> hyper) {
      return this.split(hyper).getSide();
   }

   @Override
   public abstract SubHyperplane.SplitSubHyperplane<S> split(Hyperplane<S> var1);

   @Override
   public boolean isEmpty() {
      return this.remainingRegion.isEmpty();
   }
}
