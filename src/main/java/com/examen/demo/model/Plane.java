package com.examen.demo.model;

import com.examen.demo.algorithms.RansacPlaneFitter;
import com.examen.demo.geom.Point3D;
import com.examen.demo.geom.Vec3;
import com.examen.demo.io.CsvPointReader;
import com.examen.demo.metrics.PlaneMetrics;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

  public static List<Point3D> generateTestPlane() {
    List<Point3D> cloud = new ArrayList<>();
    Random r = new Random(0);

    // Plan z = 2*x + 3*y + 5
    for (int i = 0; i < 100; i++) {
      double x = i * 0.1;
      double y = i * 0.1;
      double z = 2 * x + 3 * y + 5 + r.nextGaussian() * 0.01; // léger bruit
      cloud.add(new Point3D(x, y, z));
    }

    // Ajouter quelques outliers
    for (int i = 0; i < 5; i++) {
      double x = r.nextDouble() * 10;
      double y = r.nextDouble() * 10;
      double z = r.nextDouble() * 50 + 50;
      cloud.add(new Point3D(x, y, z));
    }

    return cloud;
  }

  /**
   * Calcul de l'inclinaison du plan par rapport à l'axe Z
   * Inclinaison = angle entre normale du plan et axe Z
   */
  public double computeInclination() {
    // normale du plan (a, b, -1)
    double nx = a;
    double ny = b;
    double nz = -1;

    // vecteur de l’axe Z
    double zx = 0;
    double zy = 0;
    double zz = 1;

    // produit scalaire
    double dot = nx * zx + ny * zy + nz * zz;

    // normes
    double normN = Math.sqrt(nx * nx + ny * ny + nz * nz);
    double normZ = Math.sqrt(zx * zx + zy * zy + zz * zz);

    // cos(theta) = (N·Z) / (||N||*||Z||)
    double cosTheta = dot / (normN * normZ);

    // angle en radians → degrés
    return Math.toDegrees(Math.acos(cosTheta));
  }

  public Vec3 normal() {
    return new Vec3(a, b, c);
  }

  public double distance(Point3D p) {
    return Math.abs(a * p.x + b * p.y + c * p.z + d);
  }

  public double distanceToPoint(Point3D p) {
    return Math.abs(a*p.getX() + b*p.getY() + c*p.getZ() + d) /
            Math.sqrt(a*a + b*b + c*c);
  }

  public static Plane from3Points(Point3D p1, Point3D p2, Point3D p3) {
    Vec3 v1 = p2.toVec().sub(p1.toVec());
    Vec3 v2 = p3.toVec().sub(p1.toVec());
    Vec3 n = v1.cross(v2);
    if (n.norm() == 0) throw new IllegalArgumentException("Points colinéaires");
    double d = -n.dot(p1.toVec());
    return new Plane(n.x, n.y, n.z, d);
  }

  public static void main(String[] args) {
    // Charger le nuage de points : test avec plan synthétique
    List<Point3D> cloud = generateTestPlane();
    System.out.println("Points de test générés : " + cloud.size());

    // Paramètres RANSAC
    int iterations = 2000;
    double inlierThreshold = 0.05;
    int minInliersForAccept = Math.min(8, cloud.size());

    // Détection du plan dominant
    RansacPlaneFitter fitter = new RansacPlaneFitter();
    RansacPlaneFitter.RansacResult res =
            fitter.findDominantPlane(cloud, iterations, inlierThreshold, minInliersForAccept);

    // Affichage des résultats
    printResult(res);

    // Export CSV
    try (PrintWriter out = new PrintWriter("points.csv")) {
      for (Point3D p : cloud) {
        out.println(p.x + "," + p.y + "," + p.z);
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    try (PrintWriter out = new PrintWriter("plane1.csv")) {
      Plane p = res.plane;
      out.println(p.a + "," + p.b + "," + p.c + "," + p.d);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    System.out.println("CSV exportés : points.csv, plane1.csv");
  }


  private static void printResult(RansacPlaneFitter.RansacResult res) {
    Plane best = res.plane;
    double incDeg = PlaneMetrics.inclinationDegFromHorizontal(best);
    double slopePct = PlaneMetrics.slopePercent(best);
    Vec3 dir = PlaneMetrics.maxSlopeDirection(best);
    System.out.printf(
        Locale.US, "Plan dominant: a=%.6f b=%.6f c=%.6f d=%.6f%n", best.a, best.b, best.c, best.d);
    System.out.println("Inliers: " + res.inliers.size());
    System.out.printf(
        Locale.US, "Inclinaison (vs horizontal): %.2f°  |  Pente: %.2f%%%n", incDeg, slopePct);
    System.out.printf(
        Locale.US, "Direction max pente (unitaire): [%.4f, %.4f, %.4f]%n", dir.x, dir.y, dir.z);
  }

  public static List<Point3D> generateSynthetic() {
    List<Point3D> cloud = new ArrayList<>();
    Random r = new Random(0);
    for (int i = 0; i < 1000; i++) {
      double x = r.nextDouble() * 10.0;
      double y = r.nextDouble() * 10.0;
      double z = 0.577 * x + r.nextGaussian() * 0.02; // ~30°
      cloud.add(new Point3D(x, y, z));
    }
    for (int i = 0; i < 60; i++) {
      double x = r.nextDouble() * 10.0;
      double y = r.nextDouble() * 10.0;
      double z = r.nextDouble() * 5.0 + 5.0; // outliers
      cloud.add(new Point3D(x, y, z));
    }
    return cloud;
  }
}
