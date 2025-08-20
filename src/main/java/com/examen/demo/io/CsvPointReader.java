package com.examen.demo.io;

import com.examen.demo.geom.Point3D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaire pour lire des points 3D à partir d’un fichier CSV.
 * <p>
 * Le format attendu est une liste de lignes contenant au moins 3 colonnes numériques :
 * <pre>
 * x,y,z
 * 1.0,2.0,3.0
 * 4.0,5.0,6.0
 * ...
 * </pre>
 * Les lignes vides, les commentaires (commençant par {@code #})
 * et les en-têtes (commençant par {@code x}) sont ignorés.
 */
public class CsvPointReader {

  /**
   * Lit un fichier CSV et retourne la liste des points 3D contenus.
   *
   * @param path chemin du fichier CSV
   * @return liste de points {@link Point3D} lus depuis le fichier
   * @throws RuntimeException si une erreur d’E/S survient lors de la lecture
   */
  public static List<Point3D> readCsvPoints(String path) {
    List<Point3D> pts = new ArrayList<>();
    try {
      for (String raw : Files.readAllLines(Paths.get(path))) {
        String line = raw.trim();

        // Ignorer les lignes vides, commentaires et en-têtes
        if (line.isEmpty() || line.startsWith("#") || line.startsWith("x")) continue;

        // Découper la ligne sur les virgules
        String[] t = line.split("\\s*,\\s*");
        if (t.length < 3) continue;

        // Conversion en coordonnées numériques
        double x = Double.parseDouble(t[0]);
        double y = Double.parseDouble(t[1]);
        double z = Double.parseDouble(t[2]);

        pts.add(new Point3D(x, y, z));
      }
    } catch (IOException e) {
      throw new RuntimeException("Erreur lecture CSV: " + e.getMessage(), e);
    }
    return pts;
  }
}
