package com.examen.demo.geom;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Point3D {
  public double x, y, z;

  public Vec3 toVec() {
    return new Vec3(x, y, z);
  }
}
