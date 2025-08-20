package com.examen.demo.geom;

/**
 * Représente un point en 3 dimensions (x, y, z).
 * Cette classe est immuable : ses coordonnées ne peuvent pas être modifiées
 * après la création de l’objet.
 */
public class Point3D {
  /** Coordonnée X du point */
  public final double x;

  /** Coordonnée Y du point */
  public final double y;

  /** Coordonnée Z du point */
  public final double z;

  /**
   * Constructeur pour créer un point 3D.
   *
   * @param x coordonnée X
   * @param y coordonnée Y
   * @param z coordonnée Z
   */
  public Point3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Convertit ce point en un vecteur {@link Vec3}.
   * Utile pour effectuer des opérations vectorielles (produit scalaire,
   * produit vectoriel, normalisation...).
   *
   * @return un vecteur équivalent à ce point (x, y, z)
   */
  public Vec3 toVec() {
    return new Vec3(x, y, z);
  }
}
