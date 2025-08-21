package com.examen.demo.service;

import com.examen.demo.geom.Point3D;
import com.examen.demo.model.Plane;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaneFittingService {

    public Plane fitPlane(List<Point3D> points) {
        if (points.size() < 3) {
            throw new IllegalArgumentException("Il faut au moins 3 points pour ajuster un plan.");
        }

        // Moyennes
        double meanX = points.stream().mapToDouble(Point3D::getX).average().orElse(0);
        double meanY = points.stream().mapToDouble(Point3D::getY).average().orElse(0);
        double meanZ = points.stream().mapToDouble(Point3D::getZ).average().orElse(0);

        // Covariance (simple méthode : vectorisation normale)
        double xx = 0, xy = 0, xz = 0, yy = 0, yz = 0;
        for (Point3D p : points) {
            double dx = p.getX() - meanX;
            double dy = p.getY() - meanY;
            double dz = p.getZ() - meanZ;
            xx += dx * dx;
            xy += dx * dy;
            xz += dx * dz;
            yy += dy * dy;
            yz += dy * dz;
        }

        // Normal du plan (méthode simple approx)
        double a = yz;
        double b = -xz;
        double c = xy;
        double d = -(a*meanX + b*meanY + c*meanZ);

        return new Plane(a, b, c, d);
    }
}
