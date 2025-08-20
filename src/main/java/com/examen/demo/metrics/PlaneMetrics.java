package com.examen.demo.metrics;

import com.examen.demo.geom.Point3D;
import com.examen.demo.geom.Vec3;
import com.examen.demo.model.Plane;
import java.util.Locale;

/**
 * Classe utilitaire fournissant des méthodes pour calculer des métriques
 * et caractéristiques associées à un plan 3D (inclinaison, pente, direction de pente...).
 */
public class PlaneMetrics {

  /**
   * Calcule l’inclinaison d’un plan par rapport à l’horizontale.
   * <p>
   * L’inclinaison est définie comme l’angle entre la normale du plan et la verticale (axe Z).
   *
   * @param plane le plan considéré
   * @return l’angle en degrés (0° = plan horizontal, 90° = plan vertical)
   */
  public static double inclinationDegFromHorizontal(Plane plane) {
    Vec3 n = plane.normal().normalize();
    Vec3 z = new Vec3(0, 0, 1);
    double cos = Math.abs(n.dot(z));
    double angleNZ = Math.toDegrees(Math.acos(clamp(cos, -1.0, 1.0)));
    return angleNZ;
  }

  /**
   * Calcule la pente du plan en pourcentage.
   * <p>
   * La pente (%) est définie comme {@code tan(angle) * 100},
   * où {@code angle} est l’inclinaison par rapport à l’horizontale.
   *
   * @param plane le plan considéré
   * @return pente du plan en pourcentage
   */
  public static double slopePercent(Plane plane) {
    double thetaDeg = inclinationDegFromHorizontal(plane);
    return Math.tan(Math.toRadians(thetaDeg)) * 100.0;
  }

  /**
   * Calcule la direction de la pente maximale sur le plan.
   * <p>
   * C’est la direction horizontale dans laquelle la pente est la plus forte.
   *
   * @param plane le plan considéré
   * @return un vecteur unitaire indiquant la direction de pente maximale
   */
  public static Vec3 maxSlopeDirection(Plane plane) {
    Vec3 n = plane.normal().normalize();
    Vec3 g = new Vec3(0, 0, -1); // gravité
    Vec3 gPar = g.sub(n.mul(g.dot(n))); // projection de g sur le plan
    double nn = gPar.norm();
    return nn == 0 ? gPar : gPar.mul(1.0 / nn);
  }

  /**
   * Classe représentant un segment incliné (entre deux points ou issu d’un plan),
   * avec sa pente exprimée en degrés et en pourcentage.
   */
  public static class SlopeSegment {
    /** Distance horizontale parcourue */
    public final double horizontalRun;

    /** Différence d’altitude (verticale) */
    public final double verticalRise;

    /** Pente en pourcentage */
    public final double slopePercent;

    /** Pente en degrés */
    public final double slopeDeg;

    /**
     * Constructeur d’un segment de pente.
     *
     * @param run distance horizontale
     * @param rise différence verticale
     * @param percent pente en pourcentage
     * @param deg pente en degrés
     */
    public SlopeSegment(double run, double rise, double percent, double deg) {
      this.horizontalRun = run;
      this.verticalRise = rise;
      this.slopePercent = percent;
      this.slopeDeg = deg;
    }

    /**
     * Retourne une description lisible du segment sous la forme :
     * {@code run=..., rise=..., slope=...% (...°)}.
     */
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

  /**
   * Calcule la pente entre deux points 3D.
   *
   * @param A premier point
   * @param B second point
   * @return un objet {@link SlopeSegment} contenant les informations de pente
   */
  public static SlopeSegment slopeBetween(Point3D A, Point3D B) {
    double dx = B.x - A.x, dy = B.y - A.y, dz = B.z - A.z;
    double run = Math.hypot(dx, dy);
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

  /**
   * Contraint une valeur {@code v} dans l’intervalle [{@code lo}, {@code hi}].
   *
   * @param v  valeur à contraindre
   * @param lo borne inférieure
   * @param hi borne supérieure
   * @return valeur bornée
   */
  private static double clamp(double v, double lo, double hi) {
    return Math.max(lo, Math.min(hi, v));
  }
}
