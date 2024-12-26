package service.application;

import service.domain.Station;

import java.util.List;

/** Outbound Port
 * Repository object. Allows to persist and query recharge stations.
 */
public interface StationDatabase {
    void add(Station station);
    List<Station> getAll();
}
