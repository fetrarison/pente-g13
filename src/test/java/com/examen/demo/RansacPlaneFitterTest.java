package com.examen.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.examen.demo.algorithms.RansacPlaneFitter;
import com.examen.demo.geom.Point3D;
import com.examen.demo.geom.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class RansacPlaneFitterTest {

  @Test
  void findsDominantPlane_onNoisySyntheticCloud() {
    List<Point3D> cloud = new ArrayList<>();
    Random r = new Random(42);

    // Plan z = 0.577*x + petit bruit
    for (int i = 0; i < 800; i++) {
      double x = r.nextDouble() * 10;
      double y = r.nextDouble() * 10;
      double z = 0.577 * x + r.nextGaussian() * 1e-3;
      cloud.add(new Point3D(x, y, z));
    }
    // Outliers
    for (int i = 0; i < 20; i++) {
      cloud.add(new Point3D(r.nextDouble() * 10, r.nextDouble() * 10, 5 + r.nextDouble() * 5));
    }

    RansacPlaneFitter fitter = new RansacPlaneFitter();
    RansacPlaneFitter.RansacResult res = fitter.findDominantPlane(cloud, 4000, 0.01, 50);

    assertNotNull(res.plane);
    assertTrue(res.inliers.size() >= 700);

    // Normale attendue ~ (-0.577, 0, 1) normalisée
    Vec3 expected = new Vec3(-0.577, 0, 1).normalize();
    Vec3 got = res.plane.normal();
    double cos = Math.abs(expected.dot(got)); // insensible au signe
    assertTrue(cos > 0.99, "Normale estimée trop éloignée de l'attendue");
  }
}
