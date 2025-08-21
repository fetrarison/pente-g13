package com.examen.demo.service;

import com.examen.demo.geom.Point3D;
import com.examen.demo.model.Plane;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RansacPlaneService {

    public Plane fitPlane(List<Point3D> points) {
        // ⚠️ Ici tu peux brancher ton vrai algo RANSAC
        // Pour l’exemple : on retourne un plan Z = 0 (a=0, b=0, c=1, d=0)
        return new Plane(0, 0, 1, 0);
    }

    public Map<String, List<Point3D>> classifyPoints(List<Point3D> points, Plane plane) {
        List<Point3D> inliers = new ArrayList<>();
        List<Point3D> outliers = new ArrayList<>();

        double threshold = 0.1; // marge de tolérance

        for (Point3D p : points) {
            double dist = plane.distanceToPoint(p);
            if (dist < threshold) {
                inliers.add(p);
            } else {
                outliers.add(p);
            }
        }

        Map<String, List<Point3D>> result = new HashMap<>();
        result.put("inliers", inliers);
        result.put("outliers", outliers);
        return result;
    }
}
