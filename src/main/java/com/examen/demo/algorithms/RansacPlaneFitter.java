package com.examen.demo.algorithms;

import com.examen.demo.geom.Point3D;
import com.examen.demo.model.Plane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RansacPlaneFitter {

  public static class RansacResult {
    public final Plane plane;
    public final List<Point3D> inliers;

    public RansacResult(Plane plane, List<Point3D> inliers) {
      this.plane = plane;
      this.inliers = inliers;
    }
  }

  public static Plane fitPlane(List<Point3D> points) {
    if (points.size() < 3) {
      throw new IllegalArgumentException("Au moins 3 points sont nécessaires pour définir un plan.");
    }

    Point3D p1 = points.get(0);
    Point3D p2 = points.get(1);
    Point3D p3 = points.get(2);

    // Vecteurs
    double ux = p2.x - p1.x;
    double uy = p2.y - p1.y;
    double uz = p2.z - p1.z;

    double vx = p3.x - p1.x;
    double vy = p3.y - p1.y;
    double vz = p3.z - p1.z;

    // Produit vectoriel (normale au plan)
    double a = uy * vz - uz * vy;
    double b = uz * vx - ux * vz;
    double c = ux * vy - uy * vx;

    // d = -(a*x0 + b*y0 + c*z0)
    double d = -(a * p1.x + b * p1.y + c * p1.z);

    return new Plane(a, b, c, d);
  }

  public RansacResult findDominantPlane(
      List<Point3D> points, int iterations, double inlierThreshold, int minInliersForAccept) {
    if (points == null || points.size() < 3)
      throw new IllegalArgumentException("Besoin d'au moins 3 points");

    Plane bestPlane = null;
    List<Point3D> bestInliers = Collections.emptyList();
    ThreadLocalRandom rnd = ThreadLocalRandom.current();

    for (int it = 0; it < iterations; it++) {
      int i1 = rnd.nextInt(points.size());
      int i2 = rnd.nextInt(points.size());
      int i3 = rnd.nextInt(points.size());
      if (i1 == i2 || i1 == i3 || i2 == i3) {
        it--;
        continue;
      }

      Plane plane;
      try {
        plane = Plane.from3Points(points.get(i1), points.get(i2), points.get(i3));
      } catch (IllegalArgumentException e) {
        continue;
      }

      List<Point3D> inliers = new ArrayList<>();
      for (Point3D p : points) {
        if (plane.distance(p) <= inlierThreshold) inliers.add(p);
      }

      if (inliers.size() > bestInliers.size()) {
        bestInliers = inliers;
        bestPlane = plane;
      }
    }

    if (bestPlane == null || bestInliers.size() < Math.max(minInliersForAccept, 3)) {
      throw new RuntimeException("Aucun plan dominant trouvé. Ajuste seuil/itérations/données.");
    }
    return new RansacResult(bestPlane, bestInliers);
  }
}
