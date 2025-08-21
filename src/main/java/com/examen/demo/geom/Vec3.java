package com.examen.demo.geom;

public class Vec3 {
  public final double x, y, z;

  public Vec3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vec3 add(Vec3 o) {
    return new Vec3(x + o.x, y + o.y, z + o.z);
  }

  public Vec3 sub(Vec3 o) {
    return new Vec3(x - o.x, y - o.y, z - o.z);
  }

  public Vec3 mul(double s) {
    return new Vec3(x * s, y * s, z * s);
  }

  public double dot(Vec3 o) {
    return x * o.x + y * o.y + z * o.z;
  }

  public Vec3 cross(Vec3 o) {
    return new Vec3(y * o.z - z * o.y, z * o.x - x * o.z, x * o.y - y * o.x);
  }

  public double norm() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  public Vec3 normalize() {
    double n = norm();
    return n == 0 ? this : new Vec3(x / n, y / n, z / n);
  }
}
