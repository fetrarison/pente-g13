package com.examen.demo.endpoint.rest.controller;

import com.examen.demo.algorithms.RansacPlaneFitter;
import com.examen.demo.geom.Point3D;
import com.examen.demo.metrics.PlaneMetrics;
import com.examen.demo.service.RansacPlaneService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plane")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PlaneController {

  private final RansacPlaneService ransacService;

  @PostMapping("/fit")
  public ResponseEntity<?> fitPlane(@RequestBody List<Point3D> points) {
    try {
      RansacPlaneFitter fitter = new RansacPlaneFitter();
      RansacPlaneFitter.RansacResult result =
          fitter.findDominantPlane(points, 2000, 0.05, Math.min(8, points.size()));

      Map<String, Object> response = new HashMap<>();
      response.put("inliers", result.inliers);
      response.put("outliers", points.stream().filter(p -> !result.inliers.contains(p)).toList());
      response.put("plane", result.plane);
      response.put("inclination", PlaneMetrics.inclinationDegFromHorizontal(result.plane));

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      e.printStackTrace(); // pour voir l’erreur exacte dans la console
      return ResponseEntity.status(500)
          .body(
              Map.of(
                  "error", e.getMessage(),
                  "stackTrace", Arrays.toString(e.getStackTrace())));
    }
  }
}
