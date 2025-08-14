package com.examen.demo.geom;

import lombok.AllArgsConstructor;

public class Point3D {
    public final double x, y, z;
    public Point3D(double x, double y, double z){ this.x=x; this.y=y; this.z=z; }
    public Vec3 toVec(){ return new Vec3(x, y, z); }
}
