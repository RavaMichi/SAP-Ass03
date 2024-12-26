package service.domain;

import io.micronaut.core.annotation.Introspected;

/**
 * Value object. Represents a 2D vector.
 */
@Introspected
public class V2d implements java.io.Serializable {
    private final double x;
    private final double y;

    public V2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double x() {
        return x;
    }
    public double y() {
        return y;
    }
    public V2d sum(V2d v){
        return new V2d(x+v.x,y+v.y);
    }
    public V2d mul(double fact){
        return new V2d(x*fact,y*fact);
    }
    public double mod(){
        return (double)Math.sqrt(x*x+y*y);
    }
    public String toString(){
        return "("+x+","+y+")";
    }

}
