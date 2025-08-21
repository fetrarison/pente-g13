package com.examen.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.examen.demo.geom.Vec3;
import org.junit.jupiter.api.Test;

public class GeometryTest {

  @Test
  void testNorm() {
    Vec3 v = new Vec3(3, 4, 0);
    assertEquals(5, v.norm(), 1e-6); // √(3²+4²)=5
  }

  @Test
  void testDotProduct() {
    Vec3 v1 = new Vec3(1, 2, 3);
    Vec3 v2 = new Vec3(4, -5, 6);
    assertEquals(12, v1.dot(v2), 1e-6); // 1*4 + 2*(-5) + 3*6 = 12
  }

  @Test
  void testCrossProduct() {
    Vec3 v1 = new Vec3(1, 0, 0);
    Vec3 v2 = new Vec3(0, 1, 0);
    Vec3 cross = v1.cross(v2);
    assertEquals(0, cross.x, 1e-9);
    assertEquals(0, cross.y, 1e-9);
    assertEquals(1, cross.z, 1e-9);
  }

  @Test
  void testAngleBetweenVectors() {
    Vec3 v1 = new Vec3(1, 0, 0);
    Vec3 v2 = new Vec3(0, 1, 0);
    double cos = v1.dot(v2) / (v1.norm() * v2.norm());
    double angle = Math.acos(cos);
    assertEquals(Math.PI / 2, angle, 1e-6); // 90°
  }

  @Test
  void testNormalize() {
    Vec3 v = new Vec3(3, 0, 4);
    Vec3 n = v.normalize();
    assertEquals(1.0, n.norm(), 1e-9);
  }
}
