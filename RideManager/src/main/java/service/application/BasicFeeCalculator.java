package service.application;

import jakarta.inject.Singleton;

import java.util.Date;

@Singleton
public class BasicFeeCalculator implements FeeCalculator {

    private double basicFee = 60;
    @Override
    public int minimumAmountForConnection() {
        return 1;
    }

    @Override
    public int calculateFee(Date startDate, Date endDate) {
        return (int)((endDate.getTime() - startDate.getTime()) * basicFee / 3600000);
    }
}
