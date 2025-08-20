package com.examen.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.examen.demo.geom.Point3D;
import com.examen.demo.geom.Vec3;
import com.examen.demo.metrics.PlaneMetrics;
import com.examen.demo.model.Plane;
import org.junit.jupiter.api.Test;

class PlaneMetricsTest {

  @Test
  void horizontalPlane_has0degInclination_and0pctSlope_andNoDirection() {
    Plane plane = new Plane(0, 0, 1, 0); // plan XY
    double inc = PlaneMetrics.inclinationDegFromHorizontal(plane);
    double slopePct = PlaneMetrics.slopePercent(plane);
    Vec3 dir = PlaneMetrics.maxSlopeDirection(plane);

    assertEquals(0.0, inc, 1e-9);
    assertEquals(0.0, slopePct, 1e-9);
    assertEquals(0.0, dir.norm(), 1e-12); // vecteur nul attendu
  }

  @Test
  void planeAt30deg_has30degInclination_and57pctSlope() {
    // Plan z = 0.577 * x  (≈ tan(30°))
    Plane plane = new Plane(-0.577, 0, 1, 0); // normale ~ (-m,0,1) (normalisée par le ctor)
    double inc = PlaneMetrics.inclinationDegFromHorizontal(plane);
    double slopePct = PlaneMetrics.slopePercent(plane);

    assertEquals(30.0, inc, 0.5); // tolérance 0.5°
    assertEquals(57.735, slopePct, 0.5); // ≈ tan(30°)*100
  }

  @Test
  void slopeBetween_points() {
    Point3D A = new Point3D(0, 0, 0);
    Point3D B = new Point3D(5, 0, 5);

    PlaneMetrics.SlopeSegment seg = PlaneMetrics.slopeBetween(A, B);

    assertEquals(5.0, seg.horizontalRun, 1e-9);
    assertEquals(5.0, seg.verticalRise, 1e-9);
    assertEquals(45.0, seg.slopeDeg, 1e-6);
    assertEquals(100.0, seg.slopePercent, 1e-6);
  }

  @Test
  void slopeBetween_verticalRunZero_handlesDegenerateCase() {
    Point3D A = new Point3D(1, 1, 0);
    Point3D B = new Point3D(1, 1, 2);

    PlaneMetrics.SlopeSegment seg = PlaneMetrics.slopeBetween(A, B);

    assertEquals(0.0, seg.horizontalRun, 1e-12);
    assertEquals(2.0, seg.verticalRise, 1e-12);
    assertEquals(90.0, seg.slopeDeg, 1e-9);
    assertTrue(Double.isInfinite(seg.slopePercent));
  }
}
