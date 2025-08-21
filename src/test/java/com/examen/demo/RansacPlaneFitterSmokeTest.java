package com.examen.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.examen.demo.algorithms.RansacPlaneFitter;
import com.examen.demo.geom.Point3D;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RansacPlaneFitterSmokeTest {

  @Test
  void findsAPlane_onThreeExactPoints() {
    List<Point3D> pts = new ArrayList<>();
    pts.add(new Point3D(0, 0, 0));
    pts.add(new Point3D(1, 0, 0));
    pts.add(new Point3D(0, 1, 0));

    RansacPlaneFitter f = new RansacPlaneFitter();
    RansacPlaneFitter.RansacResult res = f.findDominantPlane(pts, 50, 1e-9, 3);

    assertNotNull(res.plane);
    assertEquals(3, res.inliers.size());
  }
}
