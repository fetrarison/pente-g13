package com.examen.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.examen.demo.geom.Point3D;
import com.examen.demo.geom.Vec3;
import org.junit.jupiter.api.Test;

class Point3DTest {
  @Test
  void toVec_convertsCoordinates() {
    Point3D p = new Point3D(1.2, -3.4, 5.6);
    Vec3 v = p.toVec();
    assertEquals(1.2, v.x, 1e-12);
    assertEquals(-3.4, v.y, 1e-12);
    assertEquals(5.6, v.z, 1e-12);
  }
}
