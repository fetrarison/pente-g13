package com.examen.demo.metrics;

import com.examen.demo.geom.Point3D;
import com.examen.demo.geom.Vec3;
import com.examen.demo.model.Plane;

import java.util.Locale;

public class PlaneMetrics {

    public static double inclinationDegFromHorizontal(Plane plane){
        Vec3 n = plane.normal().normalize();
        Vec3 z = new Vec3(0,0,1);
        double cos = Math.abs(n.dot(z));
        double angleNZ = Math.toDegrees(Math.acos(clamp(cos, -1.0, 1.0)));
        return 90.0 - angleNZ; // angle du plan vs XY
    }

    public static double slopePercent(Plane plane){
        double thetaDeg = inclinationDegFromHorizontal(plane);
        return Math.tan(Math.toRadians(thetaDeg)) * 100.0;
    }

    public static Vec3 maxSlopeDirection(Plane plane){
        Vec3 n = plane.normal().normalize();
        Vec3 g = new Vec3(0,0,-1);
        Vec3 gPar = g.sub(n.mul(g.dot(n))); // projection de g sur le plan
        double nn = gPar.norm();
        return nn == 0 ? gPar : gPar.mul(1.0/nn);
    }

    public static class SlopeSegment {
        public final double horizontalRun;
        public final double verticalRise;
        public final double slopePercent;
        public final double slopeDeg;

        public SlopeSegment(double run, double rise, double percent, double deg){
            this.horizontalRun=run; this.verticalRise=rise; this.slopePercent=percent; this.slopeDeg=deg;
        }
        @Override public String toString(){
            return String.format(Locale.US,
                    "run=%.3f, rise=%.3f, slope=%.2f%% (%.2f°)",
                    horizontalRun, verticalRise, slopePercent, slopeDeg);
        }
    }

    public static SlopeSegment slopeBetween(Point3D A, Point3D B){
        double dx = B.x - A.x, dy = B.y - A.y, dz = B.z - A.z;
        double run = Math.hypot(dx, dy);
        double rise = dz;
        if(run == 0){
            double deg = (rise == 0) ? 0.0 : 90.0;
            double percent = (rise == 0) ? 0.0 : Double.POSITIVE_INFINITY;
            return new SlopeSegment(run, rise, percent, deg);
        }
        double deg = Math.toDegrees(Math.atan2(Math.abs(rise), run));
        double percent = Math.tan(Math.toRadians(deg)) * 100.0;
        return new SlopeSegment(run, rise, percent, deg);
    }

    private static double clamp(double v, double lo, double hi){
        return Math.max(lo, Math.min(hi, v));
    }
}