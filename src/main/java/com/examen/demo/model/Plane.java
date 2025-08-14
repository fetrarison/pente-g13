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

    public Plane(double a, double b, double c, double d){
        double n = Math.sqrt(a*a + b*b + c*c);
        if(n == 0) throw new IllegalArgumentException("Normal nulle");
        this.a = a/n; this.b = b/n; this.c = c/n; this.d = d/n;
    }

    public Vec3 normal(){ return new Vec3(a,b,c); }

    public double distance(Point3D p){
        return Math.abs(a*p.x + b*p.y + c*p.z + d);
    }

    public static Plane from3Points(Point3D p1, Point3D p2, Point3D p3){
        Vec3 v1 = p2.toVec().sub(p1.toVec());
        Vec3 v2 = p3.toVec().sub(p1.toVec());
        Vec3 n  = v1.cross(v2);
        if(n.norm() == 0) throw new IllegalArgumentException("Points colinéaires");
        double d = - n.dot(p1.toVec());
        return new Plane(n.x, n.y, n.z, d);
    }

    public static void main(String[] args){
        List<Point3D> cloud;

        if(args.length >= 1){
            cloud = CsvPointReader.readCsvPoints(args[0]);
            System.out.println("Points chargés depuis " + args[0] + " : " + cloud.size());
        } else {
            cloud = generateSynthetic(); // fallback
            System.out.println("Points synthétiques générés : " + cloud.size());
        }

        int iterations = 2000;
        double inlierThreshold = 0.05;
        int minInliersForAccept = Math.min(8, cloud.size());

        RansacPlaneFitter fitter = new RansacPlaneFitter();
        RansacPlaneFitter.RansacResult res = fitter.findDominantPlane(cloud, iterations, inlierThreshold, minInliersForAccept);
        printResult(res);

        // Option : second plan
        List<Point3D> remaining = new ArrayList<>(cloud);
        remaining.removeAll(res.inliers);
        if(remaining.size() >= 3){
            try{
                RansacPlaneFitter.RansacResult res2 = fitter.findDominantPlane(remaining, iterations, inlierThreshold, Math.min(8, remaining.size()));
                System.out.println("\nSecond plan détecté :");
                printResult(res2);
            } catch (RuntimeException ignore){}
        }

        // Exemple pente locale entre deux points
        Point3D A = new Point3D(0,0,0);
        Point3D B = new Point3D(5,0, 0.577*5);
        PlaneMetrics.SlopeSegment seg = PlaneMetrics.slopeBetween(A,B);
        System.out.println("\nPente locale entre A et B: " + seg);
    }

    private static void printResult(RansacPlaneFitter.RansacResult res){
        Plane best = res.plane;
        double incDeg = PlaneMetrics.inclinationDegFromHorizontal(best);
        double slopePct = PlaneMetrics.slopePercent(best);
        Vec3 dir = PlaneMetrics.maxSlopeDirection(best);
        System.out.printf(Locale.US, "Plan dominant: a=%.6f b=%.6f c=%.6f d=%.6f%n", best.a, best.b, best.c, best.d);
        System.out.println("Inliers: " + res.inliers.size());
        System.out.printf(Locale.US, "Inclinaison (vs horizontal): %.2f°  |  Pente: %.2f%%%n", incDeg, slopePct);
        System.out.printf(Locale.US, "Direction max pente (unitaire): [%.4f, %.4f, %.4f]%n", dir.x, dir.y, dir.z);
    }

    private static List<Point3D> generateSynthetic(){
        List<Point3D> cloud = new ArrayList<>();
        Random r = new Random(0);
        for(int i=0;i<1000;i++){
            double x = r.nextDouble()*10.0;
            double y = r.nextDouble()*10.0;
            double z = 0.577*x + r.nextGaussian()*0.02; // ~30°
            cloud.add(new Point3D(x,y,z));
        }
        for(int i=0;i<60;i++){
            double x = r.nextDouble()*10.0;
            double y = r.nextDouble()*10.0;
            double z = r.nextDouble()*5.0 + 5.0; // outliers
            cloud.add(new Point3D(x,y,z));
        }
        return cloud;
    }
}
