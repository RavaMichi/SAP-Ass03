package app.domain;

import java.util.List;

public interface RentalService {
    List<EBike> getBikes();
    List<User> getUsers();
}
