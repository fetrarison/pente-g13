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
