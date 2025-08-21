package com.examen.demo;

import static org.junit.jupiter.api.Assertions.*;

import com.examen.demo.geom.Point3D;
import com.examen.demo.io.CsvPointReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class CsvPointReaderTest {

  @Test
  void readsCsv_ignoresHeaderAndComments_andParsesValues() throws Exception {
    String csv =
        """
        x,y,z
        # comment
        0,0,0
        1 , 2 , 3
        -1.5,  4.2 ,  0
        """;
    Path tmp = Files.createTempFile("points", ".csv");
    Files.writeString(tmp, csv);

    List<Point3D> pts = CsvPointReader.readCsvPoints(tmp.toString());

    assertEquals(3, pts.size());
    assertEquals(0.0, pts.get(0).x, 1e-12);
    assertEquals(3.0, pts.get(1).z, 1e-12);
    assertEquals(-1.5, pts.get(2).x, 1e-12);
    assertEquals(4.2, pts.get(2).y, 1e-12);
  }
}
