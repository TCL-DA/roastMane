package org.apache.commons.math3.geometry.spherical.twod;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.util.FastMath;

class PropertiesComputer implements BSPTreeVisitor<Sphere2D> {
   private final double tolerance;
   private double summedArea;
   private Vector3D summedBarycenter;
   private final List<Vector3D> convexCellsInsidePoints;

   PropertiesComputer(double tolerance) {
      this.tolerance = tolerance;
      this.summedArea = 0.0;
      this.summedBarycenter = Vector3D.ZERO;
      this.convexCellsInsidePoints = new ArrayList<>();
   }

   @Override
   public BSPTreeVisitor.Order visitOrder(BSPTree<Sphere2D> node) {
      return BSPTreeVisitor.Order.MINUS_SUB_PLUS;
   }

   @Override
   public void visitInternalNode(BSPTree<Sphere2D> node) {
   }

   @Override
   public void visitLeafNode(BSPTree<Sphere2D> node) {
      if ((Boolean)node.getAttribute()) {
         SphericalPolygonsSet convex = new SphericalPolygonsSet(node.pruneAroundConvexCell(Boolean.TRUE, Boolean.FALSE, null), this.tolerance);
         List<Vertex> boundary = convex.getBoundaryLoops();
         if (boundary.size() != 1) {
            throw new MathInternalError();
         }

         double area = this.convexCellArea(boundary.get(0));
         Vector3D barycenter = this.convexCellBarycenter(boundary.get(0));
         this.convexCellsInsidePoints.add(barycenter);
         this.summedArea += area;
         this.summedBarycenter = new Vector3D(1.0, this.summedBarycenter, area, barycenter);
      }
   }

   private double convexCellArea(Vertex start) {
      int n = 0;
      double sum = 0.0;

      for (Edge e = start.getOutgoing(); n == 0 || e.getStart() != start; e = e.getEnd().getOutgoing()) {
         Vector3D previousPole = e.getCircle().getPole();
         Vector3D nextPole = e.getEnd().getOutgoing().getCircle().getPole();
         Vector3D point = e.getEnd().getLocation().getVector();
         double alpha = FastMath.atan2(Vector3D.dotProduct(nextPole, Vector3D.crossProduct(point, previousPole)), -Vector3D.dotProduct(nextPole, previousPole));
         if (alpha < 0.0) {
            alpha += Math.PI * 2;
         }

         sum += alpha;
         n++;
      }

      return sum - (double)(n - 2) * Math.PI;
   }

   private Vector3D convexCellBarycenter(Vertex start) {
      int n = 0;
      Vector3D sumB = Vector3D.ZERO;

      for (Edge e = start.getOutgoing(); n == 0 || e.getStart() != start; e = e.getEnd().getOutgoing()) {
         sumB = new Vector3D(1.0, sumB, e.getLength(), e.getCircle().getPole());
         n++;
      }

      return sumB.normalize();
   }

   public double getArea() {
      return this.summedArea;
   }

   public S2Point getBarycenter() {
      return this.summedBarycenter.getNormSq() == 0.0 ? S2Point.NaN : new S2Point(this.summedBarycenter);
   }

   public List<Vector3D> getConvexCellsInsidePoints() {
      return this.convexCellsInsidePoints;
   }
}
