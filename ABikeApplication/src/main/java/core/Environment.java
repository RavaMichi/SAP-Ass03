package core;

import java.util.List;

/**
 * Entity object. Represents the road environment used by the agents.
 */
public interface Environment {
    List<Road> getAllRoads();
    List<Road> getRoadsInRadius(V2d center, double radius);
    List<V2d> getStationPositions();
}
