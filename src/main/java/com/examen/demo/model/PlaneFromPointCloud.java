package com.examen.demo.model;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PlaneFromPointCloud {

  // --------- Structures de base ----------
  public static class Point3D {
    public final double x, y, z;

    public Point3D(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Vec3 toVec() {
      return new Vec3(x, y, z);
    }
  }

  public static class Vec3 {
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

  // --------- Plan ax + by + cz + d = 0 ----------
  public static class Plane {
    public final double a, b, c, d; // normal=(a,b,c), d

    public Plane(double a, double b, double c, double d) {
      double n = Math.sqrt(a * a + b * b + c * c);
      if (n == 0) throw new IllegalArgumentException("Normal nulle");
      this.a = a / n;
      this.b = b / n;
      this.c = c / n;
      this.d = d / n; // normalisation
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

  // --------- RANSAC ----------
  public static class RansacResult {
    public final Plane plane;
    public final List<Point3D> inliers;

    public RansacResult(Plane plane, List<Point3D> inliers) {
      this.plane = plane;
      this.inliers = inliers;
    }
  }

  public static RansacResult findDominantPlaneRANSAC(
      List<Point3D> points, int iterations, double inlierThreshold, int minInliersForAccept) {
    if (points.size() < 3) throw new IllegalArgumentException("Besoin d'au moins 3 points");

    Plane bestPlane = null;
    List<Point3D> bestInliers = Collections.emptyList();
    ThreadLocalRandom rnd = ThreadLocalRandom.current();

    for (int it = 0; it < iterations; it++) {
      // Échantillonne 3 indices distincts
      int i1 = rnd.nextInt(points.size());
      int i2 = rnd.nextInt(points.size());
      int i3 = rnd.nextInt(points.size());
      if (i1 == i2 || i1 == i3 || i2 == i3) {
        it--;
        continue;
      }

      Point3D p1 = points.get(i1);
      Point3D p2 = points.get(i2);
      Point3D p3 = points.get(i3);

      Plane plane;
      try {
        plane = Plane.from3Points(p1, p2, p3);
      } catch (IllegalArgumentException ex) {
        continue;
      } // colinéaires

      // Compte les inliers
      List<Point3D> inliers = new ArrayList<>();
      for (Point3D p : points) {
        if (plane.distance(p) <= inlierThreshold) {
          inliers.add(p);
        }
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

  // --------- Mesures utiles ----------
  // Inclinaison du plan vs horizontal (XY), en degrés
  public static double inclinationDegFromHorizontal(Plane plane) {
    Vec3 n = plane.normal().normalize();
    Vec3 z = new Vec3(0, 0, 1);
    double cos = Math.abs(n.dot(z));
    double angleNZ = Math.toDegrees(Math.acos(clamp(cos, -1.0, 1.0))); // angle entre n et Z
    return 90.0 - angleNZ; // angle du plan vs XY
  }

  // Pente (%) = tan(theta) * 100
  public static double slopePercent(Plane plane) {
    double thetaDeg = inclinationDegFromHorizontal(plane);
    double thetaRad = Math.toRadians(thetaDeg);
    return Math.tan(thetaRad) * 100.0;
  }

  // Direction de plus grande pente (vecteur unitaire dans le plan)
  public static Vec3 maxSlopeDirection(Plane plane) {
    Vec3 n = plane.normal().normalize();
    Vec3 g = new Vec3(0, 0, -1); // "gravité"
    Vec3 gPar = g.sub(n.mul(g.dot(n))); // projection de g sur le plan
    double nn = gPar.norm();
    return nn == 0 ? gPar : gPar.mul(1.0 / nn);
  }

  // Pente entre 2 points (run horizontal, rise vertical, pente % et °)
  public static class SlopeSegment {
    public final double horizontalRun;
    public final double verticalRise;
    public final double slopePercent;
    public final double slopeDeg;

    public SlopeSegment(double run, double rise, double percent, double deg) {
      this.horizontalRun = run;
      this.verticalRise = rise;
      this.slopePercent = percent;
      this.slopeDeg = deg;
    }

    @Override
    public String toString() {
      return String.format(
          Locale.US,
          "run=%.3f, rise=%.3f, slope=%.2f%% (%.2f°)",
          horizontalRun,
          verticalRise,
          slopePercent,
          slopeDeg);
    }
  }

  public static SlopeSegment slopeBetween(Point3D A, Point3D B) {
    double dx = B.x - A.x, dy = B.y - A.y, dz = B.z - A.z;
    double run = Math.hypot(dx, dy); // distance projetée XY
    double rise = dz;
    if (run == 0) {
      double deg = (rise == 0) ? 0.0 : 90.0;
      double percent = (rise == 0) ? 0.0 : Double.POSITIVE_INFINITY;
      return new SlopeSegment(run, rise, percent, deg);
    }
    double deg = Math.toDegrees(Math.atan2(Math.abs(rise), run));
    double percent = Math.tan(Math.toRadians(deg)) * 100.0;
    return new SlopeSegment(run, rise, percent, deg);
  }

  private static double clamp(double v, double lo, double hi) {
    return Math.max(lo, Math.min(hi, v));
  }

  // --------- I/O : lecture CSV ----------
  static List<Point3D> readCsvPoints(String path) {
    List<Point3D> pts = new ArrayList<>();
    try {
      for (String raw : Files.readAllLines(Paths.get(path))) {
        String line = raw.trim();
        if (line.isEmpty() || line.startsWith("#") || line.startsWith("x")) continue;
        String[] t = line.split("\\s*,\\s*");
        if (t.length < 3) continue;
        double x = Double.parseDouble(t[0]);
        double y = Double.parseDouble(t[1]);
        double z = Double.parseDouble(t[2]);
        pts.add(new Point3D(x, y, z));
      }
    } catch (IOException e) {
      throw new RuntimeException("Erreur lecture CSV: " + e.getMessage(), e);
    }
    return pts;
  }

  static void printResult(RansacResult res) {
    Plane best = res.plane;
    double incDeg = inclinationDegFromHorizontal(best);
    double slopePct = slopePercent(best);
    Vec3 dir = maxSlopeDirection(best);
    System.out.printf(
        Locale.US, "Plan dominant: a=%.6f b=%.6f c=%.6f d=%.6f%n", best.a, best.b, best.c, best.d);
    System.out.println("Inliers: " + res.inliers.size());
    System.out.printf(
        Locale.US, "Inclinaison (vs horizontal): %.2f°  |  Pente: %.2f%%%n", incDeg, slopePct);
    System.out.printf(
        Locale.US, "Direction max pente (unitaire): [%.4f, %.4f, %.4f]%n", dir.x, dir.y, dir.z);
  }

  // --------- Main ----------
  public static void main(String[] args) {
    List<Point3D> cloud;

    if (args.length >= 1) {
      cloud = readCsvPoints(args[0]);
      System.out.println("Points chargés depuis " + args[0] + " : " + cloud.size());
    } else {
      // Jeu synthétique: plan ~30° + bruit + outliers
      cloud = new ArrayList<>();
      Random r = new Random(0);
      for (int i = 0; i < 1000; i++) {
        double x = r.nextDouble() * 10.0;
        double y = r.nextDouble() * 10.0;
        double z = 0.577 * x + r.nextGaussian() * 0.02; // ~tan(30°)=0.577
        cloud.add(new Point3D(x, y, z));
      }
      for (int i = 0; i < 60; i++) {
        double x = r.nextDouble() * 10.0;
        double y = r.nextDouble() * 10.0;
        double z = r.nextDouble() * 5.0 + 5.0; // outliers
        cloud.add(new Point3D(x, y, z));
      }
      System.out.println("Points synthétiques générés : " + cloud.size());
    }

    // RANSAC — ajuste ces paramètres selon ton échelle et ton bruit
    int iterations = 2000;
    double inlierThreshold = 0.05; // mètres
    int minInliersForAccept = Math.min(8, cloud.size()); // bas pour petits CSV

    RansacResult res =
        findDominantPlaneRANSAC(cloud, iterations, inlierThreshold, minInliersForAccept);
    printResult(res);

    // Détection d’un second plan (optionnelle)
    List<Point3D> remaining = new ArrayList<>(cloud);
    remaining.removeAll(res.inliers);
    if (remaining.size() >= 3) {
      try {
        RansacResult res2 =
            findDominantPlaneRANSAC(
                remaining, iterations, inlierThreshold, Math.min(8, remaining.size()));
        System.out.println("\nSecond plan détecté :");
        printResult(res2);
      } catch (RuntimeException ignore) {
        // pas de second plan suffisamment grand
      }
    }

    // Exemple d’utilisation de slopeBetween si tu as deux points sur le plan
    Point3D A = new Point3D(0, 0, 0);
    Point3D B = new Point3D(5, 0, 0.577 * 5);
    SlopeSegment seg = slopeBetween(A, B);
    System.out.println("\nPente locale entre A et B: " + seg);
  }
}
