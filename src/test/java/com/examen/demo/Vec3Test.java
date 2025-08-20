package com.examen.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.examen.demo.geom.Vec3;
import org.junit.jupiter.api.Test;

class Vec3Test {

  @Test
  void basicOps_dot_cross_norm_normalize() {
    Vec3 i = new Vec3(1, 0, 0);
    Vec3 j = new Vec3(0, 1, 0);

    assertEquals(0.0, i.dot(j), 1e-12);
    Vec3 k = i.cross(j);
    assertEquals(0.0, k.x, 1e-12);
    assertEquals(0.0, k.y, 1e-12);
    assertEquals(1.0, k.z, 1e-12);

    Vec3 v = new Vec3(3, 4, 0);
    assertEquals(5.0, v.norm(), 1e-12);

    Vec3 u = v.normalize();
    assertEquals(1.0, u.norm(), 1e-12);
  }

  @Test
  void add_sub_mul() {
    Vec3 a = new Vec3(1, 2, 3);
    Vec3 b = new Vec3(-1, 0, 4);

    Vec3 s = a.add(b);
    assertEquals(0.0, s.x, 1e-12);
    assertEquals(2.0, s.y, 1e-12);
    assertEquals(7.0, s.z, 1e-12);

    Vec3 d = a.sub(b);
    assertEquals(2.0, d.x, 1e-12);
    assertEquals(2.0, d.y, 1e-12);
    assertEquals(-1.0, d.z, 1e-12);

    Vec3 m = a.mul(2.5);
    assertEquals(2.5, m.x, 1e-12);
    assertEquals(5.0, m.y, 1e-12);
    assertEquals(7.5, m.z, 1e-12);
  }
}
