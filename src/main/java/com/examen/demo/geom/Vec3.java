package com.examen.demo.geom;

/**
 * Représente un vecteur en 3 dimensions (x, y, z).
 * Cette classe fournit les opérations classiques de l'algèbre vectorielle :
 * addition, soustraction, produit scalaire, produit vectoriel,
 * normalisation, etc.
 *
 * Les objets de cette classe sont immuables.
 */
public class Vec3 {
  /** Composante X du vecteur */
  public final double x;

  /** Composante Y du vecteur */
  public final double y;

  /** Composante Z du vecteur */
  public final double z;

  /**
   * Constructeur d’un vecteur 3D.
   *
   * @param x valeur de la composante X
   * @param y valeur de la composante Y
   * @param z valeur de la composante Z
   */
  public Vec3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Additionne ce vecteur avec un autre.
   *
   * @param o vecteur à additionner
   * @return un nouveau vecteur correspondant à (this + o)
   */
  public Vec3 add(Vec3 o) {
    return new Vec3(x + o.x, y + o.y, z + o.z);
  }

  /**
   * Soustrait un vecteur à ce vecteur.
   *
   * @param o vecteur à soustraire
   * @return un nouveau vecteur correspondant à (this - o)
   */
  public Vec3 sub(Vec3 o) {
    return new Vec3(x - o.x, y - o.y, z - o.z);
  }

  /**
   * Multiplie ce vecteur par un scalaire.
   *
   * @param s scalaire multiplicatif
   * @return un nouveau vecteur correspondant à (this * s)
   */
  public Vec3 mul(double s) {
    return new Vec3(x * s, y * s, z * s);
  }

  /**
   * Calcule le produit scalaire (dot product) avec un autre vecteur.
   *
   * @param o autre vecteur
   * @return valeur du produit scalaire this · o
   */
  public double dot(Vec3 o) {
    return x * o.x + y * o.y + z * o.z;
  }

  /**
   * Calcule le produit vectoriel (cross product) avec un autre vecteur.
   *
   * @param o autre vecteur
   * @return un nouveau vecteur correspondant à this × o
   */
  public Vec3 cross(Vec3 o) {
    return new Vec3(
            y * o.z - z * o.y,
            z * o.x - x * o.z,
            x * o.y - y * o.x
    );
  }

  /**
   * Calcule la norme (longueur) du vecteur.
   *
   * @return ||this|| (norme euclidienne)
   */
  public double norm() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  /**
   * Normalise ce vecteur (le rend de longueur 1).
   *
   * @return un vecteur unitaire correspondant à this / ||this||,
   *         ou this si le vecteur est nul.
   */
  public Vec3 normalize() {
    double n = norm();
    return n == 0 ? this : new Vec3(x / n, y / n, z / n);
  }
}
