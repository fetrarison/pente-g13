package com.examen.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.examen.demo.geom.Point3D;
import com.examen.demo.model.Plane;
import org.junit.jupiter.api.Test;

class PlaneTest {

  @Test
  void from3Points_xyPlane() {
    Point3D p1 = new Point3D(0, 0, 0);
    Point3D p2 = new Point3D(1, 0, 0);
    Point3D p3 = new Point3D(0, 1, 0);

    Plane pl = Plane.from3Points(p1, p2, p3);

    assertEquals(0.0, pl.a, 1e-12);
    assertEquals(0.0, pl.b, 1e-12);
    assertEquals(1.0, Math.abs(pl.c), 1e-12);
    assertEquals(0.0, pl.distance(new Point3D(0.2, 0.3, 0.0)), 1e-12);
  }

  @Test
  void distance_pointOffPlane() {
    Plane pl = new Plane(0, 0, 1, 0);
    assertEquals(5.0, pl.distance(new Point3D(0, 0, 5)), 1e-12);
  }
}
