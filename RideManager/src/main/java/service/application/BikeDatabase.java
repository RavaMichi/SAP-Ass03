package service.application;

public interface BikeDatabase {
    void saveBike(String id);
    boolean doesBikeExist(String bikeId);
}
