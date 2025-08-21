package com.examen.demo.model;

import com.examen.demo.algorithms.RansacPlaneFitter;
import com.examen.demo.geom.Point3D;
import com.examen.demo.geom.Vec3;
import com.examen.demo.io.CsvPointReader;
import com.examen.demo.metrics.PlaneMetrics;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Plane {
  public final double a, b, c, d; // normal=(a,b,c) (normalisée), d

  public Plane(double a, double b, double c, double d) {
    double n = Math.sqrt(a * a + b * b + c * c);
    if (n == 0) throw new IllegalArgumentException("Normal nulle");
    this.a = a / n;
    this.b = b / n;
    this.c = c / n;
    this.d = d / n;
  }

  public Vec3 normal() {
    return new Vec3(a, b, c);
  }

  public double distance(Point3D p) {
    return Math.abs(a * p.x + b * p.y + c * p.z + d);
  }

  public static Plane from3Points(Point3D p1, Point3D p2, Point3D p3) {
    Vec3 v1 = p2.toVec().sub(p1.toVec());
    Vec3 v2 = p3.toVec().sub(p1.toVec());
    Vec3 n = v1.cross(v2);
    if (n.norm() == 0) throw new IllegalArgumentException("Points colinéaires");
    double d = -n.dot(p1.toVec());
    return new Plane(n.x, n.y, n.z, d);
  }
}
