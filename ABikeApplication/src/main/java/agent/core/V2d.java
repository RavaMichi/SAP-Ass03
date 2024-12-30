package agent.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Value object. Represents a 2D vector.
 */
@Serdeable
public class V2d implements java.io.Serializable {
    private final double x;
    private final double y;

    @JsonCreator
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
    public V2d sub(V2d v) {
        return new V2d(x-v.x,y-v.y);
    }
    public V2d mul(double fact){
        return new V2d(x*fact,y*fact);
    }
    public V2d div(double fact) {
        return new V2d(x/fact,y/fact);
    }
    public double distance(V2d other) {
        return Math.sqrt(Math.pow(x - other.x,2) + Math.pow(y - other.y,2));
    }
    public double mod(){
        return (double)Math.sqrt(x*x+y*y);
    }
    public String toString(){
        return "("+x+","+y+")";
    }

}
