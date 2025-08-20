package com.examen.demo.algorithms;

import com.examen.demo.geom.Point3D;
import com.examen.demo.model.Plane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implémentation de l’algorithme RANSAC pour ajuster un plan dominant
 * dans un nuage de points 3D.
 * <p>
 * RANSAC (RANdom SAmple Consensus) est un algorithme robuste qui
 * permet de trouver un modèle (ici un plan) en présence de bruit et d’outliers.
 */
public class RansacPlaneFitter {

  /**
   * Résultat d’un ajustement RANSAC :
   * <ul>
   *   <li>le plan dominant détecté</li>
   *   <li>la liste des points (inliers) appartenant à ce plan</li>
   * </ul>
   */
  public static class RansacResult {
    /** Plan ajusté aux inliers */
    public final Plane plane;

    /** Ensemble des points considérés comme inliers */
    public final List<Point3D> inliers;

    /**
     * Construit un résultat RANSAC.
     *
     * @param plane   plan dominant trouvé
     * @param inliers liste des points appartenant au plan
     */
    public RansacResult(Plane plane, List<Point3D> inliers) {
      this.plane = plane;
      this.inliers = inliers;
    }
  }

  /**
   * Recherche le plan dominant dans un nuage de points à l’aide de RANSAC.
   *
   * @param points              liste de points 3D
   * @param iterations          nombre maximal d’itérations
   * @param inlierThreshold     distance maximale d’un point au plan pour être considéré comme inlier
   * @param minInliersForAccept nombre minimum d’inliers requis pour accepter un plan
   * @return un objet {@link RansacResult} contenant le plan dominant et ses inliers
   * @throws IllegalArgumentException si la liste contient moins de 3 points
   * @throws RuntimeException si aucun plan valide n’est trouvé après les itérations
   */
  public RansacResult findDominantPlane(
          List<Point3D> points, int iterations, double inlierThreshold, int minInliersForAccept) {

    if (points == null || points.size() < 3)
      throw new IllegalArgumentException("Besoin d'au moins 3 points");

    Plane bestPlane = null;
    List<Point3D> bestInliers = Collections.emptyList();
    ThreadLocalRandom rnd = ThreadLocalRandom.current();

    for (int it = 0; it < iterations; it++) {
      // Sélection aléatoire de 3 points distincts
      int i1 = rnd.nextInt(points.size());
      int i2 = rnd.nextInt(points.size());
      int i3 = rnd.nextInt(points.size());
      if (i1 == i2 || i1 == i3 || i2 == i3) {
        it--; // recommencer l’itération si doublons
        continue;
      }

      Plane plane;
      try {
        // Construire un plan à partir de 3 points
        plane = Plane.from3Points(points.get(i1), points.get(i2), points.get(i3));
      } catch (IllegalArgumentException e) {
        continue; // si les points sont colinéaires, on ignore
      }

      // Identifier les inliers pour ce plan
      List<Point3D> inliers = new ArrayList<>();
      for (Point3D p : points) {
        if (plane.distance(p) <= inlierThreshold) {
          inliers.add(p);
        }
      }

      // Mettre à jour si meilleur résultat trouvé
      if (inliers.size() > bestInliers.size()) {
        bestInliers = inliers;
        bestPlane = plane;
      }
    }

    // Vérifier qu’un plan valide a été trouvé
    if (bestPlane == null || bestInliers.size() < Math.max(minInliersForAccept, 3)) {
      throw new RuntimeException("Aucun plan dominant trouvé. Ajuste seuil/itérations/données.");
    }
    return new RansacResult(bestPlane, bestInliers);
  }
}
