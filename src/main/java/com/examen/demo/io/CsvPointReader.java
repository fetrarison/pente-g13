package com.examen.demo.io;

import com.examen.demo.geom.Point3D;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CsvPointReader {

    public static List<Point3D> readCsvPoints(String path){
        List<Point3D> pts = new ArrayList<>();
        try {
            for (String raw : Files.readAllLines(Paths.get(path))) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("x")) continue;
                String[] t = line.split("\\s*,\\s*");
                if (t.length < 3) continue;
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
