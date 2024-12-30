package app;

import app.domain.RentalService;
import app.domain.VertxRentalService;
import app.presentation.AppView;

public class RunApp {
    public static void main(String[] args) {
        RentalService rentalService = new VertxRentalService();
        AppView view = new AppView(rentalService);

        view.display();
    }
}
